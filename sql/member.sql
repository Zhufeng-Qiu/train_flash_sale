drop table if exists `member`;
create table `member` (
  `id` bigint not null comment 'id',
  `mobile` varchar(12) comment 'mobile',
  primary key (`id`),
  unique key `mobile_unique` (`mobile`)
) engine=innodb default charset=utf8mb4 comment='member';

drop table if exists `passenger`;
create table `passenger` (
    `id` bigint not null comment 'id',
    `member_id` bigint not null comment 'member id',
    `name` varchar(20) not null comment 'name',
    `id_card` varchar(18) not null comment 'id card',
    `type` char(1) not null comment 'Passenger Type | Enum[PassengerTypeEnum]',
    `create_time` datetime(3) comment 'create time',
    `update_time` datetime(3) comment 'update time',
    primary key (`id`),
    index `member_id_index` (`member_id`)
) engine=innodb default charset=utf8mb4 comment='passenger';

drop table if exists `ticket`;
create table `ticket` (
    `id` bigint not null comment 'id',
    `member_id` bigint not null comment 'Member Id',
    `passenger_id` bigint not null comment 'Passenger Id',
    `passenger_name` varchar(20) comment 'Passenger Name',
    `date` date not null comment 'Date',
    `train_code` varchar(20) not null comment 'Train Number',
    `carriage_index` int not null comment 'Carriage Index',
    `row` char(2) not null comment 'Row|01, 02',
    `col` char(1) not null comment 'Column|Enum[SeatColEnum]',
    `start` varchar(20) not null comment 'Departure Station',
    `start_pinyin` varchar(50) not null comment 'Departure Station Alias',
    `start_time` time not null comment 'Start Time',
    `end` varchar(20) not null comment 'Arrival Station',
    `end_pinyin` varchar(50) not null comment 'Arrival Station Alias',
    `end_time` time not null comment 'End Time',
    `seat_type` char(1) not null comment 'Seat Type|Enum[SeatTypeEnum]',
    `create_time` datetime(3) comment 'Create Time',
    `update_time` datetime(3) comment 'Update Time',
    primary key (`id`),
    index `member_id_index` (`member_id`)
) engine=innodb default charset=utf8mb4 comment='Ticket';

