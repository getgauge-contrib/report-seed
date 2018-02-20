package org.gauge;

import gauge.messages.Messages;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.function.Consumer;

public class Reporter {
    private static final String LOCALHOST = "localhost";

    public static void main(String[] args){
        Socket gaugeSocket = connectToGauge();
        try {
            listenForMessages(gaugeSocket, dispatcher());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void listenForMessages(Socket socket, Consumer<Messages.Message> dispatcher) throws IOException {
        InputStream inputStream = socket.getInputStream();
        while (isConnected(socket)) {
            dispatcher.accept(Messages.Message.parseFrom(MessageLength.FromInputStream(inputStream).toBytes()));
        }
    }

    private static boolean isConnected(Socket socket) {
        return !socket.isClosed() && socket.isConnected();
    }

    private static Socket connectToGauge() {
        String portEnv = System.getenv("plugin_connection_port");
        int port = Integer.parseInt(portEnv);
        Socket clientSocket;
        while (true) {
            try {
                clientSocket = new Socket(LOCALHOST, port);
                break;
            } catch (Exception ignored) {
            }
        }

        return clientSocket;
    }

    private static Consumer<Messages.Message> dispatcher() {
        return (message -> {
            if (message.getMessageType() == Messages.Message.MessageType.SuiteExecutionResult) {
                System.out.println(" IS NOW DISPATCHING " + message.getMessageType());
            }
        });
    }
}

