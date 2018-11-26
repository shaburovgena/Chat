package ru.ru.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThread;
    private final TCPConnectionListener tcpConnectionListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection (TCPConnectionListener tcpConnectionListener, String ipAdd, int port) throws  IOException{
        this(tcpConnectionListener, new Socket(ipAdd, port));
    }

    public TCPConnection(TCPConnectionListener tcpConnectionListener, Socket socket) throws IOException {
        this.tcpConnectionListener = tcpConnectionListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tcpConnectionListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()) {
                        tcpConnectionListener.onReceveString(TCPConnection.this, in.readLine());
                    }
                } catch (Exception e) {

                } finally {

                }
            }
        });
        rxThread.start();
    }

    public synchronized void sendString(String value) {
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            tcpConnectionListener.onException(TCPConnection.this, e);
            disconnect();
        }

    }

    public synchronized void disconnect() {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            tcpConnectionListener.onException(TCPConnection.this, e);
        }

    }

    @Override
    public String toString() {
        return "TCPConnection" + socket.getInetAddress() + "  " + socket.getPort();
    }
}
