create table userData(
    id serial primary key ,
    email varchar,
    password varchar

);
create table fileData(
    id serial primary key,
    filename varchar,
    userData_id int,
    FOREIGN KEY (userData_id) REFERENCES userdata(id),
    date timestamp(6),
    size bigint
)