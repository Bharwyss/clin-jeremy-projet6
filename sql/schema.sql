CREATE DATABASE IF NOT EXISTS paymybuddy
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE paymybuddy;
CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(30) UNIQUE NOT NULL, 
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(60) NOT NULL,
    balance DECIMAL(10,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE transfer (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sender_id INT,
    receiver_id	INT,
    description VARCHAR(200),
    amount	DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transfer_sender
        FOREIGN KEY (sender_id)
        REFERENCES user (id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
        
	CONSTRAINT fk_transfer_receiver
        FOREIGN KEY (receiver_id)
        REFERENCES user (id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE user_connection (
    adder_id INT NOT NULL,
    added_id INT NOT NULL,
    PRIMARY KEY (adder_id, added_id),
    CONSTRAINT fk_userconnection_adder
        FOREIGN KEY (adder_id)
        REFERENCES user (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_userconnection_added
        FOREIGN KEY (added_id)
        REFERENCES user (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;