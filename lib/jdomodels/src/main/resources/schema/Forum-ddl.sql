CREATE TABLE IF NOT EXISTS `FORUM` (
  `ID` bigint(20) NOT NULL,
  `PROJECT_ID` bigint(20) NOT NULL,
  `ETAG` char(36) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE (`PROJECT_ID`),
  CONSTRAINT `FORUM_PROJECT_ID_FK` FOREIGN KEY (`PROJECT_ID`) REFERENCES `JDONODE` (`ID`) ON DELETE CASCADE
)
