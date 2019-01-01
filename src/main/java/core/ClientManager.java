package core;

import java.net.Socket;

import neospace.*;

public class ClientManager implements Runnable {

    public boolean selfDestruct = false;

    public Socket me;
    public String ID;

    private NeoReader isr;
    private NeoWriter osw;

    private Thread t;

    public ClientManager(Socket me) {
        this.me = me;

        try {
            isr = new NeoReader(me.getInputStream());
            osw = new NeoWriter(me.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {

        double lastCycle = System.currentTimeMillis();

        while (!selfDestruct) {
            if (lastCycle + Main.cycleFreq <= System.currentTimeMillis()) {
                //Core.inst.Log("Cycle");

                if (!me.isConnected()) {
                    selfDestruct = true;
                    Main.inst.Log("Connection Disconnected");
                    break;
                }

                lastCycle = System.currentTimeMillis();
                try {
                    String data = isr.Read();

                    Main.inst.Log("Read Data");

                    Main.processData(this, data);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //Core.inst.Log("Non Cycle");
            }
        }

        try {

            isr.Close();
            osw.Close();

            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void write(String dat) {
        try {
            osw.write(dat);
            osw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(char[] data) {
        try {
            osw.write(data, 0, data.length);
            osw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public char[] read(int length) {
        char[] buffer = new char[length];
        try {
            isr.read(buffer, 0, length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    private String buildString(char[] a, int b) {
        String c = "";

        for (int i = 0; i < b; i++) {
            c += a[i];
        }

        return c;
    }

}