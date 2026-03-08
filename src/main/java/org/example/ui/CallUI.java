package org.example.ui;

import org.example.client.VoiceClient;
import org.example.server.VoiceServer;

import java.util.Scanner;

public class CallUI {

    public void start(){

        Scanner scanner = new Scanner(System.in);

        System.out.println("1. Start server");
        System.out.println("2. Call user");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if(choice == 1){

            VoiceServer server = new VoiceServer();
            server.startServer();

        }else{

            VoiceClient client = new VoiceClient();
            client.connect();

        }

    }

}