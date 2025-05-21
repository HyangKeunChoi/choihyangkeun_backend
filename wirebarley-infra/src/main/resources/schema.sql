CREATE TABLE IF NOT EXISTS accounts (
    id                        BIGINT                                AUTO_INCREMENT PRIMARY KEY,
    user_id                   BIGINT                                NOT NULL COMMENT '유저 아이디',
    account_number            VARCHAR(40)                           NOT NULL COMMENT '계좌 번호' ,
    balance                   BIGINT              DEFAULT 0         NOT NULL     COMMENT '잔액' ,
    account_status            VARCHAR(15)                           NOT NULL COMMENT '계좌 상태' ,
    created_at                DATETIME                              NOT NULL,
    updated_at                DATETIME                              NOT NULL
);

CREATE TABLE IF NOT EXISTS transfers (
    id                        BIGINT                                AUTO_INCREMENT PRIMARY KEY,
    sender_account_id         BIGINT                                NOT NULL COMMENT '보낸 계좌 아이디',
    receiver_account_id       BIGINT                                NOT NULL COMMENT '받은 계좌 아이디',
    transfer_amount           INT                                   NOT NULL COMMENT '송금액',
    description               VARCHAR(255)                          NULL COMMENT '송금 메시지',
    transfer_at               DATETIME                              NULL COMMENT '송금 날짜',
    created_at                DATETIME                              NOT NULL,
    updated_at                DATETIME                              NOT NULL
);

CREATE UNIQUE INDEX idx_accounts_on_user_id
    ON accounts(user_id);

CREATE UNIQUE INDEX idx_accounts_on_account_number
    ON accounts(account_number);

CREATE INDEX idx_transfers_sender_account_id
    ON transfers(sender_account_id);

CREATE INDEX idx_transfers_receiver_account_id
    ON transfers(receiver_account_id);

CREATE INDEX idx_transfers_on_transfer_at_sender_account_id
    ON transfers(transfer_at, sender_account_id);

CREATE INDEX idx_transfers_on_transfer_at_receiver_account_id
    ON transfers(transfer_at, receiver_account_id);