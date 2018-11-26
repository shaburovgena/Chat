package ru.ru.server;

import ru.ru.network.TCPConnection;
import ru.ru.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {

    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server started");
        try (ServerSocket serverSocket = new ServerSocket(8443)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (Exception e) {
                    System.out.println("Exception " + e);
                }
            }
        } catch (IOException e) {

        }


    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnections("Client connected " + tcpConnection);

    }

    @Override
    public synchronized void onReceveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Client disconnected " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, IOException e) {
        System.out.println("TCPException " + e);
    }

    private void sendToAllConnections(String value) {
        System.out.println(value);
        final int count = connections.size();
        for (int i = 0; i < count; i++) connections.get(i).sendString(value);
    }
}
