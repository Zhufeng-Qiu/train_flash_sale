drop table if exists `station`;
create table `station` (
    `id` bigint not null comment 'id',
    `name` varchar(20) not null comment 'Station',
    `name_pinyin` varchar(50) not null comment 'Station Alias',
    `name_py` varchar(50) not null comment 'Station Initial',
    `create_time` datetime(3) comment 'Create Time',
    `update_time` datetime(3) comment 'Update Time',
    primary key (`id`),
    unique key `name_unique` (`name`)
) engine=innodb default charset=utf8mb4 comment='Station';

drop table if exists `train`;
create table `train` (
    `id` bigint not null comment 'id',
    `code` varchar(20) not null comment 'Train Number',
    `type` char(1) not null comment 'Train Type|Enum[TrainTypeEnum]',
    `start` varchar(20) not null comment 'Departure Station',
    `start_pinyin` varchar(50) not null comment 'Departure Station Alias',
    `start_time` time not null comment 'Start Time',
    `end` varchar(20) not null comment 'Arrival Station',
    `end_pinyin` varchar(50) not null comment 'Arrival Station Alias',
    `end_time` time not null comment 'End Time',
    `create_time` datetime(3) comment 'Create Time',
    `update_time` datetime(3) comment 'Update Time',
    primary key (`id`),
    unique key `code_unique` (`code`)
) engine=innodb default charset=utf8mb4 comment='Train';

drop table if exists `train_station`;
create table `train_station` (
    `id` bigint not null comment 'id',
    `train_code` varchar(20) not null comment 'Train Number',
    `index` int not null comment 'Station Index|0-based',
    `name` varchar(20) not null comment 'Station',
    `name_pinyin` varchar(50) not null comment 'Station Alias',
    `in_time` time comment 'Arrival Time',
    `out_time` time comment 'Departure Time',
    `stop_time` time comment 'Stop Duration',
    `km` decimal(8, 2) not null comment 'Mileage(km)|Distance from Previous Station',
    `create_time` datetime(3) comment 'Create Time',
    `update_time` datetime(3) comment 'Update Time',
    primary key (`id`),
    unique key `train_code_index_unique` (`train_code`, `index`),
    unique key `train_code_name_unique` (`train_code`, `name`)
) engine=innodb default charset=utf8mb4 comment='Train Station';

