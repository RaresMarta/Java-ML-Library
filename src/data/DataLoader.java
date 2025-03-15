package data;

import models.Instance;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {
    public static List<Instance<List<Double>, String>> loadFromCSV(String filePath) throws IOException {
        List<Instance<List<Double>, String>> instances = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                List<Double> features = new ArrayList<>();
                for (int i = 0; i < parts.length - 1; i++) {
                    features.add(Double.parseDouble(parts[i]));
                }
                String label = parts[parts.length - 1];
                instances.add(new Instance<>(features, label));
            }
        }
        return instances;
    }
}
