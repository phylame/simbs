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
import pw.phylame.simbs.Manager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nanu on 2015-1-6.
 */
public class NavigatePane implements PaneRender {
    private Component parentComp = null;

    private JPanel rootPane;
    private JButton btnViewBook;
    private JButton btnViewInventory;
    private JButton btnViewCustomer;

    public NavigatePane() {
        final Application app = Application.getInstance();
//
//        btnViewBook.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                app.onCommand(Constants.VIEW_BOOK);
//            }
//        });
//        btnViewInventory.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                app.onCommand(Constants.VIEW_INVENTORY);
//            }
//        });
//        btnViewCustomer.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                app.onCommand(Constants.VIEW_CUSTOMER);
//            }
//        });
    }

    @Override
    public void destroy() {

    }

    @Override
    public void setParent(Component parent) {
        parentComp = parent;
    }

    @Override
    public JPanel getPane() {
        return rootPane;
    }
}
