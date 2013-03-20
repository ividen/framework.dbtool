CREATE  TABLE `dbtool`.`test_table` (
  `xkey` INT NOT NULL ,
  `name` VARCHAR(255) NULL ,
  `version` INT NULL ,
  PRIMARY KEY (`xkey`) );


  CREATE  TABLE `dbtool`.`test_table1` (
  `bool` BIT NULL ,
  `int` INT NULL ,
  `bigint` BIGINT NULL ,
  `string` VARCHAR(256) NULL ,
  `ts1` TIMESTAMP NULL ,
  `ts2` TIMESTAMP NULL ,
  `blob` BLOB NULL ,
  `bigdecimal` DECIMAL(50) NULL );