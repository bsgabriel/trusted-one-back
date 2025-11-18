create type referral_status as enum (
    'PENDING',
    'ACCEPTED',
    'DECLINED'
);

create table referrals (
	referral_id serial not null,
	partner_id integer not null,
	expertise_id integer not null,
	user_id integer not null,
	status referral_status not null,
	referred_to varchar(100) not null,
	created_at timestamp not null,
	updated_at timestamp not null,
	constraint pk_referral primary key (referral_id),
	constraint fk_referral_partner foreign key (partner_id) references partners(partner_id),
	constraint fk_referral_expertise foreign key (expertise_id) references expertises(expertise_id),
	constraint fk_referral_user foreign key (user_id) references users(user_id)
);

comment on table referrals is 'Stores professional referrals and their current status.';
comment on column referrals.referral_id is 'Unique indentifier for the referral.';
comment on column referrals.partner_id is 'Reference to the partner being referred.';
comment on column referrals.expertise_id is 'Reference to the professional expertise area that the partner is being referred for.';
comment on column referrals.user_id is 'Reference to the user who made the referral.';
comment on column referrals.status is 'Current status of the referral.';
comment on column referrals.referred_to is 'Name of the person who received the referral';
comment on column referrals.created_at is 'Timestamp when the referral was created.';
comment on column referrals.updated_at is 'Timestamp when the referral was last updated.';
