# P2P Voice Chat - UDP Architecture (VoIP Standard)

Dự án này đã được nâng cấp lên kiến trúc **P2P dựa trên UDP**, đây là tiêu chuẩn thực tế của công nghệ Voice IP (VoIP). 

### 1. Kiến trúc mạng (Hybrid P2P)
- **Signaling (TCP - Port 5000/5002):** Dùng để trao đổi thông tin mời gọi, xác nhận tên người dùng. TCP đảm bảo lời mời luôn được gửi đến đích (Reliable).
- **Voice Stream (UDP - Port 5000):** Sau khi kết nối, âm thanh được truyền qua giao thức UDP (`DatagramSocket`). 
  - **Tốc độ:** Truyền gói tin cực nhanh, không có độ trễ treo ảnh hưởng bởi việc truyền lại gói tin lỗi của TCP.
  - **Symmetric P2P:** Cả 2 máy đều mở cổng UDP 5000 để bắn và nhận dữ liệu đồng thời.

---

## 2. Cách Chạy & Kiểm Thử
1. Chạy `Main.java` trên 2 máy tính (hoặc 2 tab trong IDE).
2. Đăng nhập `user1` và `user2`.
3. Bấm **"📞 Tìm & Gọi người khác"** trên Máy A, chọn Máy B.
4. Máy B sẽ nhận được hộp thoại thông báo rung chuông (TCP). Bấm **Yes**.
5. Cửa sổ thoại hiện ra, lúc này Mic và Loa sẽ bắt đầu truyền qua **UDP**.
   - Nếu bạn thấy âm thanh mượt mà hơn và ít lag hơn so với bản TCP cũ, nghĩa là UDP đang hoạt động tốt.
6. Bấm **"End Call"** để giải phóng tài nguyên.

---

## 3. Lưu ý kỹ thuật cho đề tài
- **Giao thức:** Sử dụng `DatagramSocket` và `DatagramPacket` trong ứng dụng.
- **Audio Format:** PCM Signed 44100Hz, 16 bit, Mono (Tiêu chuẩn chất lượng cao).
- **Buffer:** Kích thước buffer UDP được tối ưu hóa (1024 bytes) để giảm thiểu hiện tượng trễ (Jitter).