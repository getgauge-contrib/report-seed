package org.gauge;

import com.thoughtworks.gauge.Spec;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

class CsvBuilder {

    private File getTargetFile() {

        String root = System.getenv("GAUGE_PROJECT_ROOT");
        String reportsDir = System.getenv("gauge_reports_dir");
        String reportDir = "csv-report-example";
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
        String resultFileName = "result.csv";
        return dest.resolve(resultFileName).normalize().toFile();
    }

    String buildFrom(Spec.ProtoSuiteResult suiteResult) throws IOException {
        File targetFile = getTargetFile();
        FileWriter writer = new FileWriter(targetFile);
        writer.write("Spec,Scenario,Result,Time Taken\n");
        for (Spec.ProtoSpecResult spec : suiteResult.getSpecResultsList()) {
            for (Spec.ProtoItem item : spec.getProtoSpec().getItemsList()) {
                if (item.getItemType() == Spec.ProtoItem.ItemType.Scenario) {
                    Spec.ProtoScenario scenario = item.getScenario();
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
        return targetFile.toString();
    }
}
