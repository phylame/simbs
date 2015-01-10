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

import pw.phylame.ixin.IAction;
import pw.phylame.ixin.IToolkit;
import pw.phylame.ixin.frame.IFrame;
import pw.phylame.simbs.Application;
import pw.phylame.simbs.Constants;
import pw.phylame.simbs.ds.Customer;
import pw.phylame.tools.sql.DbHelper;
import pw.phylame.tools.sql.PagingResultSet;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Peng Wan on 2015-1-10.
 */
public class CustomerTablePane extends TablePane {
    public static final int CUSTOMER_COLUMN_COUNT = 6;
    public static final String SQL_SELECT_CUSTOMER = "SELECT Cid, Cname, Cphone, Cemail, Cdate, " +
            "Clevel, Climit FROM customer ";

    public static final int MAX_ROW_COUNT = 15;

    private CustomerTableModel tableModel = null;
    private JTable table = null;

    private JPopupMenu popupMenu = null;

    public CustomerTablePane() {
        DbHelper dbHelper = Application.getInstance().getDbHelper();
        try {
            PagingResultSet dataSource = dbHelper.queryAndPaging(SQL_SELECT_CUSTOMER, MAX_ROW_COUNT);
            this.tableModel = new CustomerTableModel();
            PagingResultAdapter pagingResultAdapter = new PagingResultAdapter(dataSource, this.tableModel);
            this.table = pagingResultAdapter.getTable();
            setTableAdapter(pagingResultAdapter);
            init();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    private void showContextMenu(int x, int y) {
        if (popupMenu.getComponentCount() > 0) {
            popupMenu.show(table, x, y);
        }
    }

    private void init() {
        final Application app = Application.getInstance();
        popupMenu = new JPopupMenu();
        IFrame frame = app.getFrame();
        IAction deleteAction, modifyAction, viewAction;
        modifyAction = frame.getMenuAction(Constants.EDIT_MODIFY);
        if (modifyAction != null) {
            popupMenu.add(IToolkit.createMenuItem(modifyAction, null, frame));
            if (tableModel.getRowCount() == 0) {
                modifyAction.setEnabled(false);
            } else {
                modifyAction.setEnabled(true);
            }
        }
        deleteAction = frame.getMenuAction(Constants.EDIT_DELETE);
        if (deleteAction != null) {
            popupMenu.add(IToolkit.createMenuItem(deleteAction, null, frame));
            if (tableModel.getRowCount() == 0) {
                deleteAction.setEnabled(false);
            } else {
                deleteAction.setEnabled(true);
            }
        }
        viewAction = frame.getMenuAction(Constants.EDIT_VIEW);
        if (viewAction != null) {
            popupMenu.add(IToolkit.createMenuItem(viewAction, null, frame));
            if (tableModel.getRowCount() == 0) {
                viewAction.setEnabled(false);
            } else {
                viewAction.setEnabled(true);
            }
        }

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && ! e.isMetaDown()) {
                    app.onCommand(Constants.EDIT_MODIFY);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!e.isMetaDown()) {  // only right key
                    return;
                }
                int row = table.rowAtPoint(e.getPoint());
                if (row == -1) {
                    return;
                }
                if (! table.isRowSelected(row)) {   // not selected
                    table.setRowSelectionInterval(row, row);
                }
                showContextMenu(e.getX(), e.getY());
            }
        });
    }

    public int getSelectedCustomer() {
        return tableModel.getID(table.getSelectedRow());
    }

    public int[] getSelectedCustomers() {
        int[] rows = table.getSelectedRows();
        if (rows == null) {
            return null;
        }
        int[] customers = new int[rows.length];
        int i = 0;
        for (int row: rows) {
            customers[i++] = tableModel.getID(row);
        }
        return customers;
    }

    /** Update customer at row */
    public void updateBook(int row, Customer customer) {
        tableModel.updateCustomer(row, customer);
    }

    private static class CustomerTableModel extends PagingResultTableModel {
        private PagingResultSet dataSource = null;
        private ArrayList<Customer> rows = new ArrayList<>();

        public int getID(int rowIndex) {
            if (dataSource == null || rowIndex < 0) {
                return -1;
            }
            try {
                return rows.get(rowIndex).getId();
            } catch (IndexOutOfBoundsException exp) {
                exp.printStackTrace();
                return -1;
            }
        }

        public void updateCustomer(int row, Customer customer) {
            rows.set(row, customer);
            fireTableDataChanged();
        }

        /** Update current page from ResultSet */
        public void updateCurrentPage() {
            if (dataSource == null) {
                return;
            }
            ResultSet rs = dataSource.getResultSet();
            if (rs == null) {
                fireTableDataChanged();
                return;
            }
            try {
                for (int i = 0; i < dataSource.getCurrentRows(); ++i) {
                    Customer customer = new Customer(rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getDate(5), rs.getInt(6), rs.getInt(7), null);
                    rows.add(customer);
                    rs.next();
                }
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
            fireTableDataChanged();
        }

        @Override
        public void pageUpdated(PagingResultSet dataSource) {
            this.dataSource = dataSource;
            rows.clear();
            updateCurrentPage();
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
                    return app.getString("Customer.Property.Name");
                case 1:
                    return app.getString("Customer.Property.Phone");
                case 2:
                    return app.getString("Customer.Property.Email");
                case 3:
                    return app.getString("Customer.Property.Date");
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
                Customer customer = rows.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return customer.getName();
                    case 1:
                        return customer.getPhone();
                    case 2:
                        return customer.getEmail();
                    case 3:
                        return customer.getDate();
                    case 4:
                        return customer.getLevel();
                    case 5:
                        return customer.getLimit();
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
