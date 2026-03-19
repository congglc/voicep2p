package org.example.ui.swing;

import org.example.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;

public class HomeFrame extends JFrame {
    private User currentUser;

    private JPanel rightPanel;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JLabel lblTargetName;
    private JPanel actionPanel;

    private ServerSocket signalServer;

    public HomeFrame(User user) {
        this.currentUser = user;
        setTitle("Zalo Voice - Xin chào " + user.getUsername());
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        startSignalingServer();
    }

    // Luồng lắng nghe tự động
    private void startSignalingServer() {
        new Thread(() -> {
            try {
                signalServer = new ServerSocket(5002);
                while (true) {
                    Socket socket = signalServer.accept();
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    String message = dis.readUTF();

                    if (message.startsWith("INVITE_1TO1")) {
                        String[] parts = message.split("\\|");
                        String hostName = parts[1];
                        String hostIp = parts[2];

                        SwingUtilities.invokeLater(() -> {
                            int response = JOptionPane.showConfirmDialog(this,
                                    hostName + " đang gọi cho bạn. Nhận cuộc gọi?",
                                    "Cuộc gọi đến từ " + hostName, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                            if (response == JOptionPane.YES_OPTION) {
                                CallFrame callFrame = new CallFrame(currentUser, hostIp, false);
                                callFrame.setVisible(true);
                            }
                        });
                    }
                    socket.close();
                }
            } catch (Exception e) {
                System.out.println("Lỗi luồng Signaling (Có thể test 2 app trên 1 máy): " + e.getMessage());
            }
        }).start();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(260, 0));
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        JLabel lblDanhBa = new JLabel("Danh mục", SwingConstants.CENTER);
        lblDanhBa.setFont(new Font("Arial", Font.BOLD, 16));
        lblDanhBa.setBorder(new EmptyBorder(15, 0, 15, 0));
        leftPanel.add(lblDanhBa, BorderLayout.NORTH);

        DefaultListModel<String> menuModel = new DefaultListModel<>();
        menuModel.addElement("👤 Gọi Cá Nhân (1-1)");

        JList<String> menuList = new JList<>(menuModel);
        menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuList.setFont(new Font("Arial", Font.PLAIN, 15));
        menuList.setFixedCellHeight(50);
        menuList.setSelectedIndex(0);

        menuList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showPersonalCallUI();
            }
        });

        leftPanel.add(new JScrollPane(menuList), BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        add(rightPanel, BorderLayout.CENTER);

        showPersonalCallUI();
    }

    private void setupRightPanelBase(String title, String icon) {
        rightPanel.removeAll();

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        infoPanel.setBackground(Color.WHITE);
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        lblTargetName = new JLabel(title);
        lblTargetName.setFont(new Font("Arial", Font.BOLD, 18));
        infoPanel.add(lblIcon);
        infoPanel.add(lblTargetName);
        headerPanel.add(infoPanel, BorderLayout.WEST);

        actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(Color.WHITE);
        headerPanel.add(actionPanel, BorderLayout.EAST);

        rightPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(new Color(245, 245, 245));
        bodyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel historyHeader = new JPanel(new BorderLayout());
        historyHeader.setBackground(new Color(245, 245, 245));
        JLabel lblHistory = new JLabel("🕒 Lịch Sử Cuộc Gọi Hệ Thống");
        lblHistory.setFont(new Font("Arial", Font.BOLD, 14));
        historyHeader.add(lblHistory, BorderLayout.WEST);

        String[] columns = {"Thời gian", "Nội dung cuộc gọi"};
        tableModel = new DefaultTableModel(columns, 0);
        historyTable = new JTable(tableModel);
        historyTable.setRowHeight(30);
        historyTable.setFont(new Font("Arial", Font.PLAIN, 13));
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        bodyPanel.add(scrollPane, BorderLayout.CENTER);

        rightPanel.add(bodyPanel, BorderLayout.CENTER);

        refreshHistory();

        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void showPersonalCallUI() {
        setupRightPanelBase("Gọi Cá Nhân (1-1)", "👤");

        JButton btnCall = new JButton("📞 Tìm & Gọi người khác");
        btnCall.setBackground(new Color(0, 132, 255));
        btnCall.setForeground(Color.WHITE);
        btnCall.addActionListener(e -> {
            SearchUserDialog dialog = new SearchUserDialog(this, false, currentUser.getUsername());
            dialog.setVisible(true);

            if (dialog.isConfirmed() && !dialog.getSelectedUsers().isEmpty()) {
                User targetUser = dialog.getSelectedUsers().get(0);
                if (targetUser.getIp() != null) {
                    String targetIp = targetUser.getIp();
                    String myIp = currentUser.getIp() != null ? currentUser.getIp() : "127.0.0.1";

                    new Thread(() -> {
                        try (Socket socket = new Socket(targetIp, 5002);
                             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
                            dos.writeUTF("INVITE_1TO1|" + currentUser.getUsername() + "|" + myIp);
                        } catch (Exception ex) {
                            System.out.println("Không thể gửi thông báo gọi tới " + targetUser.getUsername());
                        }
                    }).start();

                    CallFrame callFrame = new CallFrame(currentUser, null, true);
                    callFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy IP của user này!");
                }
            }
        });
        actionPanel.add(btnCall);
    }

    private void refreshHistory() {
        if (tableModel != null) {
            tableModel.setRowCount(0);
            try {
                File historyFile = new File("history.txt");
                if (historyFile.exists()) {
                    List<String> lines = Files.readAllLines(historyFile.toPath());
                    for (String line : lines) {
                        int firstColon = line.indexOf(":");
                        if(firstColon != -1 && firstColon + 1 < line.length()) {
                            String time = line.substring(0, firstColon + 6);
                            String content = line.substring(firstColon + 7).trim();
                            tableModel.addRow(new Object[]{time, content});
                        } else {
                            tableModel.addRow(new Object[]{"N/A", line});
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi đọc lịch sử: " + ex.getMessage());
            }
        }
    }
}