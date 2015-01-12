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
import pw.phylame.simbs.ui.com.PaneRender;
import pw.phylame.simbs.ui.com.RentalDetailsPane;
import pw.phylame.simbs.ui.com.SaleDetailsPane;
import pw.phylame.simbs.ui.com.StockDetailsPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BillDetailsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonClose;
    private JPanel viewArea;

    public BillDetailsDialog() {
        super();
        init();
    }

    public BillDetailsDialog(java.awt.Frame owner, String title) {
        super(owner, title);
        init();
    }

    public BillDetailsDialog(java.awt.Dialog owner, String title) {
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

        viewArea.setLayout(new BorderLayout());

        pack();
        setLocationRelativeTo(getOwner());
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void updateInfo(int no) throws SQLException {
        String sql = "SELECT Levent, Lid FROM bill WHERE Lno=?";
        PreparedStatement ps = Application.getInstance().getDbHelper().prepareStatement(sql);
        ps.setInt(1, no);
        ResultSet rs = ps.executeQuery();
        int event = -1, id = -1;
        if (rs.next()) {
            event = rs.getInt(1);
            id = rs.getInt(2);
        }
        if (event < 0 || id < 0) {
            return;
        }
        PaneRender paneRender = null;
        switch (event) {
            case Worker.EVENT_STOCK:
                paneRender = new StockDetailsPane(id);
                break;
            case Worker.EVENT_SALE:
                paneRender = new SaleDetailsPane(id);
                break;
            case Worker.EVENT_RENTAL:
                paneRender = new RentalDetailsPane(id);
                break;
            case Worker.EVENT_RETURN:
                // TODO: Add rental details viewer
                break;
            default:
                break;
        }
        rs.close();
        viewArea.removeAll();
        if (paneRender != null) {
            paneRender.setParent(this);
            viewArea.add(paneRender.getPane(), BorderLayout.CENTER);
            pack();
            setLocationRelativeTo(getOwner());
        }
        viewArea.updateUI();
    }

    public void setRecordNO(int no) {
        try {
            updateInfo(no);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewBill(java.awt.Dialog owner, int no) {
        BillDetailsDialog dialog = new BillDetailsDialog(owner,
                Application.getInstance().getString("Dialog.BillDetails.Title"));
        dialog.setRecordNO(no);
        dialog.setVisible(true);
    }

    public static void viewBill(java.awt.Frame owner, int no) {
        BillDetailsDialog dialog = new BillDetailsDialog(owner,
                Application.getInstance().getString("Dialog.BillDetails.Title"));
        dialog.setRecordNO(no);
        dialog.setVisible(true);
    }
}
