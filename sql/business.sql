drop table if exists `station`;
create table `station` (
   `id` bigint not null comment 'id',
   `name` varchar(20) not null comment 'Station',
   `name_pinyin` varchar(50) not null comment 'Station(Spell)',
   `name_py` varchar(50) not null comment 'Station(Initial)',
   `create_time` datetime(3) comment 'Create Time',
   `update_time` datetime(3) comment 'Update Time',
   primary key (`id`),
   unique key `name_unique` (`name`)
) engine=innodb default charset=utf8mb4 comment='Station';
