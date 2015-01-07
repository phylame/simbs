/*
 * This file is part of SIMBS.
 * (C) 2015 Peng Wan 
 * Create tables used in SIMBS.
 *
 */

/* create table of book information */
CREATE TABLE book_info
(
	Bisbn CHAR(17) PRIMARY KEY,								/* ISBN of the book */
	Bname VARCHAR(128) NOT NULL,							/* book name */
	Bversion VARCHAR(32),									/* version message */
	Bauthors VARCHAR(128) NOT NULL,							/* authors, if more than one, split with "," */
	Bdate DATE NOT NULL,									/* published date */
	Bcategory VARCHAR(64),									/* category */
	Bpublisher VARCHAR(64) NOT NULL,						/* the publisher */
	Bprice FLOAT(2) NOT NULL CHECK (Bprice > 0.0),			/* book price */
	Bintro VARCHAR(512)										/* a intro of the book */
);

/* create table of customer information */
CREATE TABLE customer_info
(
	Cid INT PRIMARY KEY CHECK (Cid > 0),					/* customer ID, begin from 1 */
	Cname VARCHAR(32) NOT NULL,								/* customer name */
	Cphone CHAR(16),										/* cell-phone count */
	Cemail VARCHAR(36),										/* email */
	Clevel SMALLINT,										/* customer level */
	Clent_limit SMALLINT									/* max number of customer borrowed each time */
);

/* existing book stock */
CREATE TABLE book_stock
(
	Bisbn CHAR(17) PRIMARY KEY,								/* book ISBN */
	Enumber INT NOT NULL CHECK (Enumber >= 0),				/* existing book count */
	FOREIGN KEY (Bisbn) REFERENCES book_info(Bisbn)			/* use ISBN in book_info */
);

/* sold book record */
CREATE TABLE sold_book
(
	Bisbn CHAR(17),											/* book ISBN */
	Cid INT,												/* the ID of this customer */
	Sdate DATE NOT NULL,									/* the sold date */
	Stime TIME NOT NULL,									/* the sold time */
	Snumber INT NOT NULL CHECK (Snumber > 0),				/* the count of customer bought book */
	primary key (Bisbn, Cid, Sdate, Stime),					/* the primary key */
	FOREIGN KEY (Bisbn) REFERENCES book_info(Bisbn),		/* use ISBN in book_info */
	FOREIGN KEY (Cid) REFERENCES customer_info(Cid)			/* use ID in customer_info */
);

/* lent book record */
CREATE TABLE lent_book
(
	Bisbn CHAR(17),											/* book ISBN */
	Cid INT,												/* the ID of this customer */
	Ldate DATE NOT NULL,									/* the lent date */
	Ltime TIME NOT NULL,									/* the lent time */
	Lnumber INT NOT NULL CHECK (Lnumber > 0),				/* the count of customer borrowed book */
	Lperiod SMALLINT NOT NULL CHECK (Lperiod > 0),			/* the days of customer borrowed book */
	primary key (Bisbn, Cid, Ldate),						/* the primary key */
	FOREIGN KEY (Bisbn) REFERENCES book_info(Bisbn),		/* use ISBN in book_info */
	FOREIGN KEY (Cid) REFERENCES customer_info(Cid)			/* use ID in customer_info */
);
