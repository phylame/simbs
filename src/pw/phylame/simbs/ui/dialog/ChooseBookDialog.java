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

package pw.phylame.simbs.ui.dialog;

import pw.phylame.ixin.IToolkit;
import pw.phylame.simbs.Application;
import pw.phylame.simbs.Constants;
import pw.phylame.simbs.Worker;
import pw.phylame.simbs.ds.Book;
import pw.phylame.simbs.ui.com.PaneTableModel;
import pw.phylame.simbs.ui.com.TableAdapter;
import pw.phylame.simbs.ui.com.TablePane;
import pw.phylame.tools.StringUtility;
import pw.phylame.tools.sql.PageResultSet;
import pw.phylame.tools.sql.DbHelper;

import javax.swing.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class ChooseBookDialog extends JDialog {
    public static final int BOOK_COLUMN_COUNT = 8;
    public static final String SQL_SELECT_BOOK = "SELECT book.Bisbn, Bname, Bauthors, Bdate," +
            " Bcategory, Bpublisher, Bprice, Inumber FROM book LEFT OUTER JOIN inventory" +
            " ON (inventory.bisbn = book.bisbn) ";

    private JPanel contentPane;
//    private JButton buttonOk;
    private JButton buttonCancel;
    private JButton buttonSearch;
    private JButton buttonNew;
    private JTextField tfISBN;
    private JTextField tfName;
    private JTextField tfAuthor;
    private JTextField tfCategory;
    private JTextField tfPublisher;
    private JSpinner jsDateBegin;
    private JSpinner jsDateEnd;
    private JFormattedTextField tfPriceBegin;
    private JFormattedTextField tfPriceEnd;
    private JCheckBox cbDate;
    private JCheckBox cbPrice;
    private TablePane tablePane;
    private JFormattedTextField tfInventoryBegin;
    private JFormattedTextField tfInventoryEnd;
    private JCheckBox cbInventory;

    private String isbn = null;

    public ChooseBookDialog() {
        setContentPane(contentPane);
        setModal(true);
//        getRootPane().setDefaultButton(buttonOk);

        Application app = Application.getInstance();

        buttonNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNew();
            }
        });

        buttonSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSearch();
            }
        });


