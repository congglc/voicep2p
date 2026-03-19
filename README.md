# P2P Voice Chat - Hướng Dẫn Chạy & Kiểm Thử (Chỉ Gọi 1-1)

Dự án này là một ứng dụng Voice Chat theo mô hình Peer-to-Peer (P2P), được viết bằng Java và sử dụng Socket để truyền tín hiệu âm thanh. Phần giao diện được xây dựng bằng Java Swing. Các tính năng bao gồm:
- Đăng nhập với Username/Password
- Gọi thoại âm thanh 1-1 (P2P trực tiếp)
- Lưu lịch sử cuộc gọi

---

## 1. Yêu Cầu Hệ Thống
- Java Development Kit (JDK) 21 trở lên
- Có kết nối mạng Lan/Internet (nếu test trên 2 máy tính khác nhau) hoặc dùng Loopback trên cùng 1 máy.
- Máy tính có Micro (để thu âm) và Loa (để nghe) hoạt động bình thường.

## 2. Cách Chạy Ứng Dụng 
1. Mở dự án `voice` trong IDE của bạn (IntelliJ IDEA, Eclipse, VS Code...).
2. Tìm tới file `src/main/java/org/example/Main.java`.
3. Chạy file `Main.java` (Run `Main.main()`).
4. Để test các chức năng gọi điện, bạn cần chạy **2 biến thể (instances)** của file `Main` cùng lúc. Bạn có thể mở ứng dụng 2 lần trên cùng 1 máy tính để test offline (Dùng IP là `127.0.0.1` hoặc chọn user trong danh sách nếu app tự nhận IP).

> **Tài khoản đăng nhập mặc định (theo `UserService`):**
> - **Tài khoản 1:** Username: `user1` | Password: `123`
> - **Tài khoản 2:** Username: `user2` | Password: `123`

---

## 3. Các Tính Năng & Cách Test

### Trường Hợp: Kiểm thử Gọi 1-1 (1-to-1 Call)
1. Mở **Ứng dụng 1** đăng nhập `user1`. Mở **Ứng dụng 2** đăng nhập `user2`.
2. Trên **Ứng dụng 1**, bấm nút **"📞 Tìm & Gọi người khác"**.
3. Một hộp thoại tìm kiếm hiện ra. Nhập tên `user2` hoặc để trống rồi bấm **Tìm kiếm**.
4. Chọn `user2` từ danh sách và bấm **Bắt đầu gọi**.
5. Trên **Ứng dụng 2**, một hộp thoại xác nhận sẽ hiện ra: "user1 đang gọi cho bạn. Nhận cuộc gọi?". Chọn **Yes**.
6. Cả 2 màn hình sẽ hiện trạng thái "Connected". Lúc này hãy thử nói vào Mic và nghe qua Loa.
7. Để kết thúc, bấm nút **"End Call"**.

### Trường Hợp: Kiểm thử Lịch Sử Cuộc Gọi
Mỗi lần có 1 cuộc gọi thành công, hệ thống sẽ tự lưu vào file `history.txt`.
1. Tại màn hình Home, sau khi đã thực hiện cuộc gọi, phần bảng phía dưới sẽ liệt kê lịch sử cuộc gọi.
2. Bạn có thể bấm vào danh mục bên trái hoặc khởi động lại ứng dụng để xem lịch sử đã được lưu lại trong file `history.txt`.