# customer.sql
DROP TABLE IF EXISTS customer;
CREATE TABLE customer
(
    id         INT          NOT NULL AUTO_INCREMENT,
    name       VARCHAR(255) NOT NULL,
    created_at DATETIME     NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;