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
import java.sql.SQLException;
import java.util.Date;

public class StartPromotionDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JComboBox<String> cbObject;
    private JFormattedTextField tfValue;
    private JSpinner jsDateBegin;
    private JSpinner jsDateEnd;
    private JTextField tfComment;


    public StartPromotionDialog() {
        super();
        init();
    }

    public StartPromotionDialog(java.awt.Dialog owner, String title) {
        super(owner, title);
        init();
    }

    public StartPromotionDialog(java.awt.Frame owner, String title) {
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

        Application app = Application.getInstance();
        setIconImage(pw.phylame.ixin.IToolkit.createImage(app.getString("Dialog.Promotion.Icon")));

        cbObject.addItem(app.getString("Dialog.Promotion.ItemSale"));
        cbObject.addItem(app.getString("Dialog.Promotion.ItemRental"));
        tfValue.setValue(new BigDecimal(10));
        jsDateBegin.setModel(new SpinnerDateModel());
        jsDateEnd.setModel(new SpinnerDateModel());

        pack();
        setLocationRelativeTo(getOwner());

    }

    private boolean addPromotion(int obj) {
        Application app = Application.getInstance();
        BigDecimal value = (BigDecimal) tfValue.getValue();
        if (value.compareTo(new BigDecimal(0)) <= 0 || value.compareTo(new BigDecimal(10)) > 0) {
            DialogFactory.showError(getOwner(), app.getString("Dialog.Promotion.InvalidValue"),
                    app.getString("Dialog.Promotion.Title"));
            return false;
        }
        Date begin = (Date) jsDateBegin.getValue(), end = (Date) jsDateEnd.getValue();
        int days = pw.phylame.tools.DateUtility.calculateInterval(begin, end, "d");
        if ( days < 0) {
            DialogFactory.showError(getOwner(), app.getString("Dialog.Promotion.InvalidDate"),
                    app.getString("Dialog.Promotion.Title"));
            return false;
        } else if (days == 0) {
            DialogFactory.showError(getOwner(), app.getString("Dialog.Promotion.ZeroDay"),
                    app.getString("Dialog.Promotion.Title"));
            return false;
        }
        try {
            Worker.getInstance().addPromotion(obj, value.divide(new BigDecimal(10)), begin, end,
                    tfComment.getText().trim());
        } catch (SQLException e) {
            e.printStackTrace();
            DialogFactory.showError(getOwner(), app.getString("Dialog.Promotion.SaveError"),
                    app.getString("Dialog.Promotion.Title"));
        }
        return true;
    }

    private void onOK() {
// add your code here
        if (! addPromotion(cbObject.getSelectedIndex()+1)) {
            return;
        }
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }
}
