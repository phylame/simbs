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

package pw.phylame.tools.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ResultSet with page.
 */
public class PagingResultSet {
    private DbHelper dbHelper = null;
    private String sql = null;

    private int resultSetType, resultSetConcurrency;

    private ResultSet rs = null;

    /** Number of rows in each page */
    private int pageSize = -1;

    /** Number of total rows */
    private int rowCount = -1;

    /** Number of pages */
    private int pageCount = -1;

    /** Current page, begin from 1 */
    private int currentPage = 1;

    public PagingResultSet(DbHelper dbHelper, String sql, int pageSize, int resultSetType,
                           int resultSetConcurrency) throws SQLException {
        this.dbHelper = dbHelper;
        this.sql = sql;
        this.pageSize = pageSize;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
        refresh();
    }

    /**
     * Destroy and Release resource.
     * @throws SQLException
     */
    public void close() throws SQLException {
        rs.close();
    }

    /** Get the SQL statement */
    public String getStatement() {
        return sql;
    }

    /** Set the SQL statement */
    public void setStatement(String sql) throws SQLException {
        this.sql = sql;
        refresh();
    }

    /** Re-query from database */
    public void refresh() throws SQLException {
        if (rs != null) {
            rs.close();
            rs = null;
        }

        System.out.println("SQL: "+sql);
        Statement stmt = dbHelper.getConnection().createStatement(resultSetType, resultSetConcurrency);
        rs = stmt.executeQuery(sql);
        rs.last();

        rowCount = rs.getRow();
        pageCount = (rowCount + pageSize - 1) / pageSize;
        if (pageCount > 0) {
            if (currentPage > pageCount) {  // data has been deleted in other way, move to last page
                currentPage = pageCount;
            }
            gotoPage(currentPage);
        }
    }

    /** Return the {@code ResultSet} */
    public ResultSet getResultSet() {
        return rs;
    }

    /** Returns size of page */
    public int getPageSize() {
        return pageSize;
    }

    /** Returns count of all rows */
    public int getRowCount() {
        return rowCount;
    }

    /** Returns count of pages */
    public int getPageCount() {
        return pageCount;
    }

    /** Returns number of rows in current page */
    public int getCurrentRows() {
        if (currentPage < pageCount) {      // not last page
            return pageSize;
        } else {
            return rowCount - (currentPage - 1) * pageSize;
        }
    }

    /** Returns number of current page */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Go to next page.
     * @throws PageSwitchException if currently in last page
     * @throws SQLException if occur errors when jump page
     */
    public void nextPage() throws SQLException, PageSwitchException {
        if (currentPage == pageCount) {  // the last page
            throw new PageSwitchException(currentPage, "Currently in last page");
        }
        currentPage++;
        gotoPage(currentPage);
    }

    /**
     * Go to previous page.
     * @throws PageSwitchException if currently in first page
     * @throws SQLException if occur errors when jump page
     */
    public void previousPage() throws SQLException, PageSwitchException {
        if (currentPage == 1) {           // the first page
            throw new PageSwitchException(currentPage, "Currently in first page");
        }
        currentPage--;
        gotoPage(currentPage);
    }

    /**
     * Go to specified page.
     * @param pageNumber number of the page
     * @throws IndexOutOfBoundsException if the pageNumber out of range
     * @throws SQLException if occur errors when jump page
     */
    public void gotoPage(int pageNumber) throws SQLException {
        if (pageNumber > pageCount) {
            throw new IndexOutOfBoundsException("Page number out of range");
        } else if (pageNumber < 1) {
            throw new IndexOutOfBoundsException("Page number out of range");
        }
        // go to first row in the specify page
        rs.absolute((pageNumber - 1) * pageSize + 1);
        this.currentPage = pageNumber;
    }
}
