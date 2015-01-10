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

import java.awt.Component;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Make dialogs.
 */
public final class DialogFactory {
    private static Component parent = null;

    public static void setDialogParent(Component parent) {
        DialogFactory.parent = parent;
    }

    private static HashMap<String, String> ImageFormatNames = new HashMap<>();
    static {
        String[] formats = new String[]{"jpg", "jpeg", "png", "gif", "bmp"};
        for (String fmt: formats) {
            String desc = Application.getInstance().getString("File.Format."+fmt.toUpperCase());
            ImageFormatNames.put(fmt, String.format("%s (*.%s)", desc, fmt));
        }
    }

    private static JFileChooser fileChooser = new JFileChooser();

    public static void initFileChooser(String title, List<FileFilter> filters, FileFilter initFilter,
                                       boolean acceptAll, String initDir) {
        fileChooser.setDialogTitle(title);
        fileChooser.setAcceptAllFileFilterUsed(acceptAll);
        /* remove all file filters */
        fileChooser.resetChoosableFileFilters();
        fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
        if (filters != null) {
            for (FileFilter filter : filters) {
                fileChooser.addChoosableFileFilter(filter);
            }
        }
        if (initFilter != null) {
            fileChooser.setFileFilter(initFilter);
        }
        if (initDir != null) {
            fileChooser.setCurrentDirectory(new File(initDir));
        }
    }

    public static List<FileFilter> makeFileFilters(Collection<String> formats,
                                                   Map<String, String> formatDesc,
                                                   boolean acceptAll) {
        List<FileFilter> filters = new java.util.ArrayList<>();
        if (acceptAll) {
            filters.add(new FileNameExtensionFilter(
                    String.format("%s (*.%s)",
                            Application.getInstance().getString("File.Format.AllFile"),
                            pw.phylame.tools.StringUtility.join(formats, " *.")),
                    formats.toArray(new String[0])));
        }
        for (String format: formats) {
            filters.add(new FileNameExtensionFilter(formatDesc.get(format), format));
        }
        return filters;
    }

    public static File selectOpenFile(Component parent, String title,
                                      List<FileFilter> filters,
                                      FileFilter initFilter, boolean acceptAll, String initDir) {
        initFileChooser(title, filters, initFilter, acceptAll, initDir);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fileChooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return fileChooser.getSelectedFile();
    }

    /**
     * Get the extension name of file.
     * @param name name of file
     * @return string of extension. If not contain extension return {@code ""}.
     */
    private static String getExtension(String name) {
        name = Objects.requireNonNull(name, "name");
        int index = name.lastIndexOf(".");
        if (index > 0) {
            return name.substring(index + 1);
        } else {
            return "";
        }

    }

    public static File selectSaveFile(Component parent, String title,
                                      List<FileFilter> filters,
                                      FileFilter initFilter, boolean acceptAll, String initDir) {
        initFileChooser(title, filters, initFilter, acceptAll, initDir);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fileChooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File file = fileChooser.getSelectedFile();
        /* Add file extension name if not given */
        FileNameExtensionFilter filter = (FileNameExtensionFilter) fileChooser.getFileFilter();
        if (filter.getExtensions().length == 1) {
            if ("".equals(getExtension(file.getPath()))) {
                file = new File(file.getPath() + "." + filter.getExtensions()[0]);
            }
        }
        return file;
    }

    public static File selectDirectory(Component parent, String title, String initDir) {
        initFileChooser(title, null, null, false, initDir);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return fileChooser.getSelectedFile();
    }

    public static File selectOpenImage(String dialogTitle, boolean acceptAll, String initDir) {
        return selectOpenFile(parent, dialogTitle,
                makeFileFilters(ImageFormatNames.keySet(), ImageFormatNames, acceptAll),
                null, acceptAll, initDir);
    }

    /**
     * Show text input of line edit dialog.
     * @param parent the parent component
     * @param title text of dialog title
     * @param tip tip text
     * @param initValue initialized value of editor
     * @return string value or {@code null} if user canceled operation.
     */
    public static String inputText(Component parent, String title, String tip, String initValue) {
        return (String) JOptionPane.showInputDialog(parent, tip, title,
                JOptionPane.PLAIN_MESSAGE, null, null, initValue);
    }

    public static String inputText(String title, String tip, String initValue) {
        return inputText(parent, title, tip, initValue);
    }

    /**
     * Show information message dialog.
     * @param parent the parent component
     * @param text the text content
     * @param title text of dialog title
     */
    public static void showInfo(Component parent, String text, String title) {
        JOptionPane.showMessageDialog(parent, text, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showInfo(String text, String title) {
        showInfo(parent, text, title);
    }

    /**
     * Show error message dialog.
     * @param parent the parent component
     * @param text the text content
     * @param title text of dialog title
     */
    public static void showError(Component parent, String text, String title) {
        JOptionPane.showMessageDialog(parent, text, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showError(String text, String title) {
        showError(parent, text, title);
    }

    /**
     * Show warning message dialog.
     * @param parent the parent component
     * @param text the text content
     * @param title text of dialog title
     */
    public static void showWarning(Component parent, String text, String title) {
        JOptionPane.showMessageDialog(parent, text, title, JOptionPane.WARNING_MESSAGE);
    }

    public static void showWarning(String text, String title) {
        showWarning(parent, text, title);
    }

    public static boolean showConfirm(Component parent, String text, String title) {
        int ret = JOptionPane.showConfirmDialog(parent, text, title, JOptionPane.YES_NO_OPTION);
        return ret == JOptionPane.YES_OPTION;
    }

    public static boolean showConfirm(String text, String title) {
        return showConfirm(parent, title, text);
    }
}
