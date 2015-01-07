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
import pw.phylame.simbs.ds.Book;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;

public class StoreBookDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfISBN;
    private JButton buttonChooseBook;
    private JSpinner jsNumber;

    private String oldISBN = null;

    private boolean isReady = false;

    public StoreBookDialog() {
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

        setISBN(null);
    }

    public StoreBookDialog(String title) {
        this();
        setTitle(title);
    }

    private void onOK() {
// add your code here
        Application app = Application.getInstance();
        if ("".equals(tfISBN.getText().trim())) {
            DialogFactory.showError(this, app.getString("Dialog.Store.NoISBN"), app.getString("Dialog.Store.Title"));
            return;
        }
        isReady = true;
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        isReady = false;
        dispose();
    }

    private void updateISBN() {
        String isbn = tfISBN.getText().trim();
        if (isbn.equals(oldISBN)) {
            return;
        }
        oldISBN = isbn;
        if ("".equals(isbn)) {
            buttonOK.setEnabled(false);
            return;
        }
        Book book = Worker.getInstance().getBook(isbn);
        if (book == null) {
            buttonOK.setEnabled(false);
            return;
        }
        buttonOK.setEnabled(true);
    }

    public void setISBN(String isbn) {
        if (isbn == null) {
            isbn = "";
        }
        tfISBN.setText(isbn.trim());
        updateISBN();
    }

    public String getISBN() {
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

    public static void main(String[] args) {
        StoreBookDialog dialog = new StoreBookDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
