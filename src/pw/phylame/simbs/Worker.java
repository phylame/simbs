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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Function of SIMBS.
 */
public final class Worker {
    public static final String SQL_SELECT_BOOK = "SELECT Bisbn, Bname, Bversion, Bauthors, Bdate, Bcategory," +
            " Bpublisher, Bprice, Bintro FROM book_info ";
    public static final String SQL_REGISTER_BOOK = "INSERT INTO book_info(Bisbn, Bname, Bversion, Bauthors," +
            " Bdate, Bcategory, Bpublisher, Bprice, Bintro) VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', %f, '%s') ";

    public static final String SQL_SELECT_CUSTOMER = "SELECT Cid, Cname, Cphone, Cemail, Clevel, Clent_limit " +
            "FROM customer_info ";
    public static final String SQL_REGISTER_CUSTOMER = "INSERT INTO customer_info(Cid, Cname, Cphone, Cemail," +
            " Clevel, Clent_limit) VALUES(%d, '%s', '%s', '%s', %d, %d) ";

    public static String toDateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        return sdf.format(date);
    }

    public static String toTimeString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("H:m:s");
        return sdf.format(date);
    }

    public Worker(SQLAdmin sqlAdmin) {
        this.sqlAdmin = sqlAdmin;
        Worker.instance = this;
    }

    public static Worker getInstance() {
        return instance;
    }

    public void destroy() {
        try {
            sqlAdmin.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getIntegerValue(String sql) {
        int number = -1;
        try {
            ResultSet rs = sqlAdmin.executeQuery(sql);
            while (rs.next()) {
                number = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return number;
    }

    private float getFloatValue(String sql) {
        float number = -1.0F;
        try {
            ResultSet rs = sqlAdmin.executeQuery(sql);
            while (rs.next()) {
                number = rs.getFloat(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return number;
    }

    private String getStringValue(String sql) {
        String value = null;
        try {
            ResultSet rs = sqlAdmin.executeQuery(sql);
            while (rs.next()) {
                value = rs.getString(1).trim();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static Book getBookFromResultSet(ResultSet rs) throws SQLException {
        return new Book(rs.getString(1).trim(), rs.getString(2).trim(), rs.getString(3).trim(),
                rs.getString(4).trim(), rs.getDate(5), rs.getString(6).trim(), rs.getString(7).trim(),
                rs.getFloat(8), rs.getString(9));
    }

    /**
     * Get books by condition.
     * @param condition SQL query condition.
     */
    public List<Book> getBooks(String condition) {
        if (condition == null) {
            condition = "";
        }
        String sql = SQL_SELECT_BOOK + condition;
        ArrayList<Book> books = new ArrayList<>();
        try {
            ResultSet rs = sqlAdmin.executeQuery(sql);
            while (rs.next()) {
                books.add(getBookFromResultSet(rs));
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
        List<Book> books = getBooks("WHERE Bisbn='"+isbn+"'");
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
        String sql = "SELECT COUNT(Bisbn) FROM book_info";
        return getIntegerValue(sql);
    }

    public static Customer getCustomerFromResultSet(ResultSet rs) throws SQLException {
        return new Customer(rs.getInt(1), rs.getString(2).trim(), rs.getString(3).trim(),
                rs.getString(4).trim(), rs.getInt(5), rs.getInt(6));
    }

    /**
     * Get customers by condition.
     * @param condition SQL query condition.
     */
    public List<Customer> getCustomers(String condition) {
        if (condition == null) {
            condition = "";
        }
        String sql = SQL_SELECT_CUSTOMER + condition;
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            ResultSet rs = sqlAdmin.executeQuery(sql);
            while (rs.next()) {
                customers.add(getCustomerFromResultSet(rs));
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
        List<Customer> customers = getCustomers("WHERE Cid=" + id);
        if (customers.size() == 0) {
            return null;
        } else {
            return customers.get(0);
        }
    }

    /**
     * Returns number of customers.
     */
    public int getRegisteredCustomerCount() {
        String sql = "SELECT COUNT(Cid) FROM customer_info";
        return getIntegerValue(sql);
    }

    /**
     * Returns a available customer ID.
     * <p>The ID not allocated any customer.</p>
     */
    public int getAvailableCustomerId() {
        String sql = "SELECT MAX(Cid) FROM customer_info";
        return getIntegerValue(sql) + 1;
    }

    /**
     * Get the existing number of book in stock.
     * @param isbn ISBN of queried book
     * @return number of existing book
     */
    public int getInventory(String isbn) {
        String sql = "SELECT Enumber FROM book_stock WHERE Bisbn='"+isbn+"'";
        return getIntegerValue(sql);
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
            sql = "UPDATE book_stock SET Enumber=" + newInventory + " WHERE Bisbn='" + isbn + "'";
        } else {
            sql = String.format("INSERT INTO book_stock(Bisbn, Enumber) VALUES('%s', %d)", isbn, newInventory);
        }
        sqlAdmin.executeUpdate(sql);
        return newInventory;
    }

    /**
     * Returns total inventories.
     */
    public int getTotalInventories() {
        String sql = "SELECT SUM(Enumber) FROM book_stock";
        return getIntegerValue(sql);
    }

    /**
     * Get the sales of specified book.
     * @param isbn ISBN of the book
     * @return number of sales
     */
    public int getSales(String isbn) {
        String sql = "SELECT Snumber FROM sold_book WHERE Bisbn='"+isbn+"'";
        return getIntegerValue(sql);
    }

    /**
     * Returns total sales.
     */
    public int getTotalSales() {
        String sql = "SELECT SUM(Snumber) FROM sold_book";
        return getIntegerValue(sql);
    }

    public int getLentNumber() {
        String sql = "SELECT SUM(Lnumber) FROM lent_book";
        return getIntegerValue(sql);
    }

    /**
     * Returns existing books in stock
     */
    public java.util.Map<String, Integer> getExistingBooks() {
        String sql = "SELECT Bisbn, Enumber FROM book_stock";
        HashMap<String, Integer> results = new HashMap<>();
        try {
            ResultSet rs = sqlAdmin.executeQuery(sql);
            while (rs.next()) {
                results.put(rs.getString(1).trim(), rs.getInt(2));
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
        String sql = "SELECT Clevel FROM customer_info WHERE Cid="+id;
        return getIntegerValue(sql);
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
        String sql = "UPDATE customer_info SET Clevel=" + newLevel + " WHERE Cid=" + id;
        sqlAdmin.executeUpdate(sql);
        return newLevel;
    }

    /**
     * Get lent limit of customer.
     * @param id ID of the customer
     * @return limit or -1 if occur errors
     */
    public int getLentLimit(int id) {
        String sql = "SELECT Clent_limit FROM customer_info WHERE Cid="+id;
        return getIntegerValue(sql);
    }

    /**
     * Modify lent limit of customer.
     * @param id ID of the customer
     * @param limitDiff changed level of the customer, if more than 0 increase limit,
     *               if less than 0 reduce limit, if 0 do nothing
     * @return new limit
     * @throws SQLException if occur errors when modify database
     */
    private int modifyLentLimit(int id, int limitDiff) throws SQLException {
        int limit = getLentLimit(id);
        if (limitDiff == 0) {
            return limit;
        }
        int newLimit = limit + limitDiff;
        if (newLimit < 0) {
            throw new IllegalArgumentException(
                    String.format("Invalid limitDiff, limit: %d, limitDiff: %d", limit, limitDiff));
        }
        String sql = "UPDATE customer_info SET Clent_limit=" + newLimit + " WHERE Cid=" + id;
        sqlAdmin.executeUpdate(sql);
        return newLimit;
    }

    /**
     * Register a book
     * @param book the book
     */
    public void registerBook(Book book) throws SQLException {
        String sql = String.format(SQL_REGISTER_BOOK, book.getISBN(), book.getName(), book.getVersion(),
                book.getAuthors(), Worker.toDateString(book.getDate()), book.getCategory(), book.getPublisher(),
                book.getPrice(), book.getIntro());
        sqlAdmin.executeUpdate(sql);
    }

    /**
     * Register a customer
     * @param customer the customer.
     */
    public void registerCustomer(Customer customer) throws SQLException {
        String sql = String.format(SQL_REGISTER_CUSTOMER, customer.getId(), customer.getName(), customer.getPhone(),
                customer.getEmail(), customer.getLevel(), customer.getLentLimit());
        sqlAdmin.executeUpdate(sql);
    }

    /**
     * Get the price of book.
     * @param isbn ISBN of the book
     * @return price, if price < 0 occur errors
     */
    public double getPrice(String isbn) {
        String sql = "SELECT Bprice from book_info WHERE Bisbn='"+isbn+"'";
        return getFloatValue(sql);
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
     */
    public void sellBook(String isbn, int id, int number) throws SQLException {
        // reduce inventory
        modifyInventory(isbn, -number);
        Date today = new Date();
        String sql = String.format(
                "INSERT INTO sold_book(Bisbn, Cid, Sdate, Stime, Snumber) VALUES('%s', %d, '%s', '%s', %d)",
                isbn, id, Worker.toDateString(today), Worker.toTimeString(today), number);
        sqlAdmin.executeUpdate(sql);

        double totalPrice = getPrice(isbn) * number;
        if (totalPrice > 0) {
            // increase level
            modifyLevel(id, (int) (totalPrice/Constants.PRICE_OF_INCREASE_LEVEL));
            // increase lent limit
            modifyLentLimit(id, (int) (totalPrice/Constants.PRICE_OF_INCRESE_LENT_LIMIT));
        }
    }

    /**
     * Lend book(s) to customer on today.
     * @param isbn the ISBN of lent book
     * @param id the ID of customer who borrow the book
     * @param number number of the book to lend
     * @param period days if the customer borrow the book
     */
    public void lendBook(String isbn, int id, int number, int period) throws SQLException {
        // reduce inventory
        modifyInventory(isbn, -number);
        Date today = new Date();
        String sql = String.format(
                "INSERT INTO lent_book(Bisbn, Cid, Ldate, Ltime, Lnumber, Lperiod) VALUES('%s', %d, '%s', '%s', %d, %d)",
                isbn, id, Worker.toDateString(today), Worker.toTimeString(today), number, period);
        sqlAdmin.executeUpdate(sql);
        // reduce lent limit
        modifyLentLimit(id, -number);
    }

    /**
     * Customer return book.
     * @param id the ID of customer who borrowed book
     * @param isbn ISBN of the returned book
     * @param number number of the returned book
     */
    public void returnBook(int id, String isbn, int number) throws SQLException {
        String sql = String.format("SELECT Ldate, Ltime, Lnumber, Lperiod FROM lent_book WHERE Bisbn='%s' AND Cid=%d",
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
