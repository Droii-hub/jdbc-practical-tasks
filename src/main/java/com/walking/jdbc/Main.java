package com.walking.jdbc;

import com.walking.jdbc.mapper.TicketMapper;
import com.walking.jdbc.model.Passenger;
import com.walking.jdbc.model.Ticket;
import com.walking.jdbc.repository.TicketRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
        HikariConfig configuration = new HikariConfig("hikari.properties");
        try (HikariDataSource dataSource = new HikariDataSource(configuration)) {
            FluentConfiguration flywayConfiguration= Flyway.configure()
                    .dataSource(dataSource).baselineOnMigrate(true);
            Flyway flyway=flywayConfiguration.load();
            flyway.migrate();
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
                log.info(ticket.getId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    private final static Logger log = LogManager.getLogger(Main.class);

}