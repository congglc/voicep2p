package org.example.service;

import org.example.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private static List<User> users = new ArrayList<>();

    // Giả lập Database đã gán sẵn IP (Nếu test nhiều máy thật, hãy đổi IP này)
    static {
        users.add(new User("user1", "123", "127.0.0.1"));
        users.add(new User("user2", "123", "127.0.0.1"));
        users.add(new User("user3", "123", "127.0.0.1"));
        users.add(new User("user4", "123", "127.0.0.1"));
    }

    public static User login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    // Tra cứu IP bằng Username (Chính xác)
    public static User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    // TÍNH NĂNG MỚI: Tìm kiếm User theo từ khóa (Dùng cho thanh tìm kiếm UI)
    public static List<User> searchUsers(String keyword) {
        List<User> result = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(users); // Trả về tất cả nếu không gõ gì
        }
        for (User u : users) {
            if (u.getUsername().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(u);
            }
        }
        return result;
    }
}