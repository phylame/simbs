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

import pw.phylame.simbs.Application;
import pw.phylame.simbs.Constants;
import pw.phylame.simbs.Worker;
import pw.phylame.simbs.ui.com.PagingResultTableModel;
import pw.phylame.simbs.ui.com.PagingResultAdapter;
import pw.phylame.simbs.ui.com.TablePane;
import pw.phylame.tools.sql.DbHelper;
import pw.phylame.tools.sql.PagingResultSet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReturnBookDialog extends JDialog {
    public static final int RESULT_COLUMN_COUNT = 7;
    private static final String SQL_QUERY_RENTAL = "SELECT Rdate, Rtime, Rnumber, Rperiod, Rprice," +
            " Rdeposit, Rrevenue, Rid FROM rental WHERE Bisbn='%s' AND Cid=%d";

    private JPanel contentPane;
    private JButton btnClose;
    private JTextField tfISBN;
    private JButton btnChooseBook;
    private JTextField tfCustomerName;
    private JButton btnChooseCustomer;
    private JSpinner jsNumber;
    private JLabel labelLentNumber;
    private JFormattedTextField tfLentDays;
    private JFormattedTextField tfDeposit;
    private JFormattedTextField tfTotal;
    private JFormattedTextField tfPrice;
    private TablePane tablePane;
    private JButton btnCommit;

    private String oldISBN = null;
    private int customerID = -1;

    public ReturnBookDialog() {
        super();
        init();
    }

    public ReturnBookDialog(Dialog owner, String title) {
        super(owner, title);
        init();
    }

    public ReturnBookDialog(Frame owner, String title) {
        super(owner, title);
        init();
    }

    private void init() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnClose);

        final Application app = Application.getInstance();

        tfISBN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateISBN();
            }
        });

        tfISBN.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateISBN();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateISBN();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {}
        });

        jsNumber.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                calculateTotal();
            }
        });

        final ReturnBookDialog parent = this;

        btnChooseBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String isbn = ChooseBookDialog.chooseBook(parent);
                System.gc();
                if (isbn != null) {
                    setBook(isbn);
                }
            }
        });

        btnChooseCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = ChooseCustomerDialog.chooseCustomer(parent);
                System.gc();
                if (id > 0) {
                    setCustomer(id);
                }
            }
        });

        btnCommit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCommit();
            }
        });

        btnClose.addActionListener(new ActionListener() {
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

        setIconImage(pw.phylame.ixin.IToolkit.createImage(app.getString("Dialog.Return.Icon")));

        pack();
        setLocationRelativeTo(getOwner());

        updateNumber();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void onCommit() {
        String isbn = tfISBN.getText().trim();
        if ("".equals(isbn) || customerID <= 0) {
            return;
        }
        Application app = Application.getInstance();
        RentalTableModel.RentalEntry entry =
                ((RentalTableModel) tablePane.getTableModel()).getRentalEntry(tablePane.getSelectedRow());
        int number = (int) jsNumber.getValue();
        BigDecimal deposit = (BigDecimal) tfDeposit.getValue();
        BigDecimal total = (BigDecimal) tfTotal.getValue();
        try {
            Worker.getInstance().returnBook(entry.getNo(), number, deposit, total);
            updateTable();
            updateRentalInfo();
            DialogFactory.showInfo(getOwner(), app.getString("Dialog.Return.Successful"),
                    app.getString("Dialog.Return.Title"));
        } catch (SQLException e) {
            e.printStackTrace();
            DialogFactory.showInfo(getOwner(), app.getString("Dialog.Return.Failed"),
                    app.getString("Dialog.Return.Title"));
        }
    }

    private void updateNumber() {
        setNumberInfo(0, 0, 0);
        jsNumber.setEnabled(false);
        tfLentDays.setValue(0);
        tfDeposit.setValue(new BigDecimal("0.00"));
        tfDeposit.setEditable(false);
        tfPrice.setValue(new BigDecimal("0.0"));
        tfTotal.setValue(new BigDecimal("0.00"));
        btnCommit.setEnabled(false);

        String isbn = tfISBN.getText().trim();
        if ("".equals(isbn) || customerID < 0) {
            return;
        }
        updateTable();
        updateRentalInfo();
    }

    private void setNumberInfo(int number, int begin, int lentNumber) {
        jsNumber.setModel(new SpinnerNumberModel(number, begin, lentNumber, 1));
        labelLentNumber.setText(String.format(
                Application.getInstance().getString("Dialog.Return.LabelLentNumber"), lentNumber));
    }

    private void updateRentalInfo() {
        setNumberInfo(0, 0, 0);
        jsNumber.setEnabled(false);
        tfLentDays.setValue(0);
        tfDeposit.setValue(new BigDecimal("0.00"));
        tfDeposit.setEditable(false);
        tfPrice.setValue(new BigDecimal("0.0"));
        tfTotal.setValue(new BigDecimal("0.00"));
        btnCommit.setEnabled(false);
        int row = tablePane.getSelectedRow();
        if (row < 0) {  // no selection
            return;
        }
        RentalTableModel.RentalEntry entry =
                ((RentalTableModel) tablePane.getTableModel()).getRentalEntry(row);
        if (entry != null) {
            int n = entry.getNumber();
            if (n <= 0) {   // when 0, all books returned
                btnCommit.setEnabled(false);
                return;
            }
            setNumberInfo(1, 1, n);
            jsNumber.setEnabled(true);
            java.util.Date sDate = Worker.toNormalDate(entry.getDate(), entry.getTime());
            int days = pw.phylame.tools.DateUtility.calculateInterval(sDate, new java.util.Date(), "D");
            tfLentDays.setValue(days);
            tfPrice.setValue(entry.getPrice());
            calculateTotal();
            btnCommit.setEnabled(true);
        }
    }

    private void calculateTotal() {
        int row = tablePane.getSelectedRow();
        if (row < 0) {  // no selection
            return;
        }
        RentalTableModel.RentalEntry entry =
                ((RentalTableModel) tablePane.getTableModel()).getRentalEntry(row);
        if (entry != null) {
            int totalNumber = entry.getNumber();
            int currentNumber = (int) jsNumber.getValue();
            tfDeposit.setValue(entry.getDeposit().divide(new BigDecimal(totalNumber),
                    BigDecimal.ROUND_CEILING).multiply(new BigDecimal(currentNumber)));
            tfDeposit.setEditable(true);    // we can modify deposit, example the book is broken
            java.util.Date sDate = Worker.toNormalDate(entry.getDate(), entry.getTime());
            int days = pw.phylame.tools.DateUtility.calculateInterval(sDate, new java.util.Date(), "D");
            BigDecimal v = entry.getPrice().multiply(new BigDecimal(days)).multiply(new BigDecimal(currentNumber));
            tfTotal.setValue(v);
        }
    }

    private void updateTable() {
        String sql = String.format(SQL_QUERY_RENTAL, tfISBN.getText().trim(), customerID);
        final Application app = Application.getInstance();
        DbHelper dbHelper = app.getDbHelper();
        try {
            PagingResultSet dataSet = dbHelper.queryAndPaging(sql, Constants.MAX_ROW_COUNT,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            PagingResultAdapter pagingResultAdapter = (PagingResultAdapter) tablePane.getTableAdapter();
            if (pagingResultAdapter == null) {     // first search
                final RentalTableModel tableModel = new RentalTableModel();
                pagingResultAdapter = new PagingResultAdapter(dataSet, tableModel);

                // add action to table
                final JTable table = pagingResultAdapter.getTable();
                table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


                // add details popup menu
//                final JPopupMenu popupMenu = new JPopupMenu();
//                JMenuItem menuItem = new JMenuItem(app.getString("Dialog.ReturnBook.Menu.Details"));
//                menuItem.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        int row = table.getSelectedRow();
//                        if (row < 0) {
//                            return;
//                        }
//                        // TODO: add view rental information
////                        String isbn = tableModel.getISBN(row);
////                        BookDetailsDialog.viewBook(isbn);
//                    }
//                });
//                popupMenu.add(menuItem);

                table.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.isMetaDown()) {
                            return;
                        }
                        updateRentalInfo();
                    }
                });
                tablePane.setTableAdapter(pagingResultAdapter);
                setSize((int) (getWidth() * 1.3), (int) (getHeight() * 1.3));
                setLocationRelativeTo(getOwner());
            } else {
                pagingResultAdapter.setDataSource(dataSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateISBN() {
        String s = tfISBN.getText().trim();
        if (s.equals(oldISBN)) {    // not changed
            return;
        }
        oldISBN = s;
        updateNumber();
    }

    public void setBook(String isbn) {
        if (isbn == null) {
            isbn = "";
        }
        tfISBN.setText(isbn.trim());
    }

    public String getBook() {
        return tfISBN.getText().trim();
    }

    public void setCustomer(int id) {
        this.customerID = id;
        if (id > 0) {
            String name = Worker.getInstance().getCustomerName(id);
            if (name == null) {     // invalid ID
                tfCustomerName.setText("");
            } else {
                tfCustomerName.setText(name);
            }
            updateNumber();
        }
    }

    public int getCustomer() {
        return customerID;
    }

    public int getNumber() {
        return (int) jsNumber.getValue();
    }

    public static class RentalTableModel extends PagingResultTableModel {
        public static class RentalEntry {
            private int no;
            private java.sql.Date date;
            private java.sql.Time time;
            private int number, period;
            private BigDecimal price, deposit, revenue;

            public RentalEntry(java.sql.Date date, java.sql.Time time, int number, int period,
                               BigDecimal price, BigDecimal deposit, BigDecimal revenue) {
                setDate(date);
                setTime(time);
                setNumber(number);
                setPeriod(period);
                setPrice(price);
                setDeposit(deposit);
                setRevenue(revenue);
            }

            public int getNo() {
                return no;
            }

            public void setNo(int no) {
                this.no = no;
            }

            public java.sql.Date getDate() {
                return date;
            }

            public void setDate(java.sql.Date date) {
                this.date = date;
            }

            public java.sql.Time getTime() {
                return time;
            }

            public void setTime(java.sql.Time time) {
                this.time = time;
            }

            public int getNumber() {
                return number;
            }

            public void setNumber(int number) {
                this.number = number;
            }

            public int getPeriod() {
                return period;
            }

            public void setPeriod(int period) {
                this.period = period;
            }

            public BigDecimal getPrice() {
                return price;
            }

            public void setPrice(BigDecimal price) {
                this.price = price;
            }

            public BigDecimal getDeposit() {
                return deposit;
            }

            public void setDeposit(BigDecimal deposit) {
                this.deposit = deposit;
            }

            public BigDecimal getRevenue() {
                return revenue;
            }

            public void setRevenue(BigDecimal revenue) {
                this.revenue = revenue;
            }
        }

        private PagingResultSet dataSource = null;
        private java.util.ArrayList<RentalEntry> rows = new java.util.ArrayList<>();

        public RentalTableModel() {
        }

        public RentalEntry getRentalEntry(int rowIndex) {
            if (dataSource == null) {
                return null;
            } else {
                return rows.get(rowIndex);
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
            java.sql.ResultSet rs = dataSource.getResultSet();
            if (rs == null) {
                fireTableDataChanged();
                return;
            }
            try {
                for (int i = 0; i < dataSource.getCurrentRows(); ++i) {
                    RentalEntry entry = new RentalEntry(rs.getDate(1), rs.getTime(2), rs.getInt(3),
                            rs.getInt(4), rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBigDecimal(7));
                    entry.setNo(rs.getInt(8));
                    rows.add(entry);
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
                    return app.getString("Dialog.Return.Table.Date");
                case 1:
                    return app.getString("Dialog.Return.Table.Time");
                case 2:
                    return app.getString("Dialog.Return.Table.Number");
                case 3:
                    return app.getString("Dialog.Return.Table.Period");
                case 4:
                    return app.getString("Dialog.Return.Table.Price");
                case 5:
                    return app.getString("Dialog.Return.Table.Deposit");
                case 6:
                    return app.getString("Dialog.Return.Table.Revenue");
                default:
                    return null;
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
            return RESULT_COLUMN_COUNT;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (dataSource == null) {
                return null;
            }
            try {
                RentalEntry entry = rows.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return entry.getDate();
                    case 1:
                        return entry.getTime();
                    case 2:
                        return entry.getNumber();
                    case 3:
                        return entry.getPeriod();
                    case 4:
                        return entry.getPrice();
                    case 5:
                        return entry.getDeposit();
                    case 6:
                        return entry.getRevenue();
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
