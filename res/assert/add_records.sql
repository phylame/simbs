/* add some example records */

/* add book information */
INSERT INTO book_info(Bisbn, Bname, Bversion, Bauthors, Bdate, Bcategory, Bpublisher, Bprice, Bintro)
 VALUES('978-7-04-019583-5', '���ݿ�ϵͳ����', '��4��', '��ɺ,��ʦ��', '2006-5-1', '�̲�', '�ߵȽ���������', 39.00, '���ݿ���۵� 4 ��');
INSERT INTO book_info(Bisbn, Bname, Bversion, Bauthors, Bdate, Bcategory, Bpublisher, Bprice, Bintro)
 VALUES('978-7-115-28931-5', 'Android�ƶ�Ӧ�ÿ�������һ������ƪ', '��3��', 'Lauren Darcey,Shane Conder', '2012-10-1', '�ƶ�����', '�����ʵ������', 59.00, 'Android �ƶ�����');

/* add customer information */
INSERT INTO customer_info (Cid, Cname, Cphone) VALUES(1, '����', '13878541125');
INSERT INTO customer_info (Cid, Cname, Cphone) VALUES(2, '����', '13788956945');
INSERT INTO customer_info (Cid, Cname, Cphone) VALUES(3, '����', '15539845612');
INSERT INTO customer_info (Cid, Cname, Cphone) VALUES(4, '����', '18677722631');

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

