create table if not exists notification
(
    id              bigint generated always as identity primary key,
    email           varchar(100) not null,
    phone           varchar(100) not null,
    ext_sms_id      varchar(100),
    text_sms        text,
    text_email      text,
    text_error      text,
    status          varchar(10),
    send_time_sms   timestamp,
    send_time_email timestamp
);