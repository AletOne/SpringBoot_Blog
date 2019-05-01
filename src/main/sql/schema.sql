CREATE DATABASE spring_blog;
use spring_blog;
CREATE TABLE `user` (
  `id` INT(11) unsigned not null auto_increment,
  `name` varchar(64) not null default '',
  `password` varchar(128) not null default '',
  `salt` varchar(32) not null default '',
  `head_url` varchar(256) not null default '',
  `role` varchar(32) not null default 'user',
  primary key (`id`),
  unique key `name` (`name`)
) ENGINE=InnoDB DEFAULT charset=utf8;

create table `login_ticket` (
  `id` int not null auto_increment primary key,
  `user_id` int not null,
  `ticket` varchar(45) not null,
  `expired` datetime not null,
  `status` int null default 0,
  unique index `ticket_UNIQUE` (`ticket` asc)
) ENGINE=InnoDB DEFAULT charset=utf8;

CREATE TABLE `article` (
  `id` int not null auto_increment primary key,
  `title` varchar(255) not null,
  `description` varchar(255) not null,
  `content` text null,
  `create_date` datetime not null,
  `comment_count` int not null,
  `category` varchar(32) not null
) ENGINE=InnoDB DEFAULT charset=utf8;

CREATE TABLE `tag` (
  `id` int not null auto_increment primary key,
  `name` varchar(255) not null,
  `count` int
) ENGINE=InnoDB DEFAULT charset=utf8;

CREATE TABLE `article_tag` (
  `id` int not null auto_increment primary key,
  `article_id` int not null,
  `tag_id` int not null,
  FOREIGN KEY (article_id) references article(id),
  FOREIGN KEY (tag_id) references tag(id)
) ENGINE=InnoDB DEFAULT charset=utf8;