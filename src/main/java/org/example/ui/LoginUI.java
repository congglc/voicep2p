package org.example.ui;

import org.example.model.User;
import org.example.service.UserService;

import java.util.Scanner;

public class LoginUI {

    public void start(){

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== LOGIN ===");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = UserService.login(username,password);

        if(user != null){

            System.out.println("Login success!");

            CallUI callUI = new CallUI();
            callUI.start();

        }else{

            System.out.println("Login failed");

        }

    }

}