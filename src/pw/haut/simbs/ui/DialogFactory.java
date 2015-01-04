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

package pw.haut.simbs.ui;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Make dialogs.
 */
public final class DialogFactory {
    private static Component parent = null;

    /**
     * Show text input of line edit dialog.
     * @param title text of dialog title
     * @param tip tip text
     * @param initValue initialized value of editor
     * @return string value or {@code null} if user canceled operation.
     */
    public static String inputText(String title, String tip, String initValue) {
        return (String) JOptionPane.showInputDialog(parent, tip, title,
                JOptionPane.PLAIN_MESSAGE, null, null, initValue);
    }

    /**
     * Show information message dialog.
     * @param text the text content
     * @param title text of dialog title
     */
    public static void showInfo(String text, String title) {
        JOptionPane.showMessageDialog(parent, text, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show error message dialog.
     * @param text the text content
     * @param title text of dialog title
     */
    public static void showError(String text, String title) {
        JOptionPane.showMessageDialog(parent, text, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show warning message dialog.
     * @param text the text content
     * @param title text of dialog title
     */
    public static void showWarning(String text, String title) {
        JOptionPane.showMessageDialog(parent, text, title, JOptionPane.WARNING_MESSAGE);
    }

}
