package yourname;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Java version: 17
 * Program to retrieve temperature values from a text file, then calculate and print min, mean, and max temperatures per weather station.
 */
public class TemperatureProcessor {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide the file name as an argument.");
            return;
        }
        String fileName = args[0];
        ConcurrentHashMap<String, List<Double>> temperatureData = new ConcurrentHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.lines().parallel().forEach(line -> {
                if (line.startsWith("#")) return; // Ignore comments
                String[] parts = line.split(";");
                if (parts.length != 2) return;
                String station = parts[0].trim();
                double temp;
                try {
                    temp = Double.parseDouble(parts[1].trim());
                } catch (NumberFormatException e) {
                    return; // Skip malformed lines
                }
                temperatureData.computeIfAbsent(station, k -> Collections.synchronizedList(new ArrayList<>())).add(temp);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> result = temperatureData.entrySet().parallelStream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            List<Double> temps = entry.getValue();
                            double min = temps.stream().min(Double::compare).orElse(Double.NaN);
                            double max = temps.stream().max(Double::compare).orElse(Double.NaN);
                            double mean = temps.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
                            return String.format("%.1f/%.1f/%.1f", min, mean, max);
                        }
                ));

        result.keySet().stream().sorted().forEach(key -> System.out.println(key + "=" + result.get(key)));
    }
}
