package ru.ru.client;

import ru.ru.network.TCPConnection;
import ru.ru.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {
    private static final String ipAddr = "localhost";
    private static final int port = 8443;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField nickName = new JTextField();
    private final JTextField input = new JTextField();
    private TCPConnection tcpConnection;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEnabled(false);
        log.setLineWrap(true);//Перенос по словам

        add(log, BorderLayout.CENTER);
        add(nickName, BorderLayout.NORTH);

        input.addActionListener(this);
        add(input, BorderLayout.SOUTH);

        try {
            tcpConnection = new TCPConnection(this, ipAddr, port);
        } catch (IOException e) {
            printMsg("TCPConnection exception " + e);
        }
        setVisible(true);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = input.getText();
        if (msg.equals("")) return;
        input.setText(null);
        tcpConnection.sendString(nickName.getText() + " : " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Conection added " + tcpConnection);
    }

    @Override
    public void onReceveString(TCPConnection tcpConnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection close " + tcpConnection);
    }

    @Override
    public void onException(TCPConnection tcpConnection, IOException e) {
        printMsg("TCPConnection " + e);
    }

    private synchronized void printMsg(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
