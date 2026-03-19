package org.example.client;

import javax.sound.sampled.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class VoiceSender extends Thread {

    private String targetIp;
    private int targetPort;
    private DatagramSocket udpSocket;
    private TargetDataLine microphone;
    private boolean isSending = true;

    public VoiceSender(String targetIp, int targetPort) {
        this.targetIp = targetIp;
        this.targetPort = targetPort;
    }

    public void run() {
        try {
            udpSocket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(targetIp);

            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
            microphone = AudioSystem.getTargetDataLine(format);
            microphone.open(format);
            microphone.start();

            byte[] buffer = new byte[1024]; // Smaller buffer for UDP to reduce latency/jitter

            while (isSending) {
                int count = microphone.read(buffer, 0, buffer.length);
                if (count > 0) {
                    DatagramPacket packet = new DatagramPacket(buffer, count, address, targetPort);
                    udpSocket.send(packet);
                }
            }
        } catch (Exception e) {
            if (isSending) {
                e.printStackTrace();
            }
        } finally {
            stopSending();
        }
    }

    public void stopSending() {
        isSending = false;
        if (microphone != null) {
            microphone.stop();
            microphone.close();
        }
        if (udpSocket != null && !udpSocket.isClosed()) {
            udpSocket.close();
        }
    }
}