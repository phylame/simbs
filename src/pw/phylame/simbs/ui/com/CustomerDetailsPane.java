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
import pw.phylame.simbs.ds.Customer;

import javax.swing.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * Pane for showing customer details.
 * Properties in this pane cannot be modified.
 */
public class CustomerDetailsPane extends PaneRender {
    private JFormattedTextField tfID;
    private JTextField tfName;
    private JTextField tfPhone;
    private JTextField tfEmail;
    private JFormattedTextField tfLevel;
    private JFormattedTextField tfLimit;
    private JFormattedTextField tfBoughtNumber;
    private JFormattedTextField tfBorrowedNumber;
    private JFormattedTextField tfOverdueNumber;
    private JFormattedTextField tfTotalSpending;
    private JPanel rootPane;
    private JTextField tfDate;

    public CustomerDetailsPane() {
        super();
        reset();
    }

    private void reset() {
        tfID.setValue(0);
        tfName.setText("");
        tfPhone.setText("");
        tfEmail.setText("");
        tfDate.setText("");
        tfLevel.setValue(0);
        tfLimit.setValue(0);
        tfBoughtNumber.setValue(new BigDecimal(0));
        tfBorrowedNumber.setValue(new BigDecimal(0));
        tfOverdueNumber.setValue(new BigDecimal(0));
        tfTotalSpending.setValue(new BigDecimal(0));
    }

    public void setCustomer(int id) {
        Customer customer = Worker.getInstance().getCustomer(id);
        setCustomer(customer);
    }

    public void setCustomer(Customer customer) {
        if (customer == null || customer.getId() <0 ) {
            reset();
            return;
        }
        tfID.setValue(customer.getId());
        tfName.setText(customer.getName());
        tfPhone.setText(customer.getPhone());
        tfEmail.setText(customer.getEmail());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        tfDate.setText(sdf.format(customer.getDate()));
        tfLevel.setValue(customer.getLevel());
        tfLimit.setValue(customer.getLimit());
        Worker worker = Worker.getInstance();
        int id = customer.getId(), v;
        v = worker.getBoughtNumber(id);
        if (v < 0) {
            v = 0;
        }
        tfBoughtNumber.setValue(v);
        v = worker.getBorrowedNumber(id);
        if (v < 0) {
            v = 0;
        }
        tfBorrowedNumber.setValue(v);
        v = worker.getOverdueNumber(id);
        if (v < 0) {
            v = 0;
        }
        tfOverdueNumber.setValue(v);
        BigDecimal n = worker.getTotalSpending(id);
        if (n == null) {
            n = new BigDecimal(0);
        }
        tfTotalSpending.setValue(n);
    }

    @Override
    public void destroy() {

    }

    @Override
    public JPanel getPane() {
        return rootPane;
    }
}
