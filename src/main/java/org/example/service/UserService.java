package org.example.service;

import org.example.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private static List<User> users = new ArrayList<>();

    static {

        users.add(new User("user1","123"));
        users.add(new User("user2","123"));

    }

    public static User login(String username,String password){

        for(User u : users){

            if(u.getUsername().equals(username) &&
                    u.getPassword().equals(password)){
                return u;
            }

        }

        return null;

    }

}