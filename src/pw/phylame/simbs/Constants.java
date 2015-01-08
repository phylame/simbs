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

import pw.phylame.tools.sql.Profile;

import java.util.ResourceBundle;

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

    // *********************
    // ** HyperSQL(HSQLDB)
    // *********************
    public static final String HYPER_DRIVER = "org.hsqldb.jdbcDriver";
    public static final String HYPER_PROTOCOL = "jdbc:hsqldb:hsql://";
    public static final String HYPER_HOST = "localhost";
    public static final int HYPER_PORT = -1;
    public static final String HYPER_DATABASE = "bookdb";

    // ********************
    // ** PostgreSQL
    // ********************
    public static final String POSTGRE_DRIVER = "org.postgresql.Driver";
    public static final String POSTGRE_PROTOCOL = "jdbc:postgresql://";
    public static final String POSTGRE_HOST = "localhost";
    public static final int POSTGRE_PORT = 5432;
    public static final String POSTGRE_DATABASE = "bookdb";

    private static final java.util.List<Profile> DataProfiles = new java.util.ArrayList<>();
    static {
        ResourceBundle rb = ResourceBundle.getBundle(Constants.I18N_PATH);
        DataProfiles.add(new Profile(0, rb.getString("Dialog.Login.DataSource.HyperSQL"),
                HYPER_DRIVER, HYPER_PROTOCOL, HYPER_HOST, HYPER_PORT, HYPER_DATABASE, null, null));
        DataProfiles.add(new Profile(1, rb.getString("Dialog.Login.DataSource.PostgreSQL"),
                POSTGRE_DRIVER, POSTGRE_PROTOCOL, POSTGRE_HOST, POSTGRE_PORT, POSTGRE_DATABASE, null, null));
    }

    public static java.util.List<Profile> getDatabaseProfiles() {
        return DataProfiles;
    }

    public static Profile getDatabaseProfile(int id) {
        return DataProfiles.get(id);
    }

    /** User home of SIMBS */
    public static final String SIMBS_HOME = String.format("%s/.simbs", System.getProperty("user.home"));


    /** Max rows in result table */
    public static final int MAX_ROW_COUNT = 8;

    /** Days limit for customer borrow book */
    public static final int MAX_LENT_DAYS = 100;

    /** Number of book for customer can borrow each time */
    public static final int DEFAULT_LENT_LIMIT = 10;

    public static final float PRICE_OF_INCREASE_LIMIT = 100.0F;

    public static final float PRICE_OF_INCREASE_LEVEL = 200.0F;


    // ***********************
    // ** SIMBS commands
    // ***********************

    public static final String REGISTER_BOOK = "register-book";
    public static final String REGISTER_CUSTOMER = "register-customer";

    public static final String EXIT_APP = "exit-app";

    public static final String STORE_PROPERTIES = "store-prop";

    public static final String EDIT_MODIFY = "edit-modify";
    public static final String EDIT_DELETE = "edit-delete";

    public static final String VIEW_BOOK = "view-book";
    public static final String VIEW_CUSTOMER = "view-customer";
    public static final String VIEW_INVENTORY = "view-inventory";
    public static final String SHOW_NAVIGATE = "view-navigate";

    public static final String STORE_BOOK = "store-book";
    public static final String SELL_BOOK = "sell-book";
    public static final String LEND_BOOK = "lend-book";
    public static final String RETURN_BOOK = "return-book";

    public static final String HELP_ABOUT = "help-about";
}
