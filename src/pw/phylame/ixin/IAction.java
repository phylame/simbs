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

package pw.phylame.ixin;

import pw.phylame.ixin.event.IActionEvent;
import pw.phylame.ixin.event.IActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * IAction with ID.
 * Created by Peng Wan on 14-11-1.
 */
public abstract class IAction extends AbstractAction {
    /** Key for action identifier */
    public static final String ID = "ActionCommandID";

    /** Key for tool tip text */
    public static final String TIP_TEXT = "ActionTipText";

    // id, name, icon, mnemonic, accelerator, toolTip(optional), enable(optional)
    public static IAction createAction(Object[] itemModel, final IActionListener actionListener)
            throws InvalidActionModelException {
        if (itemModel.length < 5) {
            throw new InvalidActionModelException("Require {name, icon(Icon or URL), mnemonic, " +
                    "accelerator(KeyStroke or String), command, toolTip(optional), enable(optional)}");
        }
        Object id = itemModel[0];
        if (id == null) {
            throw new InvalidActionModelException("Action id is null");
        }
        String text = (String) itemModel[1];
        Icon icon = IToolkit.getIcon(itemModel[2]);
        int mnemonic = 0;
        if (itemModel[3] != null) {
            mnemonic = IToolkit.getMnemonic(itemModel[3]);
        }
        KeyStroke keyStroke = null;
        if (itemModel[4] != null) {
            keyStroke = IToolkit.getKeyStroke(itemModel[4]);
        }
        String toolTip = null;
        if (itemModel.length > 5) {
            toolTip = (String) itemModel[5];
        }
        boolean enable = true;
        if (itemModel.length > 6) {
            enable = (boolean) itemModel[6];
        }
        final IAction action = new IAction(text, icon, id, mnemonic, keyStroke, toolTip) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (actionListener != null) {
                    IActionEvent event = new IActionEvent(e, this);
                    actionListener.actionPerformed(event);
                }
            }
        };
        action.setEnabled(enable);
        return action;
    }

    public IAction(String text, Icon icon, Object id, int mnemonic, KeyStroke accelerator, String toolTip) {
        super(text, icon);
        setId(id);
        setMnemonic(mnemonic);
        setShortcut(accelerator);
        setToolTip(toolTip);
        setTipText(toolTip);
    }

    public String getText() {
        return (String) getValue(NAME);
    }

    public void setText(String text) {
        putValue(NAME, text);
    }

    public Icon getIcon() {
        return (Icon) getValue(SMALL_ICON);
    }

    public void setIcon(Icon icon) {
        putValue(SMALL_ICON, icon);
    }

    public Object getId() {
        return getValue(ID);
    }

    public void setId(Object id) {
        putValue(ID, id);
    }

    public int getMnemonic() {
        return (Integer) getValue(MNEMONIC_KEY);
    }

    public void setMnemonic(int mnemonic) {
        putValue(MNEMONIC_KEY, mnemonic < 0 ? 0 : mnemonic);
    }

    public KeyStroke getShortcut() {
        return (KeyStroke) getValue(ACCELERATOR_KEY);
    }

    public void setShortcut(KeyStroke shortcut) {
        putValue(ACCELERATOR_KEY, shortcut);
    }

    public String getToolTip() {
        return (String) getValue(SHORT_DESCRIPTION);
    }

    public void setToolTip(String toolTip) {
        putValue(SHORT_DESCRIPTION, toolTip);
    }

    public String getTipText() {
        return (String) getValue(TIP_TEXT);
    }

    public void setTipText(String text) {
        putValue(TIP_TEXT, text);
    }

    @Override
    public String toString() {
        return String.format("IAction: id=%s text=%s", getId(), getText());
    }
}
