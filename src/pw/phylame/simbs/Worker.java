/*
 * Copyright 2015 Peng Wan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pw.phylame.simbs;

import pw.phylame.simbs.ds.Book;
import pw.phylame.simbs.ds.Customer;
import pw.phylame.simbs.ui.dialog.DialogFactory;
import pw.phylame.simbs.ui.dialog.ModifyBookDialog;
import pw.phylame.simbs.ui.dialog.ModifyCustomerDialog;
import pw.phylame.tools.sql.DbHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 * Function of SIMBS.
 */
public final class Worker {

    /**
     * Convert date to SQL date.
     * @param date the normal date
     * @return the SQL date
     */
    public static java.sql.Date toSQLDate(java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }

    /**
     * Convert date to SQL time.
     * @param date the normal date
     * @return the SQL time
     */
    public static java.sql.Time toSQLTime(java.util.Date date) {
        return new java.sql.Time(date.getTime());
    }

    /**
     * Strip space of SQL string s both begin and end.
     */
    public static String normalizeString(String s) {
        return s == null ? null : s.trim();
    }


    /** SQL statement for selecting book */
    public static final String SQL_SELECT_BOOK = "SELECT Bisbn, Bname, Bversion, Bauthors," +
            " Bcover, Bdate, Bcategory, Bpublisher, Bprice, Bintro FROM book ";

    /**
     * Get book from {@code ResultSet}.
     * <p>The SQL query statement must be {@code SQL_SELECT_BOOK}.</p>
     */
    public static Book gainBook(ResultSet rs) throws SQLException {
        return new Book(normalizeString(rs.getString(1)), normalizeString(rs.getString(2)),
                normalizeString(rs.getString(3)), normalizeString(rs.getString(4)),
                normalizeString(rs.getString(5)), rs.getDate(6), normalizeString(rs.getString(7)),
                normalizeString(rs.getString(8)), rs.getBigDecimal(9),
                normalizeString(rs.getString(10)));
    }

    /** SQL statement for selecting customer */
    public static final String SQL_SELECT_CUSTOMER = "SELECT Cid, Cname, Cphone, Cemail, Clevel," +
            " Climit, Ccomment FROM customer ";

    /**
     * Get customer from {@code ResultSet}.
     * <p>The SQL query statement must be {@code SQL_SELECT_CUSTOMER}.</p>
     */
    public static Customer gainCustomer(ResultSet rs) throws SQLException {
        return new Customer(rs.getInt(1), normalizeString(rs.getString(2)),
                normalizeString(rs.getString(3)), normalizeString(rs.getString(4)),
                rs.getInt(5), rs.getInt(6), normalizeString(rs.getString(7)));
    }


    // *************
    // ** Event ID
    // *************
    /** Stock book */
    public static final int EVENT_STOCK = 1;
    /** Sell book */
    public static final int EVENT_SALE = 2;
    /** Lend book*/
    public static final int EVENT_RENTAL = 3;
    /** Return book */
    public static final int EVENT_RETURN = 4;


    public Worker(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
        Worker.instance = this;
    }

    /** Get unique instance */
    public static Worker getInstance() {
        return instance;
    }

