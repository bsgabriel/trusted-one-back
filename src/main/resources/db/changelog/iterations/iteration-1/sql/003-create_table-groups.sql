create table groups (
    group_id serial not null,
    name varchar(60) not null,
    description varchar(255),
    user_id integer not null,
    constraint pk_groups primary key (group_id),
    constraint fk_groups_users foreign key (user_id) references users (user_id)
);

comment on column groups.group_id is 'Unique identifier for the group';
comment on column groups.name is 'Name of the organizational group';
comment on column groups.description is 'Optional description of the group purpose or details';
comment on column groups.user_id is 'Foreign key referencing the user who owns this group';
