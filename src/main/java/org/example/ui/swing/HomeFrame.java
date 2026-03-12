package org.example.ui.swing;

import org.example.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class HomeFrame extends JFrame {
    private User currentUser;

    private JPanel rightPanel;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JLabel lblTargetName;
    private JPanel actionPanel;

    public HomeFrame(User user) {
        this.currentUser = user;
        setTitle("Zalo Voice - Xin chào " + user.getUsername());
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // ================= CỘT TRÁI: DANH MỤC =================
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(260, 0));
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        JLabel lblDanhBa = new JLabel("Danh mục Gọi", SwingConstants.CENTER);
        lblDanhBa.setFont(new Font("Arial", Font.BOLD, 16));
        lblDanhBa.setBorder(new EmptyBorder(15, 0, 15, 0));
        leftPanel.add(lblDanhBa, BorderLayout.NORTH);

        DefaultListModel<String> menuModel = new DefaultListModel<>();
        menuModel.addElement("👤 Trò chuyện Cá nhân (1-1)");
        menuModel.addElement("👥 Trò chuyện Nhóm");

        JList<String> menuList = new JList<>(menuModel);
        menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuList.setFont(new Font("Arial", Font.PLAIN, 15));
        menuList.setFixedCellHeight(50);
        
        menuList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = menuList.getSelectedIndex();
                if (index == 0) {
                    showPersonalCallUI();
                } else if (index == 1) {
                    showGroupCallUI();
                }
            }
        });

        leftPanel.add(new JScrollPane(menuList), BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        // ================= CỘT PHẢI =================
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        JLabel lblWelcome = new JLabel("Chọn một chế độ gọi ở danh sách bên trái", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.ITALIC, 14));
        lblWelcome.setForeground(Color.GRAY);
        rightPanel.add(lblWelcome, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);
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

        JButton btnRefresh = new JButton("🔄 Làm mới");
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> refreshHistory());
        historyHeader.add(btnRefresh, BorderLayout.EAST);
        
        bodyPanel.add(historyHeader, BorderLayout.NORTH);

        // THAY THẾ JTEXTAREA THÀNH JTABLE
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

        JButton btnWait = new JButton("🎧 Bật máy chờ gọi đến");
        btnWait.setBackground(new Color(40, 167, 69));
        btnWait.setForeground(Color.WHITE);
        btnWait.addActionListener(e -> {
            CallFrame callFrame = new CallFrame(currentUser, null, true);
            callFrame.setVisible(true);
        });

        JButton btnCall = new JButton("📞 Gọi người khác (Nhập IP)");
        btnCall.setBackground(new Color(0, 132, 255));
        btnCall.setForeground(Color.WHITE);
        btnCall.addActionListener(e -> {
            String ip = JOptionPane.showInputDialog(this, "Nhập IP của người muốn gọi (VD: localhost):");
            if (ip != null && !ip.trim().isEmpty()) {
                CallFrame callFrame = new CallFrame(currentUser, ip, false);
                callFrame.setVisible(true);
            }
        });

        actionPanel.add(btnWait);
        actionPanel.add(btnCall);
    }

    private void showGroupCallUI() {
        setupRightPanelBase("Gọi Nhóm (Voice Room)", "👥");

        JButton btnHost = new JButton("🏠 Tạo phòng Mới");
        btnHost.setBackground(new Color(255, 193, 7));
        btnHost.addActionListener(e -> {
            GroupCallFrame groupCallFrame = new GroupCallFrame(currentUser, null, true);
            groupCallFrame.setVisible(true);
        });

        JButton btnJoin = new JButton("🔗 Vào phòng bằng IP");
        btnJoin.setBackground(new Color(0, 132, 255));
        btnJoin.setForeground(Color.WHITE);
        btnJoin.addActionListener(e -> {
            String groupIp = JOptionPane.showInputDialog(this, "Nhập IP của chủ phòng:");
            if (groupIp != null && !groupIp.trim().isEmpty()) {
                GroupCallFrame groupCallFrame = new GroupCallFrame(currentUser, groupIp, false);
                groupCallFrame.setVisible(true);
            }
        });

        actionPanel.add(btnHost);
        actionPanel.add(btnJoin);
    }

    // Đưa dữ liệu từ file text vào bảng
    private void refreshHistory() {
        if (tableModel != null) {
            tableModel.setRowCount(0); // Xóa dữ liệu cũ
            try {
                File historyFile = new File("history.txt");
                if (historyFile.exists()) {
                    List<String> lines = Files.readAllLines(historyFile.toPath());
                    for (String line : lines) {
                        // Tách chuỗi theo dấu ":" đầu tiên để lấy thời gian và nội dung
                        int firstColon = line.indexOf(":");
                        if(firstColon != -1 && firstColon + 1 < line.length()) {
                             String time = line.substring(0, firstColon + 6); // Lấy HH:mm:ss
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