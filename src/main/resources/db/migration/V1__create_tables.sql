create table employees(
    id bigserial primary key,
    created_at timestamp not null,
    deleted boolean not null,
    first_name varchar not null,
    last_name varchar not null,
    username varchar not null,
    active boolean not null
);