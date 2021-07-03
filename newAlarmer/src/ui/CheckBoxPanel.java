package ui;

import javax.swing.*;

public class CheckBoxPanel extends JPanel {
    public final JCheckBox checkBoxLeft;
    public final JCheckBox checkBoxRight;

    public CheckBoxPanel(){
        checkBoxLeft = new JCheckBox();
        checkBoxRight = new JCheckBox();

        checkBoxLeft.setHorizontalAlignment(SwingConstants.LEFT);
        checkBoxRight.setHorizontalAlignment(SwingConstants.RIGHT);

        SpringLayout layout = new SpringLayout();
        layout.putConstraint(SpringLayout.NORTH, checkBoxLeft, -3, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.WEST, checkBoxLeft, -3, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, checkBoxRight, -3, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.EAST, checkBoxRight, 3, SpringLayout.EAST, this);
        setLayout(layout);
        add(checkBoxLeft);
        add(checkBoxRight);
    }
}
