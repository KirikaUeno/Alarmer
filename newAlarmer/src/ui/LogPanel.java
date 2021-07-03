package ui;

import javax.swing.*;
import java.awt.*;

public class LogPanel extends JPanel {
    public static JTextArea textArea;

    public LogPanel(){
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        SpringLayout layout = new SpringLayout();
        layout.putConstraint(SpringLayout.EAST,scrollPane,0,SpringLayout.EAST,this);
        layout.putConstraint(SpringLayout.WEST,scrollPane,0,SpringLayout.WEST,this);
        layout.putConstraint(SpringLayout.NORTH,scrollPane,0,SpringLayout.NORTH,this);
        layout.putConstraint(SpringLayout.SOUTH,scrollPane,0,SpringLayout.SOUTH,this);
        setLayout(layout);
        setBackground(Color.BLACK);
        textArea.setEditable(false);
        add(scrollPane);
    }
}
