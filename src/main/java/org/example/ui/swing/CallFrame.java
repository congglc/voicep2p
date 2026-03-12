package org.example.ui.swing;

import org.example.client.VoiceClient;
import org.example.model.User;
import org.example.server.VoiceServer;

import javax.swing.*;
import java.awt.*;

public class CallFrame extends JFrame {
    private User currentUser;
    private String targetIp;
    private boolean isHost;

    private JLabel statusLabel;
    private JButton endCallBtn;

    private VoiceServer server;
    private VoiceClient client;

    public CallFrame(User currentUser, String targetIp, boolean isHost) {
        this.currentUser = currentUser;
        this.targetIp = targetIp;
        this.isHost = isHost;

        setTitle("1-to-1 Call");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        statusLabel = new JLabel("Status: Starting...", SwingConstants.CENTER);
        panel.add(statusLabel);

        JLabel infoLabel = new JLabel(isHost ? "Waiting for someone to call..." : "Calling " + targetIp + "...", SwingConstants.CENTER);
        panel.add(infoLabel);

        endCallBtn = new JButton("End Call");
        endCallBtn.addActionListener(e -> endCall());
        panel.add(endCallBtn);

        add(panel);

        // Handle window close
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
            server = new VoiceServer(this, currentUser);
            new Thread(() -> server.startServer()).start();
        } else {
            client = new VoiceClient(this, currentUser, targetIp);
            new Thread(() -> client.connect()).start();
        }
    }

    public void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText("Status: " + status));
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

    public void onCallEnded() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "Call Ended.");
            dispose();
        });
    }
}