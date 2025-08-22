create table expertises (
    expertise_id serial not null,
    name varchar(60) not null,
    parent_expertise_id integer,
    user_id integer not null,
    constraint pk_expertises primary key (expertise_id),
    constraint fk_parent_expertise foreign key (parent_expertise_id) references expertises (expertise_id),
    constraint fk_expertiseals_users foreign key (user_id) references users (user_id)
);

comment on column expertises.expertise_id is 'Unique identifier for the expertise';
comment on column expertises.name is 'Name of the expertise';
comment on column expertises.parent_expertise_id is 'Self-referencing foreign key for hierarchical expertise structure (e.g., Technology > Development > Frontend)';
comment on column expertises.user_id is 'Foreign key referencing the user who owns this expertise';
