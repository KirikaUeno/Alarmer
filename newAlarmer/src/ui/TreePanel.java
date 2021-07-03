package ui;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;

public class TreePanel extends JPanel {
    public static JTree channelsTree = new JTree(new DefaultMutableTreeNode());
    public static String[] availableChannelsArray = {};

    public TreePanel(MainPanel mainPanel){
        setPreferredSize(new Dimension(200,600));
        setBackground(Color.DARK_GRAY);

        channelsTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("VEPP-2000")));
        updateChannelsTree();
        channelsTree.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    mainPanel.itemPanel.addFromTree();
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });

        JScrollPane scrollPane = new JScrollPane(channelsTree);

        SpringLayout layout = new SpringLayout();
        layout.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, this);
        setLayout(layout);
        add(scrollPane);
        repaint();
    }

    public static void updateChannelsTree() {
        TreeModel model = channelsTree.getModel();

        DefaultMutableTreeNode parent;
        DefaultMutableTreeNode parentCandidate;
        DefaultMutableTreeNode newChild;
        boolean isAdded;

        parent = (DefaultMutableTreeNode)model.getRoot();
        parent.setUserObject("");

        for (String path : availableChannelsArray) {
            parent = (DefaultMutableTreeNode)model.getRoot();
            for (String s:path.split("\\/")) {
                isAdded = false;
                for (Enumeration<TreeNode> e = parent.children(); e.hasMoreElements() ;) {
                    parentCandidate = ((DefaultMutableTreeNode)e.nextElement());
                    if (parentCandidate.getUserObject().equals(s)) {
                        parent = parentCandidate;
                        isAdded = true;
                        break;
                    }
                }
                if (!isAdded) {
                    newChild = new DefaultMutableTreeNode(s);
                    parent.add(newChild);
                    parent = newChild;
                }
            }
        }
        channelsTree.updateUI();
    }

    public static void updateAvailableChannelsList(String[] newChannels) {
        availableChannelsArray = newChannels;
        updateChannelsTree();
    }
}