//        buttonOk.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onOK();
//            }
//        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setIconImage(IToolkit.createImage(app.getString("Book.Icon")));

        tablePane.setParent(this);

        pack();
        setLocationRelativeTo(null);

        jsDateBegin.setModel(new SpinnerDateModel());
        jsDateEnd.setModel(new SpinnerDateModel());
        tfPriceBegin.setValue(0.0F);
        tfPriceEnd.setValue(100.0F);

        tfInventoryBegin.setValue(1);
        tfInventoryEnd.setValue(100);

    }

    public ChooseBookDialog(String title) {
        this();
        setTitle(title);
    }

    private void onCancel() {
// add your code here if necessary
        isbn = null;
        dispose();
    }

    private void onNew() {
        Book book = Worker.getInstance().newBook();
        if (book == null) {
            return;
        }
        isbn = book.getISBN();
        dispose();
    }

    private void onSearch() {
        Application app = Application.getInstance();

        ArrayList<String> conditions = new ArrayList<>();

        String s = tfISBN.getText().trim();
        if (! "".equals(s)) {
            conditions.add("Bisbn LIKE '%"+s+"%'");
        }
        s = tfName.getText().trim();
        if (! "".equals(s)) {
            conditions.add("Bname LIKE '%" + s + "%'");
        }
        s = tfAuthor.getText().trim();
        if (! "".equals(s)) {
            conditions.add("Bauthors LIKE '%" + s + "%'");
        }
        if (cbDate.isSelected()) {
            java.util.Date begin = (java.util.Date) jsDateBegin.getValue(), end = (java.util.Date) jsDateEnd.getValue();
            if (begin.compareTo(end) > 0) {
                DialogFactory.showError(this, app.getString("Dialog.ChooseBook.InvalidDate"),
                        app.getString("Dialog.ChooseBook.Title"));
                return;
            }
            conditions.add(String.format("Bdate BETWEEN '%s' AND '%s'", Worker.toSQLDate(begin),
                    Worker.toSQLDate(end)));
        }
        s = tfCategory.getText().trim();
        if (! "".equals(s)) {
            conditions.add("Bcategory LIKE '%" + s + "%'");
        }
        s = tfPublisher.getText().trim();
        if (! "".equals(s)) {
            conditions.add("Bpublisher LIKE '%" + s + "%'");
        }
        if (cbPrice.isSelected()) {
            float begin = (float) tfPriceBegin.getValue(), end = (float) tfPriceEnd.getValue();
            if (end < begin) {
                DialogFactory.showError(this, app.getString("Dialog.ChooseBook.InvalidPrice"),
                        app.getString("Dialog.ChooseBook.Title"));
                return;
            }
            conditions.add(String.format("Bprice BETWEEN %.2f AND %.2f", begin, end));
        }
        if (cbInventory.isSelected()) {
            int begin = (int) tfInventoryBegin.getValue(), end = (int) tfInventoryEnd.getValue();
            if (end < begin) {
                DialogFactory.showError(this, app.getString("Dialog.ChooseBook.InvalidInventory"),
                        app.getString("Dialog.ChooseBook.Title"));
                return;
            }
            conditions.add(String.format("book.Bisbn = inventory.Bisbn AND Enumber BETWEEN %d AND %d",
                    begin, end));
        }

        String cond = StringUtility.join(conditions, " AND ");
        String sql = SQL_SELECT_BOOK;
        if (! "".equals(cond.trim())) {
            sql = sql +" WHERE "+cond;
        }
        DbHelper dbHelper = app.getSQLAdmin();
        try {
            PageResultSet dataSet = dbHelper.queryAndPaging(sql, Constants.MAX_ROW_COUNT);
            TableAdapter tableAdapter = tablePane.getTableAdapter();
            if (tableAdapter == null) {     // first search
                final BookTableModel tableModel = new BookTableModel();
                tableAdapter = new TableAdapter(dataSet, tableModel);
                final JTable table = tableAdapter.getTable();
                table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                table.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int row = table.getSelectedRow();
                        if (row == -1) {
                            return;
                        }
                        isbn = tableModel.getISBN(row);
                        if (isbn != null) {
                            if (e.getClickCount() == 2) {
                                dispose();
                            }
//                            } else {
//                                buttonOk.setEnabled(true);
//                            }
                        }
                    }
                });
                tablePane.setTableAdapter(tableAdapter);
                setSize((int) (getWidth() * 1.3), (int) (getHeight() * 1.3));
                setLocationRelativeTo(null);
            } else {
                tableAdapter.setDataSource(dataSet);
                tablePane.updatePageStatus();
//                if (dataSet.getRowCount() == 0) {       // not found result
//                    buttonOk.setEnabled(false);
//                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setISBN(String isbn) {
        // nothing
    }

    public String getISBN() {
        return isbn;
    }

    public static class BookTableModel extends PaneTableModel {
        private static class BookX extends Book {
            private int inventory;

            public BookX() {
                super();
            }

            public int getInventory() {
                return inventory;
            }

            public void setInventory(int inventory) {
                this.inventory = inventory;
            }
        }

        private PageResultSet dataSet = null;
        private ArrayList<BookX> rows = new ArrayList<>();

        public BookTableModel() {
        }

        public String getISBN(int rowIndex) {
            if (dataSet == null) {
                return null;
            }
            try {
                BookX entry = rows.get(rowIndex);
                return entry.getISBN();
            } catch (IndexOutOfBoundsException exp) {
                exp.printStackTrace();
                return null;
            }
        }

        @Override
        public void setDataSource(PageResultSet dataSet) {
            this.dataSet = dataSet;
            pageUpdated(dataSet);
        }

        @Override
        public void pageUpdated(PageResultSet dataSet) {
            if (dataSet == null) {
                return;
            }
            rows.clear();
            ResultSet rs = dataSet.getResultSet();
            if (rs == null) {
                fireTableDataChanged();
                return;
            }
            try {
                for (int i = 0; i < dataSet.getCurrentRows(); ++i) {
                    BookX book = new BookX();
                    book.setISBN(rs.getString(1));
                    book.setName(rs.getString(2));
                    book.setAuthors(rs.getString(3));
                    book.setDate(rs.getDate(4));
                    book.setCategory(rs.getString(5));
                    book.setPublisher(rs.getString(6));
                    book.setPrice(rs.getBigDecimal(7));
                    book.setInventory(rs.getInt(8));
                    rows.add(book);
                    rs.next();
                }
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
            fireTableDataChanged();
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
                    return app.getString("Book.Property.Date");
                case 4:
                    return app.getString("Book.Property.Category");
                case 5:
                    return app.getString("Book.Property.Publisher");
                case 6:
                    return app.getString("Book.Property.Price");
                case 7:
                    return app.getString("Book.Property.Inventory");
                default:
                    return app.getString("Book.Property.Unknown");
            }
        }

        @Override
        public int getRowCount() {
            if (dataSet == null) {
                return 0;
            } else {
                return dataSet.getCurrentRows();
            }
        }

        @Override
        public int getColumnCount() {
            return BOOK_COLUMN_COUNT;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (dataSet == null) {
                return null;
            }
            try {
                BookX entry = rows.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return entry.getISBN();
                    case 1:
                        return entry.getName();
                    case 2:
                        return entry.getAuthors();
                    case 3:
                        return entry.getDate();
                    case 4:
                        return entry.getCategory();
                    case 5:
                        return entry.getPublisher();
                    case 6:
                        return entry.getPrice();
                    case 7:
                        return entry.getInventory();
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
