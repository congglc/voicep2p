package org.example.client;

import org.example.model.User;
import org.example.service.HistoryService;
import org.example.ui.swing.CallFrame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Date;

public class VoiceClient {

    private CallFrame callFrame;
    private User currentUser;
    private String targetIp;
    private Socket socket;
    private VoiceSender sender;
    private VoiceReceiver receiver;

    public VoiceClient(CallFrame callFrame, User currentUser, String targetIp) {
        this.callFrame = callFrame;
        this.currentUser = currentUser;
        this.targetIp = targetIp;
    }

    public void connect() {
        try {
            callFrame.updateStatus("Signaling " + targetIp + "...");
            socket = new Socket(targetIp, 5000);

            // 1. TCP Handshake
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            
            dos.writeUTF(currentUser.getUsername());
            String remoteUser = dis.readUTF();

            callFrame.updateStatus("Connected (P2P UDP): " + remoteUser);
            HistoryService.save(new Date() + ": [UDP P2P] Called " + remoteUser);

            // 2. Start UDP Audio (Port 5000)
            sender = new VoiceSender(targetIp, 5000);
            receiver = new VoiceReceiver(5000);

            sender.start();
            receiver.start();

        } catch (Exception e) {
            callFrame.updateStatus("Failed to reach peer.");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (sender != null) sender.stopSending();
            if (receiver != null) receiver.stopReceiving();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}