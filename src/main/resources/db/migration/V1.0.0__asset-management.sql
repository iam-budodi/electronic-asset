create table addresses
(
    address_uuid  uuid        not null,
    city          varchar(32) not null,
    country       varchar(32) not null,
    district_name varchar(32) not null,
    postal_code   varchar(5)  not null,
    street_name   varchar(32) not null,
    primary key (address_uuid)
);

create table colleges
(
    college_uuid uuid        not null,
    college_code varchar(255),
    college_name varchar(64) not null,
    primary key (college_uuid),
    constraint unique_college_name_code unique (college_name, college_code)
);

create table departments
(
    department_uuid uuid        not null,
    department_code varchar(10),
    department_name varchar(64) not null,
    description     varchar(64),
    college_uuid    uuid        not null,
    primary key (department_uuid),
    constraint unique_department_name unique (department_name)
);

create table employees
(
    employee_uuid   uuid         not null,
    registered_at   timestamp(6),
    registered_by   varchar(64),
    updated_at      timestamp(6),
    updated_by      varchar(255),
    email_address   varchar(255) not null,
    first_name      varchar(64)  not null,
    gender          varchar(255) check (gender in ('M', 'F')),
    last_name       varchar(64)  not null,
    middle_name     varchar(64),
    phone_number    varchar(18)  not null,
    birthdate       date,
    hire_date       date         not null,
    work_id         varchar(255),
    department_uuid uuid         not null,
    primary key (employee_uuid),
    constraint unique_email_phone unique (email_address, phone_number),
    constraint unique_work_id unique (work_id)
);

create table employment_status
(
    employee_uuid     uuid         not null,
    employment_status varchar(255) not null check (employment_status in
                                                   ('CONTRACT', 'PERMANENT', 'FULL_TIME', 'VOLUNTEER', 'PROBATION',
                                                    'INTERN', 'PART_TIME', 'TERMINATED', 'FIRED', 'LAID_OFF')),
    primary key (employee_uuid, employment_status)
);

create table suppliers
(
    supplier_uuid   uuid         not null,
    registered_at   timestamp(6),
    registered_by   varchar(64),
    updated_at      timestamp(6),
    updated_by      varchar(255),
    company_email   varchar(255) not null,
    company_name    varchar(64)  not null,
    company_phone   varchar(18)  not null,
    description     varchar(500) not null,
    supplier_type   varchar(255) not null check (supplier_type in ('MANUFACTURER', 'WHOLESALER', 'RETAILER')),
    company_website varchar(255),
    primary key (supplier_uuid),
    constraint uniqueEmailAndPhone unique (company_email, company_phone)
);

alter table if exists colleges
    add constraint college_address_fk_constraint
    foreign key (college_uuid)
    references addresses;

alter table if exists departments
    add constraint college_department_fk_constraint
    foreign key (college_uuid)
    references colleges
    on
delete
cascade;

alter table if exists employees
    add constraint employee_department_fk_constraint
    foreign key (department_uuid)
    references departments
    on
delete
cascade;

alter table if exists employees
    add constraint employee_address_fk_constraint
    foreign key (employee_uuid)
    references addresses;

alter table if exists employment_status
    add constraint FK4k9qa4guunapb61iusxwplwj1
    foreign key (employee_uuid)
    references employees;

alter table if exists suppliers
    add constraint supplier_address_fk_constraint
    foreign key (supplier_uuid)
    references addresses;
insert into addresses (city, country, district_name, postal_code, street_name, address_uuid)
VALUES ('Dar es Salaam', 'Tanzania', 'kinondoni', '09191', 'sayansi', '64365d7c-354d-11ee-be56-0242ac120002');
insert into addresses (city, country, district_name, postal_code, street_name, address_uuid)
VALUES ('Dar es Salaam', 'Tanzania', 'ubungo', '15114', 'chuo kikuu', '849ea0ce-354d-11ee-be56-0242ac120002');
insert into colleges (college_uuid, college_name, college_code)
VALUES ('64365d7c-354d-11ee-be56-0242ac120002', 'College of Information and Communication Technology', 'CoICT');
insert into colleges (college_uuid, college_name, college_code)
VALUES ('849ea0ce-354d-11ee-be56-0242ac120002', 'College of Agricultural Sciences and Fisheries', 'CoAF');
insert into departments (department_uuid, department_code, description, department_name, college_uuid)
VALUES ('849ea0ce-354d-11ee-be56-0242ac120022', 'CSE', 'Computer science and Engineering',
        'Computer Science and Engineering', '64365d7c-354d-11ee-be56-0242ac120002');
insert into departments (department_uuid, department_code, description, department_name, college_uuid)
VALUES ('849ea0ce-354d-11ee-be56-0242ac120024', 'TE', 'Telecommunication engineering', 'Telecommunication Engineering',
        '64365d7c-354d-11ee-be56-0242ac120002');
insert into addresses (city, country, district_name, postal_code, street_name, address_uuid)
VALUES ('Dar es Salaam', 'Tanzania', 'Ubungo', '15114', 'mavulunza', '849ea0de-354d-11ee-be56-0242ac120024');
insert into addresses (city, country, district_name, postal_code, street_name, address_uuid)
VALUES ('Dar es Salaam', 'Tanzania', 'Ubungo', '15004', 'corner', '849ea0ee-354d-11ee-be56-0242ac120024');
insert into employees (employee_uuid, registered_at, registered_by, email_address, first_name, gender, last_name,
                       middle_name, phone_number, birthdate, hire_date, work_id, department_uuid)
VALUES ('849ea0de-354d-11ee-be56-0242ac120024', '2023-03-30', 'Habiba', 'hellen@gmail.com', 'hellen', 'F', 'John',
        'Malaba', '255716656596', '1990-01-02', '2022-07-28', '19-04-20022', '849ea0ce-354d-11ee-be56-0242ac120022');
insert into employees (employee_uuid, registered_at, registered_by, email_address, first_name, gender, last_name,
                       middle_name, phone_number, birthdate, hire_date, work_id, department_uuid)
VALUES ('849ea0ee-354d-11ee-be56-0242ac120024', '2023-03-30', 'Habiba', 'lu3@gmail.com', 'Michael', 'M', 'Mbaga',
        'Joseph', '255713333396', '1970-09-09', '2017-12-01', '19-04-02349', '849ea0ce-354d-11ee-be56-0242ac120022');
insert into addresses (city, country, district_name, postal_code, street_name, address_uuid)
VALUES ('Dar es Salaam', 'Tanzania', 'Kinondoni', '15004', 'Jangid Plaza 3rd floor, room 310',
        '71f2f7cd-85c0-4bfd-ab3a-9326f706ceb2');
insert into suppliers (supplier_uuid, registered_at, registered_by, description, company_email, company_name,
                       company_phone, supplier_type, company_website)
VALUES ('71f2f7cd-85c0-4bfd-ab3a-9326f706ceb2', '2020-09-12', 'Habiba',
        'Supplies Office enablers like routers and switch', 'support@networkassociate.co.tz', 'Network Associate',
        '+255222111777', 'WHOLESALER', 'https://www.networkassociate.co.tz');
