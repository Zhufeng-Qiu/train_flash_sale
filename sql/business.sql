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
