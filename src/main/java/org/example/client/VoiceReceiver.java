package org.example.client;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.net.Socket;

public class VoiceReceiver extends Thread {

    private Socket socket;
    private SourceDataLine speakers;
    private boolean isReceiving = true;

    public VoiceReceiver(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try{
            AudioFormat format = new AudioFormat(44100,16,1,true,false);
            speakers = AudioSystem.getSourceDataLine(format);

            speakers.open(format);
            speakers.start();

            InputStream in = socket.getInputStream();
            byte[] buffer = new byte[4096];
            int count;

            while(isReceiving && !socket.isClosed() && (count = in.read(buffer)) > 0){
                speakers.write(buffer, 0, count);
            }
        } catch(Exception e){
            if(isReceiving) {
                e.printStackTrace();
            }
        } finally {
            if(speakers != null) {
                speakers.stop();
                speakers.close();
            }
        }
    }

    public void stopReceiving() {
        isReceiving = false;
        if(speakers != null) {
            speakers.stop();
            speakers.close();
        }
    }
}