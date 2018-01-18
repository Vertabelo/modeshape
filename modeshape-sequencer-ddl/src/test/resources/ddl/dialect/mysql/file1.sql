-- Adminer 3.7.1 MySQL dump

SET NAMES utf8;
SET foreign_key_checks = 0;
SET time_zone = '+05:30';
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

DROP TABLE IF EXISTS `skills`;
CREATE TABLE `skills` (
  `skills_id` int(11) NOT NULL AUTO_INCREMENT,
  `skills_category` enum('0','1') NOT NULL DEFAULT '0' COMMENT '0=IT Skills, 1=Business Skills',
  `skills_title` varchar(255) NOT NULL,
  `skills_status` enum('0','1') NOT NULL DEFAULT '1' COMMENT '0=Inactive,1=Active',
  `skills_added` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`skills_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `skills` (`skills_id`, `skills_category`, `skills_title`, `skills_status`, `skills_added`) VALUES
(1,	'0',	'Application Development',	'1',	'2014-01-17 10:30:59'),
(2,	'0',	'Business Analytics',	'1',	'2014-01-17 10:31:04'),
(3,	'0',	'Business Intelligence',	'1',	'2014-01-17 10:36:08'),
(4,	'0',	'Business Process Modelling',	'1',	'2014-01-17 10:36:15'),
(5,	'0',	'Cloud',	'1',	'2014-01-17 10:36:20'),
(6,	'0',	'Data Analysis',	'1',	'2014-01-17 10:36:26'),
(7,	'0',	'Data Analytics',	'1',	'2014-01-17 10:36:36'),
(8,	'0',	'Data Intelligence',	'1',	'2014-01-17 10:36:43'),
(9,	'0',	'Data Mining',	'1',	'2014-01-17 10:36:48'),
(10,	'0',	'Data Science',	'1',	'2014-01-17 10:37:58'),
(11,	'0',	'Database Development',	'1',	'2014-01-17 10:38:05'),
(12,	'0',	'Defect Management',	'1',	'2014-01-17 10:38:10'),
(13,	'0',	'E-Commerce',	'1',	'2014-01-17 10:38:15'),
(14,	'0',	'Help Desk',	'1',	'2014-01-17 10:38:20'),
(15,	'0',	'Hardware',	'1',	'2014-01-17 10:38:26'),
(16,	'0',	'IT Optimization',	'1',	'2014-01-17 10:38:31'),
(17,	'0',	'IT Security',	'1',	'2014-01-17 10:38:36'),
(18,	'0',	'Linux',	'1',	'2014-01-17 10:38:42'),
(19,	'0',	'Mobile Applications',	'1',	'2014-01-17 10:38:48'),
(20,	'0',	'Microsoft Office Suite',	'1',	'2014-01-17 10:38:54'),
(21,	'0',	'Networking',	'1',	'2014-01-17 10:38:58'),
(22,	'0',	'Product Development',	'1',	'2014-01-17 10:39:04'),
(23,	'0',	'Software Development',	'1',	'2014-01-17 10:39:11'),
(24,	'0',	'System Testing',	'1',	'2014-01-17 10:39:17'),
(25,	'0',	'Test Management',	'1',	'2014-01-17 10:39:22'),
(26,	'0',	'Technical Services',	'1',	'2014-01-17 10:39:29'),
(27,	'0',	'Technical Support',	'1',	'2014-01-17 10:39:34'),
(28,	'0',	'Technical Writing',	'1',	'2014-01-17 10:39:41'),
(29,	'0',	'Training',	'1',	'2014-01-17 10:39:46'),
(30,	'0',	'Troubleshooting',	'1',	'2014-01-17 10:39:53'),
(31,	'0',	'UNIX',	'1',	'2014-01-17 10:39:58'),
(32,	'0',	'User Experience Design',	'1',	'2014-01-17 10:40:03'),
(33,	'0',	'Web Design',	'1',	'2014-01-17 10:40:08'),
(34,	'0',	'Web Development',	'1',	'2014-01-17 10:40:14'),
(35,	'1',	'Business Analysis',	'1',	'2014-01-17 10:26:44'),
(36,	'1',	'Communication',	'1',	'2014-01-17 10:26:51'),
(37,	'1',	'Consulting',	'1',	'2014-01-17 10:27:05'),
(38,	'1',	'Customer Service',	'1',	'2014-01-17 10:27:15'),
(39,	'1',	'Decision Making',	'1',	'2014-01-17 10:27:21'),
(40,	'1',	'Flexibility',	'1',	'2014-01-17 10:27:28'),
(41,	'1',	'Facilitation Skills',	'1',	'2014-01-17 10:27:36'),
(42,	'1',	'Leadership',	'1',	'2014-01-17 10:27:42'),
(43,	'1',	'Negotiation',	'1',	'2014-01-17 10:27:48'),
(44,	'1',	'Numeracy',	'1',	'2014-01-17 10:27:53'),
(45,	'1',	'Project Management',	'1',	'2014-01-17 10:28:01'),
(46,	'1',	'Problem Solving',	'1',	'2014-01-17 10:28:07'),
(47,	'1',	'Strategy Analysis and Management',	'1',	'2014-01-17 10:28:14'),
(48,	'1',	'Soft Skills',	'1',	'2014-01-17 10:28:21'),
(49,	'1',	'Self-Management',	'1',	'2014-01-17 10:28:27'),
(50,	'1',	'Teamwork',	'1',	'2014-01-17 10:28:34'),
(51,	'1',	'Team Building',	'1',	'2014-01-17 10:28:41'),
(52,	'1',	'Time Management',	'1',	'2014-01-17 10:28:47'),
(53,	'1',	'Writing Skills',	'1',	'2014-01-17 10:28:55');

-- 2014-06-18 15:55:39
