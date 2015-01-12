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
import pw.phylame.simbs.ui.com.BookConditionPane;
import pw.phylame.simbs.ui.com.PagingResultTableModel;
import pw.phylame.simbs.ui.com.PagingResultAdapter;
import pw.phylame.simbs.ui.com.TablePane;
import pw.phylame.tools.sql.PagingResultSet;
import pw.phylame.tools.sql.DbHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChooseBookDialog extends JDialog {
    public static final int BOOK_COLUMN_COUNT = 8;
    public static final String SQL_SELECT_BOOK = "SELECT book.Bisbn, Bname, Bauthors, Bdate," +
            " Bcategory, Bpublisher, Bprice, Inumber FROM book LEFT JOIN inventory" +
            " ON inventory.bisbn = book.bisbn ";

    private JPanel contentPane;
//    private JButton buttonOk;
    private JButton buttonNew;
    private JButton buttonCancel;

    private TablePane tablePane;
    private BookConditionPane condPane;

    private String isbn = null;

    public ChooseBookDialog() {
        super();
        init();
    }

    public ChooseBookDialog(Dialog owner, String title) {
        super(owner, title);
        init();
    }

    public ChooseBookDialog(Frame owner, String title) {
        super(owner, title);
        init();
    }

    private void init() {

        setContentPane(contentPane);
        setModal(true);

        JButton buttonSearch = condPane.getSearchButton();

        getRootPane().setDefaultButton(buttonSearch);

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

        condPane.setParent(this);
        tablePane.setParent(this);

        pack();
        setSize((int) (getWidth() * 1.3), (int) (getHeight() * 1.3));
        setLocationRelativeTo(getOwner());

    }

    private void onCancel() {
// add your code here if necessary
        isbn = null;
        dispose();
        condPane.destroy();
        tablePane.destroy();
    }

    private void onNew() {
        Book book = Worker.getInstance().newBook();
        if (book == null) {
            return;
        }
        isbn = book.getISBN();
        System.gc();
        dispose();
    }

    private void onSearch() {
        String cond = condPane.getQueryCondition();
        String sql = SQL_SELECT_BOOK;
        if (! "".equals(cond.trim())) {
            sql = sql +" WHERE "+cond;
        }
        final Application app = Application.getInstance();
        DbHelper dbHelper = app.getDbHelper();
        final ChooseBookDialog parent = this;
        try {
            PagingResultSet dataSource = dbHelper.queryAndPaging(sql, Constants.MAX_ROW_COUNT);
            PagingResultAdapter pagingResultAdapter = (PagingResultAdapter) tablePane.getTableAdapter();
            if (pagingResultAdapter == null) {     // first search
                final BookTableModel tableModel = new BookTableModel();
                pagingResultAdapter = new PagingResultAdapter(dataSource, tableModel);
                final JTable table = pagingResultAdapter.getTable();
                table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                // add details popup menu
                final JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem menuItem = new JMenuItem(app.getString("Dialog.ChooseBook.Menu.Details"));
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int row = table.getSelectedRow();
                        if (row < 0) {
                            return;
                        }
                        String isbn = tableModel.getISBN(row);
                        BookDetailsDialog.viewBook(isbn);
                    }
                });
                popupMenu.add(menuItem);
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
                                condPane.destroy();
                                tablePane.destroy();
                            }
                        }
                    }
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (! e.isMetaDown()) {
                            return;
                        }
                        int row = table.rowAtPoint(e.getPoint());
                        if (row == -1) {
                            return;
                        }
                        if (! table.isRowSelected(row)) {   // not selected
                            table.setRowSelectionInterval(row, row);
                        }
                        popupMenu.show(tablePane.getPane(), e.getX(), e.getY());
                    }
                });
                tablePane.setTableAdapter(pagingResultAdapter);
                setLocationRelativeTo(getOwner());
            } else {
                pagingResultAdapter.setDataSource(dataSource);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getISBN() {
        return isbn;
    }

    public static String chooseBook(java.awt.Dialog owner) {
        ChooseBookDialog dialog = new ChooseBookDialog(owner,
                Application.getInstance().getString("Dialog.ChooseBook.Title"));
        dialog.setVisible(true);
        return dialog.getISBN();
    }

    public static String chooseBook(java.awt.Frame owner) {
        ChooseBookDialog dialog = new ChooseBookDialog(owner,
                Application.getInstance().getString("Dialog.ChooseBook.Title"));
        dialog.setVisible(true);
        return dialog.getISBN();
    }

    public static class BookTableModel extends PagingResultTableModel {
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

        private PagingResultSet dataSource = null;
        private ArrayList<BookX> rows = new ArrayList<>();

        public BookTableModel() {
        }

        public String getISBN(int rowIndex) {
            if (dataSource == null) {
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
        public void pageUpdated(PagingResultSet dataSource) {
            this.dataSource = dataSource;
            rows.clear();
            if (dataSource == null) {
                fireTableDataChanged();
                return;
            }
            ResultSet rs = dataSource.getResultSet();
            if (rs == null) {
                fireTableDataChanged();
                return;
            }
            try {
                for (int i = 0; i < dataSource.getCurrentRows(); ++i) {
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
            if (dataSource == null) {
                return 0;
            } else {
                return dataSource.getCurrentRows();
            }
        }

        @Override
        public int getColumnCount() {
            return BOOK_COLUMN_COUNT;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (dataSource == null) {
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
