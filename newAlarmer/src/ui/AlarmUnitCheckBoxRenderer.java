package ui;

import data.AlarmUnit;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class AlarmUnitCheckBoxRenderer implements TableCellRenderer {
    private final Color defBack;
    public final CheckBoxPanel checkBoxPanel;

    public AlarmUnitCheckBoxRenderer() {
        checkBoxPanel = new CheckBoxPanel();
        defBack = checkBoxPanel.checkBoxLeft.getBackground();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Boolean[]) {
            Boolean[] val = (Boolean[])value;
            boolean isOn = val[0];
            boolean isOnSound = val[1];
            checkBoxPanel.checkBoxLeft.setSelected(isOn);
            checkBoxPanel.checkBoxRight.setSelected(isOnSound);
        }

        Color c;

        AlarmUnit au = ItemPanel.alarmUnitList.get(row);
        if (!au.isOn()[0]) {
            c = defBack;
        } else {
            if (au.isOk()) {
                c = Color.green;
            } else {
                c = Color.red;
            }
        }
        if (isSelected) {
            ((Component) checkBoxPanel).setBackground(c.darker());
            checkBoxPanel.checkBoxLeft.setBackground(c.darker());
            checkBoxPanel.checkBoxRight.setBackground(c.darker());
        } else {
            ((Component) checkBoxPanel).setBackground(c);
            checkBoxPanel.checkBoxLeft.setBackground(c);
            checkBoxPanel.checkBoxRight.setBackground(c);
        }


        return checkBoxPanel;
    }
}
