--liquibase formatted sql
--changeset Dmitri:fill_tables contextFilter:dev

INSERT INTO components (component_id, description) VALUES (1, 'service A');

INSERT INTO limits (component_id, level, quantity, threshold) VALUES (1, 3, 0, 3);

INSERT INTO emails (email) VALUES ('somebody@mail.com');

INSERT INTO mails (component_id, email_id) VALUES (1, 1);