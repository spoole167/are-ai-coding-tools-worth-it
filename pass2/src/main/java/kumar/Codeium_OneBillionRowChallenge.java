// Java 17
package kumar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * This program reads a text file containing temperature measurements from various weather stations,
 * calculates the minimum, mean, and maximum temperature for each station, and prints the results
 * in alphabetical order.
 *
 * @author Kumar
 */
public class Codeium_OneBillionRowChallenge {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java OneBillionRowChallenge <input_file>");
            return;
        }

        String inputFile = args[0];

        // Create a map to store the temperature statistics for each station
        Map<String, TemperatureStats> temperatureStats = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignore comments
                if (line.startsWith("#")) {
                    continue;
                }

                // Split the line into station name and temperature value
                String[] parts = line.split(";");
                if (parts.length != 2) {
                    System.err.println("Invalid input format: " + line);
                    continue;
                }

                String station = parts[0];
                try {
                    double temperature = Double.parseDouble(parts[1]);
                    // Update the temperature statistics for the station
                    temperatureStats.computeIfAbsent(station, k -> new TemperatureStats())
                            .update(temperature);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid temperature value: " + parts[1]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        // Sort the temperature statistics alphabetically by station name
        TreeMap<String, TemperatureStats> sortedTemperatureStats = new TreeMap<>(temperatureStats);

        // Build the output string
        StringBuilder output = new StringBuilder();
        output.append('{');
        for (Map.Entry<String, TemperatureStats> entry : sortedTemperatureStats.entrySet()) {
            output.append(entry.getKey()).append('=').append(entry.getValue());
            output.append(", ");
        }
        output.delete(output.length() - 2, output.length());
        output.append('}');

        System.out.println(output);
    }

    /**
     * This class represents the temperature statistics for a weather station.
     */
    static class TemperatureStats {
        private double min;
        private double sum;
        private double max;
        private int count;

        /**
         * Updates the temperature statistics with a new temperature value.
         *
         * @param temperature the new temperature value
         */
        public void update(double temperature) {
            if (count == 0 || temperature < min) {
                min = temperature;
            }
            if (temperature > max) {
                max = temperature;
            }
            sum += temperature;
            count++;
        }

        @Override
        public String toString() {
            return String.format("%.1f/%.1f/%.1f", min, sum / count, max);
        }
    }
}
