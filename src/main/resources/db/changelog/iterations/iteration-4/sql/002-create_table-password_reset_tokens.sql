create table password_reset_tokens (
    token_id serial,
    user_id integer not null,
    token varchar(36) unique not null,
    expires_at timestamp not null,
    created_at timestamp default current_timestamp,
    used_at timestamp,
    constraint pk_password_reset_tokens primary key (token_id),
    constraint fk_user foreign key (user_id) references users(user_id) on delete cascade
);

create index idx_token ON password_reset_tokens(token);

comment on table password_reset_tokens is 'Stores temporary tokens for password reset requests. Tokens expire after 60 minutes and are invalidated after use.';
comment on column password_reset_tokens.token_id is 'Primary key identifier';
comment on column password_reset_tokens.user_id is 'Reference to the user who requested the password reset';
comment on column password_reset_tokens.token is 'UUID token sent to user email for password reset verification';
comment on column password_reset_tokens.expires_at is 'Timestamp when the token expires (60 minutes from creation)';
comment on column password_reset_tokens.created_at is 'Timestamp when the token was generated';
comment on column password_reset_tokens.used_at is 'Timestamp when the token was used';
comment on index idx_token is 'Index for fast token lookup during password reset validation';
