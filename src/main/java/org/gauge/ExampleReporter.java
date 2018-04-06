package org.gauge;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.thoughtworks.gauge.Messages.*;

public class ExampleReporter {
    private static final String LOCALHOST = "127.0.0.1";
    // Set the output directory
    private static final String ReportDir = "";
    // Set the output filename
    private static final String ResultFileName = "";

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
                if(message.getMessageType() == Message.MessageType.KillProcessRequest) {
                    // gauge signalled the plugin to kill itself gracefully
                    // cleanup any resources and exit
                    if(!socket.isClosed()) {
                        socket.close();
                    }
                    System.exit(0);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        Boolean overwrite;
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

