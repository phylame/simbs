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

package pw.phylame.simbs.ui;

import static pw.phylame.simbs.Constants.*;

import pw.phylame.ixin.IMenuLabel;
import pw.phylame.ixin.IMenuModel;
import pw.phylame.simbs.Application;

/**
 * UI designer.
 */
public final class UIDesign {

    private static Application app = Application.getInstance();

    // *******************
    // ** Menu actions
    // *******************

    /* File */
    public static final Object[][] FILE_MENU_ACTIONS = {
            {REGISTER_BOOK,
                    app.getString("Menu.File.Add.Book"), app.getString("Menu.File.Add.Book.Icon"),
                    app.getString("Menu.File.Add.Book.Mnemonic"), app.getString("Menu.File.Add.Book.Shortcut"),
                    app.getString("Menu.File.Add.Book.Tip")},
            {REGISTER_CUSTOMER,
                    app.getString("Menu.File.Add.Customer"), app.getString("Menu.File.Add.Customer.Icon"),
                    app.getString("Menu.File.Add.Customer.Mnemonic"), app.getString("Menu.File.Add.Customer.Shortcut"),
                    app.getString("Menu.File.Add.Customer.Tip")},
            {STORE_PROPERTIES,
                    app.getString("Menu.File.Properties"), app.getString("Menu.File.Properties.Icon"),
                    app.getString("Menu.File.Properties.Mnemonic"), app.getString("Menu.File.Properties.Shortcut"),
                    app.getString("Menu.File.Properties.Tip")},
            {EXIT_APP,
                    app.getString("Menu.File.Exit"), app.getString("Menu.File.Exit.Icon"),
                    app.getString("Menu.File.Exit.Mnemonic"), app.getString("Menu.File.Exit.Shortcut"),
                    app.getString("Menu.File.Exit.Tip")}
    };

    /* Edit */
    public static final Object[][] EDIT_MENU_ACTIONS = {
            {EDIT_MODIFY,
                    app.getString("Menu.Edit.Modify"), app.getString("Menu.Edit.Modify.Icon"),
                    app.getString("Menu.Edit.Modify.Mnemonic"), app.getString("Menu.Edit.Modify.Shortcut"),
                    app.getString("Menu.Edit.Modify.Tip"), false},
            {EDIT_DELETE,
                    app.getString("Menu.Edit.Delete"), app.getString("Menu.Edit.Delete.Icon"),
                    app.getString("Menu.Edit.Delete.Mnemonic"), app.getString("Menu.Edit.Delete.Shortcut"),
                    app.getString("Menu.Edit.Delete.Tip"), false}
    };

    /* View */
    public static final Object[][] VIEW_MENU_ACTIONS = {
            {VIEW_BOOK,
                    app.getString("Menu.View.Book"), app.getString("Menu.View.Book.Icon"),
                    app.getString("Menu.View.Book.Mnemonic"), app.getString("Menu.View.Book.Shortcut"),
                    app.getString("Menu.View.Book.Tip")},
            {VIEW_CUSTOMER,
                    app.getString("Menu.View.Customer"), app.getString("Menu.View.Customer.Icon"),
                    app.getString("Menu.View.Customer.Mnemonic"), app.getString("Menu.View.Customer.Shortcut"),
                    app.getString("Menu.View.Customer.Tip")},
            {VIEW_HOME,
                    app.getString("Menu.View.Home"), app.getString("Menu.View.Home.Icon"),
                    app.getString("Menu.View.Home.Mnemonic"), app.getString("Menu.View.Home.Shortcut"),
                    app.getString("Menu.View.Home.Tip")},
    };

