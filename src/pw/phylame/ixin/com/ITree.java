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

package pw.phylame.ixin.com;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Tree with title label and scroll panel.
 * Created by Peng Wan on 2014/10/6.
 */
public class ITree extends JPanel {

    public ITree() {
        this("ITree");
    }
    
    public ITree(String title) {
        super(new BorderLayout());
        jTree = new JTree();
        initComp(title);
    }

    public ITree(String title, Object[] value) {
        super(new BorderLayout());
        jTree = new JTree(value);
        initComp(title);
    }

    public ITree(String title, Vector<?> value) {
        super(new BorderLayout());
        jTree = new JTree(value);
        initComp(title);
    }

    public ITree(String title, Hashtable<?,?> value) {
        super(new BorderLayout());
        jTree = new JTree(value);
        initComp(title);
    }

    public ITree(String title, TreeNode root) {
        super(new BorderLayout());
        jTree = new JTree(root);
        initComp(title);
    }

    public ITree(String title, TreeNode root, boolean asksAllowsChildren) {
        super(new BorderLayout());
        jTree = new JTree(root, asksAllowsChildren);
        initComp(title);
    }

    public ITree(String title, TreeModel newModel) {
        super(new BorderLayout());
        jTree = new JTree(newModel);
        initComp(title);
    }

    private void initComp(String title) {
        titleLabel = new JLabel(title);
        add(titleLabel, BorderLayout.NORTH);
        add(new JScrollPane(jTree), BorderLayout.CENTER);
    }

    public String getTitle() {
        return titleLabel.getText();
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public Icon getIcon() {
        return titleLabel.getIcon();
    }

    public void setIcon(Icon icon) {
        titleLabel.setIcon(icon);
    }

    /** Focus to tree. */
    @Override
    public void requestFocus() {
        jTree.requestFocus();
    }

    public JTree getTree() {
        return jTree;
    }

    // private data
    private JLabel titleLabel;
    private JTree jTree;
}
