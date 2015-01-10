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

package pw.phylame.simbs.ui.dialog;

import pw.phylame.simbs.Constants;
import pw.phylame.tools.sql.Profile;

import javax.swing.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

public class LoginDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfUserName;
    private JPasswordField tfPassword;
    private JComboBox<String> jcbSource;

    private Profile profile = null;

    private boolean isReady = false;

    public LoginDialog() {
        super();
        init();
    }

    public LoginDialog(java.awt.Dialog owner, String title) {
        super(owner, title);
        init();
    }

    public LoginDialog(java.awt.Frame owner, String title) {
        super(owner, title);
        init();
    }

    private void init() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        jcbSource.removeAllItems();
        for (Profile profile: Constants.getDatabaseProfiles()) {
            jcbSource.addItem(profile.getLabel());
        }

        loadConfig();

        pack();
        setLocationRelativeTo(null);

        if (! "".equals(tfUserName.getText().trim())) {
            tfPassword.requestFocus();
        }
    }

    private void loadConfig() {
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

        String user = prop.getProperty("user");
        if (user != null && ! "".equals(user)) {
            tfUserName.setText(user.trim());
        }
        try {
            jcbSource.setSelectedIndex(Integer.parseInt(prop.getProperty("profileId")));
        } catch (NumberFormatException exp) {
            exp.printStackTrace();
        }
    }

    private void onOK() {
        ResourceBundle rb = ResourceBundle.getBundle(Constants.I18N_PATH);
        if ("".equals(tfUserName.getText())) {
            DialogFactory.showWarning(this, rb.getString("Dialog.Login.NoUsername"), rb.getString("Dialog.Login.Title"));
            return;
        }

        profile = Constants.getDatabaseProfile(jcbSource.getSelectedIndex());
        profile.setUserName(tfUserName.getText().trim());
        profile.setPassword(new String(tfPassword.getPassword()));

        isReady = true;
        dispose();
    }

    private void onCancel() {
        isReady = false;
        dispose();
    }

    public Profile getProfile() {
        if (isReady) {
            return profile;
        } else {
            return null;
        }
    }
}
