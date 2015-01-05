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

import pw.phylame.simbs.ui.dialog.DialogFactory;
import pw.phylame.simbs.ui.dialog.LoginDialog;

import javax.swing.*;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * The entry of application.
 */
public class SimbsApplication {
    public SimbsApplication(String[] args) {
        this.args = args;
        try {
            languageBundle = ResourceBundle.getBundle(Constants.I18N_PATH, Locale.getDefault());
        } catch (MissingResourceException exp) {
            System.err.println(exp.getMessage());
            System.exit(0);
        }

        init();
    }

    private void init() {
        File homeDir = new File(Constants.SIMBS_HOME);
        if (! homeDir.exists()) {
            homeDir.mkdirs();
        }
        if (! homeDir.exists()) {
            throw new RuntimeException("Cannot create SIMBS home directory");
        }
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
        if (manager == null) {
            manager = new Manager(args, this);
        }
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
            SqlAdmin.initialize(user, pwd);
            saveUsername(user);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            DialogFactory.showError(getString("Login.Failed"), getString("Login.Title"));
            return false;
        }
    }

    private void saveUsername(String user) {
        Properties prop = new Properties();
        prop.put("user", user);
        prop.put("date", new Date().toString());
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Constants.SIMBS_HOME+"/login.prop");
            prop.store(out, "SIMBS login user name");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** Check and login */
    private boolean login() {
        LoginDialog dialog = new LoginDialog();
        dialog.setTitle(getString("Login.Title"));
        dialog.setUserName(getUserName());
        dialog.setVisible(true);
        String user = dialog.getUserName(), pwd = dialog.getPassword();
        return checkLogin(user, pwd);
    }

    private String getUserName() {
        Properties prop = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(Constants.SIMBS_HOME+"/login.prop");
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop.getProperty("user");
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

    private String[] args;

    /* The manage instance */
    private Manager manager = null;
}
