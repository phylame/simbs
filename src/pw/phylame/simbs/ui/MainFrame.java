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

package pw.phylame.simbs.ui;

import pw.phylame.ixin.IToolkit;
import pw.phylame.ixin.frame.IFrame;
import pw.phylame.simbs.Application;
import pw.phylame.simbs.Constants;
import pw.phylame.simbs.Manager;
import pw.phylame.simbs.ui.com.NavigatePane;
import pw.phylame.simbs.ui.com.PaneRender;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main frame of SIMBS.
 */
public class MainFrame extends IFrame {
    static {
        IFrame.setActionsModel(UIDesign.MENU_ACTIONS);
        IFrame.setMenuBarModel(UIDesign.MENU_BAR_MODEL);
        IFrame.setToolBarModel(UIDesign.TOOL_BAR_MODEL);
    }

    public MainFrame(Manager manager) {
        super();
        this.manager = manager;

        createComponent();

        init();
    }

    private void createComponent() {

    }

    private void init() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                manager.onCommand(Constants.EXIT_APP);
            }
        });

        setIconImage(IToolkit.createImage(app.getString("App.Icon")));

        setSize(800, 450);
        setLocationRelativeTo(null);
    }

    public void setContentArea(PaneRender paneRender) {
        paneRender.setParent(this);
        JPanel contentArea = getContentArea();
        contentArea.removeAll();
        contentArea.add(paneRender.getPane());
        contentArea.updateUI();
    }

    @Override
    public String getString(String key) {
        return app.getString(key);
    }

    @Override
    public void onMenuAction(Object cmdId) {
        manager.onCommand(cmdId);
    }

    private Manager manager;

    private Application app = Application.getInstance();
}
