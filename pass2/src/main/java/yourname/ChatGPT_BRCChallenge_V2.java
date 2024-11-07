package yourname;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

/**
 * A Java program to retrieve temperature measurements from a large text file (1 billion rows),
 * calculating min, mean, and max temperatures per weather station.
 *
 * @version Java 17 or later
 * To compile: javac BRCChallenge.java
 * To run: java yourname.BRCChallenge <filename>
 */
public class ChatGPT_BRCChallenge_V2 {

    // Data structure to hold summary stats per station
    private static final Map<String, StationStatistics> stationData = new ConcurrentHashMap<>();

    public static void main(String[] args) {


        String filename = args[0];

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            reader.lines()
                    .parallel() // Use parallel stream to process the file quickly
                    .filter(line -> !line.startsWith("#")) // Skip comment lines
                    .forEach(ChatGPT_BRCChallenge_V2::processLine);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Output the results, sorted alphabetically by station name
        String result = stationData.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> String.format("%s=%.1f/%.1f/%.1f",
                        entry.getKey(),
                        entry.getValue().getMin(),
                        entry.getValue().getMean(),
                        entry.getValue().getMax()))
                .collect(Collectors.joining(", ", "{", "}"));

       if(args.length==1) System.out.println(result);
    }

    /**
     * Processes a single line of input, extracting the station name and temperature,
     * and updates the statistics for that station.
     * @param line the line of input
     */
    private static void processLine(String line) {
        String[] parts = line.split(";");
        if (parts.length != 2) return; // Ignore malformed lines

        String station = parts[0].trim();
        double temperature;
        try {
            temperature = Double.parseDouble(parts[1].trim());
        } catch (NumberFormatException e) {
            return; // Ignore malformed temperature values
        }

        stationData.computeIfAbsent(station, k -> new StationStatistics())
                .update(temperature);
    }

    /**
     * Helper class to hold statistics for each station.
     * Uses DoubleAdder for efficient summing in a multithreaded context.
     */
    private static class StationStatistics {
        private double min = Double.MAX_VALUE;
        private double max = Double.MIN_VALUE;
        private final DoubleAdder sum = new DoubleAdder();
        private final AtomicLong count = new AtomicLong();

        public synchronized void update(double temperature) {
            min = Math.min(min, temperature);
            max = Math.max(max, temperature);
            sum.add(temperature);
            count.incrementAndGet();
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public double getMean() {
            return sum.sum() / count.get();
        }
    }
}
