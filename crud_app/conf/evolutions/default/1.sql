# --- !Ups

create sequence permit_id_seq;
create table permit (
  id                  bigint not null default nextval('permit_id_seq'),
  project             varchar(255) not null,
  owner               varchar(255) not null,
  assignee            varchar(255) not null,
  primary key (id)
);

# --- !Downs

drop table permit;
drop sequence permit_id_seq;