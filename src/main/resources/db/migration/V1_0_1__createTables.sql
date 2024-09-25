create table if not exists contact
(
    id             bigint generated always as identity primary key,
    ext_contact_id bigint       not null,
    full_name      varchar(300) not null default 'АНОНИМ',
    city           varchar(100) not null default 'Город не определен',
    phone          varchar(100) not null default 'Телефон не определен',
    comment        text
);

create table if not exists deal
(
    id          bigint generated always as identity primary key,
    ext_deal_id bigint       not null,
    title       varchar(100) not null,
    create_date timestamp    not null default now(),
    add_info    text,
    contact_id  bigint
);

create table if not exists receipt
(
    deal_id        bigint primary key,
    ext_receipt_id varchar(500) not null,
    create_date    timestamp    not null default now(),
    link           text         not null,
    value          numeric      not null,
    currency       varchar(100) not null,
    add_info       text
);

create table if not exists event
(
    id               bigint generated always as identity primary key,
    ext_event_id     bigint              not null,
    name             varchar(100) unique not null,
    type             varchar(100)        not null,
    time             varchar(100)        not null,
    adult_price      int                 not null CHECK (adult_price >= 0)      default 0,
    kid_price        int                 not null CHECK (kid_price >= 0)        default 0,
    child_age        int                 not null CHECK (child_age >= 0)        default 0,
    capacity         int                 not null CHECK (capacity >= 0)         default 0,
    adult_capacity   int                 not null CHECK (adult_capacity >= 0)   default 0,
    kid_capacity     int                 not null CHECK (kid_capacity >= 0)     default 0,
    slots_left       int                 not null CHECK (slots_left >= 0)       default 0,
    adult_slots_left int                 not null CHECK (adult_slots_left >= 0) default 0,
    kid_slots_left   int                 not null CHECK (kid_slots_left >= 0)   default 0,
    gathering_type   varchar(100)        not null,
    adult_required   bool                not null,
    city             varchar(100)        not null
);