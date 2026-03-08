package org.example.client;

import org.example.model.User;
import org.example.service.HistoryService;
import org.example.ui.swing.GroupCallFrame;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Date;

public class GroupVoiceClient {

    private GroupCallFrame callFrame;
    private User currentUser;
    private String hostIp;
    private Socket socket;
    private VoiceSender sender;
    private VoiceReceiver receiver;

    public GroupVoiceClient(GroupCallFrame callFrame, User currentUser, String hostIp) {
        this.callFrame = callFrame;
        this.currentUser = currentUser;
        this.hostIp = hostIp;
    }

    public void connect() {
        try {
            socket = new Socket(hostIp, 5001); // Group call port

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(currentUser.getUsername()); // Send username to host
            
            callFrame.addUser("Host at " + hostIp); // Basic join confirmation
            HistoryService.save(new Date() + ": Joined Group Call at " + hostIp);

            sender = new VoiceSender(socket);
            receiver = new VoiceReceiver(socket);

            sender.start();
            receiver.start();

        } catch (Exception e) {
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
