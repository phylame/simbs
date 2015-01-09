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
import pw.phylame.simbs.Constants;
import pw.phylame.simbs.Worker;
import pw.phylame.simbs.ds.Customer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.math.BigDecimal;

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
    private JFormattedTextField tfComment;
    private JFormattedTextField tfTotal;
    private JLabel labelTip;

    private String oldISBN = null;
    private int customerID = -1;
    private BigDecimal bookPrice = null;

    private boolean isReady = false;

    public SellBookDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        final Application app = Application.getInstance();
        final SellBookDialog dialog = this;

        labelTip.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String tip = String.format(app.getString("Dialog.Sell.SalePolicy"),
                        Constants.PRICE_OF_INCREASE_LEVEL, Constants.PRICE_OF_INCREASE_LIMIT);
                JOptionPane.showMessageDialog(dialog, tip,
                        app.getString("Dialog.Sell.SalePolicy.Title"), JOptionPane.PLAIN_MESSAGE);

            }
        });

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
            public void changedUpdate(DocumentEvent e) {}
        });

        buttonChooseBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseBookDialog dialog = new ChooseBookDialog(app.getString("Dialog.ChooseBook.Title"));
                String isbn = tfISBN.getText().trim();
                if (! "".equals(isbn)) {
                    dialog.setISBN(isbn);
                }
                dialog.setVisible(true);
                isbn = dialog.getISBN();
                if (isbn != null) {
                    setISBN(isbn);
                }
            }
        });

        buttonChooseCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseCustomerDialog dialog = new ChooseCustomerDialog(app.getString("Dialog.ChooseCustomer.Title"));
                if (customerID > 0) {
                    dialog.setCustomerID(customerID);
                }
                dialog.setVisible(true);
                int id = dialog.getCustomerID();
                if (id > 0) {
                    setCustomer(id);
                }
            }
        });

        jsNumber.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                calculateTotal();
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

        setIconImage(app.getFrame().getIconImage());

        pack();
        setLocationRelativeTo(null);

        updateNumber();
    }

    public SellBookDialog(String title) {
        this();
        setTitle(title);
    }

    private void onOK() {
        isReady = true;
        dispose();
    }

    private void onCancel() {
        isReady = false;
        dispose();
    }

    private void updateISBN() {
        String isbn = tfISBN.getText().trim();
        if (isbn.equals(oldISBN)) {     // not changed
            return;
        }
        oldISBN = isbn;
        updateNumber();
    }

    private void updateNumber() {

        setNumberInfo(0, 0, 0);
        jsNumber.setEnabled(false);
        tfTotal.setValue(new BigDecimal("0.00"));
        tfTotal.setEditable(false);
        tfComment.setText("");
        tfComment.setEditable(false);
        buttonOK.setEnabled(false);

        int inventory = -1;
        String s = tfISBN.getText().trim();
        if (! "".equals(s)) {
            inventory = Worker.getInstance().getInventory(s);
        }
        if (inventory <= 0) {    // no inventory
            return;
        }
        setNumberInfo(1, 1, inventory);
        jsNumber.setEnabled(true);
        tfTotal.setEditable(true);
        tfComment.setEditable(true);
        if (customerID > 0) {
            buttonOK.setEnabled(true);
        }
        calculateTotal();
    }

    private void calculateTotal() {
        String s = tfISBN.getText().trim();
        if ("".equals(s)) {     // no ISBN
            return;
        }
        int number = (int) jsNumber.getValue();
        if (bookPrice == null) {
            bookPrice = Worker.getInstance().getSalePrice(s);    // get price
        }
        if (bookPrice != null) {
            tfTotal.setValue(bookPrice.multiply(new BigDecimal(number)));
        }
    }

    private void setNumberInfo(int number, int begin, int maxNumber) {
        jsNumber.setModel(new SpinnerNumberModel(number, begin, maxNumber, 1));
        labelInventory.setText(String.format(
                Application.getInstance().getString("Dialog.Sell.LabelInventory"), maxNumber));
    }


    public void setISBN(String isbn) {
        if (isbn == null) {
            isbn = "";
        }
        tfISBN.setText(isbn.trim());
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
                updateNumber();
            }
        }
    }

    public int getCustomer() {
        if (isReady) {
            return customerID;
        } else {
            return -1;
        }
    }

    public int getNumber() {
        if (isReady) {
            return (int) jsNumber.getValue();
        } else {
            return -1;
        }
    }

    public BigDecimal getTotalPrice() {
        if (isReady) {
            return (BigDecimal) tfTotal.getValue();
        } else {
            return null;
        }
    }

    public String getComment() {
        if (isReady) {
            return tfComment.getText().trim();
        } else {
            return null;
        }
    }
}
