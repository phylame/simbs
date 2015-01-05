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

import javax.swing.*;

/**
 * Label of {@code JMenu}.
 */
public class IMenuLabel {
    private String text;
    private Icon icon;
    private int mnemonic;

    public IMenuLabel(String text, Object icon, Object mnemonic) {
        this.text = text;
        this.icon = IToolkit.getIcon(icon);
        this.mnemonic = IToolkit.getMnemonic(mnemonic);
    }

    public String getText() {
        return text;
    }

    public Icon getIcon() {
        return icon;
    }

    public int getMnemonic() {
        return mnemonic;
    }
}
