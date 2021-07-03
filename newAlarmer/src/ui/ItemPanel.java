package ui;

import company.Config;
import data.AlarmUnit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class ItemPanel extends JPanel {
    private final MainPanel mainPanel;
    private JTable watchTable;

    public static List<AlarmUnit> alarmUnitList = new LinkedList<>();

    public ItemPanel(MainPanel mainPanel){
        setPreferredSize(new Dimension(Config.boardWidth, Config.boardHeight));
        setFocusable(true);
        setName("itemPanel");
        this.mainPanel = mainPanel;


        initializeVariables();
    }

    private void initializeVariables(){
        Button showTree = new Button("showTree");
        showTree.addActionListener(e->this.mainPanel.showTree());
        Button showLog = new Button("showLog");
        showLog.addActionListener(e->this.mainPanel.showLog());

        Button saveListButton = new Button("save");
        Button loadListButton = new Button("load");
        Button selectNoneButton = new Button("none");
        Button selectAllButton = new Button("all");
        Button addFromTreeButton = new Button("add");
        Button removeFromListButton = new Button("remove");

        saveListButton.addActionListener(e->saveDefaultList());
        loadListButton.addActionListener(e->loadDefaultList());
        selectNoneButton.addActionListener(e->selectAll(new Boolean[]{false,true}));
        selectAllButton.addActionListener(e->selectAll(new Boolean[]{true,true}));
        addFromTreeButton.addActionListener(e->addFromTree());
        removeFromListButton.addActionListener(e->removeFromList());

        watchTable = new JTable();
        DefaultTableModel dtm = new AlarmUnitsTableModel(alarmUnitList);
        watchTable.setModel(dtm);
        dtm.addTableModelListener(watchTable);
        watchTable.setDefaultRenderer(Object.class, new AlarmUnitsTableRenderer());
        watchTable.setDefaultRenderer(Boolean[].class,new AlarmUnitCheckBoxRenderer());
        watchTable.setDefaultEditor(Boolean[].class,new CheckBoxEditor(new JCheckBox(),new CheckBoxPanel()));
        watchTable.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        watchTable.clearSelection();
                    }
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

        JScrollPane scrollPane = new JScrollPane(watchTable);

        SpringLayout layoutMain = new SpringLayout();
        layoutMain.putConstraint(SpringLayout.SOUTH, showTree, -5, SpringLayout.SOUTH, this);
        layoutMain.putConstraint(SpringLayout.WEST, showTree, 5, SpringLayout.WEST, this);
        layoutMain.putConstraint(SpringLayout.SOUTH, showLog, -5, SpringLayout.SOUTH, this);
        layoutMain.putConstraint(SpringLayout.WEST, showLog, 5, SpringLayout.EAST, showTree);
        layoutMain.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.NORTH, this);
        layoutMain.putConstraint(SpringLayout.SOUTH, scrollPane, -60, SpringLayout.SOUTH, this);
        layoutMain.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, this);
        layoutMain.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, this);
        layoutMain.putConstraint(SpringLayout.SOUTH, loadListButton, -5, SpringLayout.SOUTH, this);
        layoutMain.putConstraint(SpringLayout.EAST, loadListButton, -5, SpringLayout.EAST, this);
        layoutMain.putConstraint(SpringLayout.SOUTH, saveListButton, -5, SpringLayout.NORTH, loadListButton);
        layoutMain.putConstraint(SpringLayout.EAST, saveListButton, -5, SpringLayout.EAST, this);
        layoutMain.putConstraint(SpringLayout.SOUTH, selectNoneButton, 0, SpringLayout.SOUTH, removeFromListButton);
        layoutMain.putConstraint(SpringLayout.WEST, selectNoneButton, 5, SpringLayout.EAST, selectAllButton);
        layoutMain.putConstraint(SpringLayout.SOUTH, selectAllButton, 0, SpringLayout.SOUTH, removeFromListButton);
        layoutMain.putConstraint(SpringLayout.WEST, selectAllButton, 5, SpringLayout.EAST, removeFromListButton);
        layoutMain.putConstraint(SpringLayout.SOUTH, addFromTreeButton, -5, SpringLayout.SOUTH, this);
        layoutMain.putConstraint(SpringLayout.WEST, addFromTreeButton, 5, SpringLayout.EAST, selectAllButton);
        layoutMain.putConstraint(SpringLayout.SOUTH, removeFromListButton, -5, SpringLayout.NORTH, showTree);
        layoutMain.putConstraint(SpringLayout.WEST, removeFromListButton, 5, SpringLayout.WEST, this);
        setLayout(layoutMain);

        add(scrollPane);
        add(showLog);
        add(showTree);
        add(saveListButton);
        add(loadListButton);
        add(selectNoneButton);
        add(selectAllButton);
        //add(addFromTreeButton);
        add(removeFromListButton);
    }

    private void selectAll(Boolean[] isOn) {
        for (AlarmUnit au : alarmUnitList) {
            au.setOn(isOn);
        }
    }

    public void addFromTree() {
        TreePath[] selection = TreePanel.channelsTree.getSelectionPaths();
        assert selection != null;
        for (TreePath tp : selection) {
            if (!(TreePanel.channelsTree.getModel().isLeaf(tp.getLastPathComponent()))) {
                continue;
            }
            StringBuilder fullName = new StringBuilder();
            for (Object o : tp.getPath()) {
                if (fullName.length() > 0) {
                    fullName.append("/");
                }
                fullName.append(o.toString());
            }
            alarmUnitList.add(new AlarmUnit(fullName.toString(), true, true,2));
        }

        MainPanel.casCommunicator.subscribeToSelected();
        ((DefaultTableModel) watchTable.getModel()).fireTableDataChanged();
    }

    private void removeFromList() {
        java.util.List<AlarmUnit> tmpList = new LinkedList<>();
        List<AlarmUnit> auList = alarmUnitList;

        for (int i : watchTable.getSelectedRows()) {
            tmpList.add(auList.get(i));
        }

        for (AlarmUnit au : tmpList) {
            auList.remove(au);
        }
        ((DefaultTableModel) watchTable.getModel()).fireTableDataChanged();
    }

    public void loadDefaultList() {
        BufferedReader reader;
        String filePath = "/mnt/common/Kladov/alarmer.def";
        String line;
        List<AlarmUnit> auList = new LinkedList<>();
        try {
            reader  = new BufferedReader(new FileReader(filePath));
            while (reader.ready()) {
                line = reader.readLine();
                AlarmUnit au = AlarmUnit.fromString(line);
                if (au != null) {
                    auList.add(au);
                }
            }
            alarmUnitList.clear();
            alarmUnitList.addAll(auList);
            MainPanel.casCommunicator.subscribeToSelected();
            ((DefaultTableModel) watchTable.getModel()).fireTableDataChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDefaultList() {
        BufferedWriter writer;
        String filePath = "/mnt/common/Kladov/alarmer.def";
        try {
            writer = new BufferedWriter(new FileWriter(filePath));

            for (AlarmUnit au:alarmUnitList) {
                writer.write(au.toString());
                writer.newLine();
            }
            writer.flush();

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Something wrong with file: \"" + filePath + "\"",
                    "Open file error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
