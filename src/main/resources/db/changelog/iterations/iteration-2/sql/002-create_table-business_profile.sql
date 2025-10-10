create type business_profile_category as enum(
    'CORE_PRODUCTS_SERVICES',
    'UNIQUE_VALUE_PROPOSITION',
    'TARGET_CLIENT_PROFILE',
    'CONVERSATION_STARTER',
    'OPPORTUNITY_SUGGESTIONS'
);

create table business_profile (
    business_profile_id serial not null,
    partner_id integer not null,
    category business_profile_category not null,
    description text not null,
    constraint pk_business_profile primary key (business_profile_id),
    constraint fk_business_profile_partner foreign key (partner_id) references partners
);

comment on table business_profile is 'Stores detailed business information about partners to facilitate networking and referrals';
comment on column business_profile.business_profile_id is 'Unique identifier for the business profile record';
comment on column business_profile.partner_id is 'Foreign key referencing the partner who owns this business profile';
comment on column business_profile.category is 'Category of business information being stored';
comment on column business_profile.description is 'Detailed description of the business aspect';