drop table if exists `train_carriage`;
create table `train_carriage` (
    `id` bigint not null comment 'id',
    `train_code` varchar(20) not null comment 'Train Number',
    `index` int not null comment 'Carriage Index|1-based',
    `seat_type` char(1) not null comment 'Seat Type|Enum[SeatTypeEnum]',
    `seat_count` int not null comment 'Seat Count',
    `row_count` int not null comment 'Row Count',
    `col_count` int not null comment 'Column Count',
    `create_time` datetime(3) comment 'Create Time',
    `update_time` datetime(3) comment 'Update Time',
    unique key `train_code_index_unique` (`train_code`, `index`),
    primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='Train Carriage';

drop table if exists `train_seat`;
create table `train_seat` (
    `id` bigint not null comment 'id',
    `train_code` varchar(20) not null comment 'Train Number',
    `carriage_index` int not null comment 'Carriage Index',
    `row` char(2) not null comment 'Row|01, 02',
    `col` char(1) not null comment 'Column|Enum[SeatColEnum]',
    `seat_type` char(1) not null comment 'Seat Type|Enum[SeatTypeEnum]',
    `carriage_seat_index` int not null comment 'In-carriage Seat Index|1-based',
    `create_time` datetime(3) comment 'Create Time',
    `update_time` datetime(3) comment 'Update Time',
    primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='Seat';

drop table if exists `daily_train`;
create table `daily_train` (
    `id` bigint not null comment 'id',
    `date` date not null comment 'Date',
    `code` varchar(20) not null comment 'Train Number',
    `type` char(1) not null comment 'Train Type|Enum[TrainTypeEnum]',
    `start` varchar(20) not null comment 'Departure Station',
    `start_pinyin` varchar(50) not null comment 'Departure Station Alias',
    `start_time` time not null comment 'Start Time',
    `end` varchar(20) not null comment 'Arrival Station',
    `end_pinyin` varchar(50) not null comment 'Arrival Station Alias',
    `end_time` time not null comment 'End Time',
    `create_time` datetime(3) comment 'Create Time',
    `update_time` datetime(3) comment 'Update Time',
    primary key (`id`),
    unique key `date_code_unique` (`date`, `code`)
) engine=innodb default charset=utf8mb4 comment='Daily Train';

drop table if exists `daily_train_station`;
create table `daily_train_station` (
    `id` bigint not null comment 'id',
    `date` date not null comment 'Date',
    `train_code` varchar(20) not null comment 'Train Number',
    `index` int not null comment 'Station Index|0-based',
    `name` varchar(20) not null comment 'Station',
    `name_pinyin` varchar(50) not null comment 'Station Alias',
    `in_time` time comment 'Arrival Time',
    `out_time` time comment 'Departure Time',
    `stop_time` time comment 'Stop Duration',
    `km` decimal(8, 2) not null comment 'Mileage(km)|Distance from Previous Station',
    `create_time` datetime(3) comment 'Create Time',
    `update_time` datetime(3) comment 'Update Time',
    primary key (`id`),
    unique key `date_train_code_index_unique` (`date`, `train_code`, `index`),
    unique key `date_train_code_name_unique` (`date`, `train_code`, `name`)
) engine=innodb default charset=utf8mb4 comment='Daily Train Station';


drop table if exists `daily_train_carriage`;
create table `daily_train_carriage` (
    `id` bigint not null comment 'id',
    `date` date not null comment 'Date',
    `train_code` varchar(20) not null comment 'Train Number',
    `index` int not null comment 'Carriage Index|1-based',
    `seat_type` char(1) not null comment 'Seat Type|Enum[SeatTypeEnum]',
    `seat_count` int not null comment 'Seat Count',
    `row_count` int not null comment 'Row Count',
    `col_count` int not null comment 'Column Count',
    `create_time` datetime(3) comment 'Create Time',
    `update_time` datetime(3) comment 'Update Time',
    primary key (`id`),
    unique key `date_train_code_index_unique` (`date`, `train_code`, `index`)
) engine=innodb default charset=utf8mb4 comment='Daily Train Carriage';

drop table if exists `daily_train_seat`;
create table `daily_train_seat` (
    `id` bigint not null comment 'id',
    `date` date not null comment 'Date',
    `train_code` varchar(20) not null comment 'Train Number',
    `carriage_index` int not null comment 'Carriage Index',
    `row` char(2) not null comment 'Row|01, 02',
    `col` char(1) not null comment 'Column|Enum[SeatColEnum]',
    `seat_type` char(1) not null comment 'Seat Type|Enum[SeatTypeEnum]',
    `carriage_seat_index` int not null comment 'In-carriage Seat Index|1-based',
    `sell` varchar(50) not null comment 'Sale Status|Represents route stations using 01; 0: for sale, 1: sold',
    `create_time` datetime(3) comment 'Create Time',
    `update_time` datetime(3) comment 'Update Time',
    primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='Daily Train Seat';

drop table if exists `daily_train_ticket`;
create table `daily_train_ticket` (
    `id` bigint not null comment 'id',
    `date` date not null comment 'Date',
    `train_code` varchar(20) not null comment 'Train Number',
    `start` varchar(20) not null comment 'Departure Station',
    `start_pinyin` varchar(50) not null comment 'Departure Station Alias',
    `start_time` time not null comment 'Start Time',
    `start_index` int not null comment 'Departure Station Index|The Nth stop of the entire train route',
    `end` varchar(20) not null comment 'Arrival Station',
    `end_pinyin` varchar(50) not null comment 'Arrival Station Alias',
    `end_time` time not null comment 'End Time',
    `end_index` int not null comment 'Arrival Station Index|The Nth stop of the entire train route',
    `ydz` int not null comment 'First Class Remaining Tickets',
    `ydz_price` decimal(8, 2) not null comment 'First Class Ticket Price',
    `edz` int not null comment 'Second Class Remaining Tickets',
    `edz_price` decimal(8, 2) not null comment 'Second Class Ticket Price',
    `rw` int not null comment 'Soft Sleeper Remaining Tickets',
    `rw_price` decimal(8, 2) not null comment 'Soft Sleeper Ticket Price',
    `yw` int not null comment 'Hard Seat Remaining Tickets',
    `yw_price` decimal(8, 2) not null comment 'Hard Seat Ticket Price',
    `create_time` datetime(3) comment 'Create Time',
    `update_time` datetime(3) comment 'Update Time',
    primary key (`id`),
    unique key `date_train_code_start_end_unique` (`date`, `train_code`, `start`, `end`)
) engine=innodb default charset=utf8mb4 comment='Remaining Tickets Info';

drop table if exists `confirm_order`;
create table `confirm_order` (
    `id` bigint not null comment 'id',
    `member_id` bigint not null comment 'Member Id',
    `date` date not null comment 'Date',
    `train_code` varchar(20) not null comment 'Train Number',
    `start` varchar(20) not null comment 'Departure Station',
    `end` varchar(20) not null comment 'Arrival Station',
    `daily_train_ticket_id` bigint not null comment 'Remaining Ticket Id',
    `tickets` json not null comment 'Ticket',
    `status` char(1) not null comment 'Order Status|Enum[ConfirmOrderStatusEnum]',
    `create_time` datetime(3) comment 'Create Time',
    `update_time` datetime(3) comment 'Update Time',
    primary key (`id`),
    index `date_train_code_index` (`date`, `train_code`)
) engine=innodb default charset=utf8mb4 comment='Confirmed Order';

CREATE TABLE `undo_log` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `branch_id` bigint(20) NOT NULL,
    `xid` varchar(100) NOT NULL,
    `context` varchar(128) NOT NULL,
    `rollback_info` longblob NOT NULL,
    `log_status` int(11) NOT NULL,
    `log_created` datetime NOT NULL,
    `log_modified` datetime NOT NULL,
    `ext` varchar(100) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

drop table if exists `sk_token`;
create table `sk_token` (
    `id` bigint not null comment 'id',
    `date` date not null comment 'Date',
    `train_code` varchar(20) not null comment 'Train Number',
    `count` int not null comment 'Remaining Tokens',
    `create_time` datetime(3) comment 'Create Time',
    `update_time` datetime(3) comment 'Update Time',
    primary key (`id`),
    unique key `date_train_code_unique` (`date`, `train_code`)
) engine=innodb default charset=utf8mb4 comment='Flash Sale Token';
