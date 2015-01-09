/*
 * This file is part of SIMBS.
 * (C) 2015 Peng Wan
 *
 * Add some example records to database for SIMBS.
 */

/* Add book information */
INSERT INTO book (Bisbn, Bname, Bversion, Bauthors, Bdate, Bcategory, Bpublisher, Bprice, Bintro)
 VALUES ('978-7-04-019583-5', '数据库系统概论', '第4版', '王珊,萨师煊', '2006-5-1', '教材', '高等教育出版社', 39.00, '数据库概论第 4 版');
INSERT INTO book(Bisbn, Bname, Bversion, Bauthors, Bdate, Bcategory, Bpublisher, Bprice, Bintro)
 VALUES ('978-7-115-28931-5', 'Android 移动应用开发：卷一，基础篇', '第3版', 'Lauren Darcey,Shane Conder', '2012-10-1', '移动开发', '人民邮电出版社', 59.00, 'Android 移动开发');

/* Add customer information */
INSERT INTO customer (Cid, Cname, Cphone, Clevel, Climit) VALUES (1, '李明', '13878541125', 0, 10);
INSERT INTO customer (Cid, Cname, Cphone, Clevel, Climit) VALUES (2, '陈亮', '13788956945', 0, 10);
INSERT INTO customer (Cid, Cname, Cphone, Clevel, Climit) VALUES (3, '张三', '15539845612', 0, 10);
INSERT INTO customer (Cid, Cname, Cphone, Clevel, Climit) VALUES (4, '李四', '18677722631', 0, 10);

/* Add book to book stock */
INSERT INTO stock(Bisbn, Tdate, Ttime, Tnumber, Ttotal, Tcomment)
 VALUES ('978-7-04-019583-5'， '2014-12-15', '10:20:00', 10, 100.00, '');
INSERT INTO stock(Bisbn, Tdate, Ttime, Tnumber, Ttotal, Tcomment)
 VALUES ('978-7-115-28931-5'， '2014-12-15', '10:20:00', 30, 500.00, '');
INSERT INTO inventory (Bisbn, Inumber) VALUES ('978-7-04-019583-5', 10);
INSERT INTO inventory (Bisbn, Inumber) VALUES ('978-7-115-28931-5', 30);

/* Add sale record */
INSERT INTO sale (Bisbn, Cid, Snumber, Sdate, Stime, Stotal, Scomment)
 VALUES ('978-7-04-019583-5', 1, 1, '2015-1-3', '18:36:00', 28.00, '卖出的第一本书');

/* Add rental record */
INSERT INTO rental (Bisbn, Cid, Rnumber, Rdate, Rtime, Rperiod, Rtotal, Rcomment)
 VALUES ('978-7-04-019583-5', 2, 1, '2014-12-14', '22:10:59', 12, 1.00, '借出的第一本');
INSERT INTO rental (Bisbn, Cid, Rnumber, Rdate, Rtime, Rperiod, Rtotal, Rcomment)
 VALUES ('978-7-04-019583-5', 1, 1, '2014-12-15', '22:10:59', 12, 5.00, '');
INSERT INTO rental (Bisbn, Cid, Rnumber, Rdate, Rtime, Rperiod, Rtotal, Rcomment)
 VALUES ('978-7-04-019583-5', 2, 1, '2014-12-16', '22:10:59', 12, 5.00, '');