    /** Destroy worker instance */
    public void destroy() {
        try {
            dbHelper.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a integer value from query.
     * @param sql the SQL statement
     * @return the value or -1 if occur errors when querying
     */
    public int selectInteger(String sql) {
        int number = -1;
        try {
            ResultSet rs = dbHelper.executeQuery(sql);
            if (rs.next()) {
                number = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return number;
    }

    /**
     * Get a decimal value from query.
     * @param sql the SQL statement
     * @return the value or null if occur errors when querying
     */
    public BigDecimal selectDecimal(String sql) {
        BigDecimal value = null;
        try {
            ResultSet rs = dbHelper.executeQuery(sql);
            if (rs.next()) {
                value = rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Get a string value from query.
     * @param sql the SQL statement
     * @return the value or null if occur errors when querying
     */
    public String selectString(String sql) {
        String value = null;
        try {
            ResultSet rs = dbHelper.executeQuery(sql);
            if (rs.next()) {
                value = normalizeString(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return value;
    }

    // *******************
    // ** Book operations
    // *******************

    /**
     * Register a book
     * @param book the {@code Book} object
     * @throws SQLException if occur errors when modify database
     */
    public void registerBook(Book book) throws SQLException {
        String sql = "INSERT INTO book (Bisbn, Bname, Bversion, Bauthors, Bcover, Bdate," +
                " Bcategory, Bpublisher, Bprice, Bintro) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        PreparedStatement ps = dbHelper.prepareStatement(sql);

        ps.setString(1, book.getISBN());
        ps.setString(2, book.getName());
        ps.setString(3, book.getVersion());
        ps.setString(4, book.getAuthors());
        ps.setString(5, book.getCover());
        ps.setDate(6, toSQLDate(book.getDate()));
        ps.setString(7, book.getCategory());
        ps.setString(8, book.getPublisher());
        ps.setBigDecimal(9, book.getPrice());
        ps.setString(10, book.getIntro());

        ps.executeUpdate();
    }

    /**
     * Update book information.
     * <p>If book is registered then update it otherwise register this book.</p>
     * @param book the {@code Book} object
     * @throws SQLException if occur errors when modify database
     */
    public void updateBook(Book book) throws SQLException {
        if (! isBookRegistered(book.getISBN())) {   // register if not
            registerBook(book);
            return;
        }

        String sql = "UPDATE book SET Bname=?, Bversion=?, Bauthors=?, Bcover=?, Bdate=?," +
                " Bcategory=?, Bpublisher=?, Bprice=?, Bintro=? WHERE Bisbn=?";
        PreparedStatement ps = dbHelper.prepareStatement(sql);

        ps.setString(1, book.getName());
        ps.setString(2, book.getVersion());
        ps.setString(3, book.getAuthors());
        ps.setString(4, book.getCover());
        ps.setDate(5, toSQLDate(book.getDate()));
        ps.setString(6, book.getCategory());
        ps.setString(7, book.getPublisher());
        ps.setBigDecimal(8, book.getPrice());
        ps.setString(9, book.getIntro());
        ps.setString(10, book.getISBN());

        ps.executeUpdate();
    }

    /**
     * Remove a book by its ISBN.
     * @param isbn ISBN of the book
     * @throws SQLException if occur errors when modify database
     */
    public void removeBook(String isbn) throws SQLException {
        String sql = "DELETE FROM book WHERE Bisbn=?";
        PreparedStatement ps = dbHelper.prepareStatement(sql);

        ps.setString(1, isbn);

        ps.executeUpdate();
    }


    /**
     * Test book is registered or not.
     * @param isbn ISBN of book
     * @return {@code true} if registered or {@code false} if not.
     */
    public boolean isBookRegistered(String isbn) {
        return selectString(String.format("SELECT Bname FROM book WHERE Bisbn='%s'", isbn)) != null;
    }

    /**
     * Returns number of registered books.
     */
    public int getRegisteredBookCount() {
        String sql = "SELECT COUNT(Bisbn) FROM book";
        return selectInteger(sql);
    }

    /**
     * Get books by condition.
     * @param condition SQL query condition.
     */
    public java.util.List<Book> getBooks(String condition) {
        if (condition == null) {
            condition = "";
        }
        String sql = SQL_SELECT_BOOK + condition;
        java.util.ArrayList<Book> books = new java.util.ArrayList<>();
        try {
            ResultSet rs = dbHelper.executeQuery(sql);
            while (rs.next()) {
                books.add(gainBook(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Get book by its ISBN.
     * @param isbn ISBN of the book
     * @return the {@code Book} instance or {@code null} if not found
     */
    public Book getBook(String isbn) {
        java.util.List<Book> books = getBooks(String.format("WHERE Bisbn='%s'", isbn));
        if (books.size() == 0) {
            return null;
        } else {
            return books.get(0);
        }
    }

    // **************************
    // ** Book fields functions
    // **************************

    public BigDecimal getSalePromote(String isbn) {
        return new BigDecimal(1);   // not promote
    }

    public BigDecimal getRentalPromote(String isbn) {
        return new BigDecimal(1);   // not promote
    }

    /**
     * Get the price of book.
     * @param isbn ISBN of the book
     * @return price, if price < 0 occur errors
     */
    public BigDecimal getPrice(String isbn) {
        String sql = String.format("SELECT Bprice from book WHERE Bisbn='%s'", isbn);
        return selectDecimal(sql);
    }

    public BigDecimal getSalePrice(String isbn) {
        BigDecimal originPrice = getPrice(isbn);
        if (originPrice == null) {      // not found the book
            return null;
        }
        return originPrice.multiply(getSalePromote(isbn));
    }

    public BigDecimal getRentalPrice(String isbn) {
//        BigDecimal originPrice = getPrice(isbn);
//        if (originPrice == null) {      // not found the book
//            return null;
//        }
//        return originPrice.multiply(getRentalPromote(isbn));
        if (! isBookRegistered(isbn)) {
            return null;
        }
        return Constants.DEFAULT_RENTAL_PRICE.multiply(getRentalPromote(isbn));
    }

    // ************************
    // ** Customer operations
    // ************************

    /**
     * Register a customer
     * @param customer the customer.
     */
    public void registerCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customer (Cid, Cname, Cphone, Cemail, Clevel, Climit, Ccommnet)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = dbHelper.prepareStatement(sql);

        ps.setInt(1, customer.getId());
        ps.setString(2, customer.getName());
        ps.setString(3, customer.getPhone());
        ps.setString(4, customer.getEmail());
        ps.setInt(5, customer.getLevel());
        ps.setInt(6, customer.getLimit());
        ps.setString(7, customer.getComment());

        ps.executeUpdate();
    }

    /**
     * Update customer information.
     * <p>If customer is registered then update it otherwise register this customer.</p>
     * @param customer the {@code Customer} object
     * @throws SQLException if occur errors when modify database
     */
    public void updateCustomer(Customer customer) throws SQLException {
        if (! isCustomerRegistered(customer.getId())) {     // register if not
            registerCustomer(customer);
            return;
        }

        String sql = "UPDATE customer SET Cname=?, Cphone=?, Cemail=?, Clevel=?, Climit=?" +
                " WHERE Cid=?";
        PreparedStatement ps = dbHelper.prepareStatement(sql);

        ps.setString(1, customer.getName());
        ps.setString(2, customer.getPhone());
        ps.setString(3, customer.getEmail());
        ps.setInt(4, customer.getLevel());
        ps.setInt(5, customer.getLimit());
        ps.setInt(6, customer.getId());

        ps.executeUpdate();
    }

    /**
     * Remove a customer by its ID.
     * @param id ID of the customer
     * @throws SQLException if occur errors when modify database
     */
    public void removeCustomer(int id) throws SQLException {
        String sql = "DELETE FROM customer WHERE Cid=?";
        PreparedStatement ps = dbHelper.prepareStatement(sql);

        ps.setInt(1, id);

        ps.executeUpdate();
    }

    /**
     * Test customer is registered or not.
     * @param id ID of customer
     * @return {@code true} if registered or {@code false} not.
     */
    public boolean isCustomerRegistered(int id) {
        return selectString(String.format("SELECT Cname FROM customer WHERE Cid=%d", id)) != null;
    }

    /**
     * Returns number of registered customers.
     */
    public int getRegisteredCustomerCount() {
        String sql = "SELECT COUNT(Cid) FROM customer";
        return selectInteger(sql);
    }


    /**
     * Get customers by condition.
     * @param condition SQL query condition.
     */
    public java.util.List<Customer> getCustomers(String condition) {
        if (condition == null) {
            condition = "";
        }
        String sql = SQL_SELECT_CUSTOMER + condition;
        java.util.ArrayList<Customer> customers = new java.util.ArrayList<>();
        try {
            ResultSet rs = dbHelper.executeQuery(sql);
            while (rs.next()) {
                customers.add(gainCustomer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * Get customer by its ID.
     * @param id ID of the customer
     */
    public Customer getCustomer(int id) {
        java.util.List<Customer> customers = getCustomers(String.format("WHERE Cid=%d", id));
        if (customers.size() == 0) {
            return null;
        } else {
            return customers.get(0);
        }
    }

    // *****************************
    // ** Customer fields functions
    // *****************************

    /**
     * Returns a available customer ID.
     * <p>The ID not allocated any customer.</p>
     */
    public int getAvailableCustomerId() {
        String sql = "SELECT MAX(Cid) FROM customer";
        return selectInteger(sql) + 1;
    }


    /**
     * Get name of customer.
     * @param id ID of the customer
     * @return the name string or {@code null} if not found
     */
    public String getCustomerName(int id) {
        String sql = String.format("SELECT Cname FROM customer WHERE Cid=%d", id);
        return selectString(sql);
    }

    /**
     * Get level of customer.
     * @param id ID of the customer
     * @return level or -1 if occur errors
     */
    public int getCustomerLevel(int id) {
        String sql = String.format("SELECT Clevel FROM customer WHERE Cid=%d", id);
        return selectInteger(sql);
    }

    /**
     * Modify level of customer.
     * @param id ID of the customer
     * @param levelDiff changed level of the customer, if more than 0 increase level,
     *               if less than 0 reduce level, if 0 do nothing
     * @return new level
     * @throws SQLException if occur errors when modify database
     */
    private int modifyCustomerLevel(int id, int levelDiff) throws SQLException {
        int level = getCustomerLevel(id);
        if (levelDiff == 0) {
            return level;
        }
        int newLevel = level + levelDiff;
        if (newLevel < 0) {
            throw new IllegalArgumentException(
                    String.format("Invalid levelDiff, level: %d, levelDiff: %d", level, levelDiff));
        }
        String sql = "UPDATE customer SET Clevel=? WHERE Cid=?";
        PreparedStatement ps = dbHelper.prepareStatement(sql);

        ps.setInt(1, newLevel);
        ps.setInt(2, id);

        ps.executeUpdate();
        return newLevel;
    }

    /**
     * Get borrowing limits of customer.
     * @param id ID of the customer
     * @return limit or -1 if occur errors
     */
    public int geCustomerLimit(int id) {
        String sql = String.format("SELECT Climit FROM customer WHERE Cid=%d", id);
        return selectInteger(sql);
    }

    /**
     * Modify borrowing limits of customer.
     * @param id ID of the customer
     * @param limitDiff changed level of the customer, if more than 0 increase limit,
     *               if less than 0 reduce limit, if 0 do nothing
     * @return new limit
     * @throws SQLException if occur errors when modify database
     */
    private int modifyCustomerLimit(int id, int limitDiff) throws SQLException {
        int limit = geCustomerLimit(id);
        if (limitDiff == 0) {
            return limit;
        }
        int newLimit = limit + limitDiff;
        if (newLimit < 0) {
            throw new IllegalArgumentException(
                    String.format("Invalid limitDiff, limit: %d, limitDiff: %d", limit, limitDiff));
        }
        String sql = "UPDATE customer SET Climit=? WHERE Cid=?";
        PreparedStatement ps = dbHelper.prepareStatement(sql);

        ps.setInt(1, newLimit);
        ps.setInt(2, id);

        ps.executeUpdate();
        return newLimit;
    }

    // ********************
    // ** Store operations
    // ********************

    /**
     * Get the inventory number of book in stock.
     * @param isbn ISBN of queried book
     * @return number of inventory or -1 if not found the book
     */
    public int getInventory(String isbn) {
        String sql = String.format("SELECT Inumber FROM inventory WHERE Bisbn='%s'", isbn);
        return selectInteger(sql);
    }

    /**
     * Modify inventory in book stock.
     * @param isbn ISBN of the book
     * @param number changed number of the book, if more than 0 add inventory,
     *               if less than 0 reduce inventory, if 0 do nothing
     * @return new inventory
     * @throws SQLException if occur errors when modify database
     */
    private int modifyInventory(String isbn, int number) throws SQLException {
        int inventory = getInventory(isbn);
        if (number == 0) {
            return inventory;
        }
        /* get inventory of book */
        boolean existed;
        if (inventory == -1) {
            inventory = 0;
            existed = false;
        } else {
            existed = true;
        }
        int newInventory = inventory + number;
        if (newInventory < 0) {    // error number
            throw new IllegalArgumentException(
                    String.format("Invalid number, inventory: %d, number: %d", inventory, number));
        }
        String sql;
        if (existed) {
            sql = "UPDATE inventory SET Inumber=? WHERE Bisbn=?";
        } else {
            sql = "INSERT INTO inventory (Inumber, Bisbn) VALUES (?, ?)";
        }
        PreparedStatement ps = dbHelper.prepareStatement(sql);
        ps.setInt(1, newInventory);
        ps.setString(2, isbn);
        ps.executeUpdate();

        return newInventory;
    }

    /**
     * Returns total inventories.
     */
    public int getAllInventories() {
        String sql = "SELECT SUM(Inumber) FROM inventory";
        return selectInteger(sql);
    }

    /**
     * Get the sale number of specified book.
     * @param isbn ISBN of the book
     * @return number of sales or -1 if not found
     */
    public int getSaleCount(String isbn) {
        String sql = String.format("SELECT SUM(Snumber) FROM sale WHERE Bisbn='%s'", isbn);
        return selectInteger(sql);
    }

    /**
     * Returns total sales.
     */
    public int getSoldBookCount() {
        String sql = "SELECT SUM(Snumber) FROM sale";
        return selectInteger(sql);
    }

    public int getLentBookCount() {
        String sql = "SELECT SUM(Rnumber) FROM rental";
        return selectInteger(sql);
    }

//    /**
//     * Returns existing books in stock
//     */
//    public java.util.Map<String, Integer> getExistingBooks() {
//        String sql = "SELECT Bisbn, Inumber FROM inventory";
//        java.util.HashMap<String, Integer> results = new java.util.HashMap<>();
//        try {
//            ResultSet rs = dbHelper.executeQuery(sql);
//            while (rs.next()) {
//                results.put(normalizeString(rs.getString(1)), rs.getInt(2));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            results = null;
//        }
//        return results;
//    }

    /**
     * Fill a record to bill
     * @param date the date
     * @param event the event, maybe 1:stock, 2:sale, 3:rental, 4:return
     * @param id task id in its table
     */
    private void fillBill(java.util.Date date, int event, int id) throws SQLException {
        // get a available number
        int no = selectInteger("SELECT MAX(Lno) FROM bill");
        if (no < 0) {   // empty bill
            no = 0;
        }
        no++;

        String sql = "INSERT INTO bill(Lno, Ldate, Ltime, Levent, Lid) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = dbHelper.prepareStatement(sql);

        ps.setInt(1, no);
        ps.setDate(2, toSQLDate(date));
        ps.setTime(3, toSQLTime(date));
        ps.setInt(4, event);
        ps.setInt(5, id);

        ps.executeUpdate();
    }

    /**
     * Store book to book stock.
     * @param isbn the ISBN of book to stored
     * @param number the number of the book
     * @param price total price of those book
     * @param comment the comment
     */
    public void storeBook(String isbn, int number, BigDecimal price, String comment)
            throws SQLException {

        // increase inventory
        modifyInventory(isbn, number);

        // get a available ID
        int id = selectInteger("SELECT MAX(Tid) FROM stock");
        if (id < 0) {   // empty stock
            id = 0;
        }
        id++;

        // get today
        java.util.Date today = new java.util.Date();

        // fill stock listing
        String sql = "INSERT INTO stock (Tid, Bisbn, Tdate, Ttime, Tnumber, Ttotal, Tcomment)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = dbHelper.prepareStatement(sql);

        ps.setInt(1, id);
        ps.setString(2, isbn);
        ps.setDate(3, toSQLDate(today));
        ps.setTime(4, toSQLTime(today));
        ps.setInt(5, number);
        ps.setBigDecimal(6, price);
        ps.setString(7, comment);

        ps.executeUpdate();

        // fill bill
        fillBill(today, EVENT_STOCK, id);
    }

    /**
     * Sell book(s) to customer on today.
     * @param isbn the ISBN of sold book
     * @param id the ID of customer who buy the book
     * @param number number of the book to sold
     * @param price total price of those book
     * @param comment the comment
     */
    public void sellBook(String isbn, int id, int number, BigDecimal price, String comment)
            throws SQLException {

        // reduce inventory
        modifyInventory(isbn, -number);

        // get a available number
        int no = selectInteger("SELECT MAX(Sid) FROM sale");
        if (no < 0) {   // empty sale listing
            no = 0;
        }
        no++;

        // get today
        java.util.Date today = new java.util.Date();

        // fill sale listing
        String sql = "INSERT INTO sale (Sid, Bisbn, Cid, Sdate, Stime, Snumber, Stotal, Scomment)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = dbHelper.prepareStatement(sql);

        ps.setInt(1, no);
        ps.setString(2, isbn);
        ps.setInt(3, id);
        ps.setDate(4, toSQLDate(today));
        ps.setTime(5, toSQLTime(today));
        ps.setInt(6, number);
        ps.setBigDecimal(7, price);
        ps.setString(8, comment);

        ps.executeUpdate();

        // fill bill
        fillBill(today, EVENT_SALE, no);

        if (price.compareTo(new BigDecimal(0)) > 0) {  // price more than 0
            // increase level, totalPrice / PRICE_OF_INCREASE_LEVEL
            BigDecimal val = price.divide(new BigDecimal(Constants.PRICE_OF_INCREASE_LEVEL),
                    RoundingMode.FLOOR);
            modifyCustomerLevel(id, val.intValue());

            // increase borrowing limits, totalPrice / PRICE_OF_INCREASE_LIMIT
            val = price.divide(new BigDecimal(Constants.PRICE_OF_INCREASE_LIMIT), RoundingMode.FLOOR);
            modifyCustomerLimit(id, val.intValue());
        }
    }

    /**
     * Lend book(s) to customer on today.
     * @param isbn the ISBN of lent book
     * @param id the ID of customer who borrow the book
     * @param number number of the book to lend
     * @param period days if the customer borrow the book
     * @param price rental price of each book
     * @param deposit the deposit price of those book
     * @param comment the comment
     */
    public void lendBook(String isbn, int id, int number, int period, BigDecimal price,
                         BigDecimal deposit, String comment)
            throws SQLException {

        // reduce inventory
        modifyInventory(isbn, -number);

        // get a available number
        int no = selectInteger("SELECT MAX(Rid) FROM rental");
        if (no < 0) {   // empty sale listing
            no = 0;
        }
        no++;

        // get today
        java.util.Date today = new java.util.Date();
        String sql = "INSERT INTO rental (Rid, Bisbn, Cid, Rdate, Rtime, Rnumber, Rperiod, Rprice," +
                " Rdeposit, Rrevenue, Rcomment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = dbHelper.prepareStatement(sql);

        ps.setInt(1, no);
        ps.setString(2, isbn);
        ps.setInt(3, id);
        ps.setDate(4, toSQLDate(today));
        ps.setTime(5, toSQLTime(today));
        ps.setInt(6, number);
        ps.setInt(7, period);
        ps.setBigDecimal(8, price);
        ps.setBigDecimal(9, deposit);
        ps.setBigDecimal(10, new BigDecimal(0));
        ps.setString(11, comment);

        ps.executeUpdate();

        // reduce lent limit
        modifyCustomerLimit(id, -number);

        // fill bill
        fillBill(today, EVENT_RENTAL, no);
    }

    /**
     * Customer return book.
     * @param id the ID of customer who borrowed book
     * @param isbn ISBN of the returned book
     * @param number number of the returned book
     */
    public void returnBook(int id, String isbn, int number) throws SQLException {
        String sql = String.format("SELECT Rdate, Rtime, Rnumber, Rperiod FROM rental WHERE Bisbn='%s' AND Cid=%d",
                isbn, id);
        ResultSet rs = dbHelper.executeQuery(sql);
        while (rs.next()) {
            // TODO: add return operation
            break;
        }
    }

    /**
     * Notify customer return book.
     * @param id the ID of customer who borrowed book
     * @param isbn ISBN of the book that customer borrowed
     */
    public void notifyReturn(int id, String isbn) {

    }

    public BigDecimal getStockSpending() {
        String sql = "SELECT SUM(Ttotal) FROM stock";
        return selectDecimal(sql);
    }

    public BigDecimal getSaleRevenue() {
        String sql = "SELECT SUM(Stotal) FROM sale";
        return selectDecimal(sql);
    }

    public BigDecimal getRentalRevenue() {
        return new BigDecimal(0);
    }

    public BigDecimal getTotalRevenue() {
        return new BigDecimal(0);
    }

    /**
     * Create new book and register to database.
     * @return the book, if {@code null} user cancel or register failed
     */
    public Book newBook() {
        Application app = Application.getInstance();
        ModifyBookDialog dialog = new ModifyBookDialog();
        dialog.setTitle(app.getString("Dialog.AddBook.Title"));

        Book book;
        while (true) {
            dialog.setVisible(true);
            book = dialog.getBook();
            if (book == null) {
                break;
            }
            try {
                registerBook(book);
                break;
            } catch (SQLException e) {
                String state = e.getSQLState();
                if ("23505".equals(state)) {
                    DialogFactory.showError(String.format(app.getString("Dialog.AddBook.Existed"), book.getISBN()),
                            app.getString("Dialog.AddBook.Title"));
                } else {
                    e.printStackTrace();
                }
            }
        }
        return book;
    }

    /**
     * Create new customer and register to database.
     * @return the customer, if {@code null} user cancel or register failed
     */
    public Customer newCustomer() {
        ModifyCustomerDialog dialog = new ModifyCustomerDialog();
        dialog.setTitle(Application.getInstance().getString("Dialog.AddCustomer.Title"));
        dialog.setVisible(true);

        Customer customer = dialog.getCustomer();
        if (customer == null) {
            return null;
        }
        customer.setId(getAvailableCustomerId());
        try {
            registerCustomer(customer);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

    private DbHelper dbHelper = null;

    /** Unique Work instance */
    private static Worker instance = null;
}
