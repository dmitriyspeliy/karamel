create table if not exists sms_info
(
    id             bigint generated always as identity primary key,
    ext_sms_id     varchar(100) unique not null,
    phone_receiver varchar(20)         not null,
    sms_text       varchar(1000)       not null,
    status         varchar(20)         not null,
    status_code    varchar(20)         not null,
    status_text    varchar(1000),
    send_time      timestamp           not null,
    status_time    timestamp,
    cost           numeric             not null,
    balance        numeric,
    deal_ext_id    varchar(100)        not null
);

create index if not exists hash_contact_index on sms_info (ext_sms_id);
