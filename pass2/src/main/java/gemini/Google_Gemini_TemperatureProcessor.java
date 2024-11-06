package gemini;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
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
                stationTemperatures.computeIfAbsent(station, k -> new ArrayList<>()).add(temperature);
            });
        }

        // Calculate statistics for each station in parallel
        Map<String, String> stationStats = stationTemperatures.entrySet().parallelStream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            List<Double> temps = entry.getValue();
                            DoubleAdder sum = new DoubleAdder();
                            AtomicReference<Double> min = new AtomicReference<>(Double.MAX_VALUE);
                            AtomicReference<Double> max = new AtomicReference<>(Double.MIN_VALUE);
                            temps.forEach(t -> {
                                sum.add(t);
                                min.set(Math.min(min.get(), t));
                                max.set(Math.max(max.get(), t));
                            });
                            double mean = sum.doubleValue() / temps.size();
                            return String.format("%.1f/%.1f/%.1f", min, mean, max);
                        }
                ));


        // Print the results, sorted alphabetically by station name
        Map<String, String> sortedStats = stationStats.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
        (a, b) -> a, LinkedHashMap::new));
        System.out.println(sortedStats);

    }
}