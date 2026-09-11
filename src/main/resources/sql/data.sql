USE paymybuddy;

DELETE FROM transfer;
DELETE FROM user_connection;
DELETE FROM user;
ALTER TABLE user AUTO_INCREMENT = 1;
ALTER TABLE transfer AUTO_INCREMENT = 1;

INSERT INTO user (username, email, password, balance) VALUES
    ('jake', 'jake@example.com', '$2a$12$ql.6gPRRSknhQcltQxMjj.2hzz9cfP6wDxZIbkBy3U6MlPIiHPz5m', 155.00),
	('poum', 'poum@example.com', '$2a$12$ql.6gPRRSknhQcltQxMjj.2hzz9cfP6wDxZIbkBy3U6MlPIiHPz5m', 237.50),
	('sali', 'sali@example.com', '$2a$12$ql.6gPRRSknhQcltQxMjj.2hzz9cfP6wDxZIbkBy3U6MlPIiHPz5m', 119.50),
	('treay', 'treay@example.com', '$2a$12$ql.6gPRRSknhQcltQxMjj.2hzz9cfP6wDxZIbkBy3U6MlPIiHPz5m', 114.50),
	('lupe', 'lupe@example.com', '$2a$12$ql.6gPRRSknhQcltQxMjj.2hzz9cfP6wDxZIbkBy3U6MlPIiHPz5m', 123.50)
    ;

INSERT INTO user_connection (adder_id, added_id) VALUES
    (1, 2),
	(2, 1),
	(4, 2),
	(5, 2),
	(1, 3)
    ;

INSERT INTO transfer (sender_id, receiver_id, description, amount) VALUES
    (1, 2, 'resto', 25.50),
	(3, 1, 'shopping', 30.50),
	(4, 2, 'courses', 35.50),
	(5, 2, 'jeux', 26.50)
    ;