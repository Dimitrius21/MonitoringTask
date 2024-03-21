CREATE TABLE notifications (
	id BIGSERIAL PRIMARY KEY,
	happened TIMESTAMP,
	component_id BIGINT,
	level INTEGER,
	description VARCHAR(255)
);

CREATE TABLE components (
	component_id BIGINT UNIQUE,
	description VARCHAR(255)
);

CREATE TABLE limits (
	component_id BIGINT,
	level INTEGER,
	quantity INTEGER,
	threshold INTEGER,
	CONSTRAINT limits_pkey PRIMARY KEY (component_id, level),
	CONSTRAINT fk_limits_components FOREIGN KEY (component_id) REFERENCES components(component_id)
);

CREATE TABLE emails (
	id BIGSERIAL PRIMARY KEY,
	email VARCHAR(255) UNIQUE
);


CREATE TABLE mails (
	id BIGSERIAL PRIMARY KEY,
	component_id BIGINT REFERENCES components(component_id),
	email_id BIGINT REFERENCES emails(id)
);


