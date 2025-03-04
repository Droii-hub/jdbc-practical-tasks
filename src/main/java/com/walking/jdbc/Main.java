package com.walking.jdbc;

import com.walking.jdbc.mapper.PassengerMapper;
import com.walking.jdbc.mapper.TicketMapper;
import com.walking.jdbc.model.Passenger;
import com.walking.jdbc.model.Ticket;
import com.walking.jdbc.repository.PassengerRepository;
import com.walking.jdbc.repository.TicketRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Задача урока 129:
 * <a href="https://github.com/KFalcon2022/lessons/blob/master/lessons/jdbc/129/Statement.%20DDL.%20ResultSet.md#%D0%B7%D0%B0%D0%B4%D0%B0%D1%87%D0%B0">ссылка</a>
 * <p>
 *
 Создание таблицы passenger при старте приложения, если ее еще не существует.
 Атрибутивный состав можно выбрать на основе класса Passenger в том же репозитории или
 на основе практики к разделу SQL (уроки 79-112, разбор практики здесь);
 Создайте метод удаления всех существующих пассажиров;
 Создайте (или изучите предложенное в репозитории решение) метод для получения списка
 всех существующих пассажиров;
 Покройте получившуюся функциональность юнит-тестами, замокав взаимодействие с БД.

 * Задача урока 130:
 * <a href="https://github.com/KFalcon2022/lessons/blob/master/lessons/jdbc/130/PreparedStatement.%20SQL%20injection.md#%D0%B7%D0%B0%D0%B4%D0%B0%D1%87%D0%B0">ссылка</a>
 * <p>
 * Задача урока 131:
 * <a href="https://github.com/KFalcon2022/lessons/blob/master/lessons/jdbc/131/Batch.md#%D0%B7%D0%B0%D0%B4%D0%B0%D1%87%D0%B0">ссылка</a>
 * <p>
 * Задача урока 132:
 * <a href="https://github.com/KFalcon2022/lessons/blob/master/lessons/jdbc/132/JDBC.%20Tranastions.md#%D0%B7%D0%B0%D0%B4%D0%B0%D1%87%D0%B0">ссылка</a>
 */
