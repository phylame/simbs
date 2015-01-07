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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Peng Wan on 2015-1-5.
 */
public class TablePane implements PaneRender {
    private Component parentComp = null;

    private JButton btnPrev;
    private JButton btnNext;
    private JButton btnGoto;
    private JLabel pageStatus;
    private JPanel rootPane;
    private JFormattedTextField tfPageNumber;

    private JPanel tableArea;

    private TableAdapter tableAdapter = null;

    public TablePane() {
        this(null);
    }

    public TablePane(TableAdapter tableAdapter) {
        tableArea.setLayout(new BorderLayout());

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
        tfPageNumber.addActionListener(new ActionListener() {
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

        tfPageNumber.setValue(1);

        setTableAdapter(tableAdapter);
    }

    public TableAdapter getTableAdapter() {
        return tableAdapter;
    }

    public void setTableAdapter(TableAdapter tableAdapter) {
        this.tableAdapter = tableAdapter;
        updatePane();
    }

    private void updatePane() {
        if (tableAdapter != null) {
            JTable table = tableAdapter.getTable();
            tableArea.add(table.getTableHeader(), BorderLayout.NORTH);
            tableArea.add(new JScrollPane(table), BorderLayout.CENTER);
        }
        updatePageStatus();
    }

    public void updatePageStatus() {
        int currentRows = 0, pageCount = 0, currentPage = 0;
        if (tableAdapter != null) {
            currentRows = tableAdapter.getCurrentRows();
            pageCount = tableAdapter.getPageCount();
            currentPage = tableAdapter.getCurrentPage();

            if (pageCount <= 1) {   // 0 or 1 page
                btnPrev.setEnabled(false);
                btnNext.setEnabled(false);
                tfPageNumber.setEditable(false);
                btnGoto.setEnabled(false);
            } else {
                if (pageCount > 1) {
                    tfPageNumber.setEditable(true);
                    btnGoto.setEnabled(true);
                }
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
        pageStatus.setText(String.format(Application.getInstance().getString("Pane.TableViewer.LabelPageInfo"),
                currentRows, pageCount));
        tfPageNumber.setValue(currentPage);
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

    public void gotoPage() {
        if (tableAdapter == null) {
            return;
        }
        int page = (int) tfPageNumber.getValue();
        if (page < 1 || page > tableAdapter.getPageCount()) {
            Application app = Application.getInstance();
            DialogFactory.showError(parentComp, app.getString( "Pane.TableViewer.PageOutOfRange"),
                    app.getString("Pane.TableViewer.GotoPage.Title"));
            return;
        }
        tableAdapter.gotoPage(page);
        updatePageStatus();
    }

    @Override
    public void setParent(Component parent) {
        parentComp = parent;
    }

    @Override
    public JPanel getPane() {
        return rootPane;
    }
}
