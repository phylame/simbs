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

import pw.haut.simbs.ui.DialogFactory;
import pw.haut.simbs.ui.LoginDialog;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * The entry of application.
 */
public class SimbsApplication {
    public SimbsApplication(String[] args) {
        try {
            languageBundle = ResourceBundle.getBundle(Constants.I18N_PATH, Locale.getDefault());
        } catch (MissingResourceException exp) {
            System.err.println(exp.getMessage());
            System.exit(0);
        }
        manager = new Manager(args);
    }

    /** Returns the {code SimbsApplication} instance */
    public static SimbsApplication getInstance(String[] args) {
        if (instance == null) {
            instance = new SimbsApplication(args);
        }
        return instance;
    }

    /** Get the Simbs application */
    public static SimbsApplication getInstance() {
        return instance;
    }

    /** Returns the {@code Manager} instance */
    public Manager getManager() {
        return manager;
    }

    /** Get translation string by its name */
    public String getString(String key) {
        return languageBundle.getString(key);
    }

    private boolean checkLogin(String user, String pwd) {
        if (user == null || pwd == null) {
            return false;
        }
        try {
            SqlWorker.initialize(user, pwd);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            DialogFactory.showError(getString("Login.Failed"), getString("Login.Title"));
            return false;
        }
    }

    /** Check and login */
    private boolean login() {
        LoginDialog dialog = new LoginDialog();
        dialog.setTitle(getString("Login.Title"));
        dialog.setVisible(true);
        String user = dialog.getUserName(), pwd = dialog.getPassword();
        return checkLogin(user, pwd);
    }

    /** Run SimbsApplication application */
    public void run() {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (login()) {
                    getManager().start();
                } else {
                    System.exit(1);
                }
            }
        });
    }

    /* The application entry method */
    public static void main(String[] args) {
        getInstance(args).run();
    }

    /* The unique instance of SimbsApplication */
    private static SimbsApplication instance = null;

    /* language resource */
    private static ResourceBundle languageBundle = null;

    /* The manage instance */
    private Manager manager = null;
}
