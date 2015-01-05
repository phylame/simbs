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

package pw.phylame.simbs;

import java.sql.*;

/**
 * SQL operations for SIMBS.
 */
public final class SqlAdmin {
    private static SqlAdmin instance = null;

    private SqlAdmin(String user, String pwd) throws SQLException {
        try {
            Class.forName(Constants.SQL_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Not found SQL driver: "+Constants.SQL_DRIVER);
        }

        conn = DriverManager.getConnection(Constants.DB_URI, user, pwd);

        this.user = user;
        this.pwd = pwd;
    }

    public static SqlAdmin getInstance() {
        return instance;
    }

    public static void initialize(String user, String pwd) throws SQLException {
        instance = new SqlAdmin(user, pwd);
    }

    /**
     * Disconnect from SQL DBMS.
     */
    public void disconnect() throws SQLException {
        conn.close();
    }

    /**
     * Get the user name of database.
     */
    public String getUserName() {
        return user;
    }

    public void execute(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        System.out.println("SQL: " + sql);
        Statement stmt = conn.createStatement(resultSetType, resultSetConcurrency);
        stmt.execute(sql);
        stmt.close();
    }

    public void execute(String sql) throws SQLException {
        execute(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    public int executeUpdate(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        System.out.println("SQL: " + sql);
        Statement stmt = conn.createStatement(resultSetType, resultSetConcurrency);
        int n = stmt.executeUpdate(sql);
        stmt.close();
        return n;
    }

    public int executeUpdate(String sql) throws SQLException {
        return executeUpdate(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    public ResultSet executeQuery(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        System.out.println("SQL: " + sql);
        Statement stmt = conn.createStatement(resultSetType, resultSetConcurrency);
        ResultSet rs = stmt.executeQuery(sql);
        stmt.close();
        return rs;
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        return executeQuery(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    /* The database user */
    private String user;

    /* The password of the user */
    private String pwd;

    /* The SQL connection */
    private Connection conn = null;
}
