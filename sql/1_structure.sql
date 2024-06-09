CREATE DATABASE `logistics`;

USE `logistics`;

CREATE TABLE `products` (
    `id` int(11) NOT NULL,
    `groupId` int(11) NOT NULL,
    `description` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY (`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `productGroups` (
    `id` int(11) NOT NULL,
    `description` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `supplierDeclarations` (
    `salesOrderItemId` varchar(20) NOT NULL,
    `salesOrderId` varchar(20) NOT NULL,
    `salesDescription` text NULL,
    `customsTariffNumber` varchar(50) NOT NULL,
    `preferentialCountries` text NOT NULL,
    `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY `salesOrderId` (`salesOrderId`),
    KEY `created` (`created`),
    PRIMARY KEY (`salesOrderItemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
