create table if not exists passenger_new (
    id              bigserial       primary key,
    first_name      varchar(100)    not null,
    last_name       varchar(100)    not null,
    male            boolean         not null,
    birth_date      date            not null,
    last_purchase   timestamp
);