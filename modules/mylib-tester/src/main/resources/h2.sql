create sequence if not exists phones_seq start with 11;

create table if not exists phones (
  id int not null,
  number varchar(20) not null,

  constraint pk_phones primary key (id),
  constraint uk_phones unique (number)
);

create sequence if not exists persons_seq start with 7;

create table persons (
  id int not null,
  surname varchar(30) not null,
  name varchar(30) not null,
  patronymic varchar(30),
  birth_date date,

  constraint pk_persons primary key (id)
);

insert into phones (id, number) values
  (1, '123456780'),
  (2, '123456781'),
  (3, '123456782'),
  (4, '123456783'),
  (5, '123456784'),
  (6, '123456785'),
  (7, '123456786'),
  (8, '123456787'),
  (9, '123456788'),
  (10, '123456789')
;

insert into persons (id, surname, name, patronymic, birth_date) values
  (1, 'Qaz', 'Alex', null, null),
  (2, 'Qwe', 'Mary', 'Bob', '1992-03-25'),
  (3, 'Asd', 'Sofy', null, '1983-12-13')
;