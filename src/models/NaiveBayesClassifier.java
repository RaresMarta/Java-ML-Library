package models;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class NaiveBayesClassifier implements Model<List<Double>, String>, Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Double> classProbabilities;
    private Map<String, List<Double>> featureProbabilities;

    public NaiveBayesClassifier() {
        this.classProbabilities = new HashMap<>();
        this.featureProbabilities = new HashMap<>();
    }

    @Override
    public void train(List<Instance<List<Double>, String>> instances) {
        Map<String, Long> classCounts = instances.stream()
                .collect(Collectors.groupingBy(Instance::getOutput, Collectors.counting()));

        int totalInstances = instances.size();

        for (String classLabel : classCounts.keySet()) {
            classProbabilities.put(classLabel, (double) classCounts.get(classLabel) / totalInstances);

            List<List<Double>> classFeatures = instances.stream()
                    .filter(instance -> instance.getOutput().equals(classLabel))
                    .map(Instance::getInput)
                    .collect(Collectors.toList());

            List<Double> featureMeans = new ArrayList<>();
            for (int i = 0; i < classFeatures.get(0).size(); i++) {
                int atIndex = i;
                double mean = classFeatures.stream().mapToDouble(f -> f.get(atIndex)).average().orElse(0.0);
                featureMeans.add(mean);
            }
            featureProbabilities.put(classLabel, featureMeans);
        }
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
        double maxProbability = Double.NEGATIVE_INFINITY;
        String bestClass = null;

        for (String classLabel : classProbabilities.keySet()) {
            double probability = Math.log(classProbabilities.get(classLabel));
            List<Double> featureMeans = featureProbabilities.get(classLabel);

            for (int i = 0; i < instance.getInput().size(); i++) {
                probability += Math.log(1 / (Math.sqrt(2 * Math.PI))) -
                        (Math.pow(instance.getInput().get(i) - featureMeans.get(i), 2) / 2);
            }

            if (probability > maxProbability) {
                maxProbability = probability;
                bestClass = classLabel;
            }
        }

        return bestClass;
    }
}
