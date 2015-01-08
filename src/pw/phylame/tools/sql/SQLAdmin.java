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

import java.sql.*;

/**
 * Manager of database connection.
 */
public class SQLAdmin {
    private Profile profile = null;
    private Connection conn = null;

    public SQLAdmin(Profile profile) throws SQLException {
        if (profile == null) {
            throw new NullPointerException("Profile is null");
        }
        this.profile = profile;
        connect();
    }

    /** Connect to database server */
    private void connect() throws SQLException {
        try {
            Class.forName(profile.getDriver());
        } catch (ClassNotFoundException e) {
            throw new SQLException("Not found JDBC drive: "+profile.getDriver());
        }
        conn = DriverManager.getConnection(profile.getURL(), profile.getUserName(), profile.getPassword());
    }

    /** Disconnect from database server. */
    public void disconnect() throws SQLException {
        if (conn != null) {
            conn.close();
        }
        conn = null;
    }

    /** Returns currently used {@code Profile}. */
    public Profile getProfile() {
        return profile;
    }

    /** Returns currently database connection */
    public Connection getConnection() {
        return conn;
    }

    /** Check the connection */
    private void checkConnection() throws SQLException {
        if (conn == null) {
            throw new SQLException("Connection is not existed.");
        }
    }

    /**
     * Execute update operation, like CREATE, INSERT, UPDATE.
     * @param sql the SQL statement
     * @param resultSetType a result set type, see {@link java.sql.ResultSet}
     * @param resultSetConcurrency a concurrency type, see {@link java.sql.ResultSet}
     * @return number of updated rows
     * @throws SQLException if occur errors when querying
     */
    public int executeUpdate(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkConnection();
        System.out.println("SQL: "+sql);
        Statement stmt = conn.createStatement(resultSetType, resultSetConcurrency);
        int n = stmt.executeUpdate(sql);
        stmt.close();
        return n;
    }

    /**
     * Execute update operation, like CREATE, INSERT, UPDATE.
     * @param sql the SQL statement
     * @return number of updated rows
     * @throws SQLException if occur errors when querying
     */
    public int executeUpdate(String sql) throws SQLException {
        return executeUpdate(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    /**
     * Execute query operation, mostly be SELECT.
     * @param sql the SQL statement
     * @param resultSetType a result set type, see {@link java.sql.ResultSet}
     * @param resultSetConcurrency a concurrency type, see {@link java.sql.ResultSet}
     * @return the {@code ResultSet} of query
     * @throws SQLException if occur errors when querying
     */
    public ResultSet executeQuery(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkConnection();
        System.out.println("SQL: "+sql);
        Statement stmt = conn.createStatement(resultSetType, resultSetConcurrency);
        ResultSet rs = stmt.executeQuery(sql);
        stmt.close();
        return rs;
    }

    /**
     * Execute query operation, mostly be SELECT.
     * @param sql the SQL statement
     * @return the {@code ResultSet} of query
     * @throws SQLException if occur errors when querying
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        return executeQuery(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    /**
     * Create {@code PreparedStatement} object.
     * @param sql the SQL statement
     * @param resultSetType a result set type, see {@link java.sql.ResultSet}
     * @param resultSetConcurrency a concurrency type, see {@link java.sql.ResultSet}
     * @return the {@code PreparedStatement} object
     * @throws SQLException if occur SQL errors
     */
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        checkConnection();
        System.out.println("SQL: "+sql);
        return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * Create {@code PreparedStatement} object.
     * @param sql the SQL statement
     * @return the {@code PreparedStatement} object
     * @throws SQLException if occur SQL errors
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    /**
     * Query and paging results.
     * @param sql the SQL statement
     * @param pageSize size of page
     * @return {@code PageResultSet} object
     * @throws SQLException if occur errors when querying or paging
     */
    public PageResultSet queryAndPaging(String sql, int pageSize) throws SQLException {
        checkConnection();
        return new PageResultSet(this, sql, pageSize);
    }
}
