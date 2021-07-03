package ui;

import communication.CASCommunicator;
import company.Config;
import data.AlarmUnit;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class MainPanel extends JPanel {
    public final ItemPanel itemPanel = new ItemPanel(this);
    public final TreePanel treePanel = new TreePanel(this);
    public final LogPanel logPanel = new LogPanel();
    private boolean isTreeShowed = false;
    private boolean isLogShowed = false;
    private final MainFrame mainFrame;
    private final SpringLayout layout = new SpringLayout();

    public static boolean isAlarmPlayed = false;
    private static Date whenAlarmStartedPlaying = new Date();
    public static CASCommunicator casCommunicator;

    public MainPanel(MainFrame mainFrame) {
        setPreferredSize(new Dimension(Config.boardWidth, Config.boardHeight));
        this.mainFrame = mainFrame;

        setFocusable(true);
        setName("mainPanel");
        initializeVariables();
        repaint();
    }

    private void initializeVariables(){
        layout.putConstraint(SpringLayout.EAST, itemPanel, 0, SpringLayout.EAST, this);
        layout.putConstraint(SpringLayout.NORTH, itemPanel, 0, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.WEST, itemPanel, 0, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.SOUTH, itemPanel, 0, SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.EAST, treePanel, 0, SpringLayout.WEST, itemPanel);
        layout.putConstraint(SpringLayout.NORTH, treePanel, 0, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.SOUTH, treePanel, 0, SpringLayout.SOUTH, itemPanel);
        layout.putConstraint(SpringLayout.EAST, logPanel, 0, SpringLayout.EAST, this);
        layout.putConstraint(SpringLayout.NORTH, logPanel, 0, SpringLayout.SOUTH, itemPanel);
        layout.putConstraint(SpringLayout.SOUTH, logPanel, 0, SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.WEST, logPanel, 0, SpringLayout.WEST, this);
        setLayout(layout);
        casCommunicator = new CASCommunicator(this);
        try {
            casCommunicator.getAvailableChannels();
        } catch (IOException e) {
            e.printStackTrace();
        }
        repaint();
        add(logPanel);
        add(treePanel);
        add(itemPanel);
        logPanel.setVisible(false);
        treePanel.setVisible(false);
        setBackground(Color.cyan);
        loadLog();
    }

    public void showTree(){
        int width = this.getWidth();
        int height = this.getHeight();
        if(!isTreeShowed){
            try {
                casCommunicator.getAvailableChannels();
            } catch (IOException e) {
                e.printStackTrace();
            }
            TreePanel.updateChannelsTree();
            setPreferredSize(new Dimension(width +200, height));
            isTreeShowed = true;
            layout.putConstraint(SpringLayout.WEST, itemPanel, 200, SpringLayout.WEST, this);
            mainFrame.setLocation(mainFrame.getX()-200,mainFrame.getY());
            treePanel.setVisible(true);
        } else {
            setPreferredSize(new Dimension(width-200, height));
            isTreeShowed = false;
            layout.putConstraint(SpringLayout.WEST, itemPanel, 0, SpringLayout.WEST, this);
            mainFrame.setLocation(mainFrame.getX()+200,mainFrame.getY());
            treePanel.setVisible(false);
        }
        revalidate();
        mainFrame.pack();
        repaint();
    }

    public void showLog(){
        int width = this.getWidth();
        int height = this.getHeight();
        if(!isLogShowed){
            setPreferredSize(new Dimension(width, height+150));
            isLogShowed = true;
            layout.putConstraint(SpringLayout.SOUTH, itemPanel, -150, SpringLayout.SOUTH, this);
            logPanel.setVisible(true);
        } else {
            setPreferredSize(new Dimension(width, height-150));
            isLogShowed = false;
            layout.putConstraint(SpringLayout.SOUTH, itemPanel, 0, SpringLayout.SOUTH, this);
            logPanel.setVisible(false);
        }
        revalidate();
        mainFrame.pack();
        repaint();
    }

    public static void playAlarm(AlarmUnit au) {
        Date date = new Date();
        writeToLog(au);
        try {
            if(date.after(whenAlarmStartedPlaying)) {
                Runtime.getRuntime().exec("aplay /mnt/common/Kladov/klaxon_ahooga_4times.wav");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.SECOND, 5);
                whenAlarmStartedPlaying = calendar.getTime();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeToLog(AlarmUnit au){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String message = formatter.format(date)+"   "+au.getId()+"   "+au.getValue()+"\n";
        LogPanel.textArea.append(message);
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter("/mnt/common/Kladov/alarmerLogs.def",true));
            writer.append(message);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int stringCount=0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/mnt/common/Kladov/alarmerLogs.def"));
            while(reader.ready()) {
                stringCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(stringCount>50){
            try {
                writer = new BufferedWriter(new FileWriter("/mnt/common/Kladov/alarmerLogs.def"));
                writer.append("");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadLog(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/mnt/common/Kladov/alarmerLogs.def"));
            while(reader.ready()) {
                LogPanel.textArea.append(reader.readLine()+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}