create table professionals (
    professional_id SERIAL,
    name varchar(255) not null,
    company_id integer not null,
    group_id integer,
    user_id integer not null,
    constraint pk_professionals primary key (professional_id),
    constraint fk_professionals_companies foreign key (company_id) references companies (company_id),
    constraint fk_professionals_groups foreign key (group_id) references groups (group_id),
    constraint fk_professionals_users foreign key (user_id) references users (user_id)
);

comment on column professionals.professional_id is 'Unique identifier for the professional';
comment on column professionals.name is 'Full name of the professional';
comment on column professionals.company_id is 'Foreign key referencing the company where the professional works';
comment on column professionals.group_id is 'Optional foreign key referencing the organizational group the professional belongs to';
comment on column professionals.user_id is 'Foreign key referencing the user who owns this professional record';
