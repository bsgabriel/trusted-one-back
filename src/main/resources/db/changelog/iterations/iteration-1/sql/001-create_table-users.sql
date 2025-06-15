create table users (
    user_id serial,
    name varchar(255) not null,
    email varchar(255) not null unique,
    password varchar(255) not null,
    constraint pk_users primary key (user_id)
);

comment on column users.user_id is 'Unique identifier for the user';
comment on column users.name is 'Full name of the user';
comment on column users.email is 'Unique email address for user authentication';
comment on column users.password is 'Password for user authentication';
