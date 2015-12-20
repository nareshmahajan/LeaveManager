# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table leaves (
  employeeId                varchar(255),
  fromDate                  datetime(6),
  toDate                    datetime(6))
;

create table common_bean (
  id                        varchar(255),
  name                      varchar(255))
;

create table holidays (
  holidayid                 integer auto_increment not null,
  holidaydate               datetime(6),
  reason                    varchar(255),
  constraint pk_holidays primary key (holidayid))
;

create table leaves (
  leaveid                   integer auto_increment not null,
  employeeId                varchar(255),
  fromDate                  datetime(6),
  toDate                    datetime(6),
  approverId                varchar(255),
  leaveReason               varchar(255),
  status                    varchar(1),
  leavetype                 varchar(255),
  maker                     varchar(255),
  constraint ck_leaves_status check (status in ('2','0','1','3')),
  constraint pk_leaves primary key (leaveid))
;

create table user (
  id                        bigint auto_increment not null,
  auth_token                varchar(255),
  approver_id               varchar(255),
  user_id                   varchar(255),
  name                      varchar(255),
  group_id                  varchar(255),
  is_admin                  varchar(255),
  is_approver               varchar(255),
  email_id                  varchar(255),
  constraint pk_user primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table leaves;

drop table common_bean;

drop table holidays;

drop table leaves;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

