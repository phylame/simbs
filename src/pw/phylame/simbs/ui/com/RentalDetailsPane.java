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
import pw.phylame.simbs.Constants;
import pw.phylame.simbs.Worker;
import pw.phylame.simbs.ui.dialog.BookDetailsDialog;
import pw.phylame.simbs.ui.dialog.CustomerDetailsDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Created by Peng Wan on 2015-1-12.
 */
public class RentalDetailsPane extends PaneRender {
    private JPanel rootPane;
    private JFormattedTextField tfNO;
    private JTextField tfCustomerName;
    private JButton btnViewCustomer;
    private JTextField tfISBN;
    private JButton btnViewBook;
    private JTextField tfDateTime;
    private JFormattedTextField tfNumber;
    private JFormattedTextField tfPeriod;
    private JFormattedTextField tfPrice;
    private JFormattedTextField tfDeposit;
    private JTextField tfComment;

    private int customerID = -1;

    public RentalDetailsPane(int no) {
        super();
        btnViewBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String isbn = tfISBN.getText().trim();
                if (! "".equals(isbn)) {
                    BookDetailsDialog.viewBook(isbn);
                }
            }
        });
        btnViewCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (customerID > 0) {
                    CustomerDetailsDialog.viewCustomer(customerID);
                }
            }
        });
        setID(no);
    }

    private void reset() {
        tfNO.setText("");
        tfISBN.setText("");
        btnViewBook.setEnabled(false);
        tfCustomerName.setText("");
        btnViewCustomer.setEnabled(false);
        tfDateTime.setText("");
        tfNumber.setText("");
        tfPeriod.setText("");
        tfPrice.setText("");
        tfDeposit.setText("");
        tfComment.setText("");
    }

    public void setID(int no) {
        if (no <= 0) {
            reset();
        }
        String sql = "SELECT Bisbn, Cid, Rdate, Rtime, Rnumber, Rperiod, Rprice, Rdeposit, Rcomment" +
                " FROM rental WHERE Rid=?";
        try {
            PreparedStatement ps = Application.getInstance().getDbHelper().prepareStatement(sql);
            ps.setInt(1, no);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tfNO.setValue(no);
                tfISBN.setText(rs.getString(1));
                btnViewBook.setEnabled(true);
                customerID = rs.getInt(2);
                if (customerID != 0) {
                    tfCustomerName.setText(Worker.getInstance().getCustomerName(customerID));
                    btnViewCustomer.setEnabled(true);
                } else {
                    tfCustomerName.setText("");
                }
                SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
                tfDateTime.setText(sdf.format(Worker.toNormalDate(rs.getDate(3), rs.getTime(4))));
                tfNumber.setValue(rs.getInt(5));
                tfPeriod.setValue(rs.getInt(6));
                tfPrice.setValue(rs.getBigDecimal(7));
                tfDeposit.setValue(rs.getBigDecimal(8));
                tfComment.setText(Worker.normalizeString(rs.getString(9)));
            }
            rs.close();
            ps.close();
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
