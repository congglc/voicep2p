package org.example.model;

public class User {

    private String username;
    private String password;
    private String ip;

    // Hàm tạo cũ (dành cho các phần code chưa truyền IP)
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Hàm tạo mới (có truyền IP)
    public User(String username, String password, String ip) {
        this.username = username;
        this.password = password;
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}