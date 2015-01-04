/* query all book information */
SELECT Bisbn ���, Bname ����, Bversion �汾, Bauthors ����, Bdate ����, Bcategory ����,
 Bpublisher ������, Bprice �۸�, Bintro ���
FROM book_info;

/* query all customer */
SELECT Cid ���, Cname ����, Cphone �ֻ�
FROM customer_info;

/* query book stock */
SELECT book_info.Bisbn ���, Bname, Bversion �汾, Bpublisher ������, Enumber �����
FROM book_info, book_stock
WHERE book_stock.Bisbn = book_info.Bisbn;

/* query sold record */
SELECT customer_info.Cid �ͻ���, Cname ����, Sdate ʱ��, Stime ʱ��, book_info.Bisbn ���, 
 Bname ����, Snumber ��Ŀ
FROM customer_info, book_info, sold_book
WHERE sold_book.Cid = customer_info.Cid AND sold_book.Bisbn = book_info.Bisbn;

/* query lent record */
SELECT customer_info.Cid �ͻ���, Cname ����, Ldate ����, Ltime ʱ��, book_info.Bisbn ���,
 Bname ����, Lnumber ��Ŀ, Lperiod ����
FROM customer_info, book_info, lent_book
WHERE lent_book.Cid = customer_info.Cid AND lent_book.Bisbn = book_info.Bisbn;