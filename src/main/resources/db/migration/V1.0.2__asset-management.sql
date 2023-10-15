create table qr_codes
(
    qr_uuid uuid  not null,
    qr_label     bytea not null,
    primary key (qr_uuid),
    constraint unique_qr_label unique (qr_label)
);

create table categories
(
    category_uuid uuid        not null,
    category_name varchar(64) not null,
    description   varchar(1000),
    primary key (category_uuid)
);

create table assets
(
    computer_memory     integer      not null,
    computer_storage    integer      not null,
    created_at          date,
    display_size        float(53)    not null,
    graphics_card       integer,
    updated_at          date,
    asset_uuid          uuid         not null,
    category            uuid,
    qr_string           uuid unique,
    purchase            uuid         not null,
    asset_discriminator varchar(31)  not null,
    brand_name          varchar(64)  not null,
    created_by          varchar(64),
    model_name          varchar(64)  not null,
    computer_processor  varchar(255) not null,
    manufacturer        varchar(255) not null,
    model_number        varchar(255) not null,
    operating_system    varchar(255),
    serial_number       varchar(255) not null,
    updated_by          varchar(255),
    primary key (asset_uuid),
    constraint unique_serial_no unique (serial_number)
);

alter table if exists assets
    add constraint asset_category_constraint
    foreign key (category)
    references categories;

alter table if exists assets
    add constraint asset_label_constraint
    foreign key (qr_string)
    references qr_codes;

alter table if exists assets
    add constraint asset_purchase_constraint
    foreign key (purchase)
    references purchases;


