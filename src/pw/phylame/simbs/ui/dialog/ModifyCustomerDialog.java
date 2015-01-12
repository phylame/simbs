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

import pw.phylame.ixin.IToolkit;
import pw.phylame.simbs.Application;
import pw.phylame.simbs.Constants;
import pw.phylame.simbs.ds.Customer;

import javax.swing.*;
import java.awt.event.*;

public class ModifyCustomerDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfName;
    private JTextField tfPhone;
    private JTextField tfEmail;
    private JSpinner jsLimit;
    private JSpinner jsLevel;
    private JTextArea taComment;
    private JFormattedTextField tfLentLimit;

    private Customer customer = null;

    private boolean isReady = false;

    public ModifyCustomerDialog() {
        super();
        init();
    }

    public ModifyCustomerDialog(java.awt.Dialog owner, String title) {
        super(owner, title);
        init();
    }

    public ModifyCustomerDialog(java.awt.Frame owner, String title) {
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

        Application app = Application.getInstance();
        setIconImage(IToolkit.createImage(app.getString("Customer.Icon")));

        taComment.setLineWrap(true);
        taComment.setWrapStyleWord(true);
        taComment.setRows(5);

        pack();
        setSize((int)(getWidth()*1.4), (int)(getHeight()*1.2));
        setLocationRelativeTo(getOwner());

        jsLevel.setModel(new SpinnerNumberModel(0, 0, null, 1));
        jsLimit.setModel(new SpinnerNumberModel(Constants.DEFAULT_LENT_LIMIT, 1,
                Constants.DEFAULT_LENT_LIMIT*3, 1));
    }

    private void onOK() {
        Application app = Application.getInstance();
        String s = tfName.getText().trim();
        if ("".equals(s)) {
            DialogFactory.showError(this, app.getString("Dialog.ModifyCustomer.NoName"), getTitle());
            return;
        }
        if (customer == null) {
            customer = new Customer();
        }
        customer.setName(s);
        customer.setPhone(tfPhone.getText().trim());
        customer.setEmail(tfEmail.getText().trim());
        customer.setLevel((int) jsLevel.getValue());
        customer.setLimit((int) jsLimit.getValue());
        customer.setComment(taComment.getText().trim());
        isReady = true;
        dispose();
    }

    private void onCancel() {
        isReady = false;
        dispose();
    }

    public Customer getCustomer() {
        return isReady ? customer : null;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            tfName.setText(customer.getName());
            tfPhone.setText(customer.getPhone());
            tfEmail.setText(customer.getEmail());
            jsLevel.setValue(customer.getLevel());
            jsLimit.setValue(customer.getLimit());
            taComment.setText(customer.getComment());
        } else {
            tfName.setText("");
            tfPhone.setText("");
            tfEmail.setText("");
            jsLevel.setValue(0);
            jsLimit.setValue(Constants.DEFAULT_LENT_LIMIT);
            taComment.setText("");
        }
    }

    public static Customer modifyCustomer(java.awt.Dialog owner, Customer customer) {
        ModifyCustomerDialog dialog = new ModifyCustomerDialog(owner,
                Application.getInstance().getString("Dialog.ModifyCustomer.Title"));
        dialog.setCustomer(customer);
        dialog.setVisible(true);
        return dialog.getCustomer();
    }

    public static Customer modifyCustomer(java.awt.Frame owner, Customer customer) {
        ModifyCustomerDialog dialog = new ModifyCustomerDialog(owner,
                Application.getInstance().getString("Dialog.ModifyCustomer.Title"));
        dialog.setCustomer(customer);
        dialog.setVisible(true);
        return dialog.getCustomer();
    }
}
