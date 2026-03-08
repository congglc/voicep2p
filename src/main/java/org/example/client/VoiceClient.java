package org.example.client;

import java.net.Socket;
import java.util.Scanner;

public class VoiceClient {

    public void connect(){

        try{

            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter IP: ");

            String ip = scanner.nextLine();

            Socket socket = new Socket(ip,5000);

            System.out.println("Connected to server");

            VoiceSender sender = new VoiceSender(socket);
            VoiceReceiver receiver = new VoiceReceiver(socket);

            sender.start();
            receiver.start();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

}