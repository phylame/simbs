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
import pw.phylame.simbs.ui.dialog.DialogFactory;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Pane for showing multi-page data.
 */
public class TablePane extends PaneRender {
    private JPanel rootPane;
    private JPanel contentArea;
    private JLabel labelStatus;
    private JButton btnPrev;
    private JButton btnNext;
    private JFormattedTextField tfPage;
    private JButton btnGoto;

    private TablePaneAdapter tableAdapter = null;

    public TablePane() {
        this(null);
    }

    public TablePane(TablePaneAdapter tableAdapter) {
        super();
        init();
        setTableAdapter(tableAdapter);
    }

    private void init() {
        contentArea.setLayout(new BorderLayout());
        btnPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previousPage();
            }
        });
        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextPage();
            }
        });
        tfPage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gotoPage();
            }
        });
        btnGoto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gotoPage();
            }
        });
        tfPage.setValue(1);
    }

    public TablePaneAdapter getTableAdapter() {
        return tableAdapter;
    }

    public void setTableAdapter(TablePaneAdapter tableAdapter) {
        this.tableAdapter = tableAdapter;
        contentArea.removeAll();
        if (tableAdapter != null) {
            tableAdapter.setOwner(this);
            JTable table = tableAdapter.getTable();
            final JTableHeader header = table.getTableHeader();
            header.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    headerClicked(header.columnAtPoint(e.getPoint()));
                }
            });
            contentArea.add(header, BorderLayout.NORTH);
            contentArea.add(new JScrollPane(table), BorderLayout.CENTER);
        }
        contentArea.updateUI();
        updatePageStatus();
    }

    /** Update page status to the label */
    public void updatePageStatus() {
        int currentRows = 0, pageCount = 0, currentPage = 0, totalRows = 0;
        if (tableAdapter != null) {
            totalRows = tableAdapter.getRowCount();
            currentRows = tableAdapter.getCurrentRows();
            pageCount = tableAdapter.getPageCount();
            currentPage = tableAdapter.getCurrentPage();

            if (pageCount <= 1) {   // 0 or 1 page
                btnPrev.setEnabled(false);
                btnNext.setEnabled(false);
                tfPage.setEditable(false);
                btnGoto.setEnabled(false);
            } else {
                tfPage.setEditable(true);
                btnGoto.setEnabled(true);
                if (currentPage == 1) {     // first page
                    btnPrev.setEnabled(false);
                    btnNext.setEnabled(true);
                } else if (currentPage == pageCount) {      // last page
                    btnPrev.setEnabled(true);
                    btnNext.setEnabled(false);
                } else {
                    btnPrev.setEnabled(true);
                    btnNext.setEnabled(true);
                }
            }
        }
        labelStatus.setText(String.format(
                Application.getInstance().getString("Pane.TableViewer.LabelPageInfo"),
                totalRows, currentRows, pageCount));
        tfPage.setValue(currentPage);
    }

    public void previousPage() {
        if (tableAdapter == null) {
            return;
        }
        tableAdapter.previousPage();
        updatePageStatus();
    }

    public void nextPage() {
        if (tableAdapter == null) {
            return;
        }
        tableAdapter.nextPage();
        updatePageStatus();
    }

    public void gotoPage(int page) {
        tableAdapter.gotoPage(page);
        updatePageStatus();
    }

    public void gotoPage() {
        if (tableAdapter == null) {
            return;
        }
        int page = (int) tfPage.getValue();
        if (page < 1 || page > tableAdapter.getPageCount()) {
            Application app = Application.getInstance();
            DialogFactory.showError(getParent(), app.getString("Pane.TableViewer.PageOutOfRange"),
                    app.getString("Pane.TableViewer.GotoPage.Title"));
            return;
        }
        gotoPage(page);
    }

    public int[] getSelectedRows() {
        if (tableAdapter != null) {
            return tableAdapter.getTable().getSelectedRows();
        } else {
            return null;
        }
    }

    public int getSelectedRow() {
        if (tableAdapter != null) {
            return tableAdapter.getTable().getSelectedRow();
        } else {
            return -1;
        }
    }

    public int getRowCount() {
        if (tableAdapter != null) {
            return getTable().getRowCount();
        } else {
            return 0;
        }
    }

    public void reloadTable() {
        if (tableAdapter != null) {
            tableAdapter.refresh();
            updatePageStatus();
            tableUpdated();
        }
    }

    public void removeRow(int row) {
        if (tableAdapter != null) {
            tableAdapter.getTable().removeRowSelectionInterval(row, row);
            tableUpdated();
        }
    }

    public JTable getTable() {
        if (tableAdapter != null) {
            return tableAdapter.getTable();
        } else {
            return null;
        }
    }

    public TableModel getTableModel() {
        if (tableAdapter != null) {
            return getTable().getModel();
        } else {
            return null;
        }
    }

    public void focusTable() {
        if (tableAdapter != null) {
            tableAdapter.getTable().requestFocus();
        }
    }

    public void tableUpdated() {}

    public void headerClicked(int columnIndex) {}

    @Override
    public void destroy() {
        if (tableAdapter != null) {
            tableAdapter.destroy();
        }
    }

    @Override
    public JPanel getPane() {
        return rootPane;
    }
}
