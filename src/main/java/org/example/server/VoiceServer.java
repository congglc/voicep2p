package org.example.server;

import org.example.client.VoiceReceiver;
import org.example.client.VoiceSender;
import org.example.model.User;
import org.example.service.HistoryService;
import org.example.ui.swing.CallFrame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class VoiceServer {

    private CallFrame callFrame;
    private User currentUser;
    private ServerSocket serverSocket;
    private Socket socket;
    private VoiceSender sender;
    private VoiceReceiver receiver;

    public VoiceServer(CallFrame callFrame, User currentUser) {
        this.callFrame = callFrame;
        this.currentUser = currentUser;
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(5000);
            callFrame.updateStatus("Waiting for call on port 5000...");

            socket = serverSocket.accept();

            // Handshake (Send local username, receive remote username)
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            dos.writeUTF(currentUser.getUsername());
            String remoteUser = dis.readUTF();

            callFrame.updateStatus("Connected with " + remoteUser);

            // Log to history
            HistoryService.save(new Date().toString() + ": Received 1-to-1 call from " + remoteUser);

            sender = new VoiceSender(socket);
            receiver = new VoiceReceiver(socket);

            sender.start();
            receiver.start();

        } catch (Exception e) {
            if (!serverSocket.isClosed()) {
                e.printStackTrace();
            }
        }
    }

    public void stopServer() {
        try {
            if (sender != null) sender.stopSending();
            if (receiver != null) receiver.stopReceiving();
            if (socket != null && !socket.isClosed()) socket.close();
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}