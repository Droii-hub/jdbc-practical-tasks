package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.PassengerMapper;
import com.walking.jdbc.model.Passenger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

public class PassengerRepository {

    private final Logger log= LogManager.getLogger(PassengerRepository.class);
    private final PassengerMapper mapper;
    private final DataSource dataSource;

    public PassengerRepository(PassengerMapper mapper, DataSource dataSource) {
        this.mapper = mapper;
        this.dataSource=dataSource;
        //if (tableIsEmpty()) fillTable();
    }

//    private void fillTable(){
//        String sql= """
//                insert into passenger_new
//                select * from passenger""";
//        try (Connection connection=dataSource.getConnection();
//             Statement statement=connection.createStatement()){
//            statement.executeUpdate(sql);
//        } catch (SQLException e){
//            log.error(e);
//        }
//    }
//
//    private boolean tableIsEmpty(){
//        String sql="select * from passenger_new";
//        try (Connection connection=dataSource.getConnection();
//        Statement statement=connection.createStatement()){
//            ResultSet result=statement.executeQuery(sql);
//            return !result.next();
//        } catch (SQLException e){
//            log.error(e);
//            return false;
//        }
//    }

    public List<Passenger> getAll(){
        String sql="select * from passenger_new";
        try (Connection connection=dataSource.getConnection();
             Statement statement=connection.createStatement()){
            ResultSet result=statement.executeQuery(sql);
            return mapper.map(result);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Passenger getById(Long id) throws SQLException {
        String sql="select * from passenger_new where id= ?";
        try (Connection connection=dataSource.getConnection();
        PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setLong(1,id);
            ResultSet rs=statement.executeQuery();
            var passengerList=mapper.map(rs);
            if (!passengerList.isEmpty())
                return passengerList.get(0);
            else return null ;
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<Passenger> getByMale(boolean male) throws SQLException {
        String sql="select * from passenger_new where male= ?";
        try (Connection connection=dataSource.getConnection();
             PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setBoolean(1,male);
            ResultSet rs=statement.executeQuery();
            return mapper.map(rs);
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<Passenger> getByBirthDate(LocalDate birth_date) throws SQLException {
        String sql="select * from passenger_new where birth_date= ?";
        try (Connection connection=dataSource.getConnection();
             PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setDate(1,Date.valueOf(birth_date));
            ResultSet rs=statement.executeQuery();
            return mapper.map(rs);
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void create(List<Passenger> passengers) throws SQLException {
        createWithoutId(passengers.stream().filter(i->i.getId()==null).collect(Collectors.toList()));
        createWithId(passengers.stream().filter(i->i.getId()!=null).collect(Collectors.toList()));
    }

    void createWithId(List<Passenger> passengers) throws SQLException {
        if (passengers.isEmpty()) return;
        String sql = "insert into passenger_new values (?, ?, ?, ?, ?, ?)";
        try (Connection connection=dataSource.getConnection();
             PreparedStatement statement =connection.prepareStatement(sql)) {
            for (Passenger passenger:passengers) {
                statement.setLong(1, passenger.getId());
                statement.setString(2, passenger.getLastName());
                statement.setString(3, passenger.getFirstName());
                statement.setDate(4, Date.valueOf(passenger.getBirthDate()));
                statement.setBoolean(5, passenger.isMale());
                statement.setTimestamp(6, Timestamp.valueOf(passenger.getLastPurchase()));
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    void createWithoutId(List<Passenger> passengers) throws SQLException {
        if (passengers.isEmpty()) return;
        String sql ="""
    insert into passenger_new (last_name, first_name, birth_date, male, last_purchase)
     values (?, ?, ?, ?, ?)""";
        try (Connection connection=dataSource.getConnection();
             PreparedStatement statement =connection.prepareStatement(sql)) {
            for (Passenger passenger:passengers) {
                statement.setString(1, passenger.getLastName());
                statement.setString(2, passenger.getFirstName());
                statement.setDate(3, Date.valueOf(passenger.getBirthDate()));
                statement.setBoolean(4, passenger.isMale());
                statement.setTimestamp(5, Timestamp.valueOf(passenger.getLastPurchase()));
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void deleteById(List<Passenger> passengers) throws SQLException {
        String sql="delete from passenger_new where id=?";
        try (Connection connection=dataSource.getConnection();
             PreparedStatement statement =connection.prepareStatement(sql)) {
            for(Passenger passenger:passengers){
                statement.setLong(1,passenger.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void updateById(List<Passenger> passengers) throws SQLException {
        String sql="""
        update passenger_new set last_name=?,
        first_name=?,
        birth_date=?,
        male=?,
        last_purchase=? where id=?""";
        try (Connection connection=dataSource.getConnection();
             PreparedStatement statement =connection.prepareStatement(sql)) {
            for(Passenger passenger:passengers){
                statement.setString(1,passenger.getLastName());
                statement.setString(2, passenger.getFirstName());
                statement.setDate(3, Date.valueOf(passenger.getBirthDate()));
                statement.setBoolean(4, passenger.isMale());
                statement.setTimestamp(5,Timestamp.valueOf(passenger.getLastPurchase()));
                statement.setLong(6,passenger.getId());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void clearTable(){
        String sql= "truncate table passenger_new";
        try (Connection connection=dataSource.getConnection();
             Statement statement=connection.createStatement()){
            statement.executeUpdate(sql);
        } catch (SQLException e){
            log.error(e);
        }
    }
}
