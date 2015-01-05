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

import javax.swing.*;
import java.awt.event.*;
import java.util.ResourceBundle;

public class LoginDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfUN;
    private JPasswordField tfPW;

    private boolean isOk = false;

    public LoginDialog() {
        super();
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

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setResizable(false);
        setLocation(400, 200);
        pack();
    }

    private void onOK() {
// add your code here
        ResourceBundle rb = ResourceBundle.getBundle(Constants.I18N_PATH);
        if ("".equals(tfUN.getText())) {
            DialogFactory.showWarning(rb.getString("Login.NoUsername"), rb.getString("Login.Title"));
            return;
        }
        isOk = true;
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        isOk = false;
        dispose();
    }

    public String getUserName() {
        if (isOk) {
            return tfUN.getText();
        } else {
            return null;
        }
    }

    public void setUserName(String user) {
        tfUN.setText(user);
        if (user != null && ! "".equals(user)) {
            tfPW.requestFocus();
        }
    }

    public String getPassword() {
        if (isOk) {
            return new String(tfPW.getPassword());
        } else {
            return null;
        }
    }
}
