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

package pw.phylame.simbs.ui.com;

import pw.phylame.simbs.Worker;
import pw.phylame.simbs.ds.Book;
import pw.phylame.simbs.ui.dialog.ImageViewer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * Pane for showing book details.
 * Properties in this pane cannot be modified.
 */
public class BookDetailsPane extends PaneRender {
    private JPanel rootPane;
    private JTextField tfISBN;
    private JTextField tfVersion;
    private JTextField tfName;
    private JTextField tfAuthor;
    private JTextField tfCover;
    private JButton btnSaveCover;
    private JTextField tfDate;
    private JTextField tfCategory;
    private JTextField tfPublisher;
    private JFormattedTextField tfPurchasePrice;
    private JFormattedTextField tfSalePrice;
    private JFormattedTextField tfRentalPrice;
    private JFormattedTextField tfInventoryNumber;
    private JFormattedTextField tfSaleNumber;
    private JFormattedTextField tfRentalNumber;
    private JTextArea taIntro;
    private JButton btnViewCover;
    private JFormattedTextField tfMarkedPrice;

    public BookDetailsPane() {
        super();
        btnViewCover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onViewCover();
            }
        });
        btnSaveCover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveCover();
            }
        });

        taIntro.setRows(5);
        taIntro.setLineWrap(true);
        taIntro.setWrapStyleWord(true);

        reset();
    }

    private void onViewCover() {
        String path = tfCover.getText().trim();
        if ("".equals(path)) {
            return;
        }
        ImageViewer.viewImage(tfName.getText().trim(), path);
        System.gc();
    }

    private void onSaveCover() {

    }

    private void reset() {
        tfISBN.setText("");
        tfName.setText("");
        tfVersion.setText("");
        tfAuthor.setText("");
        tfCover.setText("");
        btnSaveCover.setEnabled(false);
        tfDate.setText("");
        tfCategory.setText("");
        tfPublisher.setText("");
        tfInventoryNumber.setValue(0);
        tfSaleNumber.setValue(0);
        tfRentalNumber.setValue(0);
        tfPurchasePrice.setValue(new BigDecimal(0));
        tfMarkedPrice.setValue(new BigDecimal(0));
        tfSalePrice.setValue(new BigDecimal(0));
        tfRentalPrice.setValue(new BigDecimal(0));
        taIntro.setText("");
    }

    public void setBook(String isbn) {
        Book book = Worker.getInstance().getBook(isbn);
        setBook(book);
    }

    public void setBook(Book book) {
        if (book == null || "".equals(book.getISBN())) {
            reset();
            return;
        }
        tfISBN.setText(book.getISBN());
        tfName.setText(book.getName());
        tfVersion.setText(book.getVersion());
        tfAuthor.setText(book.getAuthors());
        tfCover.setText(book.getCover());
        if (book.getCover() != null && ! "".equals(book.getCover())) {
            btnViewCover.setEnabled(true);
            btnSaveCover.setEnabled(true);
        } else {
            btnViewCover.setEnabled(false);
            btnSaveCover.setEnabled(false);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        tfDate.setText(sdf.format(book.getDate()));
        tfCategory.setText(book.getCategory());
        tfPublisher.setText(book.getPublisher());
        Worker worker = Worker.getInstance();
        String isbn = book.getISBN();
        int v = worker.getInventory(isbn);
        if (v < 0) {
            v = 0;
        }
        tfInventoryNumber.setValue(v);
        v = worker.getSaleCount(isbn);
        if (v < 0) {
            v = 0;
        }
        tfSaleNumber.setValue(v);
        v = worker.getRentalNumber(isbn);
        if (v < 0) {
            v = 0;
        }
        tfRentalNumber.setValue(v);
        BigDecimal n = worker.getPurchasePrice(isbn);
        if (n == null) {
            n = new BigDecimal(0);
        }
        tfPurchasePrice.setValue(n);
        n = worker.getMarkedPrice(isbn);
        if (n == null) {
            n = new BigDecimal(0);
        }
        tfMarkedPrice.setValue(n);
        n = worker.getSalePrice(isbn);
        if (n == null) {
            n = new BigDecimal(0);
        }
        tfSalePrice.setValue(n);
        n = worker.getRentalPrice(isbn);
        if (n == null) {
            n = new BigDecimal(0);
        }
        tfRentalPrice.setValue(n);
        taIntro.setText(book.getIntro());
    }

    @Override
    public void destroy() {

    }

    @Override
    public JPanel getPane() {
        return rootPane;
    }
}
