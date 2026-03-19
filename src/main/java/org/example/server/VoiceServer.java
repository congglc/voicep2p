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
            callFrame.updateStatus("Signaling... Port 5000");

            socket = serverSocket.accept();
            
            // 1. TCP Handshake (Exchange Usernames)
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            
            dos.writeUTF(currentUser.getUsername());
            String remoteUser = dis.readUTF();
            String remoteIp = socket.getInetAddress().getHostAddress();

            callFrame.updateStatus("Connected (P2P UDP): " + remoteUser);
            HistoryService.save(new Date() + ": [UDP P2P] Voice from " + remoteUser);

            // 2. Start UDP Audio (Port 5000)
            // Note: In true P2P, both sides send and receive on the same fixed port or negotiated ports.
            sender = new VoiceSender(remoteIp, 5000);
            receiver = new VoiceReceiver(5000);

            sender.start();
            receiver.start();

            // After handshake, we can close the TCP signaling socket or keep it for control
            // Here we'll just let it stay open or close it. Closing TCP doesn't affect UDP.

        } catch (Exception e) {
            if (serverSocket != null && !serverSocket.isClosed()) {
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