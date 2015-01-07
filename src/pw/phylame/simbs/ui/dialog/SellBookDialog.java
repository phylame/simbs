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
import pw.phylame.simbs.Worker;
import pw.phylame.simbs.ds.Customer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;

public class SellBookDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfISBN;
    private JButton buttonChooseBook;

    private JButton buttonChooseCustomer;
    private JSpinner jsNumber;
    private JLabel labelInventory;
    private JTextField tfCustomerName;

    private String oldISBN = null;
    private int customerID = -1;

    public SellBookDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        final Application app = Application.getInstance();

        tfISBN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateISBN();
            }
        });

        tfISBN.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateISBN();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateISBN();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        buttonChooseBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseBookDialog dialog = new ChooseBookDialog(app.getString("Dialog.ChooseBook.Title"));
                dialog.setVisible(true);
                String isbn = dialog.getISBN();
                if (isbn != null) {
                    setISBN(isbn);
                }
            }
        });

        buttonChooseCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseCustomerDialog dialog = new ChooseCustomerDialog(app.getString("Dialog.ChooseCustomer.Title"));
                int id = getCustomer();
                if (id > 0) {
                    dialog.setCustomerID(id);
                }
                dialog.setVisible(true);
                id = dialog.getCustomerID();
                if (id > 0) {
                    setCustomer(id);
                }
            }
        });

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

        jsNumber.setModel(new SpinnerNumberModel(1, 1, null, 1));

        setIconImage(app.getUI().getIconImage());

        pack();
        setLocationRelativeTo(null);

        updateISBN();
        checkSellCondition();
    }

    public SellBookDialog(String title) {
        this();
        setTitle(title);
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void checkSellCondition() {
        String s = tfCustomerName.getText().trim();
        if ("".equals(s)) {
            buttonOK.setEnabled(false);
            return;
        }
        if (! jsNumber.isEnabled()) {
            buttonOK.setEnabled(false);
            return;
        }
        buttonOK.setEnabled(true);
    }

    private void updateISBN() {
        Application app = Application.getInstance();
        Worker worker = Worker.getInstance();
        String s = tfISBN.getText().trim();
        if (s.equals(oldISBN)) {
            return;
        }
        oldISBN = s;
        int n = -1;
        if (! "".equals(s)) {
            n = worker.getInventory(s);
        }
        if (n < 0 ) {
            jsNumber.setValue(0);
            jsNumber.setEnabled(false);
            labelInventory.setText(String.format(app.getString("Dialog.Sell.LabelInventory"), 0));
            buttonOK.setEnabled(false);
            return;
        }
        jsNumber.setModel(new SpinnerNumberModel(1, 1, n, 1));
        jsNumber.setEnabled(true);
        labelInventory.setText(String.format(app.getString("Dialog.Sell.LabelInventory"), n));
        checkSellCondition();
    }

    public void setISBN(String isbn) {
        if (isbn == null) {
            isbn = "";
        }
        tfISBN.setText(isbn.trim());
        updateISBN();
    }

    public String getISBN() {
        return tfISBN.getText().trim();
    }

    public void setCustomer(int id) {
        this.customerID = id;
        if (id > 0) {
            Customer customer = Worker.getInstance().getCustomer(id);
            if (customer == null) {
                tfCustomerName.setText("");
                buttonOK.setEnabled(false);
            } else {
                tfCustomerName.setText(customer.getName());
                checkSellCondition();
            }
        }
    }

    public int getCustomer() {
        return customerID;
    }

    public int getSales() {
        return (int) jsNumber.getValue();
    }

    public static void main(String[] args) {
        SellBookDialog dialog = new SellBookDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
