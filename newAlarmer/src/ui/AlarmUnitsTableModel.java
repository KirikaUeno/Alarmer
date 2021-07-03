package ui;

import data.AlarmUnit;

import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;
import java.util.List;

public class AlarmUnitsTableModel extends DefaultTableModel {

    private final DecimalFormat df = new DecimalFormat("0.000");
    private final DecimalFormat dfSmall = new DecimalFormat("0.000E0");

    private final List<AlarmUnit> auList;

    public AlarmUnitsTableModel(List<AlarmUnit> _auList) {
        this.auList = _auList;
    }

    public int getRowCount() {
        if (auList == null) {
            return 0;
        }
        return auList.size();
    }

    public int getColumnCount() {
        return 6;
    }

    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return "is ON";
        } else if (columnIndex == 1) {
            return "Name";
        } else if (columnIndex == 2) {
            return "Value";
        } else if (columnIndex == 3) {
            return "Allowed Range";
        } else if (columnIndex == 4) {
            return "Delay";
        } else if (columnIndex == 5) {
            return "Count";
        }
        return null;
    }

    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Boolean[].class;
        }
        return Object.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0 || columnIndex == 3 || columnIndex == 4;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        AlarmUnit au = auList.get(rowIndex);

        if (columnIndex == 0) {
            return au.isOn();
        } else if (columnIndex == 1) {
            return au.getShortID();
        } else if (columnIndex == 2) {
            if (au.getValue() < 0.01) {
                return dfSmall.format(au.getValue());
            } else {
                return df.format(au.getValue());
            }
        } else if (columnIndex == 3) {
            return au.getIntervalsAsString();
        } else if (columnIndex == 4) {
            return String.valueOf(au.getDelay());
        } else if (columnIndex == 5) {
            return "";
        }
        return "";
    }

    public void setValueAt(Object obj, int rowIndex, int columnIndex) {
        AlarmUnit au = auList.get(rowIndex);

        if (columnIndex == 0) {
            au.setOn((Boolean[])obj);
        } else if (columnIndex == 3) {
            au.updateIntervals(obj.toString());
        } else if (columnIndex == 4) {
            au.setDelay(Double.parseDouble(obj.toString()));
        }
    }
}
