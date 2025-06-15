create table companies (
    company_id SERIAL,
    name varchar(255) not null,
    image varchar(255),
    user_id integer not null,
    constraint pk_companies primary key (company_id),
    constraint fk_companies_users foreign key (user_id) references users (user_id)
);

comment on column companies.company_id is 'Unique identifier for the company';
comment on column companies.name is 'Company name';
comment on column companies.image is 'URL or path to company logo/image';
comment on column companies.user_id is 'Foreign key referencing the user who owns this company';
