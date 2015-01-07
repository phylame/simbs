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

/**
* Profile of database connection.
*/
public final class Profile {
    /** Unique ID of the profile */
    private int id;

    /** Readable label of the profile */
    private String label;

    /** JDBC driver */
    private String driver;

    /** Protocol for connection */
    private String protocol;

    /** Host of database server */
    private String host = "localhost";

    /** The server port */
    private int port = -1;

    /** Name of the database */
    private String database;

    /** Name of the login user */
    private String userName;

    /** Password of the user */
    private String password;

    public Profile(int id, String label, String driver, String protocol, String host, int port,
                   String database, String userName, String password) {
        setId(id);
        setLabel(label);
        setDriver(driver);
        setProtocol(protocol);
        setHost(host);
        setPort(port);
        setDatabase(database);
        setUserName(userName);
        setPassword(password);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getURL() {
        StringBuilder sb = new StringBuilder(getProtocol());
        sb.append(getHost());
        if (getPort() != -1) {
            sb.append(":").append(getPort());
        }
        sb.append("/").append(getDatabase());
        return sb.toString();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String user) {
        this.userName = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pwd) {
        this.password = pwd;
    }
}
