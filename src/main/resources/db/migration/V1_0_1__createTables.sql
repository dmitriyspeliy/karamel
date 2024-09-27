create table if not exists contact
(
    deal_id        bigint primary key,
    ext_contact_id varchar(100) not null,
    full_name      varchar(300) not null,
    city           varchar(100) not null,
    phone          varchar(100) not null,
    add_info       text default 'No information'
);


create table if not exists deal
(
    id          bigint generated always as identity primary key,
    ext_deal_id varchar(100) not null,
    title       varchar(100) not null,
    create_date timestamp default now(),
    paid        bool      default false,
    add_info    text      default 'No information',
    contact_id  bigint    default 0,
    invoice_id  bigint    default 0,
    event_id    bigint    default 0
);

create table if not exists event
(
    id               bigint generated always as identity primary key,
    ext_event_id     varchar(100)        not null,
    name             varchar(100) unique not null,
    type             varchar(100)        not null,
    time             timestamp           not null,
    adult_price      numeric             not null CHECK (adult_price >= 0),
    kid_price        numeric             not null CHECK (kid_price >= 0),
    child_age        varchar(10)         not null,
    capacity         bigint              not null CHECK (capacity >= 0)         default 0,
    adult_capacity   bigint              not null CHECK (adult_capacity >= 0)   default 0,
    kid_capacity     bigint              not null CHECK (kid_capacity >= 0)     default 0,
    slots_left       bigint              not null CHECK (slots_left >= 0)       default 0,
    adult_slots_left bigint              not null CHECK (adult_slots_left >= 0) default 0,
    kid_slots_left   bigint              not null CHECK (kid_slots_left >= 0)   default 0,
    gathering_type   varchar(100)        not null,
    adult_required   bool                not null,
    city             varchar(100)        not null
);

create table if not exists invoice
(
    deal_id        bigint primary key,
    ext_invoice_id varchar(100) not null,
    signature1     varchar(100) not null,
    signature2     varchar      not null
);

create table if not exists invoice_info
(
    deal_id        bigint primary key,
    status            varchar(100) not null,
    hash              varchar(100) not null,
    success_status_id bigint default 0,
    failure_status_id bigint default 0,
    invoice_link      varchar(500) not null,
    create_at         timestamp    not null,
    bitrix_url        varchar(500) not null
);