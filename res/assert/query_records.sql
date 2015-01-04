/* query all book information */
SELECT Bisbn 书号, Bname 书名, Bversion 版本, Bauthors 作者, Bdate 日期, Bcategory 分类,
 Bpublisher 出版社, Bprice 价格, Bintro 简介
FROM book_info;

/* query all customer */
SELECT Cid 编号, Cname 姓名, Cphone 手机
FROM customer_info;

/* query book stock */
SELECT book_info.Bisbn 书号, Bname, Bversion 版本, Bpublisher 出版社, Enumber 库存量
FROM book_info, book_stock
WHERE book_stock.Bisbn = book_info.Bisbn;

/* query sold record */
SELECT customer_info.Cid 客户号, Cname 姓名, Sdate 时间, Stime 时间, book_info.Bisbn 书号, 
 Bname 书名, Snumber 数目
FROM customer_info, book_info, sold_book
WHERE sold_book.Cid = customer_info.Cid AND sold_book.Bisbn = book_info.Bisbn;

/* query lent record */
SELECT customer_info.Cid 客户号, Cname 姓名, Ldate 日期, Ltime 时间, book_info.Bisbn 书号,
 Bname 书名, Lnumber 数目, Lperiod 期限
FROM customer_info, book_info, lent_book
WHERE lent_book.Cid = customer_info.Cid AND lent_book.Bisbn = book_info.Bisbn;