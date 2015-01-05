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

/**
 * Constants for Simbs.
 */
public final class Constants {

    /** Application version */
    public static final String APP_VERSION = "1.0";

    /** Name of {@code ResourceBundle} file */
    public static final String I18N_PATH = "i18n/simbs";

    // *******************
    // ** SQL connection
    // *******************

    /** Name of SQL driver for JDBC */
    public static final String SQL_DRIVER = "org.hsqldb.jdbcDriver";

    /** Name of default database */
    public static final String DB_NAME = "bookdb";

    /** URL of SQL connection */
    public static final String DB_URI = "jdbc:hsqldb:hsql://localhost/"+DB_NAME;

    /** User home of SIMBS */
    public static final String SIMBS_HOME = String.format("%s/.simbs", System.getProperty("user.home"));

    // ***********************
    // ** SIMBS commands
    // ***********************

    public static final String FILE_EXIT = "file-exit";

    public static final String OPERATION_STORE = "op-store";
    public static final String OPERATION_SELL = "op-sell";
    public static final String OPERATION_LEND = "op-lend";
    public static final String OPERATION_RETURN = "op-return";
    public static final String HELP_ABOUT = "help-about";
}
