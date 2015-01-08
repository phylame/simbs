/*
 * This file is part of SIMBS.
 * (C) 2015 Peng Wan
 *
 * Test database used in SIMBS.
 */

/* Query all book information */
SELECT Bisbn 书号, Bname 书名, Bversion 版本, Bauthors 作者, Bcover 封面, Bdate 日期, Bcategory 分类,
 Bpublisher 出版社, Bprice 价格, Bintro 简介
FROM book;

/* Query all customer */
SELECT Cid 编号, Cname 姓名, Cphone 手机, Clevel 级别, Climit 借书上限
FROM customer;

/* Query book stock */
SELECT book.Bisbn 书号, Bname 书名, Bversion 版本, Bpublisher 出版社, Inumber 库存量
FROM book, stock
WHERE stock.Bisbn = book.Bisbn;

/* Query sold book */
SELECT customer.Cid 客户号, Cname 姓名, Sdate 日期, Stime 时间, book.Bisbn 书号,
 Bname 书名, Snumber 数目, Stotal 总价, Scomment 备注
FROM customer, book, sale
WHERE sale.Cid = customer.Cid AND sale.Bisbn = book.Bisbn;

/* Query lent book */
SELECT customer.Cid 客户号, Cname 姓名, Rdate 日期, Rtime 时间, book.Bisbn 书号,
 Bname 书名, Rnumber 数目, Rperiod 期限, Rtotal 总价, Rcomment 备注
FROM customer, book, rental
WHERE rental.Cid = customer.Cid AND rental.Bisbn = book.Bisbn;