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
import pw.phylame.tools.sql.DbHelper;
import pw.phylame.ixin.IToolkit;
import pw.phylame.ixin.frame.IFrame;
import pw.phylame.simbs.ui.dialog.LoginDialog;
import pw.phylame.simbs.ui.dialog.DialogFactory;

import java.io.*;
import java.sql.SQLException;
import java.util.Map;
import java.util.Locale;
import java.util.Properties;

/**
 * The entry of application.
 */
public class Application {
    public static final String LOGIN_FILE = Constants.SIMBS_HOME+"/login.prop";
    public static final String SETTINGS_FILE = Constants.SIMBS_HOME+"/settings.prop";

    public Application(String[] args) {
        this.args = args;
        checkHome();
        loadSettings();
        initApp();
    }

    private void checkHome() {
        java.io.File homeDir = new java.io.File(Constants.SIMBS_HOME);
        if (! homeDir.exists()) {
            homeDir.mkdirs();
        }
        if (! homeDir.exists()) {
            throw new RuntimeException("Cannot create SIMBS home directory");
        }
    }

    /** Load settings */
    private void loadSettings() {
        Properties prop = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(SETTINGS_FILE);
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
        String s = prop.getProperty("theme");
        if (s == null || "".equals(s)) {
            s = "system";
        }
        settings.put("theme", s);
        Locale locale;
        s = prop.getProperty("language");
        if (s == null || "".equals(s)) {
            locale = Locale.getDefault();
        } else {
            locale = Locale.forLanguageTag(s);
        }
        settings.put("language", locale);
    }

    private void saveSettings() {
        Properties prop = new Properties();
        prop.put("theme", javax.swing.UIManager.getLookAndFeel().getClass().getCanonicalName());
        prop.put("language", Locale.getDefault().toLanguageTag());
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(SETTINGS_FILE);
            prop.store(out, "Application settings fro Simbs");
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

    private void initApp() {
        try {
            languageBundle = java.util.ResourceBundle.getBundle(Constants.I18N_PATH,
                    (Locale) settings.get("language"));
        } catch (java.util.MissingResourceException exp) {
            System.err.println(exp.getMessage());
            System.exit(0);
        }
        setTheme((String) settings.get("theme"));
    }

    /** Set SWING Look And Feel */
    private void setTheme(String theme) {
        theme = pw.phylame.ixin.IToolkit.getLookAndFeel(theme);
        try {
            javax.swing.UIManager.setLookAndFeel(theme);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                javax.swing.UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    /** Create unique Application instance */
    private static Application createApplication(String[] args) {
        instance = new Application(args);
        return instance;
    }

    /** Get the Simbs application */
    public static Application getInstance() {
        return instance;
    }

    /** Get system arguments */
    public String[] getSystemArguments() {
        return args;
    }

    /** Get settings */
    public Map<String, Object> getSettings() {
        return settings;
    }

    /** Get SQL Helper */
    public DbHelper getDbHelper() {
        return dbHelper;
    }

    /** Returns the {@code Manager} instance */
    public Manager getManager() {
        if (manager == null) {
            manager = new Manager(this);
        }
        return manager;
    }

    /** Get the main frame of application */
    public IFrame getFrame() {
        if (manager != null) {
            return manager.getFrame();
        } else {
            return null;
        }
    }

    /** Get translation string by its name */
    public String getString(String key) {
        return languageBundle.getString(key);
    }

    /** Execute a command */
    public void onCommand(Object cmdId) {
        if (manager != null) {
            manager.onCommand(cmdId);
        }
    }

    private boolean checkLogin(Profile profile) {
        if (profile == null) {
            return false;
        }
        try {
            dbHelper = new DbHelper(profile);
            saveProfile(profile);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            DialogFactory.showError(getString("Dialog.Login.Failed"), getString("Dialog.Login.Title"));
            return false;
        }
    }

    private void saveProfile(Profile profile) {
        Properties prop = new Properties();
        prop.put("user", profile.getUserName());
        prop.put("profileId", Integer.toString(profile.getId()));
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(LOGIN_FILE);
            prop.store(out, "Login configuration for Simbs");
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
        dialog.setIconImage(IToolkit.createImage(getString("App.Icon")));
        dialog.setTitle(getString("Dialog.Login.Title"));
        dialog.setVisible(true);
        Profile profile = dialog.getProfile();
        System.gc();
        return checkLogin(profile);
    }

    /** Run Application application */
    public void run() {
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

    /** Exit application */
    public void exit() {
        saveSettings();
        System.exit(0);
    }

    /**The application entry method */
    public static void main(String[] args) {
        createApplication(args).run();
    }

    // *************
    // ** User data
    // *************

    /** The unique instance of Application */
    private static Application instance = null;

    /** Language resource */
    private static java.util.ResourceBundle languageBundle = null;

    /** Console arguments */
    private String[] args;

    /** Settings */
    private Map<String, Object> settings = new java.util.HashMap<>();

    /** SQL Administrator for SIMBS */
    private DbHelper dbHelper = null;

    /** The manage instance */
    private Manager manager = null;
}
