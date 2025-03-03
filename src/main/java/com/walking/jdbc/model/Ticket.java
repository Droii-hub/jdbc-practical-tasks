package com.walking.jdbc.model;

import java.time.LocalDateTime;

public class Ticket {
    private Long id;
    private String departure_airport;
    private String arrival_airport;
    private LocalDateTime departure_date;
    private LocalDateTime arrival_date;
    private LocalDateTime purchase_date;
    private Long passenger_id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeparture_airport() {
        return departure_airport;
    }

    public void setDeparture_airport(String departure_airport) {
        this.departure_airport = departure_airport;
    }

    public String getArrival_airport() {
        return arrival_airport;
    }

    public void setArrival_airport(String arrival_airport) {
        this.arrival_airport = arrival_airport;
    }

    public LocalDateTime getDeparture_date() {
        return departure_date;
    }

    public void setDeparture_date(LocalDateTime departure_date) {
        this.departure_date = departure_date;
    }

    public LocalDateTime getArrival_date() {
        return arrival_date;
    }

    public void setArrival_date(LocalDateTime arrival_date) {
        this.arrival_date = arrival_date;
    }

    public LocalDateTime getPurchase_date() {
        return purchase_date;
    }

    public void setPurchase_date(LocalDateTime purchase_date) {
        this.purchase_date = purchase_date;
    }

    public Long getPassenger_id() {
        return passenger_id;
    }

    public void setPassenger_id(Long passenger_id) {
        this.passenger_id = passenger_id;
    }
}
