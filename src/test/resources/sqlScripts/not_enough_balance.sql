INSERT INTO client(ID, SSN, FIRST_NAME, LAST_NAME) VALUES('32caf149-315c-4b40-8f50-4398958d5d47', '654987911077', 'Henry', 'Stone'),
('0e4f235d-3a49-4e81-a497-ae12ea930a9c', '654981231520', 'Michael', 'Bane');

INSERT INTO account(ID, CLIENT_ID, ACCOUNT_NAME, CURRENCY, BALANCE, CREATION_TIME) VALUES('0e4f235d-3a49-4e81-a497-ae12ea930a9c', '32caf149-315c-4b40-8f50-4398958d5d47', 'euroAcc', 'EUR', 4290.27, '2024-05-07 10.43.02'),
('6f70f9c7-c94d-4fa0-8f3f-e635ee6d2bfe', '0e4f235d-3a49-4e81-a497-ae12ea930a9c', 'myEur', 'EUR', 10370.54, '2024-05-08 13.43.02');