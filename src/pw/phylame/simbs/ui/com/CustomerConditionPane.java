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
import pw.phylame.simbs.ui.dialog.DialogFactory;
import pw.phylame.tools.StringUtility;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Peng Wan on 2015-1-10.
 */
public class CustomerConditionPane extends PaneRender {
    private JPanel rootPane;
    private JTextField tfName;
    private JTextField tfPhone;
    private JTextField tfEmail;
    private JFormattedTextField tfLevelBegin;
    private JFormattedTextField tfLevelEnd;
    private JCheckBox cbLevel;
    private JCheckBox cbLimit;
    private JFormattedTextField tfLimitBegin;
    private JFormattedTextField tfLimitEnd;
    private JButton buttonSearch;
    private JCheckBox cbDate;
    private JSpinner jsDateBegin;
    private JSpinner jsDateEnd;

    private String queryCondition = null;

    public CustomerConditionPane() {
        super();

        tfLevelBegin.setValue(0);
        tfLevelEnd.setValue(100);
        tfLimitBegin.setValue(0);
        tfLimitEnd.setValue(100);

        jsDateBegin.setModel(new SpinnerDateModel());
        jsDateEnd.setModel(new SpinnerDateModel());
    }

    public JButton getSearchButton() {
        return buttonSearch;
    }

    private void onSearch() {
        Application app = Application.getInstance();

        ArrayList<String> conditions = new ArrayList<>();

        String s = tfName.getText().trim();
        if (! "".equals(s)) {
            conditions.add("Cname LIKE '%"+s+"%'");
        }
        s = tfPhone.getText().trim();
        if (! "".equals(s)) {
            conditions.add("Cphone LIKE '%" + s + "%'");
        }
        s = tfEmail.getText().trim();
        if (! "".equals(s)) {
            conditions.add("Cemail LIKE '%" + s + "%'");
        }
        if (cbLevel.isSelected()) {
            int begin = (int) tfLevelBegin.getValue(), end = (int) tfLevelEnd.getValue();
            if (begin > end) {
                DialogFactory.showError(getParent(),
                        app.getString("Dialog.ChooseCustomer.InvalidLevel"),
                        app.getString("Dialog.ChooseCustomer.Title"));
                return;
            }
            conditions.add(String.format("Clevel BETWEEN %d AND %d", begin, end));
        }
        if (cbLimit.isSelected()) {
            int begin = (int) tfLimitBegin.getValue(), end = (int) tfLimitEnd.getValue();
            if (begin > end) {
                DialogFactory.showError(getParent(),
                        app.getString("Dialog.ChooseCustomer.InvalidLimit"),
                        app.getString("Dialog.ChooseCustomer.Title"));
                return;
            }
            conditions.add(String.format("Clent_limit BETWEEN %d AND %d", begin, end));
        }
        if (cbDate.isSelected()) {
            java.util.Date begin = (java.util.Date) jsDateBegin.getValue(),
                    end = (java.util.Date) jsDateEnd.getValue();
            if (begin.compareTo(end) > 0) {
                DialogFactory.showError(getParent(),
                        app.getString("Dialog.ChooseCustomer.InvalidDate"),
                        app.getString("Dialog.ChooseCustomer.Title"));
                return;
            }
            conditions.add(String.format("Cdate BETWEEN '%s' AND '%s'", Worker.toSQLDate(begin),
                    Worker.toSQLDate(end)));
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
