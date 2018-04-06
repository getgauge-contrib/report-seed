package org.gauge;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.thoughtworks.gauge.Spec;
import com.thoughtworks.gauge.Messages.*;
import com.thoughtworks.gauge.Spec.ProtoItem;
import com.thoughtworks.gauge.Spec.ProtoScenario;
import com.thoughtworks.gauge.Spec.ProtoSpecResult;
import com.thoughtworks.gauge.Spec.ProtoItem.ItemType;

public class CsvReporter {
    private static final String LOCALHOST = "127.0.0.1";
    private static final String ReportDir = "csv-report-example";
    private static final String ResultFileName = "result.csv";

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
                    SuiteExecutionResult result = message.getSuiteExecutionResult();
                    FileWriter writer = new FileWriter(getTargetFile());
                    writer.write("Spec,Scenario,Result,Time Taken\n");
                    for (ProtoSpecResult spec : result.getSuiteResult().getSpecResultsList()) {
                        for (ProtoItem item : spec.getProtoSpec().getItemsList()) {
                            if(item.getItemType() == ItemType.Scenario){
                                ProtoScenario scenario = item.getScenario();
                                String s = spec.getProtoSpec().getSpecHeading() + "," +
                                    scenario.getScenarioHeading() + "," +
                                    scenario.getExecutionStatus().toString() + "," +
                                    scenario.getExecutionTime() + "\n";
                                writer.write(s);
                            }
                        }
                    }
                    writer.flush();
                    writer.close();
                    System.exit(0);
                    return;
                }
                if(message.getMessageType() == Message.MessageType.KillProcessRequest) {
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

