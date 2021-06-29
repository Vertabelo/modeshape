CREATE TABLE author (
        id int  NOT NULL,
        car_size number(5,2)  NOT NULL,
        test_string string(10) NOT NULL,
        test_text text(10) NOT NULL,
        CONSTRAINT author_pk PRIMARY KEY (id)
);