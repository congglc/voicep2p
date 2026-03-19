package org.example.client;

import javax.sound.sampled.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class VoiceReceiver extends Thread {

    private int localPort;
    private DatagramSocket udpSocket;
    private SourceDataLine speakers;
    private boolean isReceiving = true;

    public VoiceReceiver(int localPort) {
        this.localPort = localPort;
    }

    public void run() {
        try {
            udpSocket = new DatagramSocket(localPort);

            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
            speakers = AudioSystem.getSourceDataLine(format);
            speakers.open(format);
            speakers.start();

            byte[] buffer = new byte[1024];

            while (isReceiving) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(packet);
                speakers.write(packet.getData(), 0, packet.getLength());
            }
        } catch (Exception e) {
            if (isReceiving) {
                e.printStackTrace();
            }
        } finally {
            stopReceiving();
        }
    }

    public void stopReceiving() {
        isReceiving = false;
        if (speakers != null) {
            speakers.stop();
            speakers.close();
        }
        if (udpSocket != null && !udpSocket.isClosed()) {
            udpSocket.close();
        }
    }
}