package com.walking.jdbc.mapper;

import com.walking.jdbc.model.Ticket;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketMapper {
    public List<Ticket> map(ResultSet rs) throws SQLException{
        var tickets=new ArrayList<Ticket>();
        while (rs.next()){
            tickets.add(mapRow(rs));
        }
        return tickets;
    }

    private Ticket mapRow(ResultSet rs) throws SQLException{
        var ticket=new Ticket();

        ticket.setId(rs.getLong("id"));
        ticket.setDeparture_airport(rs.getString("departure_airport"));
        ticket.setArrival_airport(rs.getString("arrival_airport"));
        ticket.setDeparture_date(rs.getTimestamp("departure_date").toLocalDateTime());
        ticket.setArrival_date(rs.getTimestamp("arrival_date").toLocalDateTime());
        ticket.setPurchase_date(rs.getTimestamp("purchase_date").toLocalDateTime());
        ticket.setPassenger_id(rs.getLong("passenger_id"));
        return ticket;
    }
}
