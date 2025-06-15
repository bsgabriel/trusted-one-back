create table professional_professions (
    professional_profession_id serial not null,
    professional_id integer not null,
    profession_id integer not null,
    available_for_referrals boolean not null default false,
    constraint pk_professional_professions primary key (professional_profession_id),
    constraint fk_professional_professions_professionals foreign key (professional_id) references professionals (professional_id),
    constraint fk_professional_professions_professions foreign key (profession_id) references professions (profession_id)
);

comment on column professional_professions.professional_profession_id is 'Unique identifier for the relationship between a professional and a profession';
comment on column professional_professions.professional_id is 'Foreign key referencing the professional';
comment on column professional_professions.profession_id is 'Foreign key referencing the profession';
comment on column professional_professions.available_for_referrals is 'Indicates whether the professional accepts referrals/indications for this specific profession';
