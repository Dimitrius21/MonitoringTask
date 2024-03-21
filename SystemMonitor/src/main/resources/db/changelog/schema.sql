--liquibase formatted sql
--changeset Dmitri:create_tables contextFilter:dev

CREATE TABLE notifications (
	id BIGSERIAL PRIMARY KEY,
	happened TIMESTAMP NOT NULL,
	component_id BIGINT NOT NULL,
	level INTEGER NOT NULL,
	description VARCHAR(255)
);

CREATE TABLE components (
	component_id BIGINT UNIQUE NOT NULL,
	description VARCHAR(255)
);

CREATE TABLE limits (
	component_id BIGINT NOT NULL,
	level INTEGER NOT NULL,
	quantity INTEGER NOT NULL,
	threshold INTEGER NOT NULL,
	CONSTRAINT limits_pkey PRIMARY KEY (component_id, level),
	CONSTRAINT fk_limits_components FOREIGN KEY (component_id) REFERENCES components(component_id)
);

CREATE TABLE emails (
	id BIGSERIAL PRIMARY KEY,
	email VARCHAR(255) UNIQUE NOT NULL
);


CREATE TABLE mails (
	id BIGSERIAL PRIMARY KEY,
	component_id BIGINT REFERENCES components(component_id),
	email_id BIGINT REFERENCES emails(id)
);
