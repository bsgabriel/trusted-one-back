create table partners (
    partner_id SERIAL,
    name varchar(255) not null,
    company_id integer not null,
    group_id integer,
    user_id integer not null,
    constraint pk_partners primary key (partner_id),
    constraint fk_partners_companies foreign key (company_id) references companies (company_id),
    constraint fk_partners_groups foreign key (group_id) references groups (group_id),
    constraint fk_partners_users foreign key (user_id) references users (user_id)
);

comment on column partners.partner_id is 'Unique identifier for the partner';
comment on column partners.name is 'Full name of the partner';
comment on column partners.company_id is 'Foreign key referencing the company where the partner works';
comment on column partners.group_id is 'Foreign key referencing the organizational group the partner belongs to';
comment on column partners.user_id is 'Foreign key referencing the user who owns this partner record';
