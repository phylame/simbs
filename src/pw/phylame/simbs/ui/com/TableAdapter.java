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

import pw.phylame.tools.sql.PageResultSet;
import pw.phylame.tools.sql.PageSwitchException;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Adapter for {@code TablePane}.
 */
public class TableAdapter {
    private PaneTableModel paneTableModel = null;
    private JTable table = null;
    private PageResultSet prs = null;

    public TableAdapter(PageResultSet prs, PaneTableModel paneTableModel) {
        if (prs == null) {
            throw new NullPointerException("prs");
        }
        if (paneTableModel == null) {
            throw new NullPointerException("tableModel");
        }
        this.prs = prs;
        this.paneTableModel = paneTableModel;

        table = new JTable(paneTableModel);
        paneTableModel.setDataSource(prs);
    }

    /**
     * Destroy and release resource.
     */
    public void destroy() {
        paneTableModel = null;
        table = null;
        try {
            prs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDataSource(PageResultSet prs) {
        this.prs = prs;
        paneTableModel.setDataSource(prs);
    }

    public JTable getTable() {
        return table;
    }

    public int getPageCount() {
        return prs.getPageCount();
    }

    public int getCurrentRows() {
        return prs.getCurrentRows();
    }

    public int getCurrentPage() {
        return prs.getCurrentPage();
    }

    public void previousPage() {
        try {
            prs.previousPage();
            paneTableModel.pageUpdated(prs);
        } catch (SQLException | PageSwitchException e) {
//            e.printStackTrace();
        }
    }

    public void nextPage() {
        try {
            prs.nextPage();
            paneTableModel.pageUpdated(prs);
        } catch (SQLException | PageSwitchException e) {
//            e.printStackTrace();
        }
    }

    public void gotoPage(int page) {
        try {
            prs.gotoPage(page);
            paneTableModel.pageUpdated(prs);
        } catch (SQLException e) {
//            e.printStackTrace();
        }
    }

    public void refresh() {
        try {
            prs.refresh();
            paneTableModel.pageUpdated(prs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
