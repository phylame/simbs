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
import pw.phylame.simbs.SimbsApplication;

/**
 * UI designer.
 */
public final class UIDesign {

    private static SimbsApplication app = SimbsApplication.getInstance();

    // *******************
    // ** Menu actions
    // *******************

    /* File */
    public static final Object[][] FILE_MENU_ACTIONS = {
            {FILE_EXIT,
                    app.getString("Menu.File.Exit"), app.getString("Menu.File.Exit.Icon"),
                    app.getString("Menu.File.Exit.Mnemonic"), app.getString("Menu.File.Exit.Shortcut"),
                    app.getString("Menu.File.Exit.Tip")}
    };

    /* Edit */
    public static final Object[][] EDIT_MENU_ACTIONS = {

    };

    /* Operation */
    public static final Object[][] OPERATION_MENU_ACTIONS = {
            {OPERATION_STORE,
                    app.getString("Menu.Operation.Store"), app.getString("Menu.Operation.Store.Icon"),
                    app.getString("Menu.Operation.Store.Mnemonic"), app.getString("Menu.Operation.Store.Shortcut"),
                    app.getString("Menu.Operation.Store.Tip")},
            {OPERATION_SELL,
                    app.getString("Menu.Operation.Sell"), app.getString("Menu.Operation.Sell.Icon"),
                    app.getString("Menu.Operation.Sell.Mnemonic"), app.getString("Menu.Operation.Sell.Shortcut"),
                    app.getString("Menu.Operation.Sell.Tip")},
            {OPERATION_LEND,
                    app.getString("Menu.Operation.Lend"), app.getString("Menu.Operation.Lend.Icon"),
                    app.getString("Menu.Operation.Lend.Mnemonic"), app.getString("Menu.Operation.Lend.Shortcut"),
                    app.getString("Menu.Operation.Lend.Tip")},
            {OPERATION_RETURN,
                    app.getString("Menu.Operation.Return"), app.getString("Menu.Operation.Return.Icon"),
                    app.getString("Menu.Operation.Return.Mnemonic"), app.getString("Menu.Operation.Return.Shortcut"),
                    app.getString("Menu.Operation.Return.Tip")},

    };

    /* Help */
    public static final Object[][] HELP_MENU_ACTIONS = {
            {HELP_ABOUT,
                    app.getString("Menu.Help.About"), app.getString("Menu.Help.About.Icon"),
                    app.getString("Menu.Help.About.Mnemonic"), app.getString("Menu.Help.About.Shortcut"),
                    app.getString("Menu.Help.About.Tip")}
    };

    public static final Object[][][] MENU_ACTIONS = {
            FILE_MENU_ACTIONS, EDIT_MENU_ACTIONS, OPERATION_MENU_ACTIONS, HELP_MENU_ACTIONS
    };

    // ***************
    // ** Menu model
    // ***************


    /* File */
    public static final Object[] FILE_MENU_MODEL = {
            new IMenuLabel(app.getString("Menu.File"), null, app.getString("Menu.File.Mnemonic")),
            null,
            FILE_EXIT
    };

    /* Edit */
    public static final Object[] EDIT_MENU_MODEL = {
            new IMenuLabel(app.getString("Menu.Edit"), null, app.getString("Menu.Edit.Mnemonic")),
    };

    /* Operation */
    public static final Object[] OPERATION_MENU_MODEL = {
            new IMenuLabel(app.getString("Menu.Operation"), null, app.getString("Menu.Operation.Mnemonic")),
            OPERATION_STORE,
            OPERATION_SELL,
            OPERATION_LEND,
            OPERATION_RETURN
    };


    /* Help */
    public static final Object[] HELP_MENU_MODEL = {
            new IMenuLabel(app.getString("Menu.Help"), null, app.getString("Menu.Help.Mnemonic")),
            HELP_ABOUT
    };

    /* Menu bar */
    public static Object[][] MENU_BAR_MODEL = {
            FILE_MENU_MODEL, EDIT_MENU_MODEL, OPERATION_MENU_MODEL, HELP_MENU_MODEL
    };

    /* Toolbar */
    public static Object[] TOOL_BAR_MODEL = {
            OPERATION_STORE, OPERATION_SELL, OPERATION_LEND, OPERATION_RETURN
    };
}
