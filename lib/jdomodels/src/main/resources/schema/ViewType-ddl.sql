CREATE TABLE IF NOT EXISTS `VIEW_TYPE` (
  `VIEW_ID` bigint(20) NOT NULL,
  `VIEW_TYPE` ENUM('file','project','file_and_table') NOT NULL,
  `ETAG` char(36) NOT NULL,
  PRIMARY KEY (`VIEW_ID`)
)