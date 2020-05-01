create database data_collection;

\connect data_collection;

create schema dc;

begin;

create table dc.events(
    id serial primary key,
    name varchar(255) not null
);

create table dc.users(
    id serial primary key,
    login varchar(255) not null unique,
    password varchar(255) not null,
    authority varchar(255) not null
);

create table dc.sessions
(
    id serial primary key,
    user_id integer not null,
    x_coordinate integer check ( x_coordinate >= 0 ),
    y_coordinate integer check ( y_coordinate >= 0 ),
    event_id integer not null,
    time timestamp not null,

    foreign key (user_id) references dc.users(id),
    foreign key (event_id) references dc.events(id)
);

create unique index on dc.users(login);
create index on dc.sessions(user_id);
create index on dc.sessions(event_id);

commit;
