-- ADDRESSES
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name)
VALUES (490, 'Dar es Salaam', 'Tanzania', 'kinondoni', '09191', 'sayansi', 'kijitonyama');

-- DEPARTMENT TABLE - add 4 more dept
insert into departments (id, department_code, description, department_name, location_id)
VALUES (100, 'CSE', 'Computer science and Engineering', 'CSE', 490);
insert into departments (id, department_code, description, department_name, location_id)
VALUES (101, 'TE', 'Telecommunication engineering', 'Telecom', 490);

-- EMPLOYEE TABLE - add at least 19 more employee
insert into employees (id, registered_at, registered_by, email_address, first_name, gender, last_name, middle_name, phone_number, birthdate, hire_date, work_id, department_fk)
VALUES (200,'2023-03-30', 'Habiba','hellen@gmail.com','hellen', 'F', 'John','Malaba','255716656596','1990-01-02','2022-07-28', '19-04-20022', 100);
insert into employees (id, registered_at, registered_by, email_address, first_name, gender, last_name, middle_name, phone_number, birthdate, hire_date, work_id, department_fk)
VALUES (201,'2023-03-30', 'Habiba',  'lu3@gmail.com', 'Michael', 'M', 'Mbaga', 'Joseph','255713333396','1970-09-09','2017-12-01','19-04-02349', 100);
insert into employees (id, registered_at, registered_by, email_address, first_name, gender, last_name, middle_name, phone_number, birthdate, hire_date, work_id, department_fk)
VALUES (202, '2023-02-20', 'Lulu', 'burhan@gmail.com', 'Burhan', 'M', 'Kihambwe', 'Salum', '255673320098', '1991-06-12', '2017-12-01', '19-04-00341', 100);
insert into employees (id, registered_at, registered_by, email_address, first_name, gender, last_name, middle_name, phone_number, birthdate, hire_date, work_id, department_fk)
VALUES (203, '2023-02-20', 'Lulu', 'amina12@gmail.com', 'Amina', 'F', 'Mtei', 'Harun', '255621344355', '1972-11-09', '2010-10-10', '19-04-00098', 100);
insert into employees (id, registered_at, registered_by, email_address, first_name, gender, last_name, middle_name, phone_number, birthdate, hire_date, work_id, department_fk)
VALUES (204, '2023-02-20', 'Lulu', 'zahoro@gmail.com', 'zahoro', 'M', 'Kuga', 'Leonard', '255768900123', '1992-07-07', '2016-11-11', '19-04-00041', 100);
insert into employees (id, registered_at, registered_by, email_address, first_name, gender, last_name, middle_name, phone_number, birthdate, hire_date, work_id, department_fk)
VALUES (205, '2023-02-20', 'Lulu', 'elviselly@gmail.com', 'Elvis', 'M', 'Sechaga', 'Kel', '255768123456', '1960-12-10', '2019-12-04', '19-04-00012', 100);
insert into employees (id, registered_at, registered_by, email_address, first_name, gender, last_name, middle_name, phone_number, birthdate, hire_date, work_id, department_fk)
VALUES (206, '2023-02-20', 'Lulu', 'michaelmoo@gmail.com', 'Michael', 'M', 'Joha', 'Leo', '255687000077', '1985-09-08', '2017-11-01', '19-04-00348', 100);

