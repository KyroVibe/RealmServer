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

        Main.inst.managers.remove(this);

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
            osw.Write(dat);
            osw.Flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String read() {
        try {
            return isr.Read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}