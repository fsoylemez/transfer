INSERT INTO client(ID, SSN, FIRST_NAME, LAST_NAME) VALUES('c595228a-fc8c-474a-ada0-e80e9980e275', '654287901077', 'Henry', 'Stone'),
('0970d83b-7e96-4921-804a-2c1f2ceda29d', '654989731520', 'Michael', 'Bane');

INSERT INTO account(ID, CLIENT_ID, ACCOUNT_NAME, CURRENCY, BALANCE, CREATION_TIME) VALUES('d3c17634-79f8-4a18-add3-aeee470b8505', 'c595228a-fc8c-474a-ada0-e80e9980e275', 'euroAcc', 'EUR', 4290.27, '2024-05-07 10.43.02'),
('4c47d15c-eb10-44c9-9fe5-4f4b9290f837', '0970d83b-7e96-4921-804a-2c1f2ceda29d', 'myEur', 'EUR', 10370.54, '2024-05-08 13.43.02');