insert into employees (id, registered_at, registered_by, email_address, first_name, gender, last_name, middle_name, phone_number, birthdate, hire_date, work_id, department_fk)
VALUES (208, '2023-01-21', 'Lulu', 'Mohamed@gmail.com', 'Mohamed', 'M', 'Mtalila', 'Hillal', '25577100200', '1995-07-07', '2019-11-01', '2019-04-02348', 101);
insert into employees (id, registered_at, registered_by, email_address, first_name, gender, last_name, middle_name, phone_number, birthdate, hire_date, work_id, department_fk)
VALUES (209, '2023-01-21', 'Habiba', 'emmanuel44@gmail.com', 'Emmanuel', 'M', 'Boshe', 'Prince', '255711890344', '1963-11-12', '1980-11-01', '2012-04-000111', 101);
insert into employees (id, registered_at, registered_by, email_address, first_name, gender, last_name, middle_name, phone_number, birthdate, hire_date, work_id, department_fk)
VALUES (210, '2023-01-21', 'Habiba', 'allansam@gmail.com', 'Allan', 'M', 'Samuel', 'Mfure', '2557109898765', '1955-03-04', '1976-11-06', '1976-04-00981', 101);
insert into employees (id, registered_at, registered_by, email_address, first_name, gender, last_name, middle_name, phone_number, birthdate, hire_date, work_id, department_fk)
VALUES (213, '2023-01-20', 'Habiba', 'aisam3@gmail.com', 'Aisa', 'F', 'Chamasi', 'Frank', '255734545465', '1980-05-05', '2000-11-01', '2000-04-00323', 101);
insert into employees (id, registered_at, registered_by, email_address, first_name, gender, last_name, middle_name, phone_number, birthdate, hire_date, work_id, department_fk)
VALUES (214, '2023-03-31', 'Habiba', 'happiness@gmail.com', 'Happiness', 'F', 'Henry', '', '255712345678', '1978-02-01', '1991-01-01', '1991-04-00001', 101);
insert into employees (id, registered_at, registered_by, email_address, first_name, gender, last_name, middle_name, phone_number, birthdate, hire_date, work_id, department_fk)
VALUES (215, '2023-04-03', 'Lulu','missmassaba@gmail.com', 'Gwantwa', 'F', 'Massaba', 'Bakari', '255789765432', '1978-10-12', '2003-09-09', '2003-04-00012', 101);
insert into employees (id, registered_at, registered_by, email_address, first_name, gender, last_name, middle_name, phone_number, birthdate, hire_date, work_id, department_fk)
VALUES (216, '2023-05-06', 'Lulu', 'revocatuswaa@gmail.com', 'Revocatus', 'M', 'Waangu', 'Elly', '25573456789', '1993-07-07', '2022-10-01', '2022-04-02001', 101);



-- SUPPLIER TABLE - at least 4 more supplier
insert into suppliers (id, registered_at, registered_by, description, company_email, company_name, company_phone, supplier_type, company_website)
VALUES (300, '2020-09-12', 'Habiba', 'Supplies Office enablers like routers and switch', 'support@networkassociate.co.tz', 'Network Associate', '+255222111777', 'WHOLESALER', 'https://www.networkassociate.co.tz');
insert into suppliers (id, registered_at, registered_by, description, company_email, company_name, company_phone, supplier_type, company_website)
VALUES (301, '2020-03-28', 'Habiba', 'Supplies Office enablers like computers and printers', 'support@qualityassociate.co.tz', 'Quality Associate', '+255222333555', 'RETAILER', 'https://www.qualityassociate.co.tz');
insert into suppliers (id, registered_at, registered_by, description, company_email, company_name, company_phone, supplier_type, company_website)
VALUES (302, '2022-08-20', 'Lulu', 'Supplies Office enablers like routers and switch', 'support@uhakikassociate.co.tz', 'Uhakika Associate', '+255222332111', 'RETAILER', 'https://www.uhakikaassociate.co.tz');
insert into suppliers (id, registered_at, registered_by, description, company_email, company_name, company_phone, supplier_type, company_website)
VALUES (303, '2020-06-28', 'Lulu', 'Supplies Office enablers like computers,scanner and printers', 'support@technologyassociate.co.tz', 'Technology Associate', '+255222333444', 'MANUFACTURER', 'https://www.technologyassociate.co.tz');

