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
    event_id integer,
    event_time timestamp not null,
    x_resolution integer not null check ( x_resolution >= 0),
    y_resolution integer not null check ( y_resolution >= 0),

    foreign key (user_id) references dc.users(id),
    foreign key (event_id) references dc.events(id)
);

create unique index on dc.users(login);
create index on dc.sessions(user_id);
create index on dc.sessions(event_id);

insert into dc.events
VALUES (1, 'MOVE'),
       (2, 'LEFT_DOWN'),
       (3, 'LEFT_UP'),
       (4, 'RIGHT_DOWN'),
       (5, 'RIGHT_UP'),
       (6, 'SCROLL_DOWN'),
       (7, 'SCROLL_UP'),
       (8, 'SCROLL_PUSH'),
       (9, 'SCROLL_PULL');

commit;
