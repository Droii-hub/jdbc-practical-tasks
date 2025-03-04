create table if not exists ticket_new (
    id                  bigserial       primary key,
    departure_airport   varchar(100)    not null,
    arrival_airport     varchar(100)    not null,
    departure_date      timestamp       not null,
    arrival_date        timestamp       not null,
    purchase_date       timestamp       not null,
    passenger_id        bigint          not null references passenger_new(id),

    constraint ticket_departure_airport_arrival_airport_departure_date_arr_key unique
        (departure_airport, arrival_airport, departure_date, arrival_date, passenger_id)
);