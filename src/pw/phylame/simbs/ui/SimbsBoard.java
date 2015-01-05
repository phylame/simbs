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

import pw.phylame.ixin.frame.SimpleFrame;
import pw.phylame.simbs.Constants;
import pw.phylame.simbs.Manager;
import pw.phylame.simbs.SimbsApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main frame of SIMBS.
 */
public class SimbsBoard extends SimpleFrame {
    static {
        SimpleFrame.setActionsModel(UIDesign.MENU_ACTIONS);
        SimpleFrame.setMenuBarModel(UIDesign.MENU_BAR_MODEL);
        SimpleFrame.setToolBarModel(UIDesign.TOOL_BAR_MODEL);
    }

    public SimbsBoard(Manager manager) {
        super();
        this.manager = manager;

        createComponent();

        init();
    }

    private void createComponent() {
        Container topPane = getContentPane();
    }

    private void init() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                manager.onCommand(Constants.FILE_EXIT);
            }
        });

        setSize(600, 337);
        setLocationRelativeTo(null);
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

    private SimbsApplication app = SimbsApplication.getInstance();
}
