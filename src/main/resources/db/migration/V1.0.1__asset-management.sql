create table purchases
(
    purchase_date           date           not null,
    purchase_price          numeric(38, 2) not null,
    purchase_quantity       integer        not null,
    purchase_uuid           uuid           not null,
    supplier_uuid           uuid,
    purchase_invoice_number varchar(255)   not null,
    primary key (purchase_uuid),
    constraint unique_invoice_number unique (purchase_invoice_number)
);

alter table if exists purchases
    add constraint purchase_supplier_fk_constraint
    foreign key (supplier_uuid)
    references suppliers;

