# P2P Voice Chat - Hướng Dẫn Chạy & Kiểm Thử

Dự án này là một ứng dụng Voice Chat theo mô hình Peer-to-Peer (P2P), được viết bằng Java và sử dụng Socket để truyền tín hiệu âm thanh. Phần giao diện được xây dựng bằng Java Swing. Các tính năng bao gồm:
- Đăng nhập với Username/Password
- Gọi thoại âm thanh 1-1 (P2P trực tiếp)
- Gọi nhóm âm thanh nhiều người (Mô hình Star-topology: 1 Host & nhiều Clients)
- Lưu lịch sử cuộc gọi

---

## 1. Yêu Cầu Hệ Thống
- Java Development Kit (JDK) 21 trở lên
- Có kết nối mạng Lan/Internet (nếu test trên 2 máy tính khác nhau)
- Máy tính có Micro (để thu âm) và Loa (để nghe) hoạt động bình thường

## 2. Cách Chạy Ứng Dụng 
1. Mở dự án `voice` trong IDE của bạn (IntelliJ IDEA, Eclipse, VS Code...).
2. Tìm tới file `src/main/java/org/example/Main.java`.
3. Chạy file `Main.java` (Run `Main.main()`).
4. Để test các chức năng gọi điện, bạn cần chạy **2 hoặc nhiều biến thể (instances)** của file `Main` cùng lúc. Bạn có thể mở ứng dụng 2 lần trên cùng 1 máy tính để test offline (Dùng IP là `localhost` hoặc `127.0.0.1`).

> **Tài khoản đăng nhập mặc định (theo `UserService`):**
> - **Tài khoản 1:** Username: `user1` | Password: `123`
> - **Tài khoản 2:** Username: `user2` | Password: `123`

---

## 3. Các Tính Năng & Cách Test Từng Trường Hợp

Sau khi đăng nhập thành công vào trang "Home", bạn có các nút chức năng sau:

### Trường Hợp 1: Kiểm thử Gọi 1-1 (1-to-1 Call)
*Tính năng này kết nối trực tiếp 2 máy qua Port 5000.*
1. Mở **Ứng dụng 1** đăng nhập user1. Mở **Ứng dụng 2** đăng nhập user2.
2. Trên **Ứng dụng 1** (đóng vai trò người nhận cuộc gọi), bấm: **"2. Wait for Call (1-to-1 Host)"**. Màn hình CallFrame hiện ra chữ "Waiting for someone to call...".
3. Trên **Ứng dụng 2** (đóng vai trò người gọi), bấm: **"1. Call a User (1-to-1)"**. 
4. Sẽ có hộp thoại hỏi nhập IP:
   - Nếu test trên 1 máy tính: nhập `localhost` hoặc `127.0.0.1` sau đó OK.
   - Nếu test trên 2 máy: nhập địa chỉ IP LAN (ví dụ `192.168.1.10`) của máy chạy Ứng dụng 1.
5. Cả 2 màn hình sẽ hiện thông báo "Connected with [Tên user kia]". Lúc này hãy thử nói vào Mic trên máy 1 và nghe trên máy 2 (và ngược lại).
6. Để kết thúc, trên một trong hai màn hình bấm nút **"End Call"** hoặc tắt cửa sổ call.

### Trường Hợp 2: Kiểm thử Gọi Nhóm (Group Call)
*Tính năng này sử dụng 1 Host làm trung tâm để nhận và phát lại âm thanh cho tất cả các máy khác tham gia, qua Port 5001.*
1. Mở ít nhất **3 Ứng dụng** (Ví dụ user1, user2 và user3).
2. Trên **Ứng dụng 1** (đóng vai trò tạo phòng), bấm nút: **"3. Create Group Call (Host)"**. Giao diện nhóm hiện lên thông báo bạn đang Host nhóm.
3. Trên **Ứng dụng 2**, bấm nút: **"4. Join Group Call"**. 
   - Nhập IP của Host (nhập `localhost` nếu chạy trên 1 máy, IP LAN nếu khác máy).
4. Trên ứng dụng 1 (Host) sẽ hiện thông báo User 2 đã join. Lúc này cả 2 đã trong phòng gọi.
5. Tương tự, trên **Ứng dụng 3**, bấm **"4. Join Group Call"**. Nhập IP của Host.
6. Lúc này Ứng dụng 1, Ứng dụng 2, Ứng dụng 3 đều đã kết nối. 
   - Nếu máy người 2 nói, âm thanh sẽ gửi đến Host (máy 1), và Host sẽ tự động gửi qua cho máy 3 nghe và phát ra loa của chính người 1. Như vậy 3 người đều nghe thấy nhau.
7. Thoát phòng bằng cách bấm **"End Group Call"** (đối với Host - sập cả phòng) hoặc **"Leave Call"** (đổi với Member).

### Trường Hợp 3: Kiểm thử Lịch Sử Cuộc Gọi
Mỗi lần có 1 cuộc gọi (nhận 1-1, gọi 1-1, tạo nhóm, vào nhóm) thì hệ thống sẽ tự lưu vào file `history.txt` ở ngoài thư mục gốc project.
1. Tại màn hình Home, sau khi đã test vài lần gọi, bấm nút: **"5. View Call History"**.
2. Một bảng thông báo sẽ hiển thị toàn bộ lịch sử (thời gian + hành động). Bạn có thể kiểm tra xem ngày giờ và action đã lưu chính xác hay chưa. 
*(Hoặc có thể tự mở file `history.txt` nằm chung thư mục với file `pom.xml` lên để theo dõi trực tiếp).*
