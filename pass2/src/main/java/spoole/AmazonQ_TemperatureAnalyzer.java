package spoole;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class implements a solution for the 1 Billion Row Challenge (1BRC).
 * It reads temperature measurements from a large text file and calculates
 * the minimum, mean, and maximum temperatures for each weather station.
 *
 * @author spoole
 * @version 1.0
 */
public class AmazonQ_TemperatureAnalyzer {

    /**
     * Main method to run the temperature analysis.
     *
     * @param args Command line arguments. The first argument should be the path to the input file.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide the input file path as an argument.");
            return;
        }

        String filePath = args[0];
        Map<String, TemperatureStats> stationStats = new TreeMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#")) { // Ignore comments
                    processLine(line, stationStats);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return;
        }

        // Print results
        System.out.println(formatResults(stationStats));
    }

    /**
     * Processes a single line from the input file and updates the statistics.
     *
     * @param line The input line containing station name and temperature.
     * @param stationStats The map to store temperature statistics for each station.
     */
    private static void processLine(String line, Map<String, TemperatureStats> stationStats) {
        String[] parts = line.split(";");
        if (parts.length == 2) {
            String station = parts[0];
            double temperature = Double.parseDouble(parts[1]);
            stationStats.computeIfAbsent(station, k -> new TemperatureStats())
                    .addMeasurement(temperature);
        }
    }

    /**
     * Formats the results as a string in the required output format.
     *
     * @param stationStats The map containing temperature statistics for each station.
     * @return A formatted string with the results.
     */
    private static String formatResults(Map<String, TemperatureStats> stationStats) {
        StringBuilder result = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, TemperatureStats> entry : stationStats.entrySet()) {
            if (!first) {
                result.append(", ");
            }
            result.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        result.append("}");
        return result.toString();
    }

    /**
     * Inner class to store and calculate temperature statistics for a station.
     */
    private static class TemperatureStats {
        private double min = Double.MAX_VALUE;
        private double max = Double.MIN_VALUE;
        private double sum = 0;
        private long count = 0;

        /**
         * Adds a new temperature measurement and updates the statistics.
         *
         * @param temperature The temperature measurement to add.
         */
        public void addMeasurement(double temperature) {
            min = Math.min(min, temperature);
            max = Math.max(max, temperature);
            sum += temperature;
            count++;
        }

        /**
         * Returns a string representation of the temperature statistics.
         *
         * @return A string in the format "min/mean/max".
         */
        @Override
        public String toString() {
            return String.format("%.1f/%.1f/%.1f", min, sum / count, max);
        }
    }
}
