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
import pw.phylame.tools.sql.PagingResultSet;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Peng Wan on 2015-1-11.
 */
public class BillTablePane extends ViewerTablePane {
    public static final int BILL_COLUMN_COUNT = 3;
    public static final String SQL_SELECT_BILL = "SELECT Lno, Ldate, Ltime, Levent, Lid FROM bill ";

    public static final int MAX_ROW_COUNT = 20;

    public BillTablePane() {
        super(new BillTableModel(), SQL_SELECT_BILL, MAX_ROW_COUNT);
        getDeleteAction().setEnabled(false);
        getModifyAction().setEnabled(false);
    }

    public int getSelectedRecord() {
        BillTableModel tableModel = (BillTableModel) getTableModel();
        return tableModel.getRecordNO(getSelectedRow());
    }

    private static class BillTableModel extends PagingResultTableModel {
        private static class Entry {
            private int no;
            private Date date;
            private int event, eventId;
            private BigDecimal money;

            public Entry(int no, Date date, int event, int eventId, BigDecimal money) {
                setNo(no);
                setDate(date);
                setEvent(event);
                setEventId(eventId);
                setMoney(money);
            }

            public int getNo() {
                return no;
            }

            public void setNo(int no) {
                this.no = no;
            }

            public Date getDate() {
                return date;
            }

            public void setDate(Date date) {
                this.date = date;
            }

            public int getEvent() {
                return event;
            }

            public void setEvent(int event) {
                this.event = event;
            }

            public int getEventId() {
                return eventId;
            }

            public void setEventId(int eventId) {
                this.eventId = eventId;
            }

            public BigDecimal getMoney() {
                return money;
            }

            public void setMoney(BigDecimal money) {
                this.money = money;
            }
        }

        private ArrayList<Entry> rows = new ArrayList<>();

        public int getRecordNO(int rowIndex) {
            if (rows.size() == 0 || rowIndex < 0) {
                return -1;
            }
            try {
                return rows.get(rowIndex).getNo();
            } catch (IndexOutOfBoundsException exp) {
                exp.printStackTrace();
                return -1;
            }
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
                    Entry entry = new Entry(rs.getInt(1), Worker.toNormalDate(rs.getDate(2),
                            rs.getTime(3)), rs.getInt(4), rs.getInt(5), null);
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
                    return app.getString("Pane.Bill.Column.NO");
                case 1:
                    return app.getString("Pane.Bill.Column.Date");
                case 2:
                    return app.getString("Pane.Bill.Column.Event");
                default:
                    return app.getString("Pane.Bill.Column.Unknown");
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
            return BILL_COLUMN_COUNT;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rows.size() == 0) {
                return null;
            }
            Application app = Application.getInstance();
            try {
                Entry entry = rows.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return entry.getNo();
                    case 1:
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        return sdf.format(entry.getDate());
                    case 2:
                        switch (entry.getEvent()) {
                            case Worker.EVENT_STOCK:
                                return app.getString("Pane.Bill.Event.Stock");
                            case Worker.EVENT_SALE:
                                return app.getString("Pane.Bill.Event.Sale");
                            case Worker.EVENT_RENTAL:
                                return app.getString("Pane.Bill.Event.Rental");
                            case Worker.EVENT_RETURN:
                                return app.getString("Pane.Bill.Event.Return");
                            default:
                                return app.getString("Pane.Bill.Event.Unknown");
                        }
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
