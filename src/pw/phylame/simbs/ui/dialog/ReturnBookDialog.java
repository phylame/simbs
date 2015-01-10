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
            " Rdeposit, Rrevenue FROM rental WHERE Bisbn='%s' AND Cid=%d";

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfISBN;
    private JButton buttonChooseBook;
    private JTextField tfCustomerName;
    private JButton buttonChooseCustomer;
    private JSpinner jsNumber;
    private JLabel labelLentNumber;
    private JFormattedTextField tfLentDays;
    private JFormattedTextField tfDeposit;
    private JFormattedTextField tfTotal;
    private JFormattedTextField tfPrice;
    private TablePane tablePane;

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
        getRootPane().setDefaultButton(buttonOK);

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

        final ReturnBookDialog parent = this;

        buttonChooseBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseBookDialog dialog = new ChooseBookDialog(parent,
                        app.getString("Dialog.ChooseBook.Title"));
                dialog.setVisible(true);
                String isbn = dialog.getISBN();
                System.gc();
                if (isbn != null) {
                    setBook(isbn);
                }
            }
        });

        buttonChooseCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseCustomerDialog dialog = new ChooseCustomerDialog(parent,
                        app.getString("Dialog.ChooseCustomer.Title"));
                int id = getCustomer();
                if (id > 0) {
                    dialog.setCustomerID(id);
                }
                dialog.setVisible(true);
                id = dialog.getCustomerID();
                System.gc();
                if (id > 0) {
                    setCustomer(id);
                }
            }
        });


        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

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

        setIconImage(pw.phylame.ixin.IToolkit.createImage(app.getString("Dialog.Return.Icon")));

        pack();
        setLocationRelativeTo(getOwner());

        updateNumber();
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void updateNumber() {
        setNumberInfo(0, 0, 0);
        jsNumber.setEnabled(false);
        updateRentalInfo();
        buttonOK.setEnabled(false);

        String isbn = tfISBN.getText().trim();
        if ("".equals(isbn) || customerID < 0) {
            return;
        }

        Worker worker = Worker.getInstance();

        int number = worker.getRentalNumber(isbn, customerID);
        if (number <= 0) {   // not found
            return;
        }

        setNumberInfo(1, 1, number);
        jsNumber.setEnabled(true);

        updateTable();

        buttonOK.setEnabled(true);

    }

    private void setNumberInfo(int number, int begin, int lentNumber) {
        jsNumber.setModel(new SpinnerNumberModel(number, begin, lentNumber, 1));
        labelLentNumber.setText(String.format(
                Application.getInstance().getString("Dialog.Return.LabelLentNumber"), lentNumber));
    }

    private void updateRentalInfo() {
        tfLentDays.setValue(0);
        tfDeposit.setValue(new BigDecimal("0.00"));
        tfPrice.setValue(new BigDecimal("0.0"));
        tfTotal.setValue(new BigDecimal("0.00"));
    }

    private void updateTable() {
        String sql = String.format(SQL_QUERY_RENTAL, tfISBN.getText().trim(), customerID);
        DbHelper dbHelper = Application.getInstance().getDbHelper();
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
                table.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int row = table.getSelectedRow();
                        if (row == -1) {
                            return;
                        }
//                        isbn = tableModel.getISBN(row);
//                        if (isbn != null) {
//                            if (e.getClickCount() == 2) {
//                                dispose();
//                            }
////                            } else {
////                                buttonOk.setEnabled(true);
////                            }
//                        }
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
                buttonOK.setEnabled(false);
            } else {
                tfCustomerName.setText(name);
                updateNumber();
            }
        }
    }

    public int getCustomer() {
        return customerID;
    }

    public int getNumber() {
        return (int) jsNumber.getValue();
    }

    public static class RentalTableModel extends PagingResultTableModel {
        private static class RentalEntry {
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
                        return entry.getPeriod();
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
