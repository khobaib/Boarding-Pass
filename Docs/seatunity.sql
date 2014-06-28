-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jun 28, 2014 at 02:36 PM
-- Server version: 5.6.17
-- PHP Version: 5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `seatunity`
--

-- --------------------------------------------------------

--
-- Table structure for table `boarding_pass`
--

CREATE TABLE IF NOT EXISTS `boarding_pass` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

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

-- --------------------------------------------------------

--
-- Table structure for table `travel_class`
--

CREATE TABLE IF NOT EXISTS `travel_class` (
  `compartment_code` varchar(2) NOT NULL,
  `class` varchar(15) NOT NULL,
  PRIMARY KEY (`compartment_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  `age` varchar(20) DEFAULT NULL,
  `profession` varchar(45) DEFAULT NULL,
  `seating_pref` enum('relax','work','small_talk','business_talk','dont_care') DEFAULT NULL,
  `some_about_you` varchar(200) DEFAULT NULL,
  `status` varchar(200) DEFAULT NULL,
  `image_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

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
  ADD CONSTRAINT `fk_user_id__user__id_many` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
