-- phpMyAdmin SQL Dump
-- version 4.0.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: May 31, 2014 at 10:52 AM
-- Server version: 5.6.12-log
-- PHP Version: 5.4.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `seatunity`
--
CREATE DATABASE IF NOT EXISTS `seatunity` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `seatunity`;

-- --------------------------------------------------------

--
-- Table structure for table `boarding_pass`
--

CREATE TABLE IF NOT EXISTS `boarding_pass` (
  `id` bigint(19) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) unsigned NOT NULL,
  `version` smallint(3) NOT NULL DEFAULT '1',
  `stringform` varchar(250) NOT NULL,
  `firstname` varchar(18) NOT NULL,
  `lastname` varchar(18) NOT NULL,
  `PNR` varchar(7) NOT NULL,
  `travel_from` varchar(3) NOT NULL,
  `travel_to` varchar(3) NOT NULL,
  `carrier` varchar(3) NOT NULL,
  `flight_no` varchar(5) NOT NULL,
  `julian_date` int(3) NOT NULL,
  `compartment_code` varchar(2) NOT NULL,
  `seat` varchar(4) NOT NULL,
  `departure` varchar(5) NOT NULL,
  `arrival` varchar(5) NOT NULL,
  `year` int(4) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_boarding_pass_user_idx` (`user_id`),
  KEY `fk_boarding_pass_compartment_code_to_class1_idx` (`compartment_code`),
  KEY `fk_boarding_pass_carrier_code_to_name1_idx` (`carrier`),
  KEY `fk_boarding_pass_location_code_to_name1_idx` (`travel_from`),
  KEY `fk_boarding_pass_location_code_to_name2_idx` (`travel_to`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=10 ;

--
-- Dumping data for table `boarding_pass`
--

INSERT INTO `boarding_pass` (`id`, `user_id`, `version`, `stringform`, `firstname`, `lastname`, `PNR`, `travel_from`, `travel_to`, `carrier`, `flight_no`, `julian_date`, `compartment_code`, `seat`, `departure`, `arrival`, `year`) VALUES
(1, 28, 1, 'M1HELDT/UWEMR         EYWX9ZS LWOMUCLH 2551 \n                        075M024A0008 355>2180OO3075BOS 022052227001\n                        262202331497901  LH                     *30601001205', 'Uwe', 'Heldt', 'YWX9ZS', 'LWO', 'MUC', 'BA', '2661', 36, 'M', '24A', '20:00', '21:10', 2014),
(2, 28, 1, 'M1HELDT/UWEMR         EYWX9ZS LWOMUCLH 2551 \n                        075M024A0008 355>2180OO3075BOS 022052227001\n                        262202331497901  LH                     *30601001205', 'Uwe', 'Heldt', 'YWX9ZS', 'LWO', 'MUC', 'LH', '2551', 75, 'M', '24A', '20:00', '21:10', 2014),
(3, 18, 1, 'M1HELDT/UWEMR         EYWX9ZS LWOMUCLH 2551 \n                        075M024A0008 355>2180OO3075BOS 022052227001\n                        262202331497901  LH                     *30601001205', 'Uwe', 'Heldt', 'YWX9ZS', 'LWO', 'MUC', 'LH', '2551', 75, 'M', '24A', '20:00', '21:10', 2014),
(4, 18, 1, 'M1HELDT/UWEMR         EYWX9ZS LWOMUCLH 2551 \n                        075M024A0008 355>2180OO3075BOS 022052227001\n                        262202331497901  LH                     *30601001205', 'Uwe', 'Heldt', 'YWX9ZS', 'LWO', 'MUC', 'BA', '2661', 36, 'M', '24A', '20:00', '21:10', 2014),
(5, 19, 1, 'M1HELDT/UWEMR         EYWX9ZS LWOMUCLH 2551 \n                        075M024A0008 355>2180OO3075BOS 022052227001\n                        262202331497901  LH                     *30601001205', 'Uwe', 'Heldt', 'YWX9ZS', 'LWO', 'MUC', 'BA', '2661', 36, 'M', '24A', '20:00', '21:10', 2014),
(6, 19, 1, 'M1HELDT/UWEMR         EYWX9ZS LWOMUCLH 2551 \n                        075M024A0008 355>2180OO3075BOS 022052227001\n                        262202331497901  LH                     *30601001205', 'Uwe', 'Heldt', 'YWX9ZS', 'LWO', 'MUC', 'LH', '2661', 75, 'M', '24A', '20:00', '21:10', 2014),
(7, 20, 1, 'M1HELDT/UWEMR         EYWX9ZS LWOMUCLH 2551 \n                        075M024A0008 355>2180OO3075BOS 022052227001\n                        262202331497901  LH                     *30601001205', 'Uwe', 'Heldt', 'YWX9ZS', 'LWO', 'MUC', 'LH', '2661', 75, 'M', '24A', '20:00', '21:10', 2014),
(8, 20, 1, 'M1HELDT/UWEMR         EYWX9ZS LWOMUCLH 2551 \n                        075M024A0008 355>2180OO3075BOS 022052227001\n                        262202331497901  LH                     *30601001205', 'Uwe', 'Heldt', 'YWX9ZS', 'LWO', 'MUC', 'LH', '2551', 75, 'M', '24A', '20:00', '21:10', 2014),
(9, 20, 1, 'M1HELDT/UWEMR         EYWX9ZS LWOMUCLH 2551 \n                        075M024A0008 355>2180OO3075BOS 022052227001\n                        262202331497901  LH                     *30601001205', 'Uwe', 'Heldt', 'YWX9ZS', 'LWO', 'MUC', 'BA', '2661', 36, 'M', '24A', '20:00', '21:10', 2014);

-- --------------------------------------------------------

--
-- Table structure for table `carrier`
--

CREATE TABLE IF NOT EXISTS `carrier` (
  `code` varchar(3) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`code`),
  UNIQUE KEY `code_UNIQUE` (`code`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `carrier`
--

INSERT INTO `carrier` (`code`, `name`) VALUES
('BA', 'British Airways'),
('LH', 'Lufthansa');

-- --------------------------------------------------------

--
-- Table structure for table `location_airport`
--

CREATE TABLE IF NOT EXISTS `location_airport` (
  `code` varchar(3) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`code`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  UNIQUE KEY `code_UNIQUE` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `location_airport`
--

INSERT INTO `location_airport` (`code`, `name`) VALUES
('LWO', 'Lviv'),
('MAN', 'Manchester'),
('MUC', 'Munich'),
('JFK', 'New York'),
('CDG', 'Paris');

-- --------------------------------------------------------

--
-- Table structure for table `travel_class`
--

CREATE TABLE IF NOT EXISTS `travel_class` (
  `compartment_code` varchar(2) NOT NULL,
  `class` varchar(15) NOT NULL,
  PRIMARY KEY (`compartment_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `travel_class`