-- -- ADDRESS TABLE - add at least 15 addresses
-- -- for employee address
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, employee_fk)
VALUES (400, 'Dar es Salaam', 'Tanzania', 'Masaki', '11001', 'UZR', 'Mpakani', 200);
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, employee_fk)
VALUES (401, 'Dar es Salaam', 'Tanzania', 'Ubungo', '11451', 'santika', 'Mpakani', 201);
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, employee_fk)
VALUES (402, 'Dar es Salaam', 'Tanzania', 'Morocco', '10000', 'light', 'uhuru', 202);
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, employee_fk)
VALUES (403, 'Dar es Salaam', 'Tanzania', 'Mikocheni', '09099', 'bluegreen', 'Madaa', 203);
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, employee_fk)
VALUES (404, 'Dar es Salaam', 'Tanzania', 'Msimbazi', '23456', 'zebra', 'peace', 204);
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, employee_fk)
VALUES (405, 'Dar es Salaam', 'Tanzania', 'Mwenge', '09876', 'green', 'mapambano', 205);
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, employee_fk)
VALUES (406, 'Dar es Salaam', 'Tanzania', 'kariakoo', '08976', 'elephant', 'elephant', 206);
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, employee_fk)
VALUES (407, 'Dar es Salaam', 'Tanzania', 'Mikocheni', '90909', 'bluegreen', 'Madaa', 208);
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, employee_fk)
VALUES (408, 'Dar es Salaam', 'Tanzania', 'oysterbay', '13459', 'upendo', 'upendo', 209);
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, employee_fk)
VALUES (409, 'Dar es Salaam', 'Tanzania', 'Mikocheni', '12348', 'umoja', 'umoja', 210);
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, employee_fk)
VALUES (412, 'Dar es Salaam', 'Tanzania', 'Mikocheni', '09909', 'bluegreen', 'Madaa', 213);
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, employee_fk)
VALUES (410, 'Dar es Salaam', 'Tanzania', 'Morroco', '09091', 'bluegreen', 'Madaa', 214);
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, employee_fk)
VALUES (411, 'Dar es Salaam', 'Tanzania', 'Mikocheni', '09099', 'bluegreen', 'Madaa', 215);
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, employee_fk)
VALUES (413, 'Dar es Salaam', 'Tanzania', 'Mikocheni', '90909', 'bluegreen', 'Madaa', 216);


-- -- for supplier address
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, supplier_fk)
VALUES (450, 'Dar es Salaam', 'Tanzania', 'Masaki', '11001', 'UZR', 'Mpakani', 300);
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, supplier_fk)
VALUES (451, 'Dar es Salaam', 'Tanzania', 'Ubungo', '11451', 'santika', 'Mpakani', 301);
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, supplier_fk)
VALUES (452, 'Dar es Salaam', 'Tanzania', 'Morocco', '10000', 'light', 'uhuru', 302);
insert into addresses (id, city, country, district_name, postal_code, street_name, ward_name, supplier_fk)
VALUES (453, 'Dar es Salaam', 'Tanzania', 'Mikocheni', '09009', 'bluegreen', 'Madaa', 303);

-- CATEGORY TABLE - different categories i.e Laptop, Desktop, Router, Switch etc
insert into categories (id, description, category_name) VALUES (500, 'Laptop computer', 'Laptop');
insert into categories (id, description, category_name) VALUES (501, 'switch', 'Switch');
insert into categories (id, description, category_name) VALUES (502, 'printer', 'printer');
insert into categories (id, description, category_name) VALUES (503, 'All routers', 'Router');
insert into categories (id, description, category_name) VALUES (504, 'scanner', 'scanner');

-- PURCHASE TABLE - five perchase max
insert into purchases (id, purchase_invoice_number, purchase_date, purchase_price, purchase_quantity, supplier_fk)
VALUES (550, 'RPC4-123456', '2022-09-12', 35000000, 15, 300);
insert into purchases (id, purchase_invoice_number, purchase_date, purchase_price, purchase_quantity, supplier_fk)
VALUES (551, 'RPC4-987654', '2022-09-12', 55000000, 16, 301);
insert into purchases (id, purchase_invoice_number, purchase_date, purchase_price, purchase_quantity, supplier_fk)
VALUES (552, 'JPC4-123456', '2023-10-12', 40000000, 15, 302);
insert into purchases (id, purchase_invoice_number, purchase_date, purchase_price, purchase_quantity, supplier_fk)
VALUES (553, 'JPC4-676543', '2022-01-12', 15000000, 15, 303);
insert into purchases (id, purchase_invoice_number, purchase_date, purchase_price, purchase_quantity, supplier_fk)
VALUES (554, 'HPC4-123456', '2021-09-12', 35000000, 10, 300);

