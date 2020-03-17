package org.gauge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.gauge.Messages;
import com.thoughtworks.gauge.Spec;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;


public class JsonBuilder {


    String buildFrom(Spec.ProtoSuiteResult suiteResult) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File targetFile = getTargetFile();
        FileWriter writer = new FileWriter(targetFile);
        gson.toJson(suiteResult, writer);
        writer.flush();
        writer.close();
        return targetFile.toString();
    }

    private File getTargetFile() {

        String root = System.getenv("GAUGE_PROJECT_ROOT");
        String reportsDir = System.getenv("gauge_reports_dir");
        String reportDir = "json-report-example";
        Path dest = Paths.get(root, reportsDir, reportDir);
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
        String resultFileName = "result.json";

        return dest.resolve(resultFileName).normalize().toFile();
    }
}