--

INSERT INTO `travel_class` (`compartment_code`, `class`) VALUES
('A', 'First Class'),
('B', 'Economy Class'),
('C', 'Business Class'),
('D', 'Business Class'),
('F', 'First Class'),
('H', 'Economy Class'),
('J', 'Business Class'),
('K', 'Economy Class'),
('L', 'Economy Class'),
('M', 'Economy Class'),
('N', 'Economy Class'),
('O', 'Economy Class'),
('P', 'First Class'),
('Q', 'Economy Class'),
('S', 'Economy Class'),
('T', 'Economy Class'),
('V', 'Economy Class'),
('W', 'Premium Economy'),
('Y', 'Economy Class');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(250) NOT NULL,
  `password` varchar(32) NOT NULL,
  `language` varchar(5) NOT NULL,
  `is_pass_provisional` varchar(10) DEFAULT '0',
  `is_reg_confirmed` varchar(10) DEFAULT '0',
  `token` varchar(40) NOT NULL DEFAULT '',
  `firstname` varchar(45) DEFAULT NULL,
  `lastname` varchar(45) DEFAULT NULL,
  `gender` enum('male','female','unspecified') DEFAULT NULL,
  `live_in` varchar(45) DEFAULT NULL,
  `age` enum('< 15','15 - 20','20 - 25','25 - 30','30 - 35','35 - 40','40 - 45','45 - 50','50 - 55','55 - 60','60 - 65','65 - 70','70 - 75','75 - 80','80 - 85','85 - 90','90 - 95','95 - 100','100 >') DEFAULT NULL,
  `profession` varchar(45) DEFAULT NULL,
  `seating_pref` enum('relax','work','small_talk','business_talk','dont_care') DEFAULT NULL,
  `some_about_you` varchar(200) DEFAULT NULL,
  `status` varchar(200) DEFAULT NULL,
  `image_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=29 ;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `email`, `password`, `language`, `is_pass_provisional`, `is_reg_confirmed`, `token`, `firstname`, `lastname`, `gender`, `live_in`, `age`, `profession`, `seating_pref`, `some_about_you`, `status`, `image_name`) VALUES
(18, 'mustansir.m.rahman@gmail.com', 'c489de452bca9ca5337f06eb60ed2e4d', 'en', '0', '1', '', 'Jane', 'Shepherd', 'unspecified', 'Normandy', '100 >', 'Citadel Council Spectre', 'small_talk', NULL, NULL, '18_dan-dick-dick.jpg'),
(19, 'mustansirmr@yahoo.com', 'c489de452bca9ca5337f06eb60ed2e4d', 'en', '0', '1', '', 'Jane', 'Shepherd', 'unspecified', 'Normandy', '100 >', 'Citadel Council Spectre', 'small_talk', NULL, NULL, '19_dan-dick-dick.jpg'),
(20, 'sumoncse085@yahoo.com', 'c489de452bca9ca5337f06eb60ed2e4d', 'en', '0', '1', '2b883fa705ec9b5041593ad8592fccc6b87730ec', 'Jane', 'Shepherd', 'unspecified', 'Normandy', '100 >', 'Citadel Council Spectre', 'small_talk', NULL, NULL, '20_dan-dick-dick.jpg'),
(28, 'mustansirmr@gmail.com', 'c489de452bca9ca5337f06eb60ed2e4d', 'en', '0', '1', '', 'Jane', 'Shepherd', 'unspecified', 'Normandy', '100 >', 'Citadel Council Spectre', 'small_talk', NULL, NULL, '28_dan-dick-dick.jpg');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `boarding_pass`
--
ALTER TABLE `boarding_pass`
  ADD CONSTRAINT `fk_carrier__carrier__code_many` FOREIGN KEY (`carrier`) REFERENCES `carrier` (`code`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_compartment_code__travel_class__compartment_code_many` FOREIGN KEY (`compartment_code`) REFERENCES `travel_class` (`compartment_code`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_from__location_ariport__code_many` FOREIGN KEY (`travel_from`) REFERENCES `location_airport` (`code`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_to__location_ariport__code_many` FOREIGN KEY (`travel_to`) REFERENCES `location_airport` (`code`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_user_id__user__id_many` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
