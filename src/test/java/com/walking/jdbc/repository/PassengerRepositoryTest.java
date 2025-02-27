package com.walking.jdbc.repository;

import com.walking.jdbc.mapper.PassengerMapper;
import com.walking.jdbc.model.Passenger;
import com.walking.jdbc.repository.PassengerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PassengerRepositoryTest {
    //Константы
    @Mock
    private PassengerMapper mapper;
    @Mock
    private Connection connection;
    @Mock
    private Statement statement;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet rs;
    @Mock
    private DataSource dataSource;
    @InjectMocks
    private PassengerRepository passengerRepository;

    @Test
    void getAll_success() throws SQLException{
        //given
        doReturn(connection).when(dataSource).getConnection();
        doReturn(statement).when(connection).createStatement();
        doReturn(rs).when(statement).executeQuery(any());
        ArrayList<Passenger> passengersList= new ArrayList<>(List.of(mock(Passenger.class)));
        doReturn(passengersList).when(mapper).map(rs);
        //when
        var actual = passengerRepository.getAll();
        //then
        assertSame(passengersList, actual);

        verify(dataSource).getConnection();
        verify(connection).createStatement();
        verify(statement).executeQuery(any());
        verify(mapper).map(rs);
    }

    @Test
    void clearTable() throws SQLException{
        //given
        doReturn(connection).when(dataSource).getConnection();
        doReturn(statement).when(connection).createStatement();
        //when
        passengerRepository.clearTable();
        //then
        verify(dataSource).getConnection();
        verify(connection).createStatement();
        verify(statement).executeUpdate("truncate table passenger_new");
    }

    @Test
    void getById_success() throws SQLException {
        //given
        doReturn(connection).when(dataSource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any(String.class));
        doReturn(rs).when(preparedStatement).executeQuery();
        ArrayList<Passenger> passengersList= new ArrayList<>(List.of(mock(Passenger.class)));
        doReturn(passengersList).when(mapper).map(rs);
        //when
        var actual=passengerRepository.getById(1L);
        //then
        assertSame(passengersList.get(0), actual);

        verify(dataSource).getConnection();
        verify(connection).prepareStatement(any(String.class));
        verify(preparedStatement).executeQuery();
        verify(mapper).map(rs);

    }

    @Test
    void getByMale_success() throws SQLException {
        //given
        doReturn(connection).when(dataSource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any(String.class));
        doReturn(rs).when(preparedStatement).executeQuery();
        ArrayList<Passenger> passengersList= new ArrayList<>(List.of(mock(Passenger.class)));
        doReturn(passengersList).when(mapper).map(rs);
        //when
        var actual=passengerRepository.getByMale(true);
        //then
        assertSame(passengersList, actual);

        verify(dataSource).getConnection();
        verify(connection).prepareStatement(any(String.class));
        verify(preparedStatement).executeQuery();
        verify(mapper).map(rs);
    }

    @Test
    void getByBirthDate_success() throws SQLException {
        //given
        doReturn(connection).when(dataSource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any(String.class));
        doReturn(rs).when(preparedStatement).executeQuery();
        ArrayList<Passenger> passengersList= new ArrayList<>(List.of(mock(Passenger.class)));
        doReturn(passengersList).when(mapper).map(rs);
        //when
        var actual=passengerRepository.getByBirthDate(LocalDate.now());
        //then
        assertSame(passengersList, actual);

        verify(dataSource).getConnection();
        verify(connection).prepareStatement(any(String.class));
        verify(preparedStatement).executeQuery();
        verify(mapper).map(rs);
    }

    @Test
    void create_success() throws SQLException{
        //given
        PassengerRepository pr=mock(PassengerRepository.class);
        ArrayList<Passenger> passengersList=new ArrayList<>(List.of(new Passenger(), new Passenger()));
        passengersList.get(0).setId(1L);
        passengersList.get(0).setLastName("WithId");
        passengersList.get(0).setBirthDate(LocalDate.now());
        passengersList.get(0).setLastPurchase(LocalDateTime.now());
        passengersList.get(1).setLastName("WithoutId");
        passengersList.get(1).setBirthDate(LocalDate.now());
        passengersList.get(1).setLastPurchase(LocalDateTime.now());
        doCallRealMethod().when(pr).create(passengersList);
        //when
        pr.create(passengersList);
        //then
        verify(pr).createWithoutId(List.of(passengersList.get(1)));
        verify(pr).createWithId(List.of(passengersList.get(0)));
    }

    @Test
    void updateById_success() throws SQLException{
        //given
        doReturn(connection).when(dataSource).getConnection();
        doReturn(preparedStatement).when(connection).prepareStatement(any(String.class));
        ArrayList<Passenger> passengersList=new ArrayList<>(List.of(new Passenger()));
        passengersList.get(0).setId(1L);
        passengersList.get(0).setLastName("WithId");
        passengersList.get(0).setBirthDate(LocalDate.now());
        passengersList.get(0).setLastPurchase(LocalDateTime.now());
        //when
        passengerRepository.updateById(passengersList);
        //then
        verify(dataSource).getConnection();
        verify(connection).prepareStatement(any(String.class));
        verify(preparedStatement).addBatch();
        verify(preparedStatement).executeBatch();

    }
}
