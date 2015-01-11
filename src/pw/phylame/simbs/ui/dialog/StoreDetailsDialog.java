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
import java.math.BigDecimal;

public class StoreDetailsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JFormattedTextField tfStockMoney;
    private JFormattedTextField tfSaleMoney;
    private JFormattedTextField tfRentalMoney;
    private JFormattedTextField tfTotalMoney;
    private JFormattedTextField tfCnum;
    private JFormattedTextField tfReg;
    private JFormattedTextField tfStock;
    private JFormattedTextField tfSold;
    private JFormattedTextField tfLent;

    public StoreDetailsDialog() {
        super();
        init();
    }

    public StoreDetailsDialog(java.awt.Frame owner, String title) {
        super(owner, title);
        init();
    }

    public StoreDetailsDialog(java.awt.Dialog owner, String title) {
        super(owner, title);
        init();
    }

    private void init() {
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

        setIconImage(pw.phylame.ixin.IToolkit.createImage(
                Application.getInstance().getString("Dialog.Info.Icon")));

        pack();
        setSize((int) (getWidth()*1.3), getHeight());
        setLocationRelativeTo(getOwner());

        updateInfo();
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

        tfReg.setValue(worker.getRegisteredBookCount());
        tfStock.setValue(worker.getAllInventories());
        tfSold.setValue(worker.getSoldBookCount());
        tfLent.setValue(worker.getRentalNumber());
        tfCnum.setValue(worker.getRegisteredCustomerCount());

        BigDecimal stockTotal = worker.getStockSpending(), saleTotal, rentalTotal;
        if (stockTotal == null) {
            stockTotal = new BigDecimal(0);
        }
        tfStockMoney.setValue(stockTotal);
        saleTotal = worker.getSaleRevenue();
        if (saleTotal == null) {
            saleTotal = new BigDecimal(0);
        }
        tfSaleMoney.setValue(saleTotal);
        rentalTotal = worker.getRentalRevenue();
        if (rentalTotal == null) {
            rentalTotal = new BigDecimal(0);
        }
        tfRentalMoney.setValue(rentalTotal);
        tfTotalMoney.setValue(saleTotal.add(rentalTotal).subtract(stockTotal));
    }
}
