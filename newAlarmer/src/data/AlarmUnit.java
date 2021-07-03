package data;

import ui.MainPanel;
import ui.TreePanel;

import javax.swing.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AlarmUnit {


    private final String id;
    private final String shortID;

    private boolean isOn;
    private boolean isOnSound;

    private final Timer delayTimer;


    private double value;

    private double delay;
    private boolean isDelayWaited = false;

    private final List<Interval> validIntervals = new LinkedList<>();

    public AlarmUnit(String id, Boolean _isOn, Boolean isOnSound, double _delay) {
        this.id = id;
        isOn = _isOn;
        this.isOnSound = isOnSound;
        delay = _delay;
        String[] ss= id.split("\\/");
        shortID = ss[ss.length-1];

        delayTimer = new Timer((int)(delay*1000), e->isDelayWaited = true);
        delayTimer.setRepeats(false);
    }

    public String toString() {
        String s = "";

        s += id + "|";
        s += ((isOn)?"true":"false") + "|";
        s += delay + "|";

        s += getIntervalsAsString();

        return s;
    }

    public static AlarmUnit fromString(String fullInfo) {
        String[] sArr = fullInfo.split("\\|",-1);
        if (sArr.length<4) {
            return null;
        }
        String _id = sArr[0];
        boolean isIdValid = false;
        for (String s: TreePanel.availableChannelsArray) {
            if (s.equals(_id)) {
                isIdValid = true;
                break;
            }
        }
        if (!isIdValid) {
            return null;
        }
        boolean _isOn = Boolean.parseBoolean(sArr[1]);
        double _delay = Double.parseDouble(sArr[2]);
        AlarmUnit au = new AlarmUnit(_id, _isOn, true, _delay);
        au.updateIntervals(sArr[3]);
        return au;
    }

    public boolean isOk () {
        if (validIntervals.isEmpty()) {
            return true;
        }

        boolean isIn = false;
        for (Interval i:validIntervals) {
            isIn = isIn | i.isIn(value);
        }
        return isIn;
    }

    public void updateIntervals (String inString) {

        validIntervals.clear();

        String[] sInts = inString.split("\\ *,\\ *");

        for (String s:sInts) {
            if ( !(s.startsWith("(") && s.endsWith(")")) ) {
                continue;
            }

            s = s.substring(1, s.length()-1);

            String[] limits = s.split(";\\ *");

            if (limits.length != 2) {
                continue;
            }

            validIntervals.add(new Interval(Double.parseDouble(limits[0]), Double.parseDouble(limits[1])));
        }

    }

    public String getIntervalsAsString () {
        StringBuilder s = new StringBuilder();
        for (Interval i:validIntervals) {
            if (s.length() > 0) {
                s.append(", ");
            }
            s.append(i.toString());
        }

        return s.toString();
    }

    public String getShortID() {
        return shortID;
    }

    public String getId() {
        return id;
    }

    public double getDelay() {
        return delay;
    }

    public void setDelay(double _delay) {
        this.delay = _delay;
        delayTimer.setInitialDelay((int)(delay*1000));
    }

    public Boolean[] isOn() {
        return new Boolean[]{isOn,isOnSound};
    }

    public void setOn(Boolean[] on) {
        isOn = on[0];
        isOnSound = on[1];

        if (isOn) {
            treatValue();
        } else {
            delayTimer.stop();
        }
    }

    public void setValue(String sVal) {
        try {
            value = Double.parseDouble(sVal);
            treatValue();
        } catch (NumberFormatException e) {
            setOn(new Boolean[]{false,true});
            MainPanel.casCommunicator.subscribeToSelected();
            e.printStackTrace();
        }
    }

    private void treatValue() {
        if (!isOn) {
            return;
        }

        if (isOk()) {
            delayTimer.stop();
            isDelayWaited = false;
        } else {
            if (isDelayWaited) {
                if(isOnSound) MainPanel.playAlarm(this);
                else MainPanel.writeToLog(this);
            } else if ( !(delayTimer.isRunning())) {
                delayTimer.start();
            }
        }
    }

    public double getValue() {
        return value;
    }

}
