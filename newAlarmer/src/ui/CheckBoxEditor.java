package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class CheckBoxEditor extends DefaultCellEditor implements ItemListener {
    CheckBoxPanel panel;
    Object value;
    public int row;

    public CheckBoxEditor(JCheckBox checkBox, CheckBoxPanel panel) {
        super(checkBox);
        this.panel = panel;
        panel.checkBoxLeft.addItemListener(this);
        panel.checkBoxRight.addItemListener(this);
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row= row;
        this.value = value;
        return panel;
    }

    public Object getCellEditorValue() {
        return new Boolean[]{panel.checkBoxLeft.isSelected(),panel.checkBoxRight.isSelected()};
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        ItemPanel.alarmUnitList.get(row).setOn(new Boolean[]{panel.checkBoxLeft.isSelected(),panel.checkBoxRight.isSelected()});
    }
}