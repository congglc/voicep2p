package org.example.server;

import org.example.model.User;
import org.example.service.HistoryService;
import org.example.ui.swing.GroupCallFrame;

import javax.sound.sampled.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupVoiceServer {

    private GroupCallFrame callFrame;
    private User currentUser;
    private ServerSocket serverSocket;
    private boolean isRunning = true;
    private List<Socket> clientSockets = new ArrayList<>();
    private TargetDataLine microphone;

    public GroupVoiceServer(GroupCallFrame callFrame, User currentUser) {
        this.callFrame = callFrame;
        this.currentUser = currentUser;
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(5001); // Port 5001 for group calls
            HistoryService.save(new Date() + ": Created Group Call.");

            // Start sending host's mic to all clients
            new Thread(this::sendHostMicrophoneData).start();

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                clientSockets.add(clientSocket);

                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

                String joinedUser = dis.readUTF();
                callFrame.addUser(joinedUser);
                HistoryService.save(new Date() + ": " + joinedUser + " joined Group Call.");

                // Forward connection to handling thread
                new Thread(() -> handleClientAudio(clientSocket)).start();
            }
        } catch (Exception e) {
            if (isRunning) e.printStackTrace();
        }
    }

    private void sendHostMicrophoneData() {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
            microphone = AudioSystem.getTargetDataLine(format);
            microphone.open(format);
            microphone.start();

            byte[] buffer = new byte[4096];
            while (isRunning && microphone != null) {
                int count = microphone.read(buffer, 0, buffer.length);
                if (count > 0 && !clientSockets.isEmpty()) {
                    List<Socket> activeSockets = new ArrayList<>(clientSockets);
                    for (Socket socket : activeSockets) {
                        try {
                            if(!socket.isClosed()) {
                                socket.getOutputStream().write(buffer, 0, count);
                            }
                        } catch (Exception ex) {
                            clientSockets.remove(socket);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (isRunning) e.printStackTrace();
        }
    }

    private void handleClientAudio(Socket clientSocket) {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
            SourceDataLine speakers = AudioSystem.getSourceDataLine(format);
            speakers.open(format);
            speakers.start();

            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            byte[] buffer = new byte[4096];
            int count;

            while (isRunning && !clientSocket.isClosed() && (count = in.read(buffer)) > 0) {
                // Play locally (host speakers)
                speakers.write(buffer, 0, count);

                // Forward to other clients
                List<Socket> activeSockets = new ArrayList<>(clientSockets);
                for (Socket otherSocket : activeSockets) {
                    if (otherSocket != clientSocket && !otherSocket.isClosed()) {
                        try {
                            otherSocket.getOutputStream().write(buffer, 0, count);
                        } catch (Exception ex) {
                            clientSockets.remove(otherSocket);
                        }
                    }
                }
            }
            speakers.stop();
            speakers.close();
            clientSockets.remove(clientSocket);
        } catch (Exception e) {
            if (isRunning) e.printStackTrace();
            clientSockets.remove(clientSocket);
        }
    }

    public void stopServer() {
        isRunning = false;
        try {
            if (microphone != null) {
                microphone.stop();
                microphone.close();
            }
            for (Socket s : clientSockets) {
                if (!s.isClosed()) s.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
