CREATE TABLE cow (
    a int  IDENTITY(1,1) ENCODE DELTA NOT NULL,
    b int  NOT NULL,
    c varchar(20)  NULL,
    PRIMARY KEY (a,b)
) BACKUP YES DISTSTYLE KEY DISTKEY (a) COMPOUND SORTKEY (b,c);

-- Table: dog
CREATE TABLE dog (
    first int  NOT NULL,
    cow_a int  NOT NULL,
    cow_b int  NOT NULL,
    second int  NOT NULL,
    UNIQUE (first, cow_a),
    PRIMARY KEY (first)
) BACKUP NO INTERLEAVED SORTKEY (first);


-- foreign keys
-- Reference: dog_cow (table: dog)
ALTER TABLE dog ADD FOREIGN KEY (cow_a, cow_b)
    REFERENCES cow (a, b)
;