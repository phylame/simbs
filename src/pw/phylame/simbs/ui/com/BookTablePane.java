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

package pw.phylame.simbs.ui.com;

import pw.phylame.simbs.Application;
import pw.phylame.simbs.Worker;
import pw.phylame.simbs.ds.Book;
import pw.phylame.tools.sql.PagingResultSet;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Peng Wan on 2015-1-7.
 */
public class BookTablePane extends ViewerTablePane {
    public static final int BOOK_COLUMN_COUNT = 6 + 2;  // sale and rental number
    public static final String SQL_SELECT_BOOK = "SELECT B.Bisbn, Bname, Bauthors, Bpublisher, Bprice, Inumber " +
            "FROM book AS B " +
            "LEFT JOIN inventory AS I ON I.Bisbn = B.Bisbn ";

    public static final int MAX_ROW_COUNT = 20;

    public BookTablePane() {
        super(new BookTableModel(), SQL_SELECT_BOOK, MAX_ROW_COUNT);
    }

    @Override
    public void headerClicked(int columnIndex) {

    }

    public String getSelectedBook() {
        BookTableModel tableModel = (BookTableModel) getTableModel();
        return tableModel.getISBN(getSelectedRow());
    }

    public String[] getSelectedBooks() {
        int[] rows = getSelectedRows();
        if (rows == null) {
            return null;
        }
        String[] books = new String[rows.length];
        int i = 0;
        BookTableModel tableModel = (BookTableModel) getTableModel();
        for (int row: rows) {
            books[i++] = tableModel.getISBN(row);
        }
        return books;
    }

    /** Update book at row */
    public void updateBook(int row, Book book) {
        BookTableModel tableModel = (BookTableModel) getTableModel();
        tableModel.updateBook(row, book);
    }

    private static class BookTableModel extends PagingResultTableModel {
        public static class Entry extends Book {
            private int inventory;
            private int saleNumber, rentalNumber;

            public Entry() {
                super();
            }

            public Entry(String isbn, String name, String version, String authors, String cover, Date date,
                         String category, String publisher, BigDecimal purchase, BigDecimal price, String intro) {
                super(isbn, name, version, authors, cover, date, category, publisher, purchase, price, intro);
            }

            public void setBook(Book book) {
                setISBN(book.getISBN());
                setName(book.getName());
                setVersion(book.getVersion());
                setAuthors(book.getAuthors());
                setCover(book.getCover());
                setDate(book.getDate());
                setCategory(book.getCategory());
                setPublisher(book.getPublisher());
                setPurchase(book.getPurchase());
                setPrice(book.getPrice());
                setIntro(book.getIntro());
            }

            public int getInventory() {
                return inventory;
            }

            public void setInventory(int inventory) {
                this.inventory = inventory;
            }

            public int getSaleNumber() {
                return saleNumber;
            }

            public void setSaleNumber(int saleNumber) {
                this.saleNumber = saleNumber;
            }

            public int getRentalNumber() {
                return rentalNumber;
            }

            public void setRentalNumber(int rentalNumber) {
                this.rentalNumber = rentalNumber;
            }
        }
        private ArrayList<Entry> rows = new ArrayList<>();

        public String getISBN(int rowIndex) {
            if (rows.size() == 0 || rowIndex < 0) {
                return null;
            }
            try {
                Book book = rows.get(rowIndex);
                return book.getISBN();
            } catch (IndexOutOfBoundsException exp) {
                exp.printStackTrace();
                return null;
            }
        }

        public void updateBook(int row, Book book) {
            rows.get(row).setBook(book);
            fireTableDataChanged();
        }

        /** Update current page from ResultSet */
        public void updateCurrentPage(PagingResultSet dataSource) {
            if (dataSource == null) {
                return;
            }
            ResultSet rs = dataSource.getResultSet();
            if (rs == null) {
                fireTableDataChanged();
                return;
            }
            Worker worker = Worker.getInstance();
            try {
                for (int i = 0; i < dataSource.getCurrentRows(); ++i) {
                    Entry book = new Entry();
                    book.setISBN(Worker.normalizeString(rs.getString(1)));
                    book.setName(Worker.normalizeString(rs.getString(2)));
                    book.setAuthors(Worker.normalizeString(rs.getString(3)));
                    book.setPublisher(Worker.normalizeString(rs.getString(4)));
                    book.setPrice(rs.getBigDecimal(5));
                    book.setInventory(rs.getInt(6));
                    book.setSaleNumber(worker.getSaleCount(book.getISBN()));
                    book.setRentalNumber(worker.getRentalNumber(book.getISBN()));
                    rows.add(book);
                    rs.next();
                }
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
            fireTableDataChanged();
        }

        @Override
        public void pageUpdated(PagingResultSet dataSource) {
            rows.clear();
            updateCurrentPage(dataSource);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public String getColumnName(int column) {
            Application app = Application.getInstance();
            switch (column) {
                case 0:
                    return app.getString("Book.Property.ISBN");
                case 1:
                    return app.getString("Book.Property.Name");
                case 2:
                    return app.getString("Book.Property.Author");
                case 3:
                    return app.getString("Book.Property.Publisher");
                case 4:
                    return app.getString("Book.Property.Price");
                case 5:
                    return app.getString("Book.Property.Inventory");
                case 6:
                    return app.getString("Book.Property.Sales");
                case 7:
                    return app.getString("Book.Property.Rentals");
                default:
                    return app.getString("Book.Property.Unknown");
            }
        }

        @Override
        public int getRowCount() {
            if (rows.size() == 0) {
                return 0;
            } else {
                return rows.size();
            }
        }

        @Override
        public int getColumnCount() {
            return BOOK_COLUMN_COUNT;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rows.size() == 0) {
                return null;
            }
            try {
                Entry book = rows.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return book.getISBN();
                    case 1:
                        return book.getName();
                    case 2:
                        return book.getAuthors();
                    case 3:
                        return book.getPublisher();
                    case 4:
                        return book.getPrice();
                    case 5:
                        return book.getInventory();
                    case 6:
                        return book.getSaleNumber();
                    case 7:
                        return book.getRentalNumber();
                    default:
                        return null;
                }
            } catch (IndexOutOfBoundsException exp) {
                exp.printStackTrace();
                return null;
            }
        }
    }
}
