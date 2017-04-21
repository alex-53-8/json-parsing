package info.biosfood.json.parser;

import org.apache.log4j.Logger;

import java.text.NumberFormat;

public class Metrics {

    public static final Logger LOG = Logger.getLogger(Metrics.class);

    public static void printMemory(String beforeMainText) {
        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append(beforeMainText).append(" {\n");
        sb.append("  free memory      : ").append(format.format(freeMemory / 1024)).append("\n");
        sb.append("  allocated memory : ").append(format.format(allocatedMemory / 1024)).append("\n");
        sb.append("  max memory       : ").append(format.format(maxMemory / 1024)).append("\n");
        sb.append("  total free memory: ").append(format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024)).append("\n");
        sb.append("}\n");

        LOG.debug(sb.toString());
    }

}
