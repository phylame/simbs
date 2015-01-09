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
import pw.phylame.simbs.ds.Customer;
import pw.phylame.simbs.ui.com.PaneTableModel;
import pw.phylame.simbs.ui.com.TableAdapter;
import pw.phylame.simbs.ui.com.TablePane;
import pw.phylame.tools.StringUtility;
import pw.phylame.tools.sql.DbHelper;
import pw.phylame.tools.sql.PageResultSet;

import javax.swing.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChooseCustomerDialog extends JDialog {
    public static final int CUSTOMER_COLUMN_COUNT = 6;
    public static final String SQL_SELECT_CUSTOMER = "SELECT Cid, Cname, Cphone, Cemail, Clevel," +
            " Climit FROM customer ";

    private JPanel contentPane;
    private JButton buttonSearch;
    private JButton buttonNew;
    private JButton buttonCancel;
    private JTextField tfName;
    private JTextField tfPhone;
    private JTextField tfEmail;
//    private JButton buttonOk;
    private TablePane tablePane;
    private JFormattedTextField tfLevelBegin;
    private JFormattedTextField tfLevelEnd;
    private JCheckBox cbLevel;
    private JCheckBox cbLimit;
    private JFormattedTextField tfLimitBegin;
    private JFormattedTextField tfLimitEnd;

    private int customerID = -1;

    public ChooseCustomerDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonSearch);

        Application app = Application.getInstance();

        buttonSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSearch();
            }
        });

        buttonNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNew();
            }
        });

//        buttonOk.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                onOk();
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

        setIconImage(IToolkit.createImage(app.getString("Customer.Icon")));

        tablePane.setParent(this);

        pack();
        setLocationRelativeTo(null);

        tfLevelBegin.setValue(0);
        tfLevelEnd.setValue(100);
        tfLimitBegin.setValue(0);
        tfLimitEnd.setValue(100);
    }

    public ChooseCustomerDialog(String title) {
        this();
        setTitle(title);
    }

    private void onNew() {
        Customer customer = Worker.getInstance().newCustomer();
        if (customer == null) {
            return;
        }
        customerID = customer.getId();
        dispose();
    }

    private void onSearch() {
        Application app = Application.getInstance();

        ArrayList<String> conditions = new ArrayList<>();

        String s = tfName.getText().trim();
        if (! "".equals(s)) {
            conditions.add("Cname LIKE '%"+s+"%'");
        }
        s = tfPhone.getText().trim();
        if (! "".equals(s)) {
            conditions.add("Cphone LIKE '%" + s + "%'");
        }
        s = tfEmail.getText().trim();
        if (! "".equals(s)) {
            conditions.add("Cemail LIKE '%" + s + "%'");
        }
        if (cbLevel.isSelected()) {
            int begin = (int) tfLevelBegin.getValue(), end = (int) tfLevelEnd.getValue();
            if (begin > end) {
                DialogFactory.showError(this, app.getString("Dialog.ChooseCustomer.InvalidLevel"),
                        app.getString("Dialog.ChooseCustomer.Title"));
                return;
            }
            conditions.add(String.format("Clevel BETWEEN %d AND %d", begin, end));
        }
        if (cbLimit.isSelected()) {
            int begin = (int) tfLimitBegin.getValue(), end = (int) tfLimitEnd.getValue();
            if (begin > end) {
                DialogFactory.showError(this, app.getString("Dialog.ChooseCustomer.InvalidLimit"),
                        app.getString("Dialog.ChooseCustomer.Title"));
                return;
            }
            conditions.add(String.format("Clent_limit BETWEEN %d AND %d", begin, end));
        }
        String cond = StringUtility.join(conditions, " AND ");
        String sql = SQL_SELECT_CUSTOMER;
        if (! "".equals(cond.trim())) {
            sql = sql +" WHERE "+cond;
        }
        DbHelper dbHelper = app.getSQLAdmin();
        try {
            PageResultSet dataSet = dbHelper.queryAndPaging(sql, Constants.MAX_ROW_COUNT);
            TableAdapter tableAdapter = tablePane.getTableAdapter();
            if (tableAdapter == null) {     // first search
                final CustomerTableModel tableModel = new CustomerTableModel();
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
                        customerID = tableModel.getCustomerID(row);
                        if (customerID > 0) {
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
                setSize((int) (getWidth() * 1.2), (int) (getHeight() * 1.4));
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

    private void onCancel() {
// add your code here if necessary
        customerID = -1;
        dispose();
    }

    public void setCustomerID(int id) {

    }

    public int getCustomerID() {
        return customerID;
    }

    public static class CustomerTableModel extends PaneTableModel {
        private PageResultSet dataSet = null;
        private ArrayList<Customer> rows = new ArrayList<>();

        public CustomerTableModel() {
        }

        public int getCustomerID(int rowIndex) {
            if (dataSet == null) {
                return -1;
            }
            try {
                Customer entry = rows.get(rowIndex);
                return entry.getId();
            } catch (IndexOutOfBoundsException exp) {
                exp.printStackTrace();
                return -1;
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
                    Customer customer = new Customer(rs.getInt(1), rs.getString(2).trim(),
                            Worker.normalizeString(rs.getString(3)),
                            Worker.normalizeString(rs.getString(4)), rs.getInt(5), rs.getInt(6), "");
                    rows.add(customer);
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
                    return app.getString("Customer.Property.ID");
                case 1:
                    return app.getString("Customer.Property.Name");
                case 2:
                    return app.getString("Customer.Property.Phone");
                case 3:
                    return app.getString("Customer.Property.Email");
                case 4:
                    return app.getString("Customer.Property.Level");
                case 5:
                    return app.getString("Customer.Property.Limit");
                default:
                    return app.getString("Customer.Property.Unknown");
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
            return CUSTOMER_COLUMN_COUNT;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (dataSet == null) {
                return null;
            }
            try {
                Customer entry = rows.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return entry.getId();
                    case 1:
                        return entry.getName();
                    case 2:
                        return entry.getPhone();
                    case 3:
                        return entry.getEmail();
                    case 4:
                        return entry.getLevel();
                    case 5:
                        return entry.getLimit();
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
