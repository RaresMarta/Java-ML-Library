package models;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


public class KNNClassifier implements Model<List<Double>, String>, Serializable {
    private static final long serialVersionUID = 1L;
    private int k;
    private List<Instance<List<Double>, String>> trainingData;

    public KNNClassifier(int k) {
        this.k = k;
        this.trainingData = new ArrayList<>();
    }

    @Override
    public void train(List<Instance<List<Double>, String>> instances) {
        this.trainingData.addAll(instances);
    }

    @Override
    public List<String> test(List<Instance<List<Double>, String>> instances) {
        List<String> predictions = new ArrayList<>();

        for (Instance<List<Double>, String> instance : instances) {
            predictions.add(predict(instance));
        }
        return predictions;
    }

    private String predict(Instance<List<Double>, String> instance) {
        return trainingData.stream()
                .sorted(Comparator.comparingDouble(i -> calculateDistance(i.getInput(), instance.getInput())))
                .limit(k)
                .collect(Collectors.groupingBy(Instance::getOutput, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private double calculateDistance(List<Double> point1, List<Double> point2) {
        if (point1.size() != point2.size()) {
            throw new IllegalArgumentException("Point dimensions must match");
        }
        double sum = 0;
        for (int i = 0; i < point1.size(); i++) {
            sum += Math.pow(point1.get(i) - point2.get(i), 2);
        }
        return Math.sqrt(sum);
    }
}