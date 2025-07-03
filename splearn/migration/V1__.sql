use splearn;
CREATE TABLE member
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    nickname      VARCHAR(100) NOT NULL,
    password_hash VARCHAR(200) NOT NULL,
    status        VARCHAR(50)  NOT NULL,
    address       VARCHAR(250) NOT NULL,
    CONSTRAINT pk_member PRIMARY KEY (id)
);

ALTER TABLE member
    ADD CONSTRAINT uc_member_address UNIQUE (address);