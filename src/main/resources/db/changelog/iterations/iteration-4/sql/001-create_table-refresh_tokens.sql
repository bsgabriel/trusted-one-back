create table refresh_tokens (
    refresh_token_id bigserial primary key,
    token varchar(36) not null unique,
    user_id bigint not null,
    expiry_date timestamp not null,
    constraint fk_refresh_token_user foreign key (user_id) references users(user_id) on delete cascade
);

create index idx_refresh_tokens_token on refresh_tokens(token);
create index idx_refresh_tokens_user_id on refresh_tokens(user_id);

comment on table refresh_tokens is 'Stores refresh tokens used for renewing user authentication sessions.';
comment on column refresh_tokens.refresh_token_id is 'Primary key identifier for the refresh token record.';
comment on column refresh_tokens.token is 'Unique refresh token value issued to the user for authentication renewal.';
comment on column refresh_tokens.user_id is 'Identifier of the user to whom this refresh token belongs.';
comment on column refresh_tokens.expiry_date is 'Date and time when the refresh token expires and becomes invalid.';