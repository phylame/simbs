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
import pw.phylame.simbs.ds.Book;
import pw.phylame.simbs.ui.com.BookDetailsPane;

import javax.swing.*;
import java.awt.event.*;

public class BookDetailsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonClose;
    private BookDetailsPane detailsPane;

    public BookDetailsDialog() {
        super();
        setTitle(Application.getInstance().getString("Dialog.BookDetails.Title"));
        init();
    }

    public BookDetailsDialog(java.awt.Dialog owner, String title) {
        super(owner, title);
        init();
    }

    public BookDetailsDialog(java.awt.Frame owner, String title) {
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
        setIconImage(pw.phylame.ixin.IToolkit.createImage(Application.getInstance().getString("Book.Icon")));

        pack();
        setLocationRelativeTo(getOwner());

        setSize((int) (getWidth()*1.4), getHeight());
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
        detailsPane.destroy();
    }

    public void setBook(String isbn) {
        detailsPane.setBook(isbn);
    }

    public void setBook(pw.phylame.simbs.ds.Book book) {
        detailsPane.setBook(book);
    }

    public static void viewBook(java.awt.Dialog owner, String isbn) {
        BookDetailsDialog dialog = new BookDetailsDialog(owner,
                Application.getInstance().getString("Dialog.BookDetails.Title"));
        dialog.setBook(isbn);
        dialog.setVisible(true);
    }

    public static void viewBook(String isbn) {
        BookDetailsDialog dialog = new BookDetailsDialog();
        dialog.setBook(isbn);
        dialog.setVisible(true);
    }

    public static void viewBook(java.awt.Frame owner, String isbn) {
        BookDetailsDialog dialog = new BookDetailsDialog(owner,
                Application.getInstance().getString("Dialog.BookDetails.Title"));
        dialog.setBook(isbn);
        dialog.setVisible(true);
    }

    public static void viewBook(Book book) {
        BookDetailsDialog dialog = new BookDetailsDialog();
        dialog.setBook(book);
        dialog.setVisible(true);
    }
}
