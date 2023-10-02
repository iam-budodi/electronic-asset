create table Purchases
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


alter table if exists Purchases
    add constraint purchase_supplier_fk_constraint
    foreign key (supplier_uuid)
    references suppliers;


insert into purchases (purchase_uuid, purchase_invoice_number, purchase_date, purchase_price, purchase_quantity,
                       supplier_uuid)
VALUES ('05bf69a2-5981-11ee-8c99-0242ac120002', 'RPC4-123456', '2022-09-12', 35000000, 15,
        '71f2f7cd-85c0-4bfd-ab3a-9326f706ceb2');
insert into purchases (purchase_uuid, purchase_invoice_number, purchase_date, purchase_price, purchase_quantity,
                       supplier_uuid)
VALUES ('05bf7154-5981-11ee-8c99-0242ac120002', 'RPC4-987654', '2022-09-12', 55000000, 16,
        '71f2f7cd-85c0-4bfd-ab3a-9326f706ceb2');
