create table partner_expertises (
    partner_expertise_id serial not null,
    partner_id integer not null,
    expertise_id integer not null,
    available_for_referrals boolean not null default false,
    constraint pk_partner_expertises primary key (partner_expertise_id),
    constraint fk_partner_expertises_partners foreign key (partner_id) references partners (partner_id),
    constraint fk_partner_expertises_expertises foreign key (expertise_id) references expertises (expertise_id)
);

comment on column partner_expertises.partner_expertise_id is 'Unique identifier for the relationship between a partner and a expertise';
comment on column partner_expertises.partner_id is 'Foreign key referencing the partner';
comment on column partner_expertises.expertise_id is 'Foreign key referencing the expertise';
comment on column partner_expertises.available_for_referrals is 'Indicates whether the partner accepts referrals/indications for this specific expertise';
