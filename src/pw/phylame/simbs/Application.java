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

import pw.phylame.ixin.IToolkit;
import pw.phylame.ixin.frame.IFrame;
import pw.phylame.simbs.ui.dialog.DialogFactory;
import pw.phylame.simbs.ui.dialog.LoginDialog;
import pw.phylame.tools.sql.Profile;
import pw.phylame.tools.sql.SQLAdmin;

//import javax.swing.*;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * The entry of application.
 */
public class Application {

    public Application(String[] args) {
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

    /** Get SQL worker */
    public SQLAdmin getSQLAdmin() {
        return sqlAdmin;
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
            sqlAdmin = new SQLAdmin(profile);
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
        dialog.setIconImage(IToolkit.createImage(getString("App.Icon")));
        dialog.setTitle(getString("Dialog.Login.Title"));
        dialog.setVisible(true);

        Profile profile = dialog.getProfile();

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

    /**The application entry method */
    public static void main(String[] args) {
        // set look and feel
        String lookAndFeel = System.getenv("SIMBS_LOOK_AND_FEEL");
        if (lookAndFeel == null || "".equals(lookAndFeel)) {
            lookAndFeel = javax.swing.UIManager.getSystemLookAndFeelClassName();
        }
        try {
            javax.swing.UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                javax.swing.UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        createApplication(args).run();
    }

    // *************
    // ** User data
    // *************

    /** The unique instance of Application */
    private static Application instance = null;

    /** Language resource */
    private static ResourceBundle languageBundle = null;

    /** Console arguments */
    private String[] args;

    /** SQL Administrator for SIMBS */
    private SQLAdmin sqlAdmin = null;

    /** The manage instance */
    private Manager manager = null;
}
