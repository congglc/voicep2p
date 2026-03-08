package org.example.server;

import org.example.client.VoiceReceiver;
import org.example.client.VoiceSender;

import java.net.ServerSocket;
import java.net.Socket;

public class VoiceServer {

    public void startServer(){

        try{

            ServerSocket serverSocket = new ServerSocket(5000);

            System.out.println("Waiting for call...");

            Socket socket = serverSocket.accept();

            System.out.println("Connected!");

            VoiceSender sender = new VoiceSender(socket);
            VoiceReceiver receiver = new VoiceReceiver(socket);

            sender.start();
            receiver.start();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

}