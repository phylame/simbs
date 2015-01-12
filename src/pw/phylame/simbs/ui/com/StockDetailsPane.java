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

import pw.phylame.simbs.Application;
import pw.phylame.simbs.Worker;
import pw.phylame.simbs.ui.dialog.BookDetailsDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Created by user on 2015/1/12.
 */
public class StockDetailsPane extends PaneRender {
    private JPanel rootPane;

    private JFormattedTextField tfID;
    private JTextField tfDateTime;
    private JTextField tfISBN;
    private JButton btnViewBook;
    private JFormattedTextField tfPurchase;
    private JFormattedTextField tfNumber;
    private JFormattedTextField tfTotal;
    private JTextField tfComment;

    public StockDetailsPane(int id) {
        super();
        btnViewBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String isbn = tfISBN.getText().trim();
                if ("".equals(isbn)) {
                    return;
                }
                BookDetailsDialog.viewBook(isbn);
            }
        });
        setID(id);
    }

    private void reset() {
        tfID.setText("");
        tfDateTime.setText("");
        tfISBN.setText("");
        btnViewBook.setEnabled(false);
        tfPurchase.setText("");
        tfNumber.setText("");
        tfTotal.setText("");
        tfComment.setText("");
    }

    public void setID(int no) {
        if (no <= 0) {
            reset();
        }
        String sql = "SELECT Bisbn, Tdate, Ttime, Tnumber, Tpurchase, Ttotal, Tcomment FROM stock" +
                " WHERE Tid=?";
        try {
            PreparedStatement ps = Application.getInstance().getDbHelper().prepareStatement(sql);
            ps.setInt(1, no);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tfID.setValue(no);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-DD HH:MM:SS");
                tfDateTime.setText(sdf.format(Worker.toNormalDate(rs.getDate(2), rs.getTime(3))));
                tfISBN.setText(rs.getString(1));
                btnViewBook.setEnabled(true);
                tfPurchase.setValue(rs.getBigDecimal(5));
                tfNumber.setValue(rs.getInt(4));
                tfTotal.setValue(rs.getBigDecimal(6));
                tfComment.setText(Worker.normalizeString(rs.getString(7)));
            }
            rs.close();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public JPanel getPane() {
        return rootPane;
    }
}
