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

package pw.phylame.ixin.frame;

import pw.phylame.ixin.IAction;
import pw.phylame.ixin.IToolkit;
import pw.phylame.ixin.event.IActionEvent;
import pw.phylame.ixin.event.IActionListener;
import pw.phylame.ixin.event.IToolTipEvent;
import pw.phylame.ixin.event.IToolTipListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple frame model.
 */
public abstract class IFrame extends JFrame implements IToolTipListener {

    public static Object[][][] getActionsModel() {
        return actionsModel;
    }

    public static void setActionsModel(Object[][][] actionsModel) {
        IFrame.actionsModel = actionsModel;
    }

    public static Object[][] getMenuBarModel() {
        return menuBarModel;
    }

    public static void setMenuBarModel(Object[][] menuBarModel) {
        IFrame.menuBarModel = menuBarModel;
    }

    public static Object[] getToolBarModel() {
        return toolBarModel;
    }

    public static void setToolBarModel(Object[] toolBarModel) {
        IFrame.toolBarModel = toolBarModel;
    }

    public IFrame() {
        super();
        init();
    }

    public IFrame(String title) {
        super(title);
        init();
    }

    private void init() {
        Container topPane = getContentPane();

        createMenu();

        createToolBar();
        topPane.add(toolBar, BorderLayout.NORTH);

        contentArea = new JPanel(new BorderLayout());
        topPane.add(contentArea, BorderLayout.CENTER);

        createStatusBar();
        topPane.add(statusLabel, BorderLayout.SOUTH);
    }

    private void createMenuActions() {
        IActionListener actionListener = new IActionListener() {
            @Override
            public void actionPerformed(IActionEvent e) {
                onMenuAction(e.getAction().getId());
            }
        };
        if (actionsModel == null) {
            return;
        }
        for (Object[][] model: actionsModel) {
            menuActions.putAll(IToolkit.createActions(model, actionListener));
        }
        toolBarActions.putAll(menuActions);
    }

    private void createMenuBar() {
        if (getJMenuBar() != null) {    // already created
            return;
        }
        if (menuBarModel == null) {
            return;
        }
        JMenuBar menuBar = new JMenuBar();
        for (Object[] menuModel: menuBarModel) {
            JMenu menu = new JMenu();
            IToolkit.addMenuItem(menu, menuModel, menuActions, this);
            menuBar.add(menu);
        }
        setJMenuBar(menuBar);
    }

    private void createMenu() {
        createMenuActions();
        createMenuBar();
    }

    public Map<Object, IAction> getMenuActions() {
        return menuActions;
    }

    public void setMenuActions(Map<Object, IAction> menuActions) {
        this.menuActions = menuActions;
    }

    public IAction getMenuAction(Object id) {
        return menuActions.get(id);
    }

    public Map<Object, IAction> getToolBarActions() {
        return toolBarActions;
    }

    public void setToolBarActions(Map<Object, IAction> toolBarActions) {
        this.toolBarActions = toolBarActions;
    }

    public IAction getToolBarAction(Object id) {
        return toolBarActions.get(id);
    }

    private void createToolBar() {
        toolBar = new JToolBar();
        if (toolBarModel != null) {
            IToolkit.addButton(toolBar, Arrays.asList(toolBarModel), toolBarActions, this);
        }
        toolBar.setRollover(true);

        /* lock toolbar menu */
        toolBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!e.isMetaDown()) {
                    return;
                }
                JPopupMenu popupMenu = new JPopupMenu();
                JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(getString("Frame.Toolbar.Lock"));

                final boolean locked = ! toolBar.isFloatable();

                menuItem.setState(locked);
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        toolBar.setFloatable(locked);

                    }
                });
                popupMenu.add(menuItem);
                popupMenu.show(toolBar, e.getX(), e.getY());
            }
        });
    }

    private void createStatusBar() {
        statusLabel = new JLabel();
        statusBar = new JPanel(new BorderLayout());
        statusBar.add(statusLabel, BorderLayout.CENTER);
    }

    @Override
    public void showingTip(IToolTipEvent e) {
        statusLabel.setText(" "+e.getToolTip());
    }

    @Override
    public void closingTip(IToolTipEvent e) {
        statusLabel.setText(" "+oldStatusText);
    }

    public abstract String getString(String key);

    public abstract void onMenuAction(Object cmdId);

    public JToolBar getToolBar() {
        return toolBar;
    }

    public JPanel getContentArea() {
        return contentArea;
    }

    public JPanel getStatusBar() {
        return statusBar;
    }

    public String getStatusText() {
        return statusLabel.getText();
    }

    public void setStatusText(String text) {
        oldStatusText = text;
        statusLabel.setText(" "+text);
    }


    // *****************
    // ** user data
    // *****************

    private static Object[][][] actionsModel = null;
    private static Object[][] menuBarModel = null;

    private static Object[] toolBarModel = null;


    private JToolBar toolBar = null;
    private JPanel contentArea = null;
    private JPanel statusBar = null;

    private JLabel statusLabel = null;
    private String oldStatusText = null;

    /** Menu actions */
    private Map<Object, IAction> menuActions = new HashMap<>();

    /** Toolbar actions */
    private Map<Object, IAction> toolBarActions = new HashMap<>();

}
