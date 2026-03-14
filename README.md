// Trong HomeFrame.java
public class HomeFrame extends JFrame {
    // ... code cũ của bạn
    private SignalingService signalingService;

    public HomeFrame(User user) {
        // ... code cũ
        
        signalingService = new SignalingService();
        signalingService.startListening((hostName, hostIp) -> {
            // Khi service nhận được tín hiệu, nó đẩy vào đây để vẽ giao diện
            SwingUtilities.invokeLater(() -> {
                int response = JOptionPane.showConfirmDialog(this,
                        hostName + " đang mời bạn vào nhóm. Tham gia ngay?",
                        "Lời mời", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    GroupCallFrame groupCallFrame = new GroupCallFrame(currentUser, hostIp, false);
                    groupCallFrame.setVisible(true);
                }
            });
        });
    }
    
    // Khi bấm nút Host tạo phòng gọi
    JButton btnHost = new JButton("🏠 Chọn User & Bắt đầu gọi");
    btnHost.addActionListener(e -> {
         // ... lấy input của người dùng và mở GroupCallFrame (tương tự đoạn code cũ)
         
         // Thay vì dùng Socket trực tiếp, gọi service:
         User targetUser = UserService.getUserByUsername(uname);
         if (targetUser != null && targetUser.getIp() != null) {
              signalingService.sendInvite(targetUser.getIp(), currentUser.getUsername(), currentUser.getIp());
         }
    });
}