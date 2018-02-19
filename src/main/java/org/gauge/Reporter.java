package org.gauge;

import com.google.protobuf.CodedInputStream;
import gauge.messages.Messages;

import java.io.ByteArrayOutputStream;
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
            dispatcher.accept(Messages.Message.parseFrom(toBytes(getMessageLength(inputStream))));
        }
    }

    private static boolean isConnected(Socket socket) {
        return !socket.isClosed() && socket.isConnected();
    }

    private static Socket connectToGauge() {
        String portEnv = System.getenv("PLUGIN_CONNECTION_PORT");
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

    private static byte[] toBytes(MessageLength messageLength) throws IOException {
        long messageSize = messageLength.getLength();
        CodedInputStream stream = messageLength.getRemainingStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 0; i < messageSize; i++) {
            outputStream.write(stream.readRawByte());
        }

        return outputStream.toByteArray();
    }

    private static MessageLength getMessageLength(InputStream is) throws IOException {
        CodedInputStream codedInputStream = CodedInputStream.newInstance(is);
        long size = codedInputStream.readRawVarint64();
        return new MessageLength(size, codedInputStream);
    }
}

