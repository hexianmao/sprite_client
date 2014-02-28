package com.alibaba.sprite.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

public class Connection {

    private static final int RECV_BUFFER_SIZE = 16 * 1024;
    private static final int SEND_BUFFER_SIZE = 8 * 1024;
    private static final int INPUT_STREAM_BUFFER = 16 * 1024;
    private static final int OUTPUT_STREAM_BUFFER = 8 * 1024;
    private static final int SOCKET_CONNECT_TIMEOUT = 10 * 1000;

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private final AtomicBoolean isClosed;
    private String uid;

    public Connection() {
        this.isClosed = new AtomicBoolean(false);
    }

    public String getUid() {
        return uid;
    }

    public void connect(String host, int port) throws IOException {
        socket = new Socket();
        socket.setTcpNoDelay(true);
        socket.setTrafficClass(0x04 | 0x10);
        socket.setPerformancePreferences(0, 2, 1);
        socket.setReceiveBufferSize(RECV_BUFFER_SIZE);
        socket.setSendBufferSize(SEND_BUFFER_SIZE);
        socket.connect(new InetSocketAddress(host, port), SOCKET_CONNECT_TIMEOUT);
        in = new BufferedInputStream(socket.getInputStream(), INPUT_STREAM_BUFFER);
        out = new BufferedOutputStream(socket.getOutputStream(), OUTPUT_STREAM_BUFFER);

        // read init packet
        AudioPacket packet = receive();
        if (packet.type == AudioPacket.COM_INIT) {
            String uid = UUID.randomUUID().toString();
            packet = new AudioPacket();
            packet.type = AudioPacket.COM_UID;
            packet.data = uid.getBytes();
            send(packet);

            // read ok packet
            packet = receive();
            if (packet.type == AudioPacket.COM_OK) {
                this.uid = uid;
                Log.d("Connection", "connect success");
            } else {
                Log.d("Connection", "connect error");
            }
        } else {
            throw new RuntimeException("connect error");
        }
    }

    public void send(AudioPacket packet) throws IOException {
        packet.write(out);
        out.flush();
    }

    public AudioPacket receive() throws IOException {
        AudioPacket packet = new AudioPacket();
        packet.read(in);
        return packet;
    }

    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("Connection", "close connection error");
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e("Connection", "close connection error");
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("Connection", "close connection error");
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        final Connection con = new Connection();
        con.connect("localhost", 8066);

        AudioPacket packet = new AudioPacket();
        packet.type = AudioPacket.COM_CALL;
        packet.data = con.getUid().getBytes();
        con.send(packet);

        packet = con.receive();
        if (packet.type == AudioPacket.COM_OK) {
            new R(con).start();
            new W(con).start();
        } else {
            con.close();
        }
    }

    static class R extends Thread {
        final Connection c;

        R(Connection c) {
            this.c = c;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    AudioPacket packet = c.receive();
                    System.out.println(packet);
                } catch (IOException e) {
                    c.close();
                }
            }
        }

    }

    static class W extends Thread {
        Connection c;

        W(Connection c) {
            this.c = c;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    AudioPacket packet = new AudioPacket();
                    packet.type = AudioPacket.COM_STREAM;
                    packet.data = UUID.randomUUID().toString().getBytes();
                    c.send(packet);

                    Thread.sleep(1000L);
                } catch (Exception e) {
                    c.close();
                }

            }
        }
    }

}
