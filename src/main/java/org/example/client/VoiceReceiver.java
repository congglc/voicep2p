package org.example.client;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.net.Socket;

public class VoiceReceiver extends Thread {

    private Socket socket;

    public VoiceReceiver(Socket socket){
        this.socket = socket;
    }

    public void run(){

        try{

            AudioFormat format =
                    new AudioFormat(44100,16,1,true,false);

            SourceDataLine speakers =
                    AudioSystem.getSourceDataLine(format);

            speakers.open(format);
            speakers.start();

            InputStream in = socket.getInputStream();

            byte[] buffer = new byte[4096];

            int count;

            while((count = in.read(buffer)) > 0){

                speakers.write(buffer,0,count);

            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

}