INSERT INTO client(ID, SSN, FIRST_NAME, LAST_NAME) VALUES('02d1d2d2-fa91-480d-abee-680f5f5a6398', '354981321098', 'Anna', 'Baker');

INSERT INTO account(ID, CLIENT_ID, ACCOUNT_NAME, CURRENCY, BALANCE) VALUES('9e66a064-69ad-4672-8a8b-f67514d53536', '02d1d2d2-fa91-480d-abee-680f5f5a6398', 'bussinessA', 'USD', 421090.27),
('7caa71c1-e67c-4497-a8ff-d26f77b4e872', '02d1d2d2-fa91-480d-abee-680f5f5a6398', 'personalA', 'USD', 10670.54);

INSERT INTO transaction_history(ID, CLIENT_ID, ACCOUNT_ID, AMOUNT, BALANCE_AFTER, DESCRIPTION, TRANSACTION_TIME) VALUES('b6cae6ca-6ad5-4368-ada0-8976bbe3c3d3', '02d1d2d2-fa91-480d-abee-680f5f5a6398', '9e66a064-69ad-4672-8a8b-f67514d53536', 35.45, 42154.55, 'expenses 1', '2024-05-08 12.51.32'),
('4bf8dde9-a65a-49c7-8e84-a6a0819014d4', '02d1d2d2-fa91-480d-abee-680f5f5a6398', '9e66a064-69ad-4672-8a8b-f67514d53536', 1453.25, 419601.30, 'expenses 2', '2024-05-08 13.43.02'),
('9ffcf86c-579a-4cb7-8a72-9c261993edf2', '02d1d2d2-fa91-480d-abee-680f5f5a6398', '9e66a064-69ad-4672-8a8b-f67514d53536', 2341.67, 417259.63, 'expenses 3', '2024-05-08 14.22.23'),
('8e180011-f679-47a4-b824-3aee630c36b8', '02d1d2d2-fa91-480d-abee-680f5f5a6398', '9e66a064-69ad-4672-8a8b-f67514d53536', 652.14, 416607.49, 'expenses 4', '2024-05-08 15.39.11'),
('a582cc4e-ab33-4662-842c-cd23493e0b6a', '02d1d2d2-fa91-480d-abee-680f5f5a6398', '9e66a064-69ad-4672-8a8b-f67514d53536', 12.58, 416594.91, 'expenses 5', '2024-05-08 16.14.29'),
('114c12ac-fb21-400c-ab8c-47b854a9841c', '02d1d2d2-fa91-480d-abee-680f5f5a6398', '9e66a064-69ad-4672-8a8b-f67514d53536', 18982.36, 397612.55, 'expenses 6', '2024-05-08 17.41.03');