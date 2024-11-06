package gemini

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

/**
 * Processes a large text file containing temperature measurements per weather station.
 * Calculates and prints the minimum, mean, and maximum temperature for each station.
 *
 * @author gemini
 */
public class Google_Gemini_TemperatureProcessor {

    public static void main(String[] args) throws IOException {
        // Java version: 17
        String filename = args[0];

        // Read the file in parallel, filtering out comments and storing data in a ConcurrentHashMap
        Map<String, List<Double>> stationTemperatures = new ConcurrentHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            reader.lines().parallel().filter(line -> !line.startsWith("#")).forEach(line -> {
                String[] parts = line.split(";");
                String station = parts[0];
                double temperature = Double.parseDouble(parts[1]);
                stationTemperatures.computeIfAbsent(station, ArrayList::new).add(temperature);
            });
        }

        // Calculate statistics for each station in parallel
        Map<String, String> stationStats = stationTemperatures.entrySet().parallelStream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> {
                            List<Double> temps = entry.getValue();
                            DoubleAdder sum = new DoubleAdder();
                            double min = Double.MAX_VALUE;
                            double max = Double.MIN_VALUE;
                            temps.forEach(t -> {
                                sum.add(t);
                                min = Math.min(min, t);
                                max = Math.max(max, t);
                            });
                            double mean = sum.doubleValue() / temps.size();
                            return String.format("%.1f/%.1f/%.1f", min, mean, max);
                        }
                ));

        // Print the results, sorted alphabetically by station name
        System.out.println(stationStats.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new)));
    }
}
