package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.*;
import java.util.ArrayList;

public class Main {

    public static Main inst;

    public static final String VERSION = "1.0.0";
    public static final int COMMPORT = 7230;
    public static final double cycleFreq = 10; // Delay in milliseconds
    public static boolean kys = false;

    private ServerSocket serverSocket;
    //public ArrayList<Socket> clients;
    public ArrayList<ClientManager> managers;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        System.out.println("--- Owens Baby Monitor v" + VERSION + " ---");

        inst = this;

        Log("Starting Server Socket");

        try {
            serverSocket = new ServerSocket(COMMPORT);

            //clients = new ArrayList<Socket>();
            managers = new ArrayList<ClientManager>();

            while (!kys) {
                try {
                    Socket a = serverSocket.accept();
                    Log("Socket Connected");

                    ClientManager client = new ClientManager(a);

                    managers.add(client);

                } catch (Exception e) {
                    Log("Exception: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Log("Exception: " + e.getMessage());
        }
    }

    public static void processData(ClientManager sender, String _data) {
        String[] data = _data.split("%");

        inst.Log(_data);

        switch (data[0].toLowerCase()) {
            case "reg":
                
            case "update":
                break;
        }
    }

    public static ClientManager getClientByName(String name) {
        for (int i = 0; i < inst.managers.size(); i++) {
            if (inst.managers.get(i).ID.equals(name)) {
                return inst.managers.get(i);
            }
        }

        return null;
    }

    public void Log(Object a) {
        System.out.println(a.toString());
    }

}