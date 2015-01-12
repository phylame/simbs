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
import pw.phylame.simbs.ds.Book;
import pw.phylame.simbs.ds.Customer;
import pw.phylame.tools.sql.DbHelper;
import pw.phylame.tools.sql.PagingResultSet;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Peng Wan on 2015-1-10.
 */
public class CustomerTablePane extends ViewerTablePane {
    public static final int CUSTOMER_COLUMN_COUNT = 7;
    public static final String SQL_SELECT_CUSTOMER = "SELECT Cid, Cname, Cphone, Cemail, Cdate," +
            " Clevel, Climit, SUM(S.Snumber), SUM(R.Rnumber)" +
            " FROM customer AS C " +
            "LEFT JOIN rental AS R ON R.Cid=C.Cid " +
            "LEFT JOIN sale AS S ON S.Cid=C.Cid " +
            "WHERE Cid<>0 " +
            "GROUP BY C.Cid ";

    public static final int MAX_ROW_COUNT = 20;

    public CustomerTablePane() {
        super(new CustomerTableModel(), SQL_SELECT_CUSTOMER, MAX_ROW_COUNT);
    }

    public int getSelectedCustomer() {
        CustomerTableModel tableModel = (CustomerTableModel) getTableModel();
        return tableModel.getID(getSelectedRow());
    }

    public int[] getSelectedCustomers() {
        int[] rows = getSelectedRows();
        if (rows == null) {
            return null;
        }
        int[] customers = new int[rows.length];
        int i = 0;
        CustomerTableModel tableModel = (CustomerTableModel) getTableModel();
        for (int row: rows) {
            customers[i++] = tableModel.getID(row);
        }
        return customers;
    }

    /** Update customer at row */
    public void updateCustomer(int row, Customer customer) {
        CustomerTableModel tableModel = (CustomerTableModel) getTableModel();
        tableModel.updateCustomer(row, customer);
    }

    private static class CustomerTableModel extends PagingResultTableModel {
        private static class Entry extends Customer {
            private int boughtNumber, borrowedNumber;

            public Entry(int id, String name, String phone, String email, Date date, int level,
                         int limit, String comment) {
                super(id, name, phone, email, date, level, limit, comment);
            }

            public void setField(Customer customer) {
                setId(customer.getId());
                setName(customer.getName());
                setPhone(customer.getPhone());
                setEmail(customer.getEmail());
                setDate(customer.getDate());
                setLevel(customer.getLevel());
                setLimit(customer.getLimit());
                setComment(customer.getComment());
            }

            public int getBoughtNumber() {
                return boughtNumber;
            }

            public void setBoughtNumber(int boughtNumber) {
                this.boughtNumber = boughtNumber;
            }

            public int getBorrowedNumber() {
                return borrowedNumber;
            }

            public void setBorrowedNumber(int borrowedNumber) {
                this.borrowedNumber = borrowedNumber;
            }
        }
        private ArrayList<Entry> rows = new ArrayList<>();

        public int getID(int rowIndex) {
            if (rows.size() == 0 || rowIndex < 0) {
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
            Entry entry = rows.get(row);
            entry.setField(customer);
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
            try {
                for (int i = 0; i < dataSource.getCurrentRows(); ++i) {
                    Entry entry = new Entry(rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getDate(5), rs.getInt(6), rs.getInt(7), null);
                    entry.setBoughtNumber(rs.getInt(8));
                    entry.setBorrowedNumber(rs.getInt(9));
                    rows.add(entry);
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
                    return app.getString("Customer.Property.ID");
                case 1:
                    return app.getString("Customer.Property.Name");
                case 2:
                    return app.getString("Customer.Property.Phone");
                case 3:
                    return app.getString("Customer.Property.Level");
                case 4:
                    return app.getString("Customer.Property.Limit");
                case 5:
                    return app.getString("Customer.Property.Bought");
                case 6:
                    return app.getString("Customer.Property.Borrowed");
                default:
                    return app.getString("Customer.Property.Unknown");
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
            return CUSTOMER_COLUMN_COUNT;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rows.size() == 0) {
                return null;
            }
            try {
                Entry customer = rows.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return customer.getId();
                    case 1:
                        return customer.getName();
                    case 2:
                        return customer.getPhone();
                    case 3:
                        return customer.getLevel();
                    case 4:
                        return customer.getLimit();
                    case 5:
                        return customer.getBoughtNumber();
                    case 6:
                        return customer.getBorrowedNumber();
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
