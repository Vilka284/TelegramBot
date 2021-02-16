drop database if exists queue;
create database queue;
\c queue

create table participant
(
    id        serial primary key,
    tag       text unique not null,
    chat_id   int unique  not null,
    operation text        not null -- last made operation
);

create table subject
(
    id      serial primary key,
    name    text not null,
    teacher text not null
);

create table schedule
(
    id         serial primary key,
    subject_id int  not null,
    day        text not null,
    hour       time not null,
    foreign key (subject_id) references subject (id)
);

create table queue
(
    id             serial primary key,
    participant_id int  not null,
    schedule_id    int  not null,
    status         text not null,
    enter_date     timestamp,
    foreign key (participant_id) references participant (id),
    foreign key (schedule_id) references schedule (id)
);