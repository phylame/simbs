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
import pw.phylame.tools.sql.SQLAdmin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


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

    public Worker(SQLAdmin sqlAdmin) {
        this.sqlAdmin = sqlAdmin;
        Worker.instance = this;
    }

    public static Worker getInstance() {
        return instance;
    }

    /** Destroy worker instance */
    public void destroy() {
        try {
            sqlAdmin.disconnect();
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
            ResultSet rs = sqlAdmin.executeQuery(sql);
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
            ResultSet rs = sqlAdmin.executeQuery(sql);
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
            ResultSet rs = sqlAdmin.executeQuery(sql);
            if (rs.next()) {
                value = normalizeString(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return value;
    }

    public static Book getBookFromResultSet(ResultSet rs) throws SQLException {
        return new Book(normalizeString(rs.getString(1)), normalizeString(rs.getString(2)),
                normalizeString(rs.getString(3)), normalizeString(rs.getString(4)),
                normalizeString(rs.getString(5)), rs.getDate(6), normalizeString(rs.getString(7)),
                normalizeString(rs.getString(8)), rs.getBigDecimal(9),
                normalizeString(rs.getString(10)));
    }

    /**
     * Get books by condition.
     * @param condition SQL query condition.
     */
    public java.util.List<Book> getBooks(String condition) {
        if (condition == null) {
            condition = "";
        }
        String sql = "SELECT Bisbn, Bname, Bversion, Bauthors, Bcover, Bdate, Bcategory, Bpublisher," +
                " Bprice, Bintro FROM book " + condition;
        java.util.ArrayList<Book> books = new java.util.ArrayList<>();
        try {
            ResultSet rs = sqlAdmin.executeQuery(sql);
            while (rs.next()) {
                Book book = getBookFromResultSet(rs);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Get book by its ISBN.
     * @param isbn ISBN of the book
     */
    public Book getBook(String isbn) {
        java.util.List<Book> books = getBooks(String.format("WHERE Bisbn='%s'", isbn));
        if (books.size() == 0) {
            return null;
        } else {
            return books.get(0);
        }
    }

    /**
     * Returns number of registered books.
     */
    public int getRegisteredBookNumber() {
        String sql = "SELECT COUNT(Bisbn) FROM book";
        return selectInteger(sql);
    }

    /**
     * Test book is registered or not.
     * @param isbn ISBN of book
     * @return {@code true} if registered or {@code false} not.
     */
    public boolean isBookRegistered(String isbn) {
        return selectString(String.format("SELECT Bname FROM book WHERE Bisbn='%s'", isbn)) != null;
    }

    public static Customer getCustomerFromResultSet(ResultSet rs) throws SQLException {
        return new Customer(rs.getInt(1), normalizeString(rs.getString(2)),
                normalizeString(rs.getString(3)), normalizeString(rs.getString(4)),
                rs.getInt(5), rs.getInt(6));
    }

    /**
     * Get customers by condition.
     * @param condition SQL query condition.
     */
    public java.util.List<Customer> getCustomers(String condition) {
        if (condition == null) {
            condition = "";
        }
        String sql = "SELECT Cid, Cname, Cphone, Cemail, Clevel, Climit FROM customer " + condition;
        java.util.ArrayList<Customer> customers = new java.util.ArrayList<>();
        try {
            ResultSet rs = sqlAdmin.executeQuery(sql);
            while (rs.next()) {
                Customer customer = getCustomerFromResultSet(rs);
                customers.add(customer);
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

    /**
     * Returns number of registered customers.
     */
    public int getRegisteredCustomerCount() {
        String sql = "SELECT COUNT(Cid) FROM customer";
        return selectInteger(sql);
    }

    /**
     * Returns a available customer ID.
     * <p>The ID not allocated any customer.</p>
     */
    public int getAvailableCustomerId() {
        String sql = "SELECT MAX(Cid) FROM customer";
        return selectInteger(sql) + 1;
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
     * Get the inventory number of book in stock.
     * @param isbn ISBN of queried book
     * @return number of inventory or -1 if not found the book
     */
    public int getInventory(String isbn) {
        String sql = String.format("SELECT Inumber FROM stock WHERE Bisbn='%s'", isbn);
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
            sql = "UPDATE stock SET Inumber=? WHERE Bisbn=?";
        } else {
            sql = "INSERT INTO stock (Inumber, Bisbn) VALUES (?, ?)";
        }
        PreparedStatement ps = sqlAdmin.prepareStatement(sql);
        ps.setInt(1, newInventory);
        ps.setString(2, isbn);
        ps.executeUpdate();

        return newInventory;
    }

    /**
     * Returns total inventories.
     */
    public int getTotalInventories() {
        String sql = "SELECT SUM(Inumber) FROM stock";
        return selectInteger(sql);
    }

    /**
     * Get the sale of specified book.
     * @param isbn ISBN of the book
     * @return number of sales
     */
    public int getSale(String isbn) {
        String sql = String.format("SELECT Snumber FROM sale WHERE Bisbn='%s'", isbn);
        return selectInteger(sql);
    }

    /**
     * Returns total sales.
     */
    public int getTotalSales() {
        String sql = "SELECT SUM(Snumber) FROM sale";
        return selectInteger(sql);
    }

    public int getLentNumber() {
        String sql = "SELECT SUM(Rnumber) FROM rental";
        return selectInteger(sql);
    }

    /**
     * Returns existing books in stock
     */
    public java.util.Map<String, Integer> getExistingBooks() {
        String sql = "SELECT Bisbn, Inumber FROM stock";
        java.util.HashMap<String, Integer> results = new java.util.HashMap<>();
        try {
            ResultSet rs = sqlAdmin.executeQuery(sql);
            while (rs.next()) {
                results.put(normalizeString(rs.getString(1)), rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            results = null;
        }
        return results;
    }

    /**
     * Get level of customer.
     * @param id ID of the customer
     * @return level or -1 if occur errors
     */
    public int getLevel(int id) {
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
    private int modifyLevel(int id, int levelDiff) throws SQLException {
        int level = getLevel(id);
        if (levelDiff == 0) {
            return level;
        }
        int newLevel = level + levelDiff;
        if (newLevel < 0) {
            throw new IllegalArgumentException(
                    String.format("Invalid levelDiff, level: %d, levelDiff: %d", level, levelDiff));
        }
        String sql = "UPDATE customer SET Clevel=? WHERE Cid=?";
        PreparedStatement ps = sqlAdmin.prepareStatement(sql);

        ps.setInt(1, newLevel);
        ps.setInt(2, id);

        ps.executeUpdate();
        return newLevel;
    }

    /**
     * Get lent limit of customer.
     * @param id ID of the customer
     * @return limit or -1 if occur errors
     */
    public int geLimit(int id) {
        String sql = String.format("SELECT Climit FROM customer WHERE Cid=%d", id);
        return selectInteger(sql);
    }

    /**
     * Modify lent limit of customer.
     * @param id ID of the customer
     * @param limitDiff changed level of the customer, if more than 0 increase limit,
     *               if less than 0 reduce limit, if 0 do nothing
     * @return new limit
     * @throws SQLException if occur errors when modify database
     */
    private int modifyLimit(int id, int limitDiff) throws SQLException {
        int limit = geLimit(id);
        if (limitDiff == 0) {
            return limit;
        }
        int newLimit = limit + limitDiff;
        if (newLimit < 0) {
            throw new IllegalArgumentException(
                    String.format("Invalid limitDiff, limit: %d, limitDiff: %d", limit, limitDiff));
        }
        String sql = "UPDATE customer SET Climit=? WHERE Cid=?";
        PreparedStatement ps = sqlAdmin.prepareStatement(sql);

        ps.setInt(1, newLimit);
        ps.setInt(2, id);

        ps.executeUpdate();
        return newLimit;
    }

    /**
     * Register a book
     * @param book the book
     */
    public void registerBook(Book book) throws SQLException {
        String sql = "INSERT INTO book (Bisbn, Bname, Bversion, Bauthors, Bcover, Bdate, Bcategory," +
                " Bpublisher, Bprice, Bintro) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        PreparedStatement ps = sqlAdmin.prepareStatement(sql);

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
     * Register a customer
     * @param customer the customer.
     */
    public void registerCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customer (Cid, Cname, Cphone, Cemail, Clevel, Climit) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = sqlAdmin.prepareStatement(sql);

        ps.setInt(1, customer.getId());
        ps.setString(2, customer.getName());
        ps.setString(3, customer.getPhone());
        ps.setString(4, customer.getEmail());
        ps.setInt(5, customer.getLevel());
        ps.setInt(6, customer.getLimit());

        ps.executeUpdate();
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

    /**
     * Store book to book stock.
     * @param isbn the ISBN of book to stored
     * @param number the number of the book
     */
    public void storeBook(String isbn, int number) throws SQLException {
        // increase inventory
        modifyInventory(isbn, number);
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
        // get today
        java.util.Date today = new java.util.Date();

        String sql = "INSERT INTO sale(Bisbn, Cid, Sdate, Stime, Snumber, Stotal, Scomment)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = sqlAdmin.prepareStatement(sql);

        ps.setString(1, isbn);
        ps.setInt(2, id);
        ps.setDate(3, toSQLDate(today));
        ps.setTime(4, toSQLTime(today));
        ps.setInt(5, number);
        ps.setBigDecimal(6, price);
        ps.setString(7, comment);

        ps.executeUpdate();

        if (price.compareTo(new BigDecimal(0)) > 0) {  // more than 0
            // increase level, totalPrice / PRICE_OF_INCREASE_LEVEL
            BigDecimal val = price.divide(new BigDecimal(Constants.PRICE_OF_INCREASE_LEVEL),
                    RoundingMode.FLOOR);
            modifyLevel(id, val.intValue());

            // increase lent limit, totalPrice / PRICE_OF_INCREASE_LIMIT
            val = price.divide(new BigDecimal(Constants.PRICE_OF_INCREASE_LIMIT), RoundingMode.FLOOR);
            modifyLimit(id, val.intValue());
        }
    }

    /**
     * Lend book(s) to customer on today.
     * @param isbn the ISBN of lent book
     * @param id the ID of customer who borrow the book
     * @param number number of the book to lend
     * @param period days if the customer borrow the book
     * @param price total price of those book
     * @param comment the comment
     */
    public void lendBook(String isbn, int id, int number, int period, BigDecimal price, String comment)
            throws SQLException {
        // reduce inventory
        modifyInventory(isbn, -number);
        java.util.Date today = new java.util.Date();
        String sql = "INSERT INTO rental (Bisbn, Cid, Rdate, Rtime, Rnumber, Rperiod, Rtotal, Rcomment)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = sqlAdmin.prepareStatement(sql);

        ps.setString(1, isbn);
        ps.setInt(2, id);
        ps.setDate(3, toSQLDate(today));
        ps.setTime(4, toSQLTime(today));
        ps.setInt(5, number);
        ps.setInt(6, period);
        ps.setBigDecimal(7, price);
        ps.setString(8, comment);

        ps.executeUpdate();

        // reduce lent limit
        modifyLimit(id, -number);
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
        ResultSet rs = sqlAdmin.executeQuery(sql);
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

    private SQLAdmin sqlAdmin = null;

    /** Unique Work instance */
    private static Worker instance = null;
}
