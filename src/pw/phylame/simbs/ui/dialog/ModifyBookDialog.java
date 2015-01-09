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
import pw.phylame.simbs.ds.Book;

import javax.swing.*;
import java.awt.event.*;

public class ModifyBookDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfISBN;
    private JTextField tfName;
    private JTextField tfVersion;
    private JTextField tfAuthors;
    private JTextField tfCategory;
    private JTextField tfPublisher;
    private JSpinner jsDate;
    private JTextArea taIntro;
    private JFormattedTextField tfPrice;
    private JTextField tfCover;
    private JButton btnChooseCover;

    private Book book = null;

    private boolean isReady = false;

    public ModifyBookDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        btnChooseCover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onChooseCover();
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

        jsDate.setModel(new SpinnerDateModel());
        tfPrice.setValue(0.0F);
        taIntro.setRows(5);

        Application app = Application.getInstance();
        setIconImage(IToolkit.createImage(app.getString("Book.Icon")));

        pack();

        setSize(500, getHeight()+20);

        setLocationRelativeTo(null);
    }

    public ModifyBookDialog(String title) {
        this();
        setTitle(title);
    }

    private void onChooseCover() {
        // TODO: add choose cover
    }

    private void onOK() {
// add your code here
        Application app = Application.getInstance();
        if (book == null) {
            book = new Book();
        }
        String s = tfISBN.getText().trim();
        if ("".equals(s)) {
            DialogFactory.showError(this, app.getString("Dialog.ModifyBook.NoISBN"), getTitle());
            return;
        }
        book.setISBN(s);
        s = tfName.getText().trim();
        if ("".equals(s)) {
            DialogFactory.showError(this, app.getString("Dialog.ModifyBook.NoName"), getTitle());
            return;
        }
        book.setName(s);
        book.setVersion(tfVersion.getText().trim());
        book.setCover(tfCover.getText().trim());
        s = tfAuthors.getText().trim();
        if ("".equals(s)) {
            DialogFactory.showError(this, app.getString("Dialog.ModifyBook.NoAuthors"), getTitle());
            return;
        }
        book.setAuthors(s);
        book.setDate((java.util.Date) jsDate.getValue());
        book.setCategory(tfCategory.getText().trim());
        s = tfPublisher.getText().trim();
        if ("".equals(s)) {
            DialogFactory.showError(this, app.getString("Dialog.ModifyBook.NoPublisher"), getTitle());
            return;
        }
        book.setPublisher(s);
        // TODO
//        book.setPrice((float)tfPrice.getValue());
        book.setIntro(taIntro.getText().trim());
        isReady = true;
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        isReady = false;
        dispose();
    }

    public Book getBook() {
        return isReady ? book : null;
    }

    public void setBook(Book book, boolean isISBNEditable) {
        this.book = book;
        if (book != null) {
            tfISBN.setText(book.getISBN());
            tfName.setText(book.getName());
            tfVersion.setText(book.getVersion());
            tfCover.setText(book.getCover());
            tfAuthors.setText(book.getAuthors());
            jsDate.setValue(book.getDate());
            tfCategory.setText(book.getCategory());
            tfPublisher.setText(book.getPublisher());
            tfPrice.setValue(book.getPrice().doubleValue());
            taIntro.setText(book.getIntro());
        } else {
            tfISBN.setText("");
            tfName.setText("");
            tfVersion.setText("");
            tfCover.setText("");
            tfAuthors.setText("");
            jsDate.setValue(new java.util.Date());
            tfCategory.setText("");
            tfPublisher.setText("");
            tfPrice.setValue(0.0D);
            taIntro.setText("");
        }
        tfISBN.setEditable(isISBNEditable);
    }
}
