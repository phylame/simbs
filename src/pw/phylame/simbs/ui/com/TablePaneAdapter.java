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

/**
 * Adapter for {@code TablePane}.
 */
public abstract class TablePaneAdapter {
    private TablePane owner = null;

    protected TablePaneAdapter() {

    }

    /**
     * Set owner to the adapter. This method will invoke by {@code TablePane}.
     * <p>This method should not be overrided.</p>
     * @param tablePane the owner
     */
    public void setOwner(TablePane tablePane) {
        owner = tablePane;
    }

    /** Get the owner of the adapter */
    public TablePane getOwner() {
        return owner;
    }

    /** Destroy this adapter */
    public abstract void destroy();

    /** Get the table */
    public abstract javax.swing.JTable getTable();

    /** Get number of all pages */
    public abstract int getPageCount();

    /** Get current page number */
    public abstract int getCurrentPage();

    /** Get total amount of rows */
    public abstract int getRowCount();

    /** Get number of rows in current page */
    public abstract int getCurrentRows();

    /** Go to previous page */
    public abstract void previousPage();

    /** Go to next page */
    public abstract void nextPage();

    /* Go to page by page number */
    public abstract void gotoPage(int page);

    /** Refresh table */
    public abstract void refresh();

}
