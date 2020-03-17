package org.gauge;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.util.concurrent.Executors;

public class CsvReporter {
    public static void main(String[] args) throws Exception {
        ServiceHandler handler = new ServiceHandler();
        Server server = ServerBuilder.forPort(0)
                .addService(handler)
                .executor(Executors.newSingleThreadExecutor())
                .build();
        handler.addServer(server);
        server.start();
        int port = server.getPort();
        System.out.println("Listening on port:" + port);
        server.awaitTermination();
    }

}

