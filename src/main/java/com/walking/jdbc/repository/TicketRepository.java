package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.TicketMapper;
import com.walking.jdbc.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

public class TicketRepository {
    private final Logger log= LogManager.getLogger(TicketRepository.class);
    private final TicketMapper mapper;
    private final DataSource dataSource;

    public TicketRepository(TicketMapper mapper, DataSource dataSource) {
        this.mapper = mapper;
        this.dataSource = dataSource;
    }

    public void create(List<Ticket> tickets) throws SQLException{
        createWithoutId(tickets.stream().filter(i->i.getId()==null).collect(Collectors.toList()));
        createWithId(tickets.stream().filter(i->i.getId()!=null).collect(Collectors.toList()));
    }

    void createWithoutId(List<Ticket> tickets) throws SQLException{
        if (tickets.isEmpty()) return;
        String sql= """
                insert into ticket_new (departure_airport, arrival_airport, departure_date,
                 arrival_date, purchase_date, passenger_id)
                 values(?, ?, ?, ?, ?, ?)""";
        try (Connection connection=dataSource.getConnection();
             PreparedStatement statement=connection.prepareStatement(sql)){
            for (Ticket ticket:tickets){
                statement.setString(1,ticket.getDeparture_airport());
                statement.setString(2, ticket.getArrival_airport());
                statement.setTimestamp(3, Timestamp.valueOf(ticket.getDeparture_date()));
                statement.setTimestamp(4, Timestamp.valueOf(ticket.getArrival_date()));
                statement.setTimestamp(5, Timestamp.valueOf(ticket.getPurchase_date()));
                statement.setLong(6, ticket.getPassenger_id());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    void createWithId(List<Ticket> tickets)throws SQLException{
        if (tickets.isEmpty()) return;
        String sql= "insert into ticket_new values(?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection=dataSource.getConnection();
             PreparedStatement statement=connection.prepareStatement(sql)){
            for (Ticket ticket:tickets){
                statement.setLong(1, ticket.getId());
                statement.setString(2,ticket.getDeparture_airport());
                statement.setString(3, ticket.getArrival_airport());
                statement.setTimestamp(4, Timestamp.valueOf(ticket.getDeparture_date()));
                statement.setTimestamp(5, Timestamp.valueOf(ticket.getArrival_date()));
                statement.setTimestamp(6, Timestamp.valueOf(ticket.getPurchase_date()));
                statement.setLong(7, ticket.getPassenger_id());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Ticket getById(Long id) throws SQLException{
        String sql="select * from ticket_new where id=?";
        try (Connection connection=dataSource.getConnection();
             PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setLong(1,id);
            ResultSet result=statement.executeQuery();
            var ticketList=mapper.map(result);
            if (!ticketList.isEmpty())
                return ticketList.get(0);
            else
                return null;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<Ticket> getByPassengerId(Long passengerId) throws SQLException{
        String sql="select * from ticket_new where passenger_id=?";
        try (Connection connection=dataSource.getConnection();
             PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setLong(1,passengerId);
            ResultSet result=statement.executeQuery();
            return mapper.map(result);
        } catch (SQLException e){
            log.error("Ошибка при получении списка билетов пассажира {}", e.getMessage());
            return null;
        }
    }

    public void deleteById(List<Ticket> tickets) throws SQLException{
        String sql= "delete from ticket_new where id=?";
        try (Connection connection=dataSource.getConnection();
             PreparedStatement statement=connection.prepareStatement(sql)){
            for (Ticket ticket:tickets){
                statement.setLong(1, ticket.getId());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void updateById(List<Ticket> tickets)throws SQLException{
        String sql= """
                update ticket_new set departure_airport=?,
                arrival_airport=?,
                departure_date=?,
                arrival_date=?,
                purchase_date=?,
                passenger_id=? where id=?""";
        try (Connection connection=dataSource.getConnection();
             PreparedStatement statement=connection.prepareStatement(sql)){
            for (Ticket ticket:tickets){
                statement.setLong(7, ticket.getId());
                statement.setString(1,ticket.getDeparture_airport());
                statement.setString(2, ticket.getArrival_airport());
                statement.setTimestamp(3, Timestamp.valueOf(ticket.getDeparture_date()));
                statement.setTimestamp(4, Timestamp.valueOf(ticket.getArrival_date()));
                statement.setTimestamp(5, Timestamp.valueOf(ticket.getPurchase_date()));
                statement.setLong(6, ticket.getPassenger_id());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void buyFirstTicket(Ticket ticket)throws SQLException{
        String sqlPassenger="insert into passenger_new (id) values (?)";
        String sqlTicket= """
                insert into ticket_new (departure_airport, arrival_airport, departure_date,
                 arrival_date, purchase_date, passenger_id)
                 values(?, ?, ?, ?, ?, ?)""";
        try(Connection connection=dataSource.getConnection();
        PreparedStatement passengerStatement=connection.prepareStatement(sqlPassenger);
        PreparedStatement ticketStatement=connection.prepareStatement(sqlTicket)){
            connection.setAutoCommit(false);
            passengerStatement.setLong(1,ticket.getPassenger_id());
            ticketStatement.setString(1,ticket.getDeparture_airport());
            ticketStatement.setString(2, ticket.getArrival_airport());
            ticketStatement.setTimestamp(3, Timestamp.valueOf(ticket.getDeparture_date()));
            ticketStatement.setTimestamp(4, Timestamp.valueOf(ticket.getArrival_date()));
            ticketStatement.setTimestamp(5, Timestamp.valueOf(ticket.getPurchase_date()));
            ticketStatement.setLong(6, ticket.getPassenger_id());

            try{
                passengerStatement.executeUpdate();
                ticketStatement.executeUpdate();
            } catch (Exception e){
                connection.rollback();
                log.error("Транзакция была откачена");
            }
        } catch (SQLException e){
            log.error(e);
        }
    }
}
