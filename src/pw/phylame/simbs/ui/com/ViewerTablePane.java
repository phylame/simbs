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
import java.sql.SQLException;

/**
 * Created by Peng Wan on 2015-1-11.
 */
public class ViewerTablePane extends TablePane {
    private JPopupMenu popupMenu = null;
    private IAction deleteAction, modifyAction, viewAction;

    public ViewerTablePane(PagingResultTableModel tableModel, String sqlQuery, int maxRow) {
        DbHelper dbHelper = Application.getInstance().getDbHelper();
        try {
            PagingResultSet dataSource = dbHelper.queryAndPaging(sqlQuery, maxRow);
            PagingResultAdapter pagingResultAdapter = new PagingResultAdapter(dataSource, tableModel);
            setTableAdapter(pagingResultAdapter);
            init();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        deleteAction.setEnabled(false);
        modifyAction.setEnabled(false);
        viewAction.setEnabled(false);
    }

    @Override
    public void tableUpdated() {
        updateActionStatus();
    }

    public IAction getDeleteAction() {
        return deleteAction;
    }

    public IAction getModifyAction() {
        return modifyAction;
    }

    public IAction getViewAction() {
        return viewAction;
    }

    private void showContextMenu(int x, int y) {
        if (popupMenu.getComponentCount() > 0) {
            popupMenu.show(getTable(), x, y);
        }
    }

    private void init() {
        final Application app = Application.getInstance();
        popupMenu = new JPopupMenu();
        IFrame frame = app.getFrame();
        modifyAction = frame.getMenuAction(Constants.EDIT_MODIFY);
        if (modifyAction != null) {
            popupMenu.add(IToolkit.createMenuItem(modifyAction, null, frame));
        }
        deleteAction = frame.getMenuAction(Constants.EDIT_DELETE);
        if (deleteAction != null) {
            popupMenu.add(IToolkit.createMenuItem(deleteAction, null, frame));
        }
        viewAction = frame.getMenuAction(Constants.EDIT_VIEW);
        if (viewAction != null) {
            popupMenu.add(IToolkit.createMenuItem(viewAction, null, frame));
        }

        final JTable table = getTable();
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
        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
        updateActionStatus();
    }

    private void updateActionStatus() {
        if (getRowCount() > 0) {
            viewAction.setEnabled(true);
            modifyAction.setEnabled(true);
            deleteAction.setEnabled(true);
        } else {
            viewAction.setEnabled(false);
            modifyAction.setEnabled(false);
            deleteAction.setEnabled(false);
        }
    }
}
