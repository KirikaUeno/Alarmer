package ui;

import data.AlarmUnit;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class AlarmUnitsTableRenderer extends DefaultTableCellRenderer {
    private Color defBack;

    public AlarmUnitsTableRenderer() {
        super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        Color c;

        if (defBack==null) {
            defBack = getBackground();
        }

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
            cell.setBackground(c.darker());
        } else {
            cell.setBackground(c);
        }

        return cell;
    }
}
