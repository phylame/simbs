/*
 * This file is part of SIMBS.
 * (C) 2015 Peng Wan
 *
 * Create database tables used in SIMBS.
 */

/* create table of book information */
CREATE TABLE book
(
	Bisbn CHAR(17) PRIMARY KEY,											/* ISBN of the book */
	Bname VARCHAR(128) NOT NULL,										/* book name */
	Bversion VARCHAR(64),												/* version message */
	Bauthors VARCHAR(128) NOT NULL,										/* authors, if more than one, split with "," */
	Bcover VARCHAR(512),												/* path of cover image */
	Bdate DATE NOT NULL,												/* published date */
	Bcategory VARCHAR(128),												/* category, if more than one, split with "," */
	Bpublisher VARCHAR(128) NOT NULL,									/* the publisher */
	Bprice DECIMAL(10, 2) NOT NULL CHECK (Bprice >= 0.0),				/* book price */
	Bintro VARCHAR(512)													/* a intro of the book */
);

/* create table of customer information */
CREATE TABLE customer
(
	Cid INT PRIMARY KEY CHECK (Cid > 0),								/* customer ID, begin from 1 */
	Cname VARCHAR(64) NOT NULL,											/* customer name */
	Cphone VARCHAR(16),													/* cell-phone count */
	Cemail VARCHAR(48),													/* email */
	Clevel SMALLINT NOT NULL CHECK (Clevel >= 0),						/* customer level */
	Climit SMALLINT NOT NULL CHECK (Climit >= 0)						/* max number of book that customer can borrowed each time */
);

/* create table of book stock */
CREATE TABLE stock
(
	Bisbn CHAR(17) PRIMARY KEY,											/* book ISBN */
	Inumber INT NOT NULL CHECK (Inumber >= 0),							/* inventory number */
	FOREIGN KEY (Bisbn) REFERENCES book(Bisbn)							/* use ISBN in book */
);

/* create table of sale list */
CREATE TABLE sale
(
	Bisbn CHAR(17) NOT NULL,											/* book ISBN */
	Cid INT NOT NULL,													/* the ID of this customer */
	Sdate DATE NOT NULL,												/* the sold date */
	Stime TIME NOT NULL,												/* the sold time */
	Snumber INT NOT NULL CHECK (Snumber > 0),							/* the count of customer bought book */
	Stotal DECIMAL(10, 2) NOT NULL CHECK (Stotal >= 0),					/* total price of those book */
	Scomment VARCHAR(128),												/* the comments */
	PRIMARY KEY (Bisbn, Cid, Sdate, Stime),								/* the primary key */
	FOREIGN KEY (Bisbn) REFERENCES book(Bisbn),							/* use ISBN in book */
	FOREIGN KEY (Cid) REFERENCES customer(Cid)							/* use ID in customer */
);

/* create table of rental list */
CREATE TABLE rental
(
	Bisbn CHAR(17) NOT NULL,											/* book ISBN */
	Cid INT NOT NULL,													/* the ID of this customer */
	Rdate DATE NOT NULL,												/* the date when lend the book */
	Rtime TIME NOT NULL,												/* the time when lend the book */
	Rnumber INT NOT NULL CHECK (Rnumber > 0),							/* the count of customer borrowed book */
	Rperiod SMALLINT NOT NULL CHECK (Rperiod > 0),						/* the days of customer borrowed book */
	Rtotal DECIMAL(10, 2) NOT NULL CHECK (Rtotal >= 0),					/* total price of those book */
	Rcomment VARCHAR(128),												/* the comments */
	PRIMARY KEY (Bisbn, Cid, Rdate),									/* the primary key */
	FOREIGN KEY (Bisbn) REFERENCES book(Bisbn),							/* use ISBN in book */
	FOREIGN KEY (Cid) REFERENCES customer(Cid)							/* use ID in customer */
);
