-- MySQL dump 10.13  Distrib 5.1.48, for Win64 (unknown)
--
-- Host: localhost    Database: algotrader
-- ------------------------------------------------------
-- Server version    5.1.48-community-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `cash_balance`
--

LOCK TABLES `cash_balance` WRITE;
/*!40000 ALTER TABLE `cash_balance` DISABLE KEYS */;
INSERT INTO `cash_balance` (`ID`, `CURRENCY`, `AMOUNT`, `STRATEGY_FK`) VALUES (1,'EUR','1000000.00',0);
/*!40000 ALTER TABLE `cash_balance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `equity_index`
--

LOCK TABLES `equity_index` WRITE;
/*!40000 ALTER TABLE `equity_index` DISABLE KEYS */;
INSERT INTO `equity_index` (`ID`) VALUES (2);
INSERT INTO `equity_index` (`ID`) VALUES (3);
INSERT INTO `equity_index` (`ID`) VALUES (4);
INSERT INTO `equity_index` (`ID`) VALUES (5);
INSERT INTO `equity_index` (`ID`) VALUES (12);
INSERT INTO `equity_index` (`ID`) VALUES (29);
/*!40000 ALTER TABLE `equity_index` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `forex`
--

LOCK TABLES `forex` WRITE;
/*!40000 ALTER TABLE `forex` DISABLE KEYS */;
INSERT INTO `forex` (`ID`, `BASE_CURRENCY`) VALUES (8,'EUR');
INSERT INTO `forex` (`ID`, `BASE_CURRENCY`) VALUES (9,'USD');
INSERT INTO `forex` (`ID`, `BASE_CURRENCY`) VALUES (10,'EUR');
INSERT INTO `forex` (`ID`, `BASE_CURRENCY`) VALUES (11,'GBP');
/*!40000 ALTER TABLE `forex` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `future`
--

LOCK TABLES `future` WRITE;
/*!40000 ALTER TABLE `future` DISABLE KEYS */;
/*!40000 ALTER TABLE `future` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `future_family`
--

LOCK TABLES `future_family` WRITE;
/*!40000 ALTER TABLE `future_family` DISABLE KEYS */;
INSERT INTO `future_family` (`ID`, `LENGTH`, `INTREST`, `DIVIDEND`, `MARGIN_PARAMETER`, `EXPIRATION_TYPE`, `EXPIRATION_MONTHS`) VALUES (10,3,0.0015000000,0.0300000000,490.9000000000,'NEXT_3_RD_FRIDAY_3_MONTHS',3);
INSERT INTO `future_family` (`ID`, `LENGTH`, `INTREST`, `DIVIDEND`, `MARGIN_PARAMETER`, `EXPIRATION_TYPE`, `EXPIRATION_MONTHS`) VALUES (22,8,0.0015000000,0.0300000000,490.9000000000,'THIRTY_DAYS_BEFORE_NEXT_3_RD_FRIDAY',1);
/*!40000 ALTER TABLE `future_family` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `generic_future`
--

LOCK TABLES `generic_future` WRITE;
/*!40000 ALTER TABLE `generic_future` DISABLE KEYS */;
INSERT INTO `generic_future` (`ID`, `DURATION`) VALUES (13,1);
INSERT INTO `generic_future` (`ID`, `DURATION`) VALUES (14,1);
INSERT INTO `generic_future` (`ID`, `DURATION`) VALUES (15,1);
INSERT INTO `generic_future` (`ID`, `DURATION`) VALUES (21,1);
INSERT INTO `generic_future` (`ID`, `DURATION`) VALUES (22,2);
INSERT INTO `generic_future` (`ID`, `DURATION`) VALUES (23,3);
INSERT INTO `generic_future` (`ID`, `DURATION`) VALUES (24,4);
INSERT INTO `generic_future` (`ID`, `DURATION`) VALUES (25,5);
INSERT INTO `generic_future` (`ID`, `DURATION`) VALUES (26,6);
INSERT INTO `generic_future` (`ID`, `DURATION`) VALUES (27,7);
INSERT INTO `generic_future` (`ID`, `DURATION`) VALUES (28,8);
/*!40000 ALTER TABLE `generic_future` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `generic_future_family`
--

LOCK TABLES `generic_future_family` WRITE;
/*!40000 ALTER TABLE `generic_future_family` DISABLE KEYS */;
INSERT INTO `generic_future_family` (`ID`, `EXPIRATION_TYPE`) VALUES (12,'NEXT_3_RD_FRIDAY');
INSERT INTO `generic_future_family` (`ID`, `EXPIRATION_TYPE`) VALUES (13,'NEXT_3_RD_FRIDAY');
INSERT INTO `generic_future_family` (`ID`, `EXPIRATION_TYPE`) VALUES (14,'NEXT_3_RD_FRIDAY');
INSERT INTO `generic_future_family` (`ID`, `EXPIRATION_TYPE`) VALUES (21,'THIRTY_DAYS_BEFORE_NEXT_3_RD_FRIDAY');
/*!40000 ALTER TABLE `generic_future_family` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `intrest_rate`
--

LOCK TABLES `intrest_rate` WRITE;
/*!40000 ALTER TABLE `intrest_rate` DISABLE KEYS */;
INSERT INTO `intrest_rate` (`ID`, `DURATION`) VALUES (6,604800000);
INSERT INTO `intrest_rate` (`ID`, `DURATION`) VALUES (7,2592000000);
/*!40000 ALTER TABLE `intrest_rate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `security`
--

LOCK TABLES `security` WRITE;
/*!40000 ALTER TABLE `security` DISABLE KEYS */;
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (2,'DE0009652396','ESTX50',NULL,3,4,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (3,'DE000A0C3QF1','V2TX',2,NULL,5,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (4,'CH0008616382','SMI',NULL,5,1,7);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (5,'CH0019900841','V3X',4,NULL,2,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (6,'CH0007475228','CHFDOMDEP1W',NULL,NULL,7,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (7,'CH0007473702','CHFDOMDEP1M',NULL,NULL,7,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (8,'EU0009654078','EUR.CHF',NULL,NULL,8,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (9,'XC0009652816','USD.CHF',NULL,NULL,8,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (10,'EU0009652759','EUR.USD',NULL,NULL,9,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (11,'GB0031973075','GBP.USD',NULL,NULL,9,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (12,'US78378X1072','SPX',NULL,29,11,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (13,'GFES1M','GFES1M',NULL,NULL,12,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (14,'GFLH1M','GFLH1M',NULL,NULL,13,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (15,'GFNG1M','GFNG1M',NULL,NULL,14,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (21,'UX1','UX1',29,NULL,21,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (22,'UX2','UX2',29,NULL,21,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (23,'UX3','UX3',29,NULL,21,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (24,'UX4','UX4',29,NULL,21,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (25,'UX5','UX5',29,NULL,21,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (26,'UX6','UX6',29,NULL,21,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (27,'UX7','UX7',29,NULL,21,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (28,'UX8','UX8',29,NULL,21,NULL);
INSERT INTO `security` (`id`, `ISIN`, `SYMBOL`, `UNDERLAYING_FK`, `VOLATILITY_FK`, `SECURITY_FAMILY_FK`, `INTREST_RATE_FAMILY_FK`) VALUES (29,'XD0016891059','VIX',12,NULL,20,NULL);
/*!40000 ALTER TABLE `security` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `security_family`
--

LOCK TABLES `security_family` WRITE;
/*!40000 ALTER TABLE `security_family` DISABLE KEYS */;
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (1,'SMI','SOFFEX','CHF',1,0.1,'0.00000','09:00:00','17:20:00','\0','\0',NULL,NULL,NULL,NULL,'MINUTE',NULL);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (2,'VSMI','DTB','CHF',1,0.01,NULL,'09:00:00','17:20:00','\0','\0',NULL,NULL,NULL,NULL,'MINUTE',4);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (3,'OSMI','SOFFEX','CHF',10,0.1,'2.80000','09:00:00','17:20:00','','',0.033,11.7,0.037,25,'MINUTE',4);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (4,'ESTX50','DTB','EUR',1,0.01,NULL,'09:00:00','17:30:00','\0','\0',NULL,NULL,NULL,NULL,'MINUTE',NULL);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (5,'V2TX','DTB','EUR',1,0.01,NULL,'09:00:00','17:30:00','\0','\0',NULL,NULL,NULL,NULL,'MINUTE',2);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (6,'OESX','DTB','EUR',10,0.1,'2.80000','09:00:00','17:30:00','','',0.0133,6.7265,0.015,20,'MINUTE',2);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (7,'DEPOSIT RATE CHF','IDEALPRO','CHF',1,0.01,NULL,'09:00:00','17:30:00','\0','\0',NULL,NULL,NULL,NULL,'DAY',NULL);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (8,'CHF FOREX','IDEALPRO','CHF',1,5e-005,'0.00002','09:00:00','18:00:00','','\0',0,0.0001,0,0.0004,'HOUR',NULL);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (9,'USD FOREX','IDEALPRO','USD',1,5e-005,'0.00002','09:00:00','18:00:00','','\0',0,0.0001,0,0.0004,'HOUR',NULL);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (10,'FSMI','SOFFEX','CHF',10,1,'4.00000','07:50:00','22:00:00','','',0.0003,0,0.037,25,'MINUTE',4);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (11,'SPX','SMART','USD',1,0.01,'0.00000','15:30:00','22:00:00','','\0',NULL,NULL,NULL,NULL,'MINUTE',NULL);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (12,'GFES','GLOBEX','USD',1,0.01,'0.00000','08:00:00','18:00:00','\0','\0',NULL,NULL,NULL,NULL,'HOUR',NULL);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (13,'GFLH','CME','USD',1,0.01,'0.00000','08:00:00','18:00:00','\0','\0',NULL,NULL,NULL,NULL,'HOUR',NULL);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (14,'GFNG','NYMEX','USD',1,0.01,'0.00000','08:00:00','18:00:00','\0','\0',NULL,NULL,NULL,NULL,'HOUR',NULL);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (20,'VIX','CBOE','USD',1,0.01,NULL,'15:30:00','22:00:00','\0','\0',NULL,NULL,NULL,NULL,'MINUTE',12);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (21,'UX','CFE','USD',1000,0.05,NULL,'14:20:00','22:15:00','\0','\0',NULL,NULL,NULL,NULL,'MINUTE',29);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (22,'FVIX','CFE','USD',1000,0.05,'1.87000','14:20:00','22:15:00','','',0,75,0,200,'MINUTE',29);
INSERT INTO `security_family` (`id`, `NAME`, `MARKET`, `CURRENCY`, `CONTRACT_SIZE`, `TICK_SIZE`, `COMMISSION`, `MARKET_OPEN`, `MARKET_CLOSE`, `TRADEABLE`, `SIMULATABLE`, `SPREAD_SLOPE`, `SPREAD_CONSTANT`, `MAX_SPREAD_SLOPE`, `MAX_SPREAD_CONSTANT`, `PERIODICITY`, `UNDERLAYING_FK`) VALUES (23,'OSPX','CBOE','USD',100,0.1,'2.80000','15:30:00','22:15:00','','',0.013,6.726,0.015,20,'MINUTE',12);
/*!40000 ALTER TABLE `security_family` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `stock`
--

LOCK TABLES `stock` WRITE;
/*!40000 ALTER TABLE `stock` DISABLE KEYS */;
/*!40000 ALTER TABLE `stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `stock_option`
--

LOCK TABLES `stock_option` WRITE;
/*!40000 ALTER TABLE `stock_option` DISABLE KEYS */;
/*!40000 ALTER TABLE `stock_option` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `stock_option_family`
--

LOCK TABLES `stock_option_family` WRITE;
/*!40000 ALTER TABLE `stock_option_family` DISABLE KEYS */;
INSERT INTO `stock_option_family` (`ID`, `STRIKE_DISTANCE`, `INTREST`, `DIVIDEND`, `MARGIN_PARAMETER`, `EXPIRATION_TYPE`) VALUES (3,50.0000000000,0.0015000000,0.0300000000,0.0750000000,'NEXT_3_RD_FRIDAY');
INSERT INTO `stock_option_family` (`ID`, `STRIKE_DISTANCE`, `INTREST`, `DIVIDEND`, `MARGIN_PARAMETER`, `EXPIRATION_TYPE`) VALUES (6,50.0000000000,0.0060000000,0.0300000000,0.0750000000,'NEXT_3_RD_FRIDAY');
INSERT INTO `stock_option_family` (`ID`, `STRIKE_DISTANCE`, `INTREST`, `DIVIDEND`, `MARGIN_PARAMETER`, `EXPIRATION_TYPE`) VALUES (23,5.0000000000,0.0090000000,0.0200000000,0.0750000000,'NEXT_3_RD_FRIDAY');
/*!40000 ALTER TABLE `stock_option_family` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `strategy`
--

LOCK TABLES `strategy` WRITE;
/*!40000 ALTER TABLE `strategy` DISABLE KEYS */;
INSERT INTO `strategy` (`id`, `NAME`, `FAMILY`, `AUTO_ACTIVATE`, `ALLOCATION`, `MODULES`) VALUES (0,'BASE','BASE','',0.000,'base');
INSERT INTO `strategy` (`id`, `NAME`, `FAMILY`, `AUTO_ACTIVATE`, `ALLOCATION`, `MODULES`) VALUES (1,'SMI','THETA','\0',0.000,'theta-init,theta-main');
INSERT INTO `strategy` (`id`, `NAME`, `FAMILY`, `AUTO_ACTIVATE`, `ALLOCATION`, `MODULES`) VALUES (2,'ESTX50','THETA','\0',0.000,'theta-init,theta-main');
INSERT INTO `strategy` (`id`, `NAME`, `FAMILY`, `AUTO_ACTIVATE`, `ALLOCATION`, `MODULES`) VALUES (3,'MULTIIND','MULTIIND','\0',0.000,'multiind-movavcross,multiind-main');
INSERT INTO `strategy` (`id`, `NAME`, `FAMILY`, `AUTO_ACTIVATE`, `ALLOCATION`, `MODULES`) VALUES (4,'EASTWEST','EASTWEST','',1.000,'volcarry-init,volcarry-main');
/*!40000 ALTER TABLE `strategy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `watch_list_item`
--

LOCK TABLES `watch_list_item` WRITE;
/*!40000 ALTER TABLE `watch_list_item` DISABLE KEYS */;
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (1,'',2,2);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (2,'',3,2);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (3,'',4,1);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (4,'',8,1);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (5,'',5,1);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (6,'',12,3);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (7,'',13,3);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (8,'',14,3);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (9,'',15,3);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (10,'',29,4);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (11,'',21,4);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (12,'',22,4);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (13,'',23,4);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (14,'',24,4);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (15,'',25,4);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (16,'',26,4);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (17,'',27,4);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (18,'',28,4);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (19,'',10,4);
INSERT INTO `watch_list_item` (`id`, `PERSISTENT`, `SECURITY_FK`, `STRATEGY_FK`) VALUES (20,'',12,4);
/*!40000 ALTER TABLE `watch_list_item` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-07-25 18:02:13
