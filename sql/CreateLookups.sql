CREATE TABLE `ds`.`webprovider` (
  `webproviderid` INT NOT NULL,
  `description` VARCHAR(255) NULL,
  `name` VARCHAR(255) NULL,
  `definition` VARCHAR(255) NULL,
  `notes` VARCHAR(1000) NULL,
  PRIMARY KEY (`webproviderid`));

CREATE TABLE `ds`.`scheduler` (
  `schedulerid` INT NOT NULL,
  `description` VARCHAR(255) NULL,
  `name` VARCHAR(255) NULL,
  `definition` VARCHAR(255) NULL,
  `notes` VARCHAR(1000) NULL,
  PRIMARY KEY (`schedulerid`));

CREATE TABLE `ds`.`generalmatch` (
  `generalmatchid` INT NOT NULL,
  `description` VARCHAR(255) NULL,
  `name` VARCHAR(255) NULL,
  `definition` VARCHAR(255) NULL,
  `notes` VARCHAR(1000) NULL,
  PRIMARY KEY (`generalmatchid`));

CREATE TABLE `ds`.`urlextraction` (
  `urlextractionid` INT NOT NULL,
  `description` VARCHAR(255) NULL,
  `name` VARCHAR(255) NULL,
  `definition` VARCHAR(255) NULL,
  `notes` VARCHAR(1000) NULL,
  PRIMARY KEY (`urlextractionid`));

CREATE TABLE `ds`.`stringextraction` (
  `stringextractionid` INT NOT NULL,
  `description` VARCHAR(255) NULL,
  `name` VARCHAR(255) NULL,
  `definition` VARCHAR(255) NULL,
  `notes` VARCHAR(1000) NULL,
  PRIMARY KEY (`stringextractionid`));
