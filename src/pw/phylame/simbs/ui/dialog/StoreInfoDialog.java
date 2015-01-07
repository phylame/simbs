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

        pack();
        setLocationRelativeTo(null);
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public void setInfo(int regNumber, int stockNumber, int soldNumber, int lentNumber, int customerNumber) {
        tfReg.setText(Integer.toString(regNumber));
        tfStock.setText(Integer.toString(stockNumber));
        tfSold.setText(Integer.toString(soldNumber));
        tfLent.setText(Integer.toString(lentNumber));
        tfCnum.setText(Integer.toString(customerNumber));
    }

    public static void main(String[] args) {
        StoreInfoDialog dialog = new StoreInfoDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
