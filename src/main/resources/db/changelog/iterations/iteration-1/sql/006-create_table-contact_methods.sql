create type contact_type as enum('EMAIL', 'PHONE', 'LINKEDIN', 'OTHER');
create table contact_methods (
    contact_method_id serial not null,
    partner_id integer not null,
    type contact_type not null,
    info varchar(255) not null,
    constraint pk_contact_methods primary key (contact_method_id),
    constraint fk_contact_methods_partners foreign key (partner_id) references partners (partner_id)
);

comment on column contact_methods.contact_method_id is 'Unique identifier for the contact method';
comment on column contact_methods.partner_id is 'Foreign key referencing the partner this contact belongs to';
comment on column contact_methods.type is 'Type of contact method';
comment on column contact_methods.info is 'Contact information (email address, phone number, LinkedIn URL, etc.)';