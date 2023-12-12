-- MySQL dump 10.13  Distrib 8.0.35, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: gear_galaxy_rent
-- ------------------------------------------------------
-- Server version	8.0.35-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categories`
--

-- DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories`
VALUES (1,1,'Samochody, motocykle, hulajnogi elektryczne, hulajnogi tradycyjne, rowery','Pojazdy Transportowe'),
       (2,2,'Aparaty, obiektywy, oświetlenie, akcesoria','Sprzęt fotograficzny'),
       (3,3,'Stoły, krzesła, ławki, meble pokojowe, meble dziecięce','Meble'),
       (4,4,'Telewizory, radia, odtwarzacze, głośniki ','Sprzęd RTV'),
       (5,5,'Lodówki, pralki, zmywarki, mikrofalówki, piekarniki, mikrofalówki, blendery','Sprzęt AGD'),
       (6,6,'Lokale użytkowe, sale bankietowe, sale konferencyjne','Lokale'),
       (7,7,'Domy, mieszkania, pokoje, garaże, magazyny','Nieruchomości'),
       (8,8,'Komputery, telefony, tablety, odtwarzacze MP3 ','Elektronika'),
       (9,9,'Inne produkty nie pasujące do pozostałych kategorii','Inne');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `items` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `description` varchar(1500) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `name` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `category_id` bigint DEFAULT NULL,
                         `owner_id` bigint DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         KEY `FKjcdcde7htb3tyjgouo4g9xbmr` (`category_id`),
                         KEY `FKe37yi0i6rmaqcqickvb1vty22` (`owner_id`),
                         CONSTRAINT `FKe37yi0i6rmaqcqickvb1vty22` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`),
                         CONSTRAINT `FKjcdcde7htb3tyjgouo4g9xbmr` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
/*!40000 ALTER TABLE `items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rentals`
--

DROP TABLE IF EXISTS `rentals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rentals` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `created` datetime(6) DEFAULT NULL,
                           `payment_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `price` decimal(38,2) DEFAULT NULL,
                           `rent_from` datetime(6) DEFAULT NULL,
                           `rent_to` datetime(6) DEFAULT NULL,
                           `status` tinyint DEFAULT NULL,
                           `updated` datetime(6) DEFAULT NULL,
                           `item_id` bigint NOT NULL,
                           `leaser_id` bigint DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           KEY `FK9t4jdsn113em9kyh6wkl81efm` (`item_id`),
                           KEY `FKt781ejftq4w8jud1mi5mlga3g` (`leaser_id`),
                           CONSTRAINT `FK9t4jdsn113em9kyh6wkl81efm` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`),
                           CONSTRAINT `FKt781ejftq4w8jud1mi5mlga3g` FOREIGN KEY (`leaser_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rentals`
--

LOCK TABLES `rentals` WRITE;
/*!40000 ALTER TABLE `rentals` DISABLE KEYS */;
/*!40000 ALTER TABLE `rentals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rentals_history`
--

DROP TABLE IF EXISTS `rentals_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rentals_history` (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `created` datetime(6) DEFAULT NULL,
                                   `owner_email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `owner_first_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `owner_phone_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `owners_last_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `price` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `product_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `rental_id` bigint DEFAULT NULL,
                                   PRIMARY KEY (`id`),
                                   KEY `FKj9dpjnllpfaft437i13k5fj68` (`rental_id`),
                                   CONSTRAINT `FKj9dpjnllpfaft437i13k5fj68` FOREIGN KEY (`rental_id`) REFERENCES `rentals` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rentals_history`
--

LOCK TABLES `rentals_history` WRITE;
/*!40000 ALTER TABLE `rentals_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `rentals_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_company`
--

DROP TABLE IF EXISTS `user_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_company` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `nip` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `phone_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `user_id` bigint DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `UK_55jpvggb144ggjbw2jc2kow6q` (`user_id`),
                                CONSTRAINT `FKb13pv71qpw5stvdkovmm50xsm` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_company`
--

LOCK TABLES `user_company` WRITE;
/*!40000 ALTER TABLE `user_company` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `first_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `last_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
                         UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-12-11 10:31:24
