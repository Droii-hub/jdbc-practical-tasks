package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.PassengerMapper;
import com.walking.jdbc.model.Passenger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;

public class PassengerRepository {

    private final Logger log= LogManager.getLogger(PassengerRepository.class);
    private final PassengerMapper mapper;

    public PassengerRepository(PassengerMapper mapper) {
        this.mapper = mapper;
        createTableIfNotExist();
        if (tableIsEmpty()) fillTable();
    }

    private void createTableIfNotExist(){
        String sql="""
                 create table if not exists passenger_new (id bigserial,
                 last_name varchar(100),
                 first_name varchar(100),
                 birth_date date,
                 male bool default true,
                 last_purchase timestamp,
                 favorite_airports text[])
                 """;
        try (Connection connection=getConnection();
                Statement statement=connection.createStatement()){
            statement.executeUpdate(sql);
        } catch (SQLException e){
            log.error(e);
        }
    }

    private void fillTable(){
        String sql= """
                insert into passenger_new
                select * from passenger""";
        try (Connection connection=getConnection();
             Statement statement=connection.createStatement()){
            statement.executeUpdate(sql);
        } catch (SQLException e){
            log.error(e);
        }
    }

    private boolean tableIsEmpty(){
        String sql="select * from passenger_new";
        try (Connection connection=getConnection();
        Statement statement=connection.createStatement()){
            ResultSet result=statement.executeQuery(sql);
            return !result.next();
        } catch (SQLException e){
            log.error(e);
            return false;
        }
    }

    public List<Passenger> getAll(){
        String sql="select * from passenger_new";
        try (Connection connection=getConnection();
             Statement statement=connection.createStatement()){
            ResultSet result=statement.executeQuery(sql);
            return mapper.map(result);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void clearTable(){
        String sql= "truncate table passenger_new";
        try (Connection connection=getConnection();
             Statement statement=connection.createStatement()){
            statement.executeUpdate(sql);
        } catch (SQLException e){
            log.error(e);
        }
    }
    //    Реализация защищенная от SQL-инъекций:
    public List<Passenger> findByFullName(String firstName, String lastName) {
        String sql = "select * from passenger where first_name = ? and last_name = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);

            ResultSet result = statement.executeQuery();

            return mapper.map(result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    Реализация уязвимая для SQL-инъекций:
//    public List<Passenger> findByFullName(String firstName, String lastName) {
//        try (Connection connection = getConnection();
//             Statement statement = connection.createStatement()) {
//
//            String sql = "select * from passenger where first_name = %s and last_name = %s"
//                    .formatted(
//                            "'" + firstName + "'",
//                            "'" + lastName + "'"
//                    );
//
//            ResultSet result = statement.executeQuery(sql);
//
//            return mapper.map(result);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public List<Passenger> findAll() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery("select * from passenger");

            return mapper.map(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пассажиров", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/practice",
                "postgres",
                "postgres");
    }
}
