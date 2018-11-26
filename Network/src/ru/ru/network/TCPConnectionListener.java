package ru.ru.network;

import java.io.IOException;

public interface TCPConnectionListener {
    void onConnectionReady(TCPConnection tcpConnection);
    void onReceveString(TCPConnection tcpConnection, String value);
    void onDisconnect (TCPConnection tcpConnection);
    void onException(TCPConnection tcpConnection, IOException e);


}
