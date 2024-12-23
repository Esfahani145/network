package org.example.serverclient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private DefaultListModel<String> onlineUsersModel;
    private JList<String> onlineUsersList;

    public Client(String address, int port) {
        try {
            socket = new Socket(address, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            initUI();
            new Thread(new IncomingMessagesHandler()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        frame = new JFrame("Backgammon Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        textField = new JTextField();
        panel.add(textField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        panel.add(sendButton, BorderLayout.EAST);

        onlineUsersModel = new DefaultListModel<>();
        onlineUsersList = new JList<>(onlineUsersModel);
        onlineUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        onlineUsersList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedUser = onlineUsersList.getSelectedValue();
                if (selectedUser != null) {
                    out.println("start " + selectedUser);
                }
            }
        });
        frame.add(new JScrollPane(onlineUsersList), BorderLayout.WEST);

        frame.add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void sendMessage() {
        String message = textField.getText();
        if (!message.isEmpty()) {
            out.println(message);
            textField.setText("");
        }
    }

    private class IncomingMessagesHandler implements Runnable {
        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("Online Users:")) {
                        updateOnlineUsers(message);
                    } else if (message.startsWith("Initial Board:") || message.startsWith("Updated board status:")) {
                        textArea.setText("");
                        textArea.append(message.substring(message.indexOf('\n') + 1) + "\n");
                    } else {
                        textArea.append(message + "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void updateOnlineUsers(String message) {
            onlineUsersModel.clear();
            String[] users = message.substring(14).split(" ");
            for (String user : users) {
                if (!user.isEmpty()) {
                    onlineUsersModel.addElement(user);
                }
            }
        }
    }

    public static void main(String[] args) {
        new Client("localhost", 12345);
    }
}
