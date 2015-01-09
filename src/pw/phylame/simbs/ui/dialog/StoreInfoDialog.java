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
import java.awt.event.*;

public class StoreInfoDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField tfStock;
    private JTextField tfSold;
    private JTextField tfLent;
    private JTextField tfCnum;
    private JTextField tfReg;
    private JTextField tfStockMoney;
    private JTextField tfSaleMoney;
    private JTextField tfRentalMoney;
    private JTextField tfTotalMoney;

    public StoreInfoDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
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

        setIconImage(Application.getInstance().getFrame().getIconImage());

        pack();
        setLocationRelativeTo(null);

        updateInfo();
    }

    public StoreInfoDialog(String title) {
        this();
        setTitle(title);
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void updateInfo() {
        Worker worker = Worker.getInstance();

        tfReg.setText(Integer.toString(worker.getRegisteredBookCount()));
        tfStock.setText(Integer.toString(worker.getAllInventories()));
        tfSold.setText(Integer.toString(worker.getSoldBookCount()));
        tfLent.setText(Integer.toString(worker.getLentBookCount()));
        tfCnum.setText(Integer.toString(worker.getRegisteredCustomerCount()));

        tfStockMoney.setText(worker.getStockSpending().toString());
        tfSaleMoney.setText(worker.getSaleRevenue().toString());
        tfRentalMoney.setText(worker.getRentalRevenue().toString());
        tfTotalMoney.setText(worker.getTotalRevenue().toString());
    }
}
