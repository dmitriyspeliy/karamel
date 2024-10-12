create table if not exists contact
(
    id             bigint generated always as identity primary key,
    ext_contact_id varchar(100) not null,
    full_name      varchar(300) not null,
    email          varchar(300) default 'No email',
    city           varchar(100) not null,
    phone          varchar(100) not null,
    add_info       text         default 'No information'
);

create index hash_contact_index on contact (ext_contact_id);

create table if not exists deal
(
    id          bigint generated always as identity primary key,
    ext_deal_id varchar(100) not null,
    title       varchar(100) not null,
    type        varchar(100) not null,
    create_date timestamp   default now(),
    paid        bool        default false,
    add_info    text        default 'No information',
    contact_id  bigint      default 0,
    event_id    bigint      default 0,
    kid_count   int         default 0,
    kid_price   numeric     default 0,
    kid_age     varchar(10) default '0',
    adult_count int         default 0,
    adult_price numeric     default 0
);

create index hash_deal_index on deal (ext_deal_id);

create table if not exists event
(
    id             bigint generated always as identity primary key,
    ext_event_id   varchar(100) not null,
    name           varchar(100) not null,
    type           varchar(100) not null,
    time           timestamp    not null,
    adult_price    numeric      not null CHECK (adult_price >= 0),
    kid_price      numeric      not null CHECK (kid_price >= 0),
    child_age      varchar(10)  not null,
    capacity       bigint       not null CHECK (capacity >= 0)       default 0,
    adult_capacity bigint       not null CHECK (adult_capacity >= 0) default 0,
    kid_capacity   bigint       not null CHECK (kid_capacity >= 0)   default 0,
    gathering_type varchar(100) not null,
    adult_required bool         not null,
    city           varchar(100) not null
);

create index hash_event_index on event (ext_event_id);
create unique index hash_event_name_city_index on event (name, city);

create table if not exists invoice
(
    deal_id              bigint primary key,
    ext_invoice_id       varchar(100) not null,
    body                 text         not null,
    create_at            timestamp    not null,
    status               varchar(20)  not null,
    invoice_link         varchar      not null,
    total_sum            numeric      not null,
    state                text,
    count_of_send_ticket int default 0
);

create index hash_invoice_index on invoice (ext_invoice_id);
