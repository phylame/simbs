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

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;

public class LendBookDialog extends JDialog {
    public static final int DEFAULT_DAYS = 30;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfISBN;
    private JTextField tfCustomerName;
    private JSpinner jsNumber;
    private JSpinner jsPeriod;
    private JButton buttonChooseBook;
    private JButton buttonChooseCustomer;
    private JLabel labelInventory;
    private JLabel labelMaxPeriod;
    private JFormattedTextField tfDeposit;
    private JTextField tfComment;
    private JFormattedTextField tfPrice;
    private JLabel labelTip;

    private String oldISBN = null;
    private int customerID = -1;
    private BigDecimal bookPrice = null;

    private boolean isReady = false;

    public LendBookDialog() {
        super();
        init();
    }

    public LendBookDialog(Frame owner, String title) {
        super(owner, title);
        init();
    }

    public LendBookDialog(Dialog owner, String title) {
        super(owner, title);
    }

    private void init() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        final Application app = Application.getInstance();

        final LendBookDialog dialog = this;
        labelTip.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String tip = String.format(app.getString("Dialog.Lend.RentalPolicy"),
                        Constants.DEFAULT_LENT_LIMIT, Constants.PRICE_OF_INCREASE_LIMIT,
                        DEFAULT_DAYS, Constants.MAX_LENT_DAYS);
                JOptionPane.showMessageDialog(dialog, tip,
                        app.getString("Dialog.Lend.RentalPolicy.Title"), JOptionPane.PLAIN_MESSAGE);
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

        jsNumber.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                calculateDeposit();
            }
        });

        jsPeriod.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                calculateDeposit();
            }
        });

        final LendBookDialog parent = this;

        buttonChooseBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseBookDialog dialog = new ChooseBookDialog(parent,
                        app.getString("Dialog.ChooseBook.Title"));
                dialog.setVisible(true);
                String isbn = dialog.getISBN();
                System.gc();
                if (isbn != null) {
                    setBook(isbn);
                }
            }
        });

        buttonChooseCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseCustomerDialog dialog = new ChooseCustomerDialog(parent,
                        app.getString("Dialog.ChooseCustomer.Title"));
                int id = getCustomer();
                if (id > 0) {
                    dialog.setCustomerID(id);
                }
                dialog.setVisible(true);
                id = dialog.getCustomerID();
                System.gc();
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

        setIconImage(pw.phylame.ixin.IToolkit.createImage(app.getString("Dialog.Lend.Icon")));

        pack();
        setLocationRelativeTo(getOwner());

        updateLimitAndPeriod();
    }

    private void onOK() {
        isReady = true;
        dispose();
    }

    private void onCancel() {
        customerID = -1;
        isReady = false;
        dispose();
    }

    private void updateISBN() {
        String s = tfISBN.getText().trim();
        if (s.equals(oldISBN)) {    // not changed
            return;
        }
        oldISBN = s;
        updateLimitAndPeriod();
    }

    private void updatePrice() {
        String s = tfISBN.getText().trim();
        // rental price
        BigDecimal price = null;
        if (! "".equals(s)) {
            price = Worker.getInstance().getRentalPrice(s);
        }
        if (price != null) {    // has this book
            tfPrice.setEditable(true);
            tfPrice.setValue(price);
        } else {
            tfPrice.setEditable(false);
            tfPrice.setValue(new BigDecimal("0.00"));
        }
    }

    private void updateLimitAndPeriod() {
        Worker worker = Worker.getInstance();

        int limit = -1;
        if (customerID > 0) {
            limit = worker.geCustomerLimit(customerID);
        }
        int inventory = -1;
        String s = tfISBN.getText().trim();
        if (! "".equals(s)) {
            inventory = worker.getInventory(s);
        }

        // set default value
        updatePrice();

        setNumberInfo(0, 0, 0);
        jsNumber.setEnabled(false);
        setPeriodInfo(0, 0, 0);
        jsPeriod.setEnabled(false);
        tfDeposit.setValue(new BigDecimal("0.00"));
        tfDeposit.setEditable(false);
        tfComment.setText("");
        tfComment.setEditable(false);
        buttonOK.setEnabled(false);

        if (limit <= 0 || inventory <= 0) {
            return;
        }

        int maxNumber = Math.min(limit, inventory);
        setNumberInfo(1, 1, maxNumber);
        setPeriodInfo(DEFAULT_DAYS, 1, Constants.MAX_LENT_DAYS);
        updatePrice();
        jsNumber.setEnabled(true);
        jsPeriod.setEnabled(true);
        tfDeposit.setEditable(true);
        tfComment.setEditable(true);
        buttonOK.setEnabled(true);
        calculateDeposit();
    }

    private void setNumberInfo(int number, int begin, int maxNumber) {
        jsNumber.setModel(new SpinnerNumberModel(number, begin, maxNumber, 1));
        labelInventory.setText(String.format(
                Application.getInstance().getString("Dialog.Lend.LabelMaxNumber"), maxNumber));
    }

    private void setPeriodInfo(int period, int begin, int maxPeriod) {
        jsPeriod.setModel(new SpinnerNumberModel(period, begin, maxPeriod, 1));
        labelMaxPeriod.setText(String.format(
                Application.getInstance().getString("Dialog.Lend.LabelMaxPeriod"), maxPeriod));
    }

    private void calculateDeposit() {
        String s = tfISBN.getText().trim();
        if ("".equals(s)) {     // not ISBN
            return;
        }
        int number = (int) jsNumber.getValue();
        Worker worker = Worker.getInstance();
        if (bookPrice == null) {
            bookPrice = worker.getPrice(s);;
        }
        if (bookPrice != null) {
            tfDeposit.setValue(bookPrice.multiply(new BigDecimal(number)));
        }
    }

    public String getBook() {
        return tfISBN.getText().trim();
    }

    public void setBook(String isbn) {
        if (isbn == null) {
            isbn = "";
        }
        tfISBN.setText(isbn.trim());
        bookPrice = null;
    }

    public int getCustomer() {
        return customerID;
    }

    public void setCustomer(int id) {
        this.customerID = id;
        if (id > 0) {
            String name = Worker.getInstance().getCustomerName(id);
            if (name == null) {     // invalid ID
                tfCustomerName.setText("");
                buttonOK.setEnabled(false);
            } else {
                tfCustomerName.setText(name);
                updateLimitAndPeriod();
            }
        }
    }

    public BigDecimal getPrice() {
        if (isReady) {
            return (BigDecimal) tfPrice.getValue();
        } else {
            return null;
        }
    }

    public int getNumber() {
        if (isReady) {
            return (int) jsNumber.getValue();
        } else {
            return -1;
        }
    }

    public int getPeriod() {
        if (isReady) {
            return (int) jsPeriod.getValue();
        } else {
            return -1;
        }
    }

    public BigDecimal getDeposit() {
        if (isReady) {
            return (BigDecimal) tfDeposit.getValue();
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
