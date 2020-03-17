package org.gauge;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class ExampleReporter {
    // Set the output directory
    private static final String ReportDir = "";
    // Set the output filename
    private static final String ResultFileName = "";

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

    // This method is useful if the plugin plans to write the report output to filesystem.
    // Feel free to delete.
    // The convention is as follows:
    // output is available at $GAUGE_REPORTS_DIR/<report plugin name>
    // As an example, the html-report plugin writes its output to
    //   reports/html-report
    // when $overwrite_reports=false, the reports are not overwritten.
    // As an example, the html-report plugin writes its output to
    //   reports/html-report/<timestamp>
    // The $overwrite_reports env var can be ignored if the plugin does not write to filesystem.

    public static File getTargetFile() {
        String root = System.getenv("GAUGE_PROJECT_ROOT");
        String reportsDir = System.getenv("gauge_reports_dir");

        Path dest = Paths.get(root, reportsDir, ReportDir);
        boolean overwrite;
        try {
            overwrite = Boolean.parseBoolean(System.getenv("overwrite_reports"));
        } catch (Exception e) {
            overwrite = true; //default
        }

        if (!overwrite) {
            // Do not overwrite previous report
            // Gauge convention is that when overwrite=false, generate a report in a timestamped subdirectory
            String subdir = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(new Date());
            dest = dest.resolve(subdir);
        }
        dest.toFile().mkdirs();
        return dest.resolve(ResultFileName).normalize().toFile();
    }

}

