package communication;

import data.AlarmUnit;
import ui.ItemPanel;
import ui.MainPanel;
import ui.TreePanel;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

public class CASCommunicator extends Thread {

    private final java.util.List<CASListener> serverListeners = new LinkedList<>();

    private Socket socket = new Socket();

    protected PrintWriter out = null;
    protected InputStream in = null;

    private boolean isConnected = false;
    private MainPanel mainPanel;

    public CASCommunicator(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        connectToServer();
        start();
    }

    public void connectToServer() {
        try {
            if ( socket.isConnected() && !((socket.isClosed()) || (socket.isInputShutdown()) || (socket.isOutputShutdown())) ) {
                return;
            }
            socket = new Socket("zeus", 20041);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = socket.getInputStream();
            isConnected = true;

        } catch (IOException e) {
            isConnected = false;
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "No connection to CAS:\n" + "zeus:" + "20041",
                    "Network error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void disconnectFromServer() {
        try {
            socket.close();
            in.close();
            out.close();
            isConnected = false;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        StringBuilder buffer = new StringBuilder();
        char ch;

        boolean flag = true;
        while (flag) {
            try {
                if ( !isConnected ) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                if ( (socket.isClosed()) || (socket.isInputShutdown()) || (socket.isOutputShutdown()) ) {
                    if (isConnected) {
                        disconnectFromServer();
                    }
                }

                ch = (char) in.read();

                if (ch=='\n') {
                    treatCommand(buffer.toString());
                    buffer = new StringBuilder();
                } else {
                    buffer.append(ch);
                }

            } catch (IOException e) {
                disconnectFromServer();
                e.printStackTrace();
            }
        }
    }

    synchronized private void treatCommand(String rawCommand) {
        String[] keyValPairs = rawCommand.split("\\|");

        if (rawCommand.contains("name:ChannelsList")) {
            for (String kv:keyValPairs) {
                String[] keyVals = kv.split("\\:");
                if (keyVals[0].equals("val")) {
                    String[] vals = keyVals[1].split(",");
                    TreePanel.updateAvailableChannelsList(vals);
                }
            }


        } else  {
            String sVal;
            for (AlarmUnit au: ItemPanel.alarmUnitList) {
                if (au.getId().equals(keyValPairs[0].split("\\:")[1])) {
                    for (String kv:keyValPairs) {
                        if (kv.startsWith("val:")) {
                            sVal = kv.substring(4);
                            au.setValue(sVal);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        mainPanel.repaint();
    }

    synchronized public void addServerListener(CASListener scl) {
        serverListeners.add(scl);
    }

    synchronized public void removeServerListener(CASListener scl) {
        serverListeners.remove(scl);
    }

    synchronized private void notifyServerListeners(String message) {
        for (CASListener scl:serverListeners) {
            scl.messageFromServerRecieved(message);
        }

    }

    public void getAvailableChannels() throws IOException {
        if (!isConnected) {
            return;
        }
        String query = "name:ChannelsList|method:get\n";

        out.print(query);
        out.flush();
    }

    public void sendCommand(String command) {
        if (!isConnected) {
            return;
        }
        out.print(command);
        out.flush();
    }

    public void subscribeToSelected () {

        String command;
        for (AlarmUnit au:ItemPanel.alarmUnitList) {
            if (au.isOn()[0]) {
                command = "name:" + au.getId() + "|method:subscr\n";
                sendCommand(command);
            }

        }

    }

}