public class Main {
    public static void main(String[] args){
//        PGSimpleDataSource pgSimpleDataSource = new PGSimpleDataSource();
//        pgSimpleDataSource.setDatabaseName("practice");
//        pgSimpleDataSource.setServerNames(new String[]{"localhost"});
//        pgSimpleDataSource.setUser("postgres");
//        pgSimpleDataSource.setPassword("postgres");
        HikariConfig configuration = new HikariConfig("hikari.properties");
        try (HikariDataSource dataSource = new HikariDataSource(configuration)) {
            FluentConfiguration flywayConfiguration= Flyway.configure()
                    .dataSource(dataSource).baselineOnMigrate(true);
            Flyway flyway=flywayConfiguration.load();
            flyway.migrate();
            //createTableIfNotExist(dataSource);
            PassengerRepository passengerRepository = new PassengerRepository(new PassengerMapper(), dataSource);
            Passenger passenger = new Passenger();
            passenger.setLastName("Horitonov");
            passenger.setFirstName("Petr");
            passenger.setMale(true);
            passenger.setBirthDate(LocalDate.of(1990, 3, 4));
            passenger.setLastPurchase(LocalDateTime.now());
            TicketRepository ticketRepository=new TicketRepository(new TicketMapper(), dataSource);
            Ticket ticket=new Ticket();
            ticket.setArrival_date(LocalDateTime.of(2025,3,5,3,30,0));
            ticket.setDeparture_date(LocalDateTime.of(2025,3,5,2,30,0));
            ticket.setPurchase_date(LocalDateTime.of(2025,3,1,18,30,0));
            ticket.setDeparture_airport("Minsk");
            ticket.setArrival_airport("Novosibirsk");
            try {
                ticket=ticketRepository.buyFirstTicket(ticket, passenger);
                System.out.println(ticket.getId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    private final static Logger log = LogManager.getLogger(Main.class);

    private static void createTableIfNotExist(DataSource dataSource){
        String sql="""
                 create table if not exists passenger_new (id bigserial,
                 last_name varchar(100),
                 first_name varchar(100),
                 birth_date date,
                 male bool default true,
                 last_purchase timestamp)
                 """;
        try (Connection connection=dataSource.getConnection();
             Statement statement=connection.createStatement()){
            statement.executeUpdate(sql);
        } catch (SQLException e){
            log.error(e);
        }
    }
//
//    private void createConnectionExample() {
//        try (Connection connection = DriverManager.getConnection(
//                "jdbc:postgresql://localhost:5432/practice",
//                "postgres",
//                "postgres")) {
//            // Дальнейшая работа с БД через connection
//        } catch (SQLException e) {
//            log.error(e);
//        }
//    }

//    private void createStatementExample() {
//        try (Connection connection = getConnection();
//             Statement statement = connection.createStatement()) {
//            // Дальнейшая работа с БД через statement
//        } catch (SQLException e) {
//            log.error(e);
//        }
//    }
//
//    private void executeDDLExample() {
//        try (Connection connection = getConnection();
//             Statement statement = connection.createStatement()) {
//
//            var sql = """
//                    create table table_example (
//                        column_example  bigint
//                    )
//                    """;
//
//            statement.execute(sql);
//        } catch (SQLException e) {
//            log.error(e);
//        }
//    }

//    private void executeSelectQueryExample() {
//        try (Connection connection = getConnection();
//             Statement statement = connection.createStatement()) {
//
////            Отправка SELECT-запроса через Statement
//            statement.execute("select * from passenger");
//
////            Получение результатов SELECT-запроса.
////            Данный ResultSet будет содержать все строки и столбцы таблицы passenger.
//            ResultSet result = statement.getResultSet();
//
////            ... - Дальнейшая обработка ResultSet
//        } catch (SQLException e) {
//            log.error(e);
//        }
//    }
//
//    private void executeQueryExample() {
//        try (Connection connection = getConnection();
//             Statement statement = connection.createStatement()) {
//
////            Отправка SELECT-запроса через Statement
//            ResultSet result = statement.executeQuery("select * from passenger");
//
////            ... - Дальнейшая обработка ResultSet
//        } catch (SQLException e) {
//            log.error(e);
//        }
//    }
//
//    private void executeUpdateExample() {
//        try (Connection connection = getConnection();
//             Statement statement = connection.createStatement()) {
//
//            var sql = """
//                    create table table_example (
//                        column_example  bigint
//                    )
//                    """;
//
//            statement.executeUpdate(sql);
//        } catch (SQLException e) {
//            log.error(e);
//        }
//    }
//
//    private void findAllPassengersExample() {
//        var passengerMapper = new PassengerMapper();
//        var passengerRepository = new PassengerRepository(passengerMapper);
//
//        passengerRepository.findAll();
//    }
//
//    private void executeParameterizedQueryWithPreparedStatementExample() {
//        try (Connection connection = getConnection();
//             PreparedStatement statement = connection.prepareStatement("select * from passenger where id = ?")) {
//
//            // ... - Подготовка данных для выполнения запроса
//
//            ResultSet result = statement.executeQuery();
//
//            // ... - Дальнейшая обработка ResultSet
//        } catch (SQLException e) {
//            log.error(e);
//        }
//    }
//
//    private void executeStaticQueryWithPreparedStatementExample() {
//        try (Connection connection = getConnection();
//             PreparedStatement statement = connection.prepareStatement("select * from passenger")) {
//
//            ResultSet result = statement.executeQuery();
//
//            // ... - Дальнейшая обработка ResultSet
//        } catch (SQLException e) {
//            log.error(e);
//        }
//    }
//
//    private void findPassengersByFullNameExample() {
//        var passengerMapper = new PassengerMapper();
//        var passengerRepository = new PassengerRepository(passengerMapper);
//
//        passengerRepository.findByFullName("Ivan", "Ivanov");
//    }
//
//    private void addBatchStatementExampleExample() {
//        try (Connection connection = getConnection();
//             Statement statement = connection.createStatement()) {
//
//            statement.addBatch("insert into passenger (first_name, last_name, birth_date) values ('Name0', 'Surname0', '1997-12-20')");
//            statement.addBatch("insert into passenger (first_name, last_name, birth_date) values ('Name1', 'Surname1', '1997-12-20')");
//            statement.addBatch("insert into passenger (first_name, last_name, birth_date) values ('Name2', 'Surname2', '1997-12-20')");
//            statement.addBatch("update passenger set first_name = 'IVAN' where id = 1");
//
////            ... - Выполнение батча
//        } catch (SQLException e) {
//            log.error(e);
//        }
//    }
//
//    private void executeBatchStatementExampleExample() {
//        try (Connection connection = getConnection();
//             Statement statement = connection.createStatement()) {
//
//            statement.addBatch("insert into passenger (first_name, last_name, birth_date) values ('Name0', 'Surname0', '1997-12-20')");
//            statement.addBatch("insert into passenger (first_name, last_name, birth_date) values ('Name1', 'Surname1', '1997-12-20')");
//            statement.addBatch("insert into passenger (first_name, last_name, birth_date) values ('Name2', 'Surname2', '1997-12-20')");
//            statement.addBatch("update passenger set first_name = 'IVAN' where id = 1");
//
//            statement.executeBatch();
//        } catch (SQLException e) {
//            log.error(e);
//        }
//    }
//
//    private void addBatchPreparedStatementExampleExample(Collection<Passenger> passengers) {
//        var sql = "insert into passenger (first_name, last_name, birth_date) values (?, ?, ?)";
//
//        try (Connection connection = getConnection();
//             PreparedStatement statement = connection.prepareStatement(sql)) {
//
//            for (Passenger passenger : passengers) {
//                statement.setString(1, passenger.getFirstName());
//                statement.setString(2, passenger.getLastName());
//
//                Date birthDate = Date.valueOf(passenger.getBirthDate());
//                statement.setDate(3, birthDate);
//
//                statement.addBatch();
//            }
//
//            statement.executeBatch();
//        } catch (SQLException e) {
//            log.error(e);
//        }
//    }
//
//    private void executeInTransactionExample() {
//        try (Connection connection = getConnection();
//             Statement statement = connection.createStatement()) {
//
//            connection.setAutoCommit(false);
//
////            Опционально. В данном случае - установка уровня изоляции REPEATABLE READ
//            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
//
//            try {
//                statement.executeUpdate("insert into passenger (first_name, last_name, birth_date) values ('Name2', 'Surname2', '1997-12-20')");
//                statement.executeUpdate("update passenger set first_name = 'IVAN' where id = 1");
//
//                connection.commit();
//            } catch (Exception e) {
//                connection.rollback();
//                log.error("Транзакция была откачена");
//            }
//        } catch (SQLException e) {
//            log.error(e);
//        }
//    }
//
//    private Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(
//                "jdbc:postgresql://localhost:5432/test_db",
//                "postgres",
//                "postgres");
//    }
}