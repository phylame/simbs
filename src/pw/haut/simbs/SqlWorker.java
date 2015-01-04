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

package pw.haut.simbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * SQL operations for SIMBS.
 */
public final class SqlWorker {
    private static SqlWorker instance = null;

    private SqlWorker(String user, String pwd) throws SQLException {
        try {
            Class.forName(Constants.SQL_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Not found SQL driver: "+Constants.SQL_DRIVER);
        }

        conn = DriverManager.getConnection(Constants.DB_URI, user, pwd);
    }

    public static SqlWorker getInstance() {
        return instance;
    }

    public static void initialize(String user, String pwd) throws SQLException {
        instance = new SqlWorker(user, pwd);

    }

    /* The database user */
    private String user;

    /* The password of the user */
    private String pwd;

    /* The SQL connection */
    private Connection conn = null;
}
