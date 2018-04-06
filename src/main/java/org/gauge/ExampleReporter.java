package org.gauge;

import java.io.IOException;
import java.net.Socket;
import com.thoughtworks.gauge.Messages.*;

public class ExampleReporter {
    private static final String LOCALHOST = "127.0.0.1";

    public static void main(String[] args){
        String portEnv = System.getenv("plugin_connection_port");
        int port = Integer.parseInt(portEnv);
        Socket socket;
        while (true) {
            try {
                socket = new Socket(LOCALHOST, port);
                break;
            } catch (Exception ignored) {
            }
        }
        
        try {
            while (!socket.isClosed() && socket.isConnected()) {
                Message message = Message.parseDelimitedFrom(socket.getInputStream());
                if(message.getMessageType() == Message.MessageType.SuiteExecutionResult) {
                    // The result is now available example:
                    // SuiteExecutionResult result = message.getSuiteExecutionResult();
                    System.out.println("Execution Result received.");
                    System.exit(0);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

