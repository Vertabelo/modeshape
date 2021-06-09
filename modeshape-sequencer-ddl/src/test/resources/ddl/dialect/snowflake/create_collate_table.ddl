CREATE TABLE collation_demo (
    uncollated_phrase VARCHAR,
    utf8_phrase VARCHAR COLLATE 'utf8',
    english_phrase VARCHAR COLLATE 'en',
    spanish_phrase VARCHAR COLLATE 'sp'
);
