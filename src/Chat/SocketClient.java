package Chat;

import java.io.*;
import java.net.*;

public class SocketClient implements Runnable {

    public int port;
    public String serverAddr;
    public Socket socket;
    public ClientForm ui;
    public ObjectInputStream In;
    public ObjectOutputStream Out;

    public SocketClient(ClientForm frame) throws IOException {
        ui = frame;
        this.serverAddr = ui.serverAddr;
        this.port = ui.port;
        socket = new Socket(InetAddress.getByName(serverAddr), port);

        Out = new ObjectOutputStream(socket.getOutputStream());
        Out.flush();
        In = new ObjectInputStream(socket.getInputStream());
    }

    
    @Override
    public void run() {
        boolean keepRunning = true;
        while (keepRunning) {
            try {
                Pesan msg = (Pesan) In.readObject();
                System.out.println("Pesan datang : " + msg.toString());

                if (msg.type.equals("message")) {
                    if (msg.recipient.equals(ui.username)) {
                        ui.clientDisp.append("[" + msg.sender + " > Me] : " + msg.content + "\n");
                    } else {
                        ui.clientDisp.append("[" + msg.sender + " > " + msg.recipient + "] : " + msg.content + "\n");

                    }
                } else if (msg.type.equals("login")) {
                    if (msg.content.equals("TRUE")) {
                        ui.clientDisp.append("[SERVER > Me] : Login Successful\n");

                    } else {
                        ui.clientDisp.append("[SERVER > Me] : Login Failed\n");
                    }
                } else if (msg.type.equals("test")) {
                } else if (msg.type.equals("newuser")) {
                    if (!msg.content.equals(ui.username)) {
                        boolean exists = false;
                        for (int i = 0; i < ui.model.getSize(); i++) {
                            if (ui.model.getElementAt(i).equals(msg.content)) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            ui.model.addElement(msg.content);
                        }
                    }
                } else if (msg.type.equals("signup")) {
                    if (msg.content.equals("TRUE")) {
                        ui.clientDisp.append("[SERVER > Me] : Singup Successful\n");

                    } else {
                        ui.clientDisp.append("[SERVER > Me] : Signup Failed\n");
                    }
                } else if (msg.type.equals("signout")) {
                    if (msg.content.equals(ui.username)) {
                        ui.clientDisp.append("[" + msg.sender + " > Me] : Bye\n");
                        for (int i = 1; i < ui.model.size(); i++) {
                            ui.model.removeElementAt(i);
                        }

                        ui.clientThread.stop();
                    } else {
                        ui.model.removeElement(msg.content);
                        ui.clientDisp.append("[" + msg.sender + " > All] : " + msg.content + " has signed out\n");
                    }
                } else {
                    ui.clientDisp.append("[SERVER > Me] : Unknown message type\n");
                }
            } catch (Exception ex) {
                keepRunning = false;
                ui.clientDisp.append("[Application > Me] : Koneksi Gagal \n");
                ui.hostField.setEditable(true);
                ui.portField.setEditable(true);
                for (int i = 1; i < ui.model.size(); i++) {
                    ui.model.removeElementAt(i);
                }

                ui.clientThread.stop();

                System.out.println("Exception SocketClient run()");
                ex.printStackTrace();
            }
        }
    }

    public void send(Pesan msg) {
        try {
            Out.writeObject(msg);
            Out.flush();
            System.out.println("Pesan Keluar : " + msg.toString());
        } catch (IOException ex) {
            System.out.println("Exception SocketClient send()");
        }
    }

    public void closeThread(Thread t) {
        t = null;
    }
}
