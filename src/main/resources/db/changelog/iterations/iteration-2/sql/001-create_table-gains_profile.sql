create type gains_category as enum('GOAL', 'ACCOMPLISHMENT', 'INTEREST', 'NETWORK', 'SKILL');

create table gains_profile (
   gains_profile_id serial not null,
   partner_id integer not null,
   category gains_category not null,
   info varchar(255) not null,
   constraint pk_gains_profile primary key (gains_profile_id),
   constraint fk_gains_profile_partner foreign key (partner_id) references partners
);

comment on column gains_profile.gains_profile_id is 'Unique identifier for the GAINS profile record';
comment on column gains_profile.partner_id is 'Foreign key referencing the partner owner of this GAINS profile record';
comment on column gains_profile.category is 'Category of the GAINS profile information (GOAL, ACCOMPLISHMENT, INTEREST, NETWORK, SKILL)';
comment on column gains_profile.info is 'Description or detail of the GAINS item, e.g., a specific goal, interest, accomplishment, network, or skill';
