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
import pw.phylame.simbs.ui.com.CustomerConditionPane;
import pw.phylame.simbs.ui.com.PagingResultTableModel;
import pw.phylame.simbs.ui.com.PagingResultAdapter;
import pw.phylame.simbs.ui.com.TablePane;
import pw.phylame.tools.sql.DbHelper;
import pw.phylame.tools.sql.PagingResultSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChooseCustomerDialog extends JDialog {
    public static final int CUSTOMER_COLUMN_COUNT = 6;
    public static final String SQL_SELECT_CUSTOMER = "SELECT Cid, Cname, Cphone, Cemail, Clevel," +
            " Climit FROM customer WHERE Cid<>0 ";

    private JPanel contentPane;

    private JButton buttonNew;
    private JButton buttonCancel;

    private TablePane tablePane;
    private CustomerConditionPane condPane;


    private int customerID = -1;

    public ChooseCustomerDialog() {
        super();
        init();
    }

    public ChooseCustomerDialog(Dialog owner, String title) {
        super(owner, title);
        init();
    }

    public ChooseCustomerDialog(Frame owner, String title) {
        super(owner, title);
        init();
    }

    private void init() {
        setContentPane(contentPane);
        setModal(true);

        JButton buttonSearch = condPane.getSearchButton();

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

        condPane.setParent(this);
        tablePane.setParent(this);

        pack();
        setSize((int) (getWidth() * 1.2), (int) (getHeight() * 1.4));
        setLocationRelativeTo(getOwner());
    }

    private void onNew() {
        Customer customer = Worker.getInstance().newCustomer();
        if (customer == null) {
            return;
        }
        customerID = customer.getId();
        System.gc();
        dispose();
    }

    private void onSearch() {
        String cond = condPane.getQueryCondition();

        String sql = SQL_SELECT_CUSTOMER;
        if (! "".equals(cond.trim())) {
            sql = sql + " AND " + cond;
        }
        final Application app = Application.getInstance();
        final ChooseCustomerDialog parent = this;
        DbHelper dbHelper = app.getDbHelper();
        try {
            PagingResultSet dataSet = dbHelper.queryAndPaging(sql, Constants.MAX_ROW_COUNT);
            PagingResultAdapter pagingResultAdapter = (PagingResultAdapter) tablePane.getTableAdapter();
            if (pagingResultAdapter == null) {     // first search
                final CustomerTableModel tableModel = new CustomerTableModel();
                pagingResultAdapter = new PagingResultAdapter(dataSet, tableModel);

                // add action to table
                final JTable table = pagingResultAdapter.getTable();
                table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                // add details popup menu
                final JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem menuItem = new JMenuItem(app.getString("Dialog.ChooseCustomer.Menu.Details"));
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int row = table.getSelectedRow();
                        if (row < 0) {
                            return;
                        }
                        int id = tableModel.getCustomerID(row);
                        CustomerDetailsDialog.viewCustomer(id);
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
                        customerID = tableModel.getCustomerID(row);
                        if (customerID > 0) {
                            if (e.getClickCount() == 2) {
                                dispose();
                                condPane.destroy();
                                tablePane.destroy();
                            }
                        }
                    }
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (!e.isMetaDown()) {
                            return;
                        }
                        int row = table.rowAtPoint(e.getPoint());
                        if (row == -1) {
                            return;
                        }
                        if (!table.isRowSelected(row)) {   // not selected
                            table.setRowSelectionInterval(row, row);
                        }
                        popupMenu.show(tablePane.getPane(), e.getX(), e.getY());
                    }
                });
                tablePane.setTableAdapter(pagingResultAdapter);
                setLocationRelativeTo(getOwner());
            } else {
                pagingResultAdapter.setDataSource(dataSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void onCancel() {
// add your code here if necessary
        customerID = -1;
        dispose();
        condPane.destroy();
        tablePane.destroy();
    }

    public int getCustomerID() {
        return customerID;
    }

    public static int chooseCustomer(java.awt.Dialog owner) {
        ChooseCustomerDialog dialog = new ChooseCustomerDialog(owner,
                Application.getInstance().getString("Dialog.ChooseCustomer.Title"));
        dialog.setVisible(true);
        return dialog.getCustomerID();
    }

    public static int chooseCustomer(java.awt.Frame owner) {
        ChooseCustomerDialog dialog = new ChooseCustomerDialog(owner,
                Application.getInstance().getString("Dialog.ChooseCustomer.Title"));
        dialog.setVisible(true);
        return dialog.getCustomerID();
    }

    public static class CustomerTableModel extends PagingResultTableModel {
        private PagingResultSet dataSource = null;
        private ArrayList<Customer> rows = new ArrayList<>();

        public CustomerTableModel() {
        }

        public int getCustomerID(int rowIndex) {
            if (rows.size() == 0) {
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
                    Customer customer = new Customer(rs.getInt(1), rs.getString(2).trim(),
                            Worker.normalizeString(rs.getString(3)),
                            Worker.normalizeString(rs.getString(4)),
                            new java.util.Date(), rs.getInt(5), rs.getInt(6), "");
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
            if (dataSource == null) {
                return 0;
            } else {
                return dataSource.getCurrentRows();
            }
        }

        @Override
        public int getColumnCount() {
            return CUSTOMER_COLUMN_COUNT;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (dataSource == null) {
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
