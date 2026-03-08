package org.example.client;

import javax.sound.sampled.*;
import java.io.OutputStream;
import java.net.Socket;

public class VoiceSender extends Thread {

    private Socket socket;
    private TargetDataLine microphone;
    private boolean isSending = true;

    public VoiceSender(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try{
            AudioFormat format = new AudioFormat(44100,16,1,true,false);
            microphone = AudioSystem.getTargetDataLine(format);

            microphone.open(format);
            microphone.start();

            OutputStream out = socket.getOutputStream();
            byte[] buffer = new byte[4096];

            while(isSending && !socket.isClosed()){
                int count = microphone.read(buffer, 0, buffer.length);
                if(count > 0){
                    out.write(buffer, 0, count);
                }
            }
        } catch(Exception e) {
            if(isSending) {
                e.printStackTrace();
            }
        } finally {
            if(microphone != null) {
                microphone.stop();
                microphone.close();
            }
        }
    }

    public void stopSending() {
        isSending = false;
        if(microphone != null) {
            microphone.stop();
            microphone.close();
        }
    }
}