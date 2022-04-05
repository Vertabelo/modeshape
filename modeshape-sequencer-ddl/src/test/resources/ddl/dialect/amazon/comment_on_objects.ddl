CREATE TABLE client (
    id int  NOT NULL,
    full_name varchar(255)  NOT NULL,
    email varchar(255)  NOT NULL,
    PRIMARY KEY (id)
) ;
ALTER TABLE client ADD CONSTRAINT client_email UNIQUE (email);
COMMENT ON TABLE client IS 'Test client';
COMMENT ON COLUMN client.full_name IS 'Test client.full_name';
COMMENT ON CONSTRAINT client_email ON client is 'Test client_email constraint';