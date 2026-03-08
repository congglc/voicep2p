package org.example.ui.swing;

import org.example.client.GroupVoiceClient;
import org.example.model.User;
import org.example.server.GroupVoiceServer;

import javax.swing.*;
import java.awt.*;

public class GroupCallFrame extends JFrame {
    private User currentUser;
    private String targetIp;
    private boolean isHost;

    private JTextArea usersArea;
    private JButton endCallBtn;

    private GroupVoiceServer server;
    private GroupVoiceClient client;

    public GroupCallFrame(User currentUser, String targetIp, boolean isHost) {
        this.currentUser = currentUser;
        this.targetIp = targetIp;
        this.isHost = isHost;

        setTitle("Group Call" + (isHost ? " (Host)" : ""));
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel infoLabel = new JLabel(isHost ? "You are hosting the group call on port 5001." : "Joining " + targetIp + "...");
        panel.add(infoLabel, BorderLayout.NORTH);

        usersArea = new JTextArea("Connected Users:\n" + currentUser.getUsername() + " (You)\n");
        usersArea.setEditable(false);
        panel.add(new JScrollPane(usersArea), BorderLayout.CENTER);

        endCallBtn = new JButton(isHost ? "End Group Call" : "Leave Call");
        endCallBtn.addActionListener(e -> endCall());
        panel.add(endCallBtn, BorderLayout.SOUTH);

        add(panel);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                endCall();
            }
        });

        startConnection();
    }

    private void startConnection() {
        if (isHost) {
            server = new GroupVoiceServer(this, currentUser);
            new Thread(() -> server.startServer()).start();
        } else {
            client = new GroupVoiceClient(this, currentUser, targetIp);
            new Thread(() -> client.connect()).start();
        }
    }

    public void addUser(String username) {
        SwingUtilities.invokeLater(() -> usersArea.append(username + "\n"));
    }

    private void endCall() {
        if (server != null) {
            server.stopServer();
        }
        if (client != null) {
            client.disconnect();
        }
        dispose();
    }
}
