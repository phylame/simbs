/* add some example records */

/* add book information */
INSERT INTO book_info(Bisbn, Bname, Bversion, Bauthors, Bdate, Bcategory, Bpublisher, Bprice, Bintro)
 VALUES('978-7-04-019583-5', '数据库系统概论', '第4版', '王珊,萨师煊', '2006-5-1', '教材', '高等教育出版社', 39.00, '数据库概论第 4 版');
INSERT INTO book_info(Bisbn, Bname, Bversion, Bauthors, Bdate, Bcategory, Bpublisher, Bprice, Bintro)
 VALUES('978-7-115-28931-5', 'Android移动应用开发：卷一，基础篇', '第3版', 'Lauren Darcey,Shane Conder', '2012-10-1', '移动开发', '人民邮电出版社', 59.00, 'Android 移动开发');

/* add customer information */
INSERT INTO customer_info (Cid, Cname, Cphone) VALUES(1, '李明', '13878541125');
INSERT INTO customer_info (Cid, Cname, Cphone) VALUES(2, '陈亮', '13788956945');
INSERT INTO customer_info (Cid, Cname, Cphone) VALUES(3, '张三', '15539845612');
INSERT INTO customer_info (Cid, Cname, Cphone) VALUES(4, '李四', '18677722631');

/* add book to book stock */
INSERT INTO book_stock(Bisbn, Enumber) VALUES('978-7-04-019583-5', 10);
INSERT INTO book_stock(Bisbn, Enumber) VALUES('978-7-115-28931-5', 30);

/* add sold book record */
INSERT INTO sold_book(Bisbn, Cid, Snumber, Sdate, Stime)
 VALUES('978-7-04-019583-5', 1, 1, '2015-1-3', '18:36:00');

/* add lent book record */
INSERT INTO lent_book(Bisbn, Cid, Lnumber, Ldate, Ltime, Lperiod)
 VALUES('978-7-04-019583-5', 2, 1, '2014-12-14', '22:10:59', 12);
INSERT INTO lent_book(Bisbn, Cid, Lnumber, Ldate, Ltime, Lperiod)
 VALUES('978-7-04-019583-5', 1, 1, '2014-12-14', '22:10:59', 12);
INSERT INTO lent_book(Bisbn, Cid, Lnumber, Ldate, Ltime, Lperiod)
 VALUES('978-7-04-019583-5', 2, 1, '2014-12-24', '22:10:59', 12);

