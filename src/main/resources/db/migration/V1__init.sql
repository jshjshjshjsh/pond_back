-- pond.`member` definition
CREATE TABLE `member` (
                          `id` varchar(255) NOT NULL,
                          `name` varchar(255) NOT NULL,
                          `pw` varchar(255) NOT NULL,
                          `sabun` varchar(255) NOT NULL,
                          `role` enum('ROLE_ADMIN','ROLE_LEADER','ROLE_NORMAL') NOT NULL,
                          PRIMARY KEY (`sabun`),
                          UNIQUE KEY `UKjp8ds32yg1soswx2rkiagm768` (`id`)
);

-- pond.team definition
CREATE TABLE `team` (
                        `create_time` datetime(6) DEFAULT NULL,
                        `id` bigint NOT NULL,
                        `team_name` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `UKsob22siqdnn2rfsxk6f00pgwb` (`team_name`)
);

-- pond.team_seq definition
CREATE TABLE `team_seq` (
                            `next_val` bigint DEFAULT NULL
);

-- pond.member_team definition
CREATE TABLE `member_team` (
                               `team_id` bigint NOT NULL,
                               `member_sabun` varchar(255) NOT NULL,
                               `member` varchar(255) NOT NULL,
                               `team` bigint NOT NULL,
                               PRIMARY KEY (`team_id`,`member_sabun`),
                               KEY `FKglhhnmnk3v79a0gmb8x3vnwen` (`member_sabun`),
                               CONSTRAINT `FKfly863tmgmm8wnj0u1sqgqu5u` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`),
                               CONSTRAINT `FKglhhnmnk3v79a0gmb8x3vnwen` FOREIGN KEY (`member_sabun`) REFERENCES `member` (`sabun`)
);

-- pond.member_team_seq definition
CREATE TABLE `member_team_seq` (
                                   `next_val` bigint DEFAULT NULL
);

-- -------------------------------------------------------------------------------------------------

-- pond.work_history definition
CREATE TABLE `work_history` (
                                `end_date` datetime(6) NOT NULL,
                                `id` bigint NOT NULL,
                                `start_date` datetime(6) NOT NULL,
                                `team_team_code` bigint DEFAULT NULL,
                                `content` varchar(255) DEFAULT NULL,
                                `member_sabun` varchar(255) DEFAULT NULL,
                                `title` varchar(255) NOT NULL,
                                `is_share` bit(1) DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                KEY `FK2s01o7hlwpp7u35cuj5epnlgd` (`member_sabun`),
                                KEY `FK1ddkaxjv5988a2bpdbe25ybt6` (`team_team_code`),
                                CONSTRAINT `FK1ddkaxjv5988a2bpdbe25ybt6` FOREIGN KEY (`team_team_code`) REFERENCES `team` (`id`),
                                CONSTRAINT `FK2s01o7hlwpp7u35cuj5epnlgd` FOREIGN KEY (`member_sabun`) REFERENCES `member` (`sabun`)
);


-- pond.work_history_seq definition
CREATE TABLE `work_history_seq` (
                                    `next_val` bigint DEFAULT NULL
);


-- pond.work_summary definition
CREATE TABLE `work_summary` (
                                `id` bigint NOT NULL,
                                `is_share` bit(1) DEFAULT NULL,
                                `month` int NOT NULL,
                                `summary` longtext,
                                `year` int NOT NULL,
                                `member_sabun` varchar(255) DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                KEY `FKcdfuclg382ddr9wym8uc26o5t` (`member_sabun`),
                                CONSTRAINT `FKcdfuclg382ddr9wym8uc26o5t` FOREIGN KEY (`member_sabun`) REFERENCES `member` (`sabun`)
);

-- pond.work_summary_seq definition
CREATE TABLE `work_summary_seq` (
                                    `next_val` bigint DEFAULT NULL
);

-- -------------------------------------------------------------------------------------------------

-- pond.mileage definition
CREATE TABLE `mileage` (
                           `amount` bigint DEFAULT '0',
                           `id` bigint NOT NULL,
                           `member_id` varchar(255) DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `UKovcw2d0005wc9pbatuoeuclnc` (`member_id`),
                           CONSTRAINT `FKgf0chvwpkrukput10x9sqpopj` FOREIGN KEY (`member_id`) REFERENCES `member` (`sabun`)
);

-- pond.mileage_seq definition
CREATE TABLE `mileage_seq` (
                               `next_val` bigint DEFAULT NULL
);

-- pond.`point` definition
CREATE TABLE `point` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `amount` bigint NOT NULL DEFAULT '0',
                         `member_id` varchar(255) NOT NULL,
                         `version` bigint DEFAULT NULL,
                         PRIMARY KEY (`id`)
);

-- -------------------------------------------------------------------------------------------------
