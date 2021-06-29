-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2021-06-28 07:13:02.453

-- tables
-- Table: aliasy_typow_danych
CREATE TABLE aliasy_typow_danych (
                                     aa integer  NOT NULL,
                                     bb numeric(12)  NOT NULL,
                                     cc float4  NOT NULL,
                                     ddddddddddddddddd float8  NOT NULL,
                                     eee double precision  NOT NULL,
                                     fff real  NOT NULL,
                                     gg character(10)  NOT NULL,
                                     yy stgring(12)  NOT NULL,
                                     nn timestamp_ltz  NOT NULL,
                                     uu timestamp_ntz  COLLATE ssdsds AUTOINCREMENT 444 NOT NULL,
                                     CONSTRAINT aliasy_typow_danych_pk PRIMARY KEY (aa)
);

-- Table: author
CREATE TABLE author (
                        id int  NOT NULL,
                        first_name char(100)  NOT NULL,
                        last_name varchar(99)  NOT NULL,
                        is_popular boolean  NOT NULL,
                        hight Float  NOT NULL,
                        weight double  NOT NULL,
                        feet_size real  NOT NULL,
                        salary decimal(10,2)  NOT NULL,
                        pesel bigint  NOT NULL,
                        seq int  NOT NULL,
                        car_size number(5,2)  NOT NULL,
                        time_of_birth datetime  NOT NULL,
                        first_publication timestamp  NOT NULL,
                        breakfast_time time  NOT NULL,
                        CONSTRAINT author_pk PRIMARY KEY (id)
);

-- Table: book
CREATE TRANSIENT TABLE MYSCHEMA.book (
    id int  NOT NULL,
    isbn char(12)  NOT NULL,
    title varchar(100)  NOT NULL,
    publicatation_date date  NOT NULL,
    price decimal(12,2)  NOT NULL,
    rate smallint  NOT NULL,
    comments text  NOT NULL,
    cover_photo binary  NOT NULL,
    geo int  NOT NULL,
    CONSTRAINT book_pk PRIMARY KEY (id)
)
CLUSTER BY (abc)
 STAGE_FILE_FORMAT = (qqq)
 STAGE_COPY_OPTIONS = (qwer)
 DATA_RETENTION_TIME_IN_DAYS = 12
 MAX_DATA_EXTENSION_TIME_IN_DAYS = 34
 CHANGE_TRACKING = TRUE
 DEFAULT_DDL_COLLATION = 'qwerrrr'
COMMENT = 'Testowy komentarz';

-- Table: book_author
CREATE TABLE book_author (
                             book_id int  NOT NULL,
                             author_id int  NOT NULL,
                             CONSTRAINT book_author_pk PRIMARY KEY (book_id,author_id)
);

-- Table: inne_typy_danych
CREATE TABLE inne_typy_danych (
                                  aaa varbinary  NOT NULL DEFAULT qqqq,
                                  bbb geography  NOT NULL,
                                  ccc variant  NOT NULL,
                                  ddd object  NOT NULL,
                                  eee array  NOT NULL,
                                  CONSTRAINT inne_typy_danych_pk PRIMARY KEY (aaa)
);

-- Table: pk_test
CREATE TABLE pk_test (
                         pk1 int  NOT NULL,
                         pk2 int  NOT NULL,
                         pk3 int  NOT NULL,
                         xxx int  NOT NULL,
                         yyy int  NOT NULL,
                         CONSTRAINT pk_test_ak_1 UNIQUE (pk3),
                         CONSTRAINT pk_test_ak_2 UNIQUE (xxx, yyy),
                         CONSTRAINT pk_test_pk PRIMARY KEY (pk1,pk2)
);

-- foreign keys
-- Reference: book_author_author (table: book_author)
ALTER TABLE book_author ADD CONSTRAINT book_author_author
    FOREIGN KEY (author_id)
        REFERENCES author (id)
;

-- Reference: book_author_book (table: book_author)
ALTER TABLE book_author ADD CONSTRAINT book_author_book
    FOREIGN KEY (book_id)
        REFERENCES MYSCHEMA.book (id)
;

-- sequences
-- Sequence: author_seq
CREATE SEQUENCE author_seq
    START WITH 1
    INCREMENT BY 1;

-- Sequence: book_seq
CREATE SEQUENCE book_seq
    START WITH 1
    INCREMENT BY 1;

-- Sequence: test_seq
CREATE SEQUENCE test_seq
    START WITH 1
    INCREMENT BY 2
COMMENT = 'abxcxxxx'
;

-- End of file.
