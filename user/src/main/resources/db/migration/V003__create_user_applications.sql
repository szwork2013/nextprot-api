CREATE TABLE np_users.user_applications (
  application_id BIGSERIAL PRIMARY KEY,
  application_name VARCHAR(100) NOT NULL,
  description VARCHAR(100) NOT NULL,
  organisation VARCHAR(100),
  responsible_name VARCHAR(100),
  responsible_email VARCHAR(100),
  website VARCHAR(100),
  owner_id bigint REFERENCES np_users.users(user_id) ON DELETE CASCADE,
  token VARCHAR(1024) NOT NULL, -- api id
  status VARCHAR(10) NOT NULL default 'ACTIVE' CHECK (status IN ('ACTIVE', 'BANNED')),
  user_data_access VARCHAR (2) NOT NULL default 'RO' CHECK (user_data_access IN ('RO', 'RW')),
  origins varchar(512), -- hostname hosting the webapp, used to make sure the call to the API is performed from that origin
  -- last_session_date TIMESTAMP,
  creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE index user_application_owner_app_udx ON np_users.user_applications USING btree (owner_id, application_name);
