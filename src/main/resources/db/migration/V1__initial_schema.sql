CREATE TABLE CLIENT(
    ID uuid,
    SSN varchar(12) not null,
    FIRST_NAME varchar(255) not null,
    LAST_NAME varchar(255) not null,
    EMAIL_ADDRESS varchar(255),
    PHONE_NUMBER varchar(255),
    CREATION_TIME timestamp default current_timestamp,
    VERSION int not null default 0,
    PRIMARY KEY (ID),
    UNIQUE (SSN)
);


CREATE TABLE ACCOUNT(
    ID uuid,
    CLIENT_ID uuid,
    ACCOUNT_NAME varchar(255) not null,
    CURRENCY varchar(3) not null,
    BALANCE decimal(10,2) not null default 0,
    CREATION_TIME timestamp default current_timestamp,
    VERSION int not null default 0,
    PRIMARY KEY (ID),
    FOREIGN KEY(CLIENT_ID) REFERENCES CLIENT(ID)
);

CREATE INDEX account_idx_clientId ON ACCOUNT(CLIENT_ID);

CREATE TABLE TRANSACTION_HISTORY(
    ID uuid,
    CLIENT_ID uuid,
    ACCOUNT_ID uuid,
    TRANSACTION_TIME timestamp default current_timestamp,
    AMOUNT decimal(10,2) not null,
    BALANCE_AFTER decimal(10,2) not null,
    DESCRIPTION varchar(255),
    VERSION int not null default 0,
    PRIMARY KEY (ID),
    FOREIGN KEY(CLIENT_ID) REFERENCES CLIENT(ID),
    FOREIGN KEY(ACCOUNT_ID) REFERENCES ACCOUNT(ID)
);

CREATE INDEX transaction_history_idx_accountId ON TRANSACTION_HISTORY(ACCOUNT_ID);
