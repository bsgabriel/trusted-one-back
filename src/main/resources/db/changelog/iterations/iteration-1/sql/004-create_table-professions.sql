create table professions (
    profession_id serial not null,
    name varchar(60) not null,
    parent_profession_id integer,
    user_id integer not null,
    constraint pk_professions primary key (profession_id),
    constraint fk_parent_profession foreign key (parent_profession_id) references professions (profession_id),
    constraint fk_professionals_users foreign key (user_id) references users (user_id)
);

comment on column professions.profession_id is 'Unique identifier for the profession';
comment on column professions.name is 'Name of the profession or area of expertise';
comment on column professions.parent_profession_id is 'Self-referencing foreign key for hierarchical profession structure (e.g., Technology > Development > Frontend)';
comment on column professions.user_id is 'Foreign key referencing the user who owns this profession';
