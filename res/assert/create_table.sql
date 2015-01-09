/*
 * This file is part of SIMBS.
 * (C) 2015 Peng Wan
 *
 * Create database tables used in SIMBS.
 */

/* Book information, field name starts with "B" */
CREATE TABLE book
(
	/* The ISBN */
	Bisbn CHAR(17) PRIMARY KEY,

	/* The name */
	Bname VARCHAR(128) NOT NULL,

	/* Version message */
	Bversion VARCHAR(64),

	/* Authors, split names with "," if more than one author */
	Bauthors VARCHAR(128) NOT NULL,

	/* Path of cover image */
	Bcover VARCHAR(512),

	/* Publication date */
	Bdate DATE NOT NULL,

	/* Category, split value with "," if more than one item */
	Bcategory VARCHAR(128),

	/* The publisher */
	Bpublisher VARCHAR(128) NOT NULL,

	/* The price */
	Bprice DECIMAL(10, 2) NOT NULL CHECK (Bprice >= 0.0),

	/* Introduction of the book */
	Bintro VARCHAR(512)
);

/* Customer information, field name starts with "C" */
CREATE TABLE customer
(
	/* Customer ID, begin from 1 */
	Cid INT PRIMARY KEY CHECK (Cid > 0),

	/* Customer name */
	Cname VARCHAR(64) NOT NULL,

	/* Phone number */
	Cphone VARCHAR(16),

	/* Email address */
	Cemail VARCHAR(48),

	/* Customer level, default is 0 */
	Clevel SMALLINT DEFAULT 0 NOT NULL CHECK (Clevel >= 0),

	/* Limits for borrowing books, default is 10 */
	Climit SMALLINT DEFAULT 10 NOT NULL CHECK (Climit >= 0)

	/* The comments */
	Ccomment VARCHAR(512),
);

/* Inventory listing, field name starts with "I" */
CREATE TABLE inventory
(
	/* ISBN of book */
	Bisbn CHAR(17) PRIMARY KEY,

	/* Inventory number */
	Inumber INT NOT NULL CHECK (Inumber >= 0),

	FOREIGN KEY (Bisbn) REFERENCES book(Bisbn)
);

/* Stock listing, field name starts with "T" */
CREATE TABLE stock
(
	/* ID of record, begin from 1 */
	Tid INT PRIMARY KEY CHECK (Tid > 0),

	/* ISBN of book */
	Bisbn CHAR(17),

	/* The date */
	Tdate DATE NOT NULL,

	/* The time */
	Ttime TIME NOT NULL,

	/* The Number of those books */
	Tnumber INT NOT NULL CHECK (Tnumber > 0),

	/* Total price of those books */
    Ttotal DECIMAL(10, 2) NOT NULL CHECK (Ttotal >= 0),

    /* The comments */
	Tcomment VARCHAR(512),

    FOREIGN KEY (Bisbn) REFERENCES book(Bisbn),
);

/* Sale listing, field name starts with "S" */
CREATE TABLE sale
(
	/* ID of record, begin from 1 */
	Sid INT PRIMARY KEY CHECK (Sid > 0),

	/* ISBN of the book */
	Bisbn CHAR(17) NOT NULL,

	/* ID of the customer */
	Cid INT NOT NULL,

	/* The date */
	Sdate DATE NOT NULL,

	/* The time */
	Stime TIME NOT NULL,

	/* The number of this bought book */
	Snumber INT NOT NULL CHECK (Snumber > 0),

	/* Total price of those books */
	Stotal DECIMAL(10, 2) NOT NULL CHECK (Stotal >= 0),

	/* The comments */
	Scomment VARCHAR(128),

	FOREIGN KEY (Bisbn) REFERENCES book(Bisbn),
	FOREIGN KEY (Cid) REFERENCES customer(Cid)
);

/* Rental list, field name starts with "R" */
CREATE TABLE rental
(
	/* ID of record, begin from 1 */
	Rid INT PRIMARY KEY CHECK (Rid > 0),

	/* ISBN of the book */
	Bisbn CHAR(17) NOT NULL,

	/* ID of the customer */
	Cid INT NOT NULL,

	/* The date */
	Rdate DATE NOT NULL,

	/* The time */
	Rtime TIME NOT NULL,

	/* The number of book that customer borrowed, when 0 that customer returned */
	Rnumber INT NOT NULL CHECK (Rnumber >= 0),

	/* The days of customer borrowed book */
	Rperiod SMALLINT NOT NULL CHECK (Rperiod > 0),

	/* The rental price of each book */
	Rprice DECIMAL(10, 2) DEFAULT 0 NOT NULL CHECK ( Rprice >= 0),

	/* Deposit price of those book */
	Rdeposit DECIMAL(10, 2) NOT NULL CHECK (Rdeposit >= 0),

	/* The revenue money of this rental task */
	Rrevenue DECIMAL(10, 2) NOT NULL CHECK (Rrevenue >= 0),

	/* The comments */
	Rcomment VARCHAR(128),

	FOREIGN KEY (Bisbn) REFERENCES book(Bisbn),
	FOREIGN KEY (Cid) REFERENCES customer(Cid)
);

/* The bill, field name starts with "L" */
CREATE TABLE bill
(
	/* Record number, begin from 1 */
	Lno INT PRIMARY KEY CHECK (Lno > 0),

	/* The date */
	Ldate DATE NOT NULL,

	/* The time */
	Ltime TIME NOT NULL,

	/* Event of the bill, maybe 1:stock, 2:sale, 3:rental, 4:return */
	Levent INT NOT NULL CHECK (Levent IN (1, 2, 3, 4)),

	/* Task ID in its table, such as Tid, Sid, Rid, Rid */
	Lid INT NOT NULL CHECK (Lid > 0),
);
