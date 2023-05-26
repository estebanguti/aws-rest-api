create table image(
    id               BIGINT          PRIMARY KEY AUTO_INCREMENT,
    name             VARCHAR(20),
    image_size       BIGINT,
    file_extension   VARCHAR(40) ,
    updated_at       DATETIME       DEFAULT NOW()
);