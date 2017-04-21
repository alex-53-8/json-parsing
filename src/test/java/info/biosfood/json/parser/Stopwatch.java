package info.biosfood.json.parser;

public class Stopwatch {

    public static Stopwatch start() {
        return new Stopwatch();
    }

    private final long start = System.currentTimeMillis();
    private long end;

    public Stopwatch stop() {
        end = System.currentTimeMillis();
        return this;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long difference() {
        return getEnd() - getStart();
    }

    public String toString() {
        return new StringBuffer()
                .append("Stopwatch: ")
                .append("{\n")
                .append("  started at: ").append(getStart()).append(",\n")
                .append("  ended at  : ").append(getEnd()).append(",\n")
                .append("  duration  : ").append(difference()).append("\n")
                .append("}\n")
                .toString();
    }

}
