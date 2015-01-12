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

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.math.BigDecimal;

public class StoreBookDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfISBN;
    private JButton buttonChooseBook;
    private JFormattedTextField tfPurchase;
    private JSpinner jsNumber;
    private JFormattedTextField tfTotal;
    private JTextField tfComment;

    private String oldISBN = null;

    private boolean isReady = false;

    public StoreBookDialog() {
        super();
        init();
    }

    public StoreBookDialog(java.awt.Dialog owner, String title) {
        super(owner, title);
        init();
    }

    public StoreBookDialog(java.awt.Frame owner, String title) {
        super(owner, title);
        init();
    }

    private void init() {
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
        tfPurchase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateTotal();
            }
        });
        tfPurchase.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                calculateTotal();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                calculateTotal();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        jsNumber.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                calculateTotal();
            }
        });

        final StoreBookDialog parent = this;

        buttonChooseBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String isbn = ChooseBookDialog.chooseBook(parent);
                System.gc();
                if (isbn != null) {
                    setBook(isbn);
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

        setIconImage(pw.phylame.ixin.IToolkit.createImage(app.getString("Dialog.Store.Icon")));

        pack();
        setLocationRelativeTo(getOwner());

        updateISBN();
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

        tfPurchase.setValue(new BigDecimal(0));
        tfPurchase.setEditable(false);

        jsNumber.setValue(1);
        jsNumber.setEnabled(false);

        tfTotal.setValue(new BigDecimal(0));
        tfTotal.setEditable(false);

        tfComment.setText("");
        tfComment.setEditable(false);

        buttonOK.setEnabled(false);

        oldISBN = isbn;
        if ("".equals(isbn)) {
            return;
        }
        if (! Worker.getInstance().isBookRegistered(isbn)) {    // not registered
            return;
        }
        tfPurchase.setEditable(true);
        jsNumber.setEnabled(true);
        tfTotal.setEditable(true);
        tfComment.setEditable(true);
        buttonOK.setEnabled(true);
        calculateTotal();
    }

    public void setBook(String isbn) {
        if (isbn == null) {
            isbn = "";
        }
        tfISBN.setText(isbn.trim());
    }

    public String getBook() {
        if (isReady) {
            return tfISBN.getText().trim();
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

    private void calculateTotal() {
        String s = tfISBN.getText().trim();
        if ("".equals(s)) {     // no ISBN
            return;
        }
        BigDecimal price = (BigDecimal) tfPurchase.getValue();
        Application app = Application.getInstance();
        if (price.compareTo(new BigDecimal(0)) < 0) {
            DialogFactory.showError(getOwner(), app.getString("Dialog.Store.InvalidPurchase"),
                    app.getString("Dialog.Store.Title"));
            return;
        }
        int number = (int) jsNumber.getValue();
        tfTotal.setValue(price.multiply(new BigDecimal(number)));
    }

    public BigDecimal getPurchasePrice() {
        if (isReady) {
            return (BigDecimal) tfPurchase.getValue();
        } else {
            return null;
        }
    }

    public BigDecimal getTotalPrice() {
        if (isReady) {
            return (BigDecimal) tfTotal.getValue();
        } else {
            return null;
        }
    }

    public void setComment(String comment) {
        if (comment == null) {
            tfComment.setText("");
        } else {
            tfComment.setText(comment.trim());
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
