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

package pw.phylame.simbs.ui.dialog;

import pw.phylame.simbs.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ImageViewer extends JDialog {
    private JPanel contentPane;
    private JLabel labelImage;
    private JLabel labelInfo;

    public ImageViewer() {
        super();
        init();
    }

    public ImageViewer(java.awt.Dialog owner, String title) {
        super(owner, title);
        init();
    }

    public ImageViewer(java.awt.Frame owner, String title) {
        super(owner, title);
        init();
    }

    private void init() {
        setContentPane(contentPane);
        setModal(true);

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setIconImage(Application.getInstance().getFrame().getIconImage());

        pack();
        setSize(400, 500);
        setLocationRelativeTo(getOwner());

        setPixel(-1, -1);
    }

    private void setPixel(int w, int h) {
        labelInfo.setText(String.format(Application.getInstance().getString(
                "Dialog.ImageViewer.LabelInfo"), w, h));
    }

    private void resetImage(Icon icon) {
        if (icon == null) {
            labelImage.setText(Application.getInstance().getString("Dialog.ImageViewer.InvalidImage"));
            labelImage.setIcon(null);
            setPixel(-1, -1);
        } else {
            labelImage.setText(null);
            labelImage.setIcon(icon);
            setPixel(icon.getIconWidth(), icon.getIconHeight());
        }
    }

    public void setImage(String path) {
        if (path == null) {
            resetImage(null);
        } else {
            ImageIcon image = new ImageIcon(path);
            resetImage(image);
        }
    }

    public static void viewImage(String title, String path) {
        ImageViewer dialog = new ImageViewer();
        dialog.setTitle(title);
        dialog.setImage(path);
        dialog.setVisible(true);
    }

    public static void viewImage(java.awt.Dialog owner, String title, String path) {
        ImageViewer dialog = new ImageViewer(owner, title);
        dialog.setImage(path);
        dialog.setVisible(true);
    }

    public static void viewImage(java.awt.Frame owner, String title, String path) {
        ImageViewer dialog = new ImageViewer(owner, title);
        dialog.setImage(path);
        dialog.setVisible(true);
    }
}
