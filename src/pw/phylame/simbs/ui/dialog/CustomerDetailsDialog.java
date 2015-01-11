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

import pw.phylame.simbs.Application;
import pw.phylame.simbs.ds.Customer;
import pw.phylame.simbs.ui.com.CustomerDetailsPane;

import javax.swing.*;
import java.awt.event.*;

public class CustomerDetailsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonClose;
    private CustomerDetailsPane detailsPane;

    public CustomerDetailsDialog() {
        super();
        init();
    }

    public CustomerDetailsDialog(java.awt.Dialog owner, String title) {
        super(owner, title);
        init();
    }

    public CustomerDetailsDialog(java.awt.Frame owner, String title) {
        super(owner, title);
        init();
    }

    private void init() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonClose);

        buttonClose.addActionListener(new ActionListener() {
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

        detailsPane.setParent(this);
        setIconImage(pw.phylame.ixin.IToolkit.createImage(Application.getInstance().getString("Customer.Icon")));

        pack();
        setLocationRelativeTo(getOwner());

        setSize((int) (getWidth()*1.4), getHeight());
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
        detailsPane.destroy();
    }

    public void setCustomer(int id) {
        detailsPane.setCustomer(id);
    }

    public void setCustomer(Customer customer) {
        detailsPane.setCustomer(customer);
    }
}
