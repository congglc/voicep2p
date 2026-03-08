package org.example.ui.swing;

import org.example.model.User;
import org.example.service.HistoryService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;

public class HomeFrame extends JFrame {
    private User currentUser;

    public HomeFrame(User user) {
        this.currentUser = user;

        setTitle("P2P Voice Chat - Home (" + currentUser.getUsername() + ")");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton callUserBtn = new JButton("1. Call a User (1-to-1)");
        JButton waitCallBtn = new JButton("2. Wait for Call (1-to-1 Host)");
        JButton createGroupBtn = new JButton("3. Create Group Call (Host)");
        JButton joinGroupBtn = new JButton("4. Join Group Call");
        JButton historyBtn = new JButton("5. View Call History");

        callUserBtn.addActionListener(e -> {
            String ip = JOptionPane.showInputDialog(this, "Enter user IP to call:");
            if (ip != null && !ip.trim().isEmpty()) {
                CallFrame callFrame = new CallFrame(currentUser, ip, false);
                callFrame.setVisible(true);
            }
        });

        waitCallBtn.addActionListener(e -> {
            CallFrame callFrame = new CallFrame(currentUser, null, true);
            callFrame.setVisible(true);
        });

        createGroupBtn.addActionListener(e -> {
            GroupCallFrame groupCallFrame = new GroupCallFrame(currentUser, null, true);
            groupCallFrame.setVisible(true);
        });

        joinGroupBtn.addActionListener(e -> {
            String groupIp = JOptionPane.showInputDialog(this, "Enter Group Host IP to join:");
            if (groupIp != null && !groupIp.trim().isEmpty()) {
                GroupCallFrame groupCallFrame = new GroupCallFrame(currentUser, groupIp, false);
                groupCallFrame.setVisible(true);
            }
        });

        historyBtn.addActionListener(e -> showHistory());

        panel.add(callUserBtn);
        panel.add(waitCallBtn);
        panel.add(createGroupBtn);
        panel.add(joinGroupBtn);
        panel.add(historyBtn);

        add(panel);
    }

    private void showHistory() {
        try {
            File historyFile = new File("history.txt");
            String historyContent = "";
            if (historyFile.exists()) {
                historyContent = new String(Files.readAllBytes(historyFile.toPath()));
            } else {
                historyContent = "No history available.";
            }

            JTextArea textArea = new JTextArea(historyContent);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(350, 200));

            JOptionPane.showMessageDialog(this, scrollPane, "Call History", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error reading history file.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
