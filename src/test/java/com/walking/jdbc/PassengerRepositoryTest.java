package com.walking.jdbc;

import com.walking.jdbc.mapper.PassengerMapper;
import com.walking.jdbc.model.Passenger;
import com.walking.jdbc.repository.PassengerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PassengerRepositoryTest {
    //Константы
    private PassengerMapper mapper;
    private Connection connection;
    private Statement statement;
    private PassengerRepository passengerRepository;

    @Test
    void getAll_success() throws SQLException{
        //given
        try(MockedStatic<DriverManager> dm=mockStatic(DriverManager.class)) {
            connection=mock(Connection.class);
            statement=mock(Statement.class);
            dm.when(() -> DriverManager.getConnection(any(),any(),any())).thenReturn(connection);
            doReturn(statement).when(connection).createStatement();
            ResultSet rs = mock(ResultSet.class);
            doReturn(rs).when(statement).executeQuery(any());
            passengerRepository=new PassengerRepository(mapper);
            ArrayList<Passenger> passengersList= new ArrayList<>(List.of(mock(Passenger.class)));
            doReturn(passengersList).when(mapper).map(rs);
            //when
            var actual = passengerRepository.getAll();
            //then
            assertSame(passengersList, actual);

            verify(connection,times(4)).createStatement();
            verify(statement,times(2)).executeQuery(any());
            verify(mapper).map(rs);
        }
    }

    @Test
    void clearTable() throws SQLException{
        //given
        try (MockedStatic<DriverManager> dm=mockStatic(DriverManager.class)){
            connection=mock(Connection.class);
            statement=mock(Statement.class);
            dm.when(()->DriverManager.getConnection(any(),any(),any())).thenReturn(connection);
            doReturn(statement).when(connection).createStatement();
            ResultSet rs=mock(ResultSet.class);
            doReturn(rs).when(statement).executeQuery(any());
            passengerRepository=new PassengerRepository(mapper);
            //when
            passengerRepository.clearTable();
            //then
            verify(connection,times(4)).createStatement();
            verify(statement).executeUpdate("truncate table passenger_new");
        }
    }

    @BeforeEach
    void setUp(){
        mapper =Mockito.mock(PassengerMapper.class);
    }

    @AfterEach
    void tearDown(){

    }
}
