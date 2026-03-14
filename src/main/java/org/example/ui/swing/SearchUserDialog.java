package org.example.ui.swing;

import org.example.model.User;
import org.example.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SearchUserDialog extends JDialog {
    private List<User> selectedUsers = new ArrayList<>();
    private boolean confirmed = false;

    private DefaultListModel<String> listModel;
    private JList<String> userList;
    private List<User> currentDisplayedUsers = new ArrayList<>();

    public SearchUserDialog(Frame parent, boolean multiSelect, String currentUsername) {
        super(parent, multiSelect ? "Tạo nhóm - Chọn bạn bè" : "Gọi 1-1 - Chọn người gọi", true);
        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Header - Thanh Tìm kiếm
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField txtSearch = new JTextField();
        JButton btnSearch = new JButton("Tìm kiếm");
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        add(searchPanel, BorderLayout.NORTH);

        // Body - Danh sách hiển thị User
        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        // Nếu là gọi nhóm thì cho phép chọn nhiều, gọi 1-1 thì chỉ chọn 1
        userList.setSelectionMode(multiSelect ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);
        userList.setFont(new Font("Arial", Font.PLAIN, 15));
        userList.setFixedCellHeight(40);
        add(new JScrollPane(userList), BorderLayout.CENTER);

        // Footer - Các nút chức năng
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Hủy");
        JButton btnConfirm = new JButton(multiSelect ? "Tạo Nhóm Ngay" : "Gọi Ngay");
        btnConfirm.setBackground(new Color(0, 132, 255));
        btnConfirm.setForeground(Color.WHITE);

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnConfirm);
        add(bottomPanel, BorderLayout.SOUTH);

        // Bắt sự kiện Tìm kiếm
        btnSearch.addActionListener(e -> loadUsers(txtSearch.getText().trim(), currentUsername));
        txtSearch.addActionListener(e -> loadUsers(txtSearch.getText().trim(), currentUsername)); // Bấm Enter để tìm

        btnCancel.addActionListener(e -> dispose());

        // Bắt sự kiện khi chốt chọn người
        btnConfirm.addActionListener(e -> {
            int[] selectedIndices = userList.getSelectedIndices();
            if (selectedIndices.length == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất 1 người!");
                return;
            }
            for (int i : selectedIndices) {
                selectedUsers.add(currentDisplayedUsers.get(i));
            }
            confirmed = true;
            dispose(); // Đóng cửa sổ tìm kiếm
        });

        // Load toàn bộ user khi vừa mở cửa sổ
        loadUsers("", currentUsername);
    }

    private void loadUsers(String keyword, String currentUsername) {
        listModel.clear();
        currentDisplayedUsers.clear();
        List<User> results = UserService.searchUsers(keyword);

        for (User u : results) {
            // Loại bỏ chính bản thân mình khỏi danh sách hiển thị
            if (!u.getUsername().equalsIgnoreCase(currentUsername)) {
                currentDisplayedUsers.add(u);
                listModel.addElement(" 👤 " + u.getUsername() + " (IP: " + u.getIp() + ")");
            }
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }
}