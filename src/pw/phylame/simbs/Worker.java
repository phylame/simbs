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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Function of SIMBS.
 */
public final class Worker {

    public static String toDateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        return sdf.format(date);
    }

    public static String toTimeString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("H:m:s");
        return sdf.format(date);
    }

    private Worker() {
        sqlAdmin = SqlAdmin.getInstance();
    }

    public static Worker getInstance() {
        if (instance == null) {
            instance = new Worker();
        }
        return instance;
    }

    public void destroy() {
        try {
            sqlAdmin.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Book createBookInfo() {
        return null;
    }

    public void addBookInfo(Book book) {

    }

    public void updateBookInfo(Book book, String isbn) {

    }

    public Customer createCustomerInfo() {
        return null;
    }

    public void addCustomerInfo(Customer customer) {

    }

    public void updateCustomerInfo(Customer customer, int id) {

    }

    /**
     * Get books by condition.
     * @param condition SQL query condition.
     */
    public Book[] getBooks(String condition) {
        if (condition == null) {
            condition = "";
        }
        String sql = "SELECT Bisbn, Bname, Bversion, Bauthors, Bdate, Bcategory, Bpublisher," +
                " Bprice, Bintro FROM book_info "+condition;
        ArrayList<Book> books = new ArrayList<>();
        try {
            ResultSet rs = sqlAdmin.executeQuery(sql);
            while (rs.next()) {
                Book book = new Book(rs.getString(1).trim(), rs.getString(2).trim(), rs.getString(3).trim(),
                        rs.getString(4).trim(), rs.getDate(5), rs.getString(6).trim(), rs.getString(7).trim(),
                        rs.getFloat(8), rs.getString(9));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books.toArray(new Book[0]);
    }

    /**
     * Get book by its ISBN.
     * @param isbn ISBN of the book
     */
    public Book getBook(String isbn) {
        Book[] books = getBooks("WHERE Bisbn='"+isbn+"'");
        if (books == null || books.length == 0) {
            return null;
        } else {
            return books[0];
        }
    }

    /**
     * Returns number of books.
     */
    public int getBookNumber() {
        String sql = "SELECT COUNT(Bisbn) FROM book_info";
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

    /**
     * Get customers by condition.
     * @param condition SQL query condition.
     */
    public Customer[] getCustomers(String condition) {
        if (condition == null) {
            condition = "";
        }
        String sql = "SELECT Cid, Cname, Cphone, Cemail FROM customer_info "+condition;
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            ResultSet rs = sqlAdmin.executeQuery(sql);
            while (rs.next()) {
                Customer customer = new Customer(rs.getInt(1), rs.getString(2).trim(), rs.getString(3).trim(),
                        rs.getString(4).trim());
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers.toArray(new Customer[0]);
    }

    /**
     * Get customer by its ID.
     * @param id ID of the customer
     */
    public Customer getCustomer(int id) {
        Customer[] customers = getCustomers("WHERE Cid=" + id);
        if (customers == null || customers.length == 0) {
            return null;
        } else {
            return customers[0];
        }
    }

    /**
     * Returns number of customers.
     */
    public int getCustomerNumber() {
        String sql = "SELECT COUNT(Cid) FROM customer_info";
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

    /**
     * Returns the max ID in customer_info.
     */
    public int getMaxCustomerId() {
        String sql = "SELECT MAX(Cid) FROM customer_info";
        int id = -1;
        try {
            ResultSet rs = sqlAdmin.executeQuery(sql);
            while (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * Get the existing number of book in stock.
     * @param isbn ISBN of queried book
     * @return number of existing book
     */
    public int getExistingBookNumber(String isbn) {
        String sql = "SELECT Enumber FROM book_stock WHERE Bisbn='"+isbn+"'";
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

    private void addStockRecord(String isbn, int number) throws SQLException {
        String sql = String.format("INSERT INTO book_stock(Bisbn, Enumber) VAULES('%s', %d)", isbn, number);
        sqlAdmin.executeUpdate(sql);
    }

    private void modifyStockRecord(String isbn, int number) throws SQLException {
        /* get current number of book */
        int existingNumber = getExistingBookNumber(isbn);
        if (existingNumber == -1) {
            return;
        }
        existingNumber += number;
        String sql = "UPDATE book_stock SET Enumber="+existingNumber+" WHERE Bisbn='"+isbn+"'";
        sqlAdmin.executeUpdate(sql);
    }

    private void addSoldRecord(String isbn, int id, Date date, int number) throws SQLException {
        String sql = String.format(
                "INSERT INTO sold_book(Bisbn, Cid, Sdate, Stime, Snumber) VAULES('%s', %d, '%s', '%s', %d)",
                isbn, id, Worker.toDateString(date), Worker.toTimeString(date), number);
        sqlAdmin.executeUpdate(sql);
    }

    private void addLentRecord(String isbn, int id, Date date, int number, int period) throws SQLException {
        String sql = String.format(
                "INSERT INTO lent_book(Bisbn, Cid, Ldate, Ltime, Lnumber, Lperiod) VAULES('%s', %d, '%s', '%s', %d, %d)",
                isbn, id, Worker.toDateString(date), Worker.toTimeString(date), number, period);
        sqlAdmin.executeUpdate(sql);
    }

    /**
     * Store book to book stock.
     * @param isbn the ISBN of book to stored
     * @param number the number of the book
     */
    public void storeBook(String isbn, int number) {
        SimbsApplication app = SimbsApplication.getInstance();
        Book book = getBook(isbn);
        if (book == null) {     // book not in book_info
            DialogFactory.showError(String.format(app.getString("Store.NoBook"), isbn),  app.getString("Store.Title"));
            return;
        }
        try {
            if (getExistingBookNumber(isbn) < 0) {   // not in stock
                addStockRecord(isbn, number);
            } else {
                modifyStockRecord(isbn, number);
            }
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    /**
     * Sell book(s) to customer on today.
     * @param isbn the ISBN of sold book
     * @param id the ID of customer who buy the book
     * @param number number of the book to sold
     */
    public void sellBook(String isbn, int id, int number) {
        SimbsApplication app = SimbsApplication.getInstance();
        Book book = getBook(isbn);
        if (book == null) {
            DialogFactory.showError(String.format(app.getString("Sell.NotFoundBook"), isbn),
                    app.getString("Sell.Title"));
            return;
        }

        int existingNumber = getExistingBookNumber(isbn);
        if (existingNumber <= 0) {
            DialogFactory.showWarning(String.format(app.getString("Sell.StockEmpty"), book.getName()),
                    app.getString("Sell.Title"));
            return;
        } else if (number > existingNumber) {
            DialogFactory.showWarning(String.format(app.getString("Sell.NoEnoughBook"), book.getName()),
                    app.getString("Sell.Title"));
            return;
        }
        Customer customer = getCustomer(id);
        if (customer == null) {
            DialogFactory.showError(String.format(app.getString("Sell.NotFoundCustomer"), id),
                    app.getString("Sell.Title"));
            return;
        }
        try {
            modifyStockRecord(isbn, -number);
            addSoldRecord(isbn, id, new Date(), number);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lend book(s) to customer on today.
     * @param isbn the ISBN of lent book
     * @param id the ID of customer who borrow the book
     * @param number number of the book to lend
     * @param period days if the customer borrow the book
     */
    public void lendBook(String isbn, int id, int number, int period) {
        SimbsApplication app = SimbsApplication.getInstance();
        Book book = getBook(isbn);
        if (book == null) {     // not in book_info
            DialogFactory.showError(String.format(app.getString("Lend.NotFoundBook"), isbn),
                    app.getString("Lend.Title"));
            return;
        }

        int existingNumber = getExistingBookNumber(isbn);
        if (existingNumber <= 0) {
            DialogFactory.showWarning(String.format(app.getString("Lend.StockEmpty"), book.getName()),
                    app.getString("Lend.Title"));
            return;
        } else if (number > existingNumber) {
            DialogFactory.showWarning(String.format(app.getString("Lend.NoEnoughBook"), book.getName()),
                    app.getString("Lend.Title"));
            return;
        }
        Customer customer = getCustomer(id);
        if (customer == null) {
            DialogFactory.showError(String.format(app.getString("Lend.NotFoundCustomer"), id),
                    app.getString("Lend.Title"));
            return;
        }
        try {
            modifyStockRecord(isbn, -number);
            addLentRecord(isbn, id, new Date(), number, period);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Customer return book.
     * @param id the ID of customer who borrowed book
     * @param isbn ISBN of the returned book
     * @param number number of the returned book
     */
    public void returnBook(int id, String isbn, int number) {
        SimbsApplication app = SimbsApplication.getInstance();
        Book book = getBook(isbn);
        if (book == null) {     // not in book_info
            DialogFactory.showError(String.format(app.getString("Return.NotFoundBook"), isbn),
                    app.getString("Return.Title"));
            return;
        }
        String sql = String.format("SELECT Ldate, Ltime, Lnumber, Lperiod FROM lent_book WHERE Bisbn='%s' AND Cid=%d",
                isbn, id);
        try {
            ResultSet rs = sqlAdmin.executeQuery(sql);
            while (rs.next()) {
                // TODO: add return operation
                break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify customer return book.
     * @param id the ID of customer who borrowed book
     * @param isbn ISBN of the book that customer borrowed
     */
    public void notifyReturn(int id, String isbn) {

    }

    private static Worker instance = null;

    private SqlAdmin sqlAdmin = null;
}