-- ASSETS TABLE - add at least 20 computers
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 600, 'LENOVO', 'Lenovo', 'Thinkbook 14 G2 ITL', 'MPNXB192709Z', 'M/23QNWJ', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '550');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 601, 'LENOVO', 'Lenovo', 'Thinkbook 14 G2 ITL', 'M-XB192709Z', 'M$23QNWJ', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '550');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 602, 'LENOVO', 'Lenovo', 'Thinkbook 14 G2 ITL', 'MPNXB-192709Z', 'MP-23QNWJ', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '550');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 603, 'HP', 'hp', 'Pro-book 650 G2', 'MP/B192709Z', 'MP2ANWJ', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '550');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 604, 'HP', 'hp', 'Pro-book 650 G2', 'MPN/XB-19279A', 'MP11QNWJ', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '553');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 605, 'HP', 'hp', 'Pro-book 650 G2', 'MXB192709Z', 'MP23/NWJ', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '553');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 606, 'HP', 'hp', 'Elite-book 830 G5', 'MPNXB/2709Z', 'MP00QNWJ', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '553');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 607, 'HP', 'hp', 'Elite-book 830 G5', 'M$XB192709Z', 'MPON2301', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '553');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 608, 'LENOVO', 'Lenovo', 'Thinkbook 14 G2 ITL', 'MP//B-192715A', 'MP2300', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '550');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 609, 'LENOVO', 'Lenovo', 'Thinkbook 14 G2 ITL', 'MPZYXB192709', 'M$XB192709Z', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '550');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 610, 'LENOVO', 'Lenovo', 'Thinkbook 14 G2 ITL', 'M-XAB192709Z', 'MBPNXB192709Z', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '550');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 611, 'LENOVO', 'Lenovo', 'Thinkbook 14 G2 ITL', 'MPNXB002709Z', 'MP20001', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '551');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 612, 'LENOVO', 'Lenovo', 'Thinkbook 14 G2 ITL', 'FPNXB192709Z', 'XAB192709Z', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '551');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 613, 'LENOVO', 'Lenovo', 'Thinkbook 14 G2 ITL', 'CDNXB192709Z', 'MP23QNWJ', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '551');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 614, 'LENOVO', 'Lenovo', 'Thinkbook 14 G2 ITL', 'MBPNXB192709Z', 'MP000QNWJ', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '554');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 615, 'DELL', 'Dell', 'Thinkbook 14 G2 ITL', 'APNXB192709Z', 'kjNXB192709Z', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '554');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 616, 'DELL', 'Dell', 'Thinkbook 14 G2 ITL', 'OPNXB19270K78', 'NP23//WJ', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz   2.42 GHz', '2000000', 500, '554');
insert into assets (asset_discriminator, id, brand_name, manufacturer, model_name, model_number, serial_number, display_size, graphics_card, computer_memory, operating_system, computer_processor, computer_storage, category_fk, purchase_fk)
VALUES ('COMPUTER', 617, 'LENOVO', 'Lenovo', 'Thinkbook 14 G2 ITL', 'kjNXB192709Z', 'MBP//XB192700', '14', '4115', '8192', 'Windows 11 Enterprise 64-bit (10.0, Build 22621) (22621.ni_release.220506-1250)', '11th Gen Intel(R) Core(TM) i-1135G7 @ 2.40GHz   2.42 GHz', '1000000', 500, '554');

-- ALLOCATIONS
insert into asset_allocations (id, allocation_date, allocation_remarks, asset_fk, employee_fk)
VALUES (700, '2023-05-03', 'testing queries', 617, 200);

insert into allocation_status (allocation_id, status) VALUES (700, 'ALLOCATED');
