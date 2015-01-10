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

import javax.swing.*;

import pw.phylame.simbs.Worker;
import pw.phylame.simbs.Application;
import pw.phylame.tools.StringUtility;
import pw.phylame.simbs.ui.dialog.DialogFactory;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Peng Wan on 2015-1-10.
 */
public class BookConditionPane extends PaneRender {

    private JTextField tfISBN;
    private JTextField tfName;
    private JTextField tfAuthor;
    private JTextField tfCategory;
    private JTextField tfPublisher;
    private JSpinner jsDateBegin;
    private JSpinner jsDateEnd;
    private JFormattedTextField tfPriceBegin;
    private JFormattedTextField tfPriceEnd;
    private JCheckBox cbDate;
    private JCheckBox cbPrice;
    private JFormattedTextField tfInventoryBegin;
    private JFormattedTextField tfInventoryEnd;
    private JCheckBox cbInventory;
    private JButton buttonSearch;
    private JPanel rootPane;

    private String queryCondition = null;

    public BookConditionPane() {
        super();

        jsDateBegin.setModel(new SpinnerDateModel());
        jsDateEnd.setModel(new SpinnerDateModel());
        tfPriceBegin.setValue(0.0F);
        tfPriceEnd.setValue(100.0F);

        tfInventoryBegin.setValue(1);
        tfInventoryEnd.setValue(100);
    }

    public JButton getSearchButton() {
        return buttonSearch;
    }

    private void onSearch() {
        queryCondition = null;

        Application app = Application.getInstance();

        ArrayList<String> conditions = new ArrayList<>();

        String s = tfISBN.getText().trim();
        if (! "".equals(s)) {
            conditions.add("Bisbn LIKE '%"+s+"%'");
        }
        s = tfName.getText().trim();
        if (! "".equals(s)) {
            conditions.add("Bname LIKE '%" + s + "%'");
        }
        s = tfAuthor.getText().trim();
        if (! "".equals(s)) {
            conditions.add("Bauthors LIKE '%" + s + "%'");
        }
        if (cbDate.isSelected()) {
            java.util.Date begin = (java.util.Date) jsDateBegin.getValue(),
                    end = (java.util.Date) jsDateEnd.getValue();
            if (begin.compareTo(end) > 0) {
                DialogFactory.showError(getParent(), app.getString("Dialog.ChooseBook.InvalidDate"),
                        app.getString("Dialog.ChooseBook.Title"));
                return;
            }
            conditions.add(String.format("Bdate BETWEEN '%s' AND '%s'", Worker.toSQLDate(begin),
                    Worker.toSQLDate(end)));
        }
        s = tfCategory.getText().trim();
        if (! "".equals(s)) {
            conditions.add("Bcategory LIKE '%" + s + "%'");
        }
        s = tfPublisher.getText().trim();
        if (! "".equals(s)) {
            conditions.add("Bpublisher LIKE '%" + s + "%'");
        }
        if (cbPrice.isSelected()) {
            float begin = (float) tfPriceBegin.getValue(), end = (float) tfPriceEnd.getValue();
            if (end < begin) {
                DialogFactory.showError(getParent(), app.getString("Dialog.ChooseBook.InvalidPrice"),
                        app.getString("Dialog.ChooseBook.Title"));
                return;
            }
            conditions.add(String.format("Bprice BETWEEN %.2f AND %.2f", begin, end));
        }
        if (cbInventory.isSelected()) {
            int begin = (int) tfInventoryBegin.getValue(), end = (int) tfInventoryEnd.getValue();
            if (end < begin) {
                DialogFactory.showError(getParent(), app.getString("Dialog.ChooseBook.InvalidInventory"),
                        app.getString("Dialog.ChooseBook.Title"));
                return;
            }
            conditions.add(String.format("book.Bisbn=inventory.Bisbn AND Inumber BETWEEN %d AND %d",
                    begin, end));
        }

        queryCondition = StringUtility.join(conditions, " AND ");
    }

    public String getQueryCondition() {
        onSearch();
        return queryCondition;
    }

    @Override
    public void destroy() {

    }

    @Override
    public JPanel getPane() {
        return rootPane;
    }
}
