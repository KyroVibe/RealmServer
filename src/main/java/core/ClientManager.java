package core;

import java.net.Socket;

import neospace.Vector3;
import neospace.NeoReader;
import neospace.NeoWriter;

public class ClientManager implements Runnable {

    public boolean selfDestruct = false;

    public Socket me;
    public String ID;

    private NeoReader isr;
    private NeoWriter osw;

    private Thread t;

    private Vector3 pos, rot;

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

    public void SetTransformShit(String a) {
        String[] dat = a.split("%");
        pos = new Vector3(Double.parseDouble(dat[0]), Double.parseDouble(dat[1]), Double.parseDouble(dat[2]));
        rot = new Vector3(Double.parseDouble(dat[3]), Double.parseDouble(dat[4]), Double.parseDouble(dat[5]));
    }

    public String GetTransformShit() {
        return pos.x + "%" + pos.y + "%" + pos.z + "%" + rot.x + "%" + rot.y + "%" + rot.z;
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