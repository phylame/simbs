/*
 * This file is part of SIMBS.
 * (C) 2015 Peng Wan
 *
 * Delete all tables used in SIMBS.
 */

/* Remove all records in tables */
DELETE FROM bill;
DELETE FROM promotion;
DELETE FROM return;
DELETE FROM rental;
DELETE FROM sale;
DELETE FROM inventory;
DELETE FROM stock;
DELETE FROM customer;
DELETE FROM book;

/* Delete all tables */
DROP TABLE promotion IF EXISTS;
DROP TABLE return IF EXISTS;
DROP TABLE rental IF EXISTS;
DROP TABLE sale IF EXISTS;
DROP TABLE stock IF EXISTS;
DROP TABLE inventory IF EXISTS;
DROP TABLE bill IF EXISTS;
DROP TABLE customer IF EXISTS;
DROP TABLE book IF EXISTS;