    /* Options */
    public static final Object[][] OPTIONS_MENU_ACTIONS = {
            {STORE_BOOK,
                    app.getString("Menu.Options.Store"), app.getString("Menu.Options.Store.Icon"),
                    app.getString("Menu.Options.Store.Mnemonic"), app.getString("Menu.Options.Store.Shortcut"),
                    app.getString("Menu.Options.Store.Tip")},
            {SELL_BOOK,
                    app.getString("Menu.Options.Sell"), app.getString("Menu.Options.Sell.Icon"),
                    app.getString("Menu.Options.Sell.Mnemonic"), app.getString("Menu.Options.Sell.Shortcut"),
                    app.getString("Menu.Options.Sell.Tip")},
            {LEND_BOOK,
                    app.getString("Menu.Options.Lend"), app.getString("Menu.Options.Lend.Icon"),
                    app.getString("Menu.Options.Lend.Mnemonic"), app.getString("Menu.Options.Lend.Shortcut"),
                    app.getString("Menu.Options.Lend.Tip")},
            {RETURN_BOOK,
                    app.getString("Menu.Options.Return"), app.getString("Menu.Options.Return.Icon"),
                    app.getString("Menu.Options.Return.Mnemonic"), app.getString("Menu.Options.Return.Shortcut"),
                    app.getString("Menu.Options.Return.Tip")},

    };

    /* Help */
    public static final Object[][] HELP_MENU_ACTIONS = {
            {HELP_ABOUT,
                    app.getString("Menu.Help.About"), app.getString("Menu.Help.About.Icon"),
                    app.getString("Menu.Help.About.Mnemonic"), app.getString("Menu.Help.About.Shortcut"),
                    app.getString("Menu.Help.About.Tip")}
    };

    public static final Object[][][] MENU_ACTIONS = {
            FILE_MENU_ACTIONS, EDIT_MENU_ACTIONS, VIEW_MENU_ACTIONS, OPTIONS_MENU_ACTIONS,
            HELP_MENU_ACTIONS
    };

    // ***************
    // ** Menu model
    // ***************


    /* File */
    private static final Object[] FILE_ADD_MENU = {
            new IMenuLabel(app.getString("Menu.File.Add"), app.getString("Menu.File.Add.Icon"),
                    app.getString("Menu.File.Add.Mnemonic")),
            REGISTER_BOOK, REGISTER_CUSTOMER
    };
    public static final Object[] FILE_MENU_MODEL = {
            new IMenuLabel(app.getString("Menu.File"), null, app.getString("Menu.File.Mnemonic")),
            FILE_ADD_MENU,
            null,
            STORE_PROPERTIES,
            null,
            EXIT_APP
    };

    /* Edit */
    public static final Object[] EDIT_MENU_MODEL = {
            new IMenuLabel(app.getString("Menu.Edit"), null, app.getString("Menu.Edit.Mnemonic")),
            EDIT_MODIFY,
            EDIT_DELETE
    };

    /* View */
    private static final Object[] VIEW_MENU_MODEL = {
            new IMenuLabel(app.getString("Menu.View"), null,
                    app.getString("Menu.View.Mnemonic")),
            new IMenuModel(VIEW_BOOK, IMenuModel.MenuType.RADIO, false),
            new IMenuModel(VIEW_CUSTOMER, IMenuModel.MenuType.RADIO, false),
            new IMenuModel(VIEW_HOME, IMenuModel.MenuType.RADIO, true),
    };

    /* Search */
    public static final Object[] SEARCH_MENU_MODEL = {
            new IMenuLabel(app.getString("Menu.View"), null, app.getString("Menu.View.Mnemonic")),

    };

    /* Options */
    public static final Object[] OPTIONS_MENU_MODEL = {
            new IMenuLabel(app.getString("Menu.Options"), null, app.getString("Menu.Options.Mnemonic")),
            STORE_BOOK,
            SELL_BOOK,
            LEND_BOOK,
            RETURN_BOOK
    };


    /* Help */
    public static final Object[] HELP_MENU_MODEL = {
            new IMenuLabel(app.getString("Menu.Help"), null, app.getString("Menu.Help.Mnemonic")),
            HELP_ABOUT
    };

    /* Menu bar */
    public static Object[][] MENU_BAR_MODEL = {
            FILE_MENU_MODEL, EDIT_MENU_MODEL,
            OPTIONS_MENU_MODEL, VIEW_MENU_MODEL,  HELP_MENU_MODEL
    };

    /* Toolbar */
    public static Object[] TOOL_BAR_MODEL = {
            REGISTER_BOOK, REGISTER_CUSTOMER,
            null,
            STORE_BOOK, SELL_BOOK, LEND_BOOK, RETURN_BOOK
    };
}
