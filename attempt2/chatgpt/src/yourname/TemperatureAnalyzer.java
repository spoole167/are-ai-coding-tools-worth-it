package yourname; // Replace with your actual name

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Java 17+
 * Program to retrieve temperature measurement values from a large text file and calculate
 * min, mean, and max temperature values per weather station.
 * 
 * The input file name is provided as the first argument on the command line.
 * Records beginning with '#' are treated as comments and ignored.
 * 
 * Usage: java yourname.TemperatureAnalyzer <input_file_path>
 */
public class TemperatureAnalyzer {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java yourname.TemperatureAnalyzer <input_file_path>");
            System.exit(1);
        }

        String fileName = args[0];
        TemperatureAnalyzer analyzer = new TemperatureAnalyzer();
        Map<String, DoubleSummaryStatistics> result = analyzer.processFile(fileName);

        result.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                String station = entry.getKey();
                DoubleSummaryStatistics stats = entry.getValue();
                System.out.printf("%s=%.1f/%.1f/%.1f%n", station, stats.getMin(), stats.getAverage(), stats.getMax());
            });
    }

    /**
     * Processes the input file and calculates temperature statistics per weather station.
     * @param fileName the path to the input file
     * @return a map with weather station names and their temperature statistics
     */
    public Map<String, DoubleSummaryStatistics> processFile(String fileName) {
        Map<String, DoubleSummaryStatistics> stationStats = new ConcurrentHashMap<>();

        try (FileChannel fileChannel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ)) {
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());

            ForkJoinPool pool = new ForkJoinPool(); // Parallel processing
            pool.invoke(new ProcessTask(buffer, stationStats));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stationStats;
    }

    /**
     * Task for processing file contents in parallel.
     */
    private static class ProcessTask extends RecursiveTask<Void> {
        private static final int THRESHOLD = 1_000_000; // Tune based on testing
        private final MappedByteBuffer buffer;
        private final Map<String, DoubleSummaryStatistics> stationStats;

        ProcessTask(MappedByteBuffer buffer, Map<String, DoubleSummaryStatistics> stationStats) {
            this.buffer = buffer;
            this.stationStats = stationStats;
        }

        @Override
        protected Void compute() {
            if (buffer.remaining() <= THRESHOLD) {
                processBuffer(buffer);
                return null;
            }

            int mid = buffer.position() + buffer.remaining() / 2;
            buffer.position(mid);
            buffer.mark(); // Mark the split point for the second half

            ProcessTask task1 = new ProcessTask((MappedByteBuffer) buffer.duplicate().flip(), stationStats);
            ProcessTask task2 = new ProcessTask((MappedByteBuffer) buffer.reset(), stationStats);

            invokeAll(task1, task2);
            return null;
        }

        private void processBuffer(MappedByteBuffer localBuffer) {
            StringBuilder currentLine = new StringBuilder();
            while (localBuffer.hasRemaining()) {
                char c = (char) localBuffer.get();
                if (c == '\n') {
                    processLine(currentLine.toString());
                    currentLine.setLength(0); // Reset the line buffer
                } else {
                    currentLine.append(c);
                }
            }
            if (currentLine.length() > 0) {
                processLine(currentLine.toString());
            }
        }

        private void processLine(String line) {
            if (line.startsWith("#") || line.isBlank()) return;

            String[] parts = line.split(";");
            if (parts.length != 2) return;

            String station = parts[0].trim();
            double temperature;
            try {
                temperature = Double.parseDouble(parts[1].trim());
            } catch (NumberFormatException e) {
                return; // Skip invalid entries
            }

            stationStats.computeIfAbsent(station, k -> new DoubleSummaryStatistics())
                        .accept(temperature);
        }
    }
}
