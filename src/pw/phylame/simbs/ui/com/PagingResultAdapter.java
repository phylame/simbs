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

import pw.phylame.tools.sql.PagingResultSet;
import pw.phylame.tools.sql.PageSwitchException;

import javax.swing.JTable;
import java.sql.SQLException;

/**
 * Adapter for showing data in {@code PagingResultSet}.
 */
public class PagingResultAdapter extends TablePaneAdapter {
    private PagingResultTableModel paneTableModel;
    private JTable table = null;
    private PagingResultSet prs = null;

    public PagingResultAdapter(PagingResultSet prs, PagingResultTableModel paneTableModel) {
        super();
        if (prs == null) {
            throw new NullPointerException("prs");
        }
        if (paneTableModel == null) {
            throw new NullPointerException("tableModel");
        }

        this.paneTableModel = paneTableModel;
        table = new JTable(paneTableModel);

        setDataSource(prs);
    }

    @Override
    public void destroy() {
        paneTableModel = null;
        table = null;
        try {
            prs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDataSource(PagingResultSet prs) {
        this.prs = prs;
        paneTableModel.pageUpdated(prs);
        /* update page status */
        TablePane tablePane = getOwner();
        if (tablePane != null) {
            tablePane.updatePageStatus();
        }
    }

    @Override
    public JTable getTable() {
        return table;
    }

    @Override
    public int getPageCount() {
        return prs.getPageCount();
    }

    @Override
    public int getCurrentRows() {
        return prs.getCurrentRows();
    }

    @Override
    public int getCurrentPage() {
        return prs.getCurrentPage();
    }

    @Override
    public void previousPage() {
        try {
            prs.previousPage();
            paneTableModel.pageUpdated(prs);
        } catch (SQLException | PageSwitchException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nextPage() {
        try {
            prs.nextPage();
            paneTableModel.pageUpdated(prs);
        } catch (SQLException | PageSwitchException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gotoPage(int page) {
        try {
            prs.gotoPage(page);
            paneTableModel.pageUpdated(prs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() {
        try {
            prs.refresh();
            paneTableModel.pageUpdated(prs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
