package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.TicketMapper;
import com.walking.jdbc.model.Passenger;
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

    public Ticket buyFirstTicket(Ticket ticket, Passenger passenger)throws SQLException{
        String sqlInsertPassenger="""
    insert into passenger_new (last_name, first_name, birth_date, male, last_purchase)
     values (?, ?, ?, ?, ?)""";
        String sqlGetPassenger= """
                select id from passenger_new where last_name=? and first_name=? and birth_date=?""";
        String sqlInsertTicket= """
                insert into ticket_new (departure_airport, arrival_airport, departure_date,
                 arrival_date, purchase_date, passenger_id)
                 values(?, ?, ?, ?, ?, ?)""";
        String sqlGetTicket= """
                select * from ticket_new where departure_airport=? and arrival_airport=? and departure_date=? and
                 arrival_date=? and purchase_date=? and passenger_id=?
                """;
        try(Connection connection=dataSource.getConnection();
        PreparedStatement insertPassengerStatement=connection.prepareStatement(sqlInsertPassenger);
        PreparedStatement getPassengerStatement=connection.prepareStatement(sqlGetPassenger);
        PreparedStatement ticketInsertStatement=connection.prepareStatement(sqlInsertTicket);
        PreparedStatement getTicketStatement=connection.prepareStatement(sqlGetTicket)){
            connection.setAutoCommit(false);
            insertPassengerStatement.setString(1, passenger.getLastName());
            insertPassengerStatement.setString(2, passenger.getFirstName());
            insertPassengerStatement.setDate(3, Date.valueOf(passenger.getBirthDate()));
            insertPassengerStatement.setBoolean(4, passenger.isMale());
            insertPassengerStatement.setTimestamp(5, Timestamp.valueOf(passenger.getLastPurchase()));

            getPassengerStatement.setString(1, passenger.getLastName());
            getPassengerStatement.setString(2, passenger.getFirstName());
            getPassengerStatement.setDate(3, Date.valueOf(passenger.getBirthDate()));


            ticketInsertStatement.setString(1,ticket.getDeparture_airport());
            ticketInsertStatement.setString(2, ticket.getArrival_airport());
            ticketInsertStatement.setTimestamp(3, Timestamp.valueOf(ticket.getDeparture_date()));
            ticketInsertStatement.setTimestamp(4, Timestamp.valueOf(ticket.getArrival_date()));
            ticketInsertStatement.setTimestamp(5, Timestamp.valueOf(ticket.getPurchase_date()));

            getTicketStatement.setString(1,ticket.getDeparture_airport());
            getTicketStatement.setString(2, ticket.getArrival_airport());
            getTicketStatement.setTimestamp(3, Timestamp.valueOf(ticket.getDeparture_date()));
            getTicketStatement.setTimestamp(4, Timestamp.valueOf(ticket.getArrival_date()));
            getTicketStatement.setTimestamp(5, Timestamp.valueOf(ticket.getPurchase_date()));

            try{
                insertPassengerStatement.executeUpdate();
                var rs=getPassengerStatement.executeQuery();
                rs.next();
                ticketInsertStatement.setLong(6, rs.getLong("id"));
                getTicketStatement.setLong(6, rs.getLong("id"));
                ticketInsertStatement.executeUpdate();
                rs=getTicketStatement.executeQuery();
                connection.commit();
                var ticketList=mapper.map(rs);
                if (!ticketList.isEmpty())
                    ticket=ticketList.get(0);
                else
                    throw new RuntimeException("Билет не создан");
            } catch (Exception e){
                connection.rollback();
                log.error("Транзакция была откачена");
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return ticket;
    }
}
