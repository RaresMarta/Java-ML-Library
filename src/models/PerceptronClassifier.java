package models;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PerceptronClassifier implements Model<List<Double>, String>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private List<Double> weights;
    private double bias;
    private final double learningRate;

    public PerceptronClassifier(double learningRate) {
        this.learningRate = learningRate;
        this.bias = 0.0;
    }

    @Override
    public void train(List<Instance<List<Double>, String>> instances) {
        int numFeatures = instances.get(0).getInput().size();
        weights = new ArrayList<>(Collections.nCopies(numFeatures, 0.0));

        int threshold = 1000;
        int iteration = 0;
        boolean converged;

        do {
            converged = true;
            for (Instance<List<Double>, String> instance : instances) {
                double prediction = predictRaw(instance.getInput());
                int actual = instance.getOutput().equals("1") ? 1 : -1;

                if (actual * prediction <= 0) {
                    converged = false;
                    for (int i = 0; i < weights.size(); i++) {
                        weights.set(i, weights.get(i) + learningRate * actual * instance.getInput().get(i));
                    }
                    bias += learningRate * actual;
                }
            }
            iteration++;
        } while (!converged && iteration < threshold);

        if (!converged) {
            System.out.println("training stopped after reaching the threshold of iterations.");
        }
    }

    @Override
    public List<String> test(List<Instance<List<Double>, String>> instances) {
        return instances.stream()
                .map(this::predict)
                .collect(Collectors.toList());
    }

    private double predictRaw(List<Double> features) {
        double sum = bias;
        for (int i = 0; i < features.size(); i++) {
            sum += features.get(i) * weights.get(i);
        }
        return sum;
    }

    private String predict(Instance<List<Double>, String> instance) {
        return predictRaw(instance.getInput()) >= 0 ? "1" : "0";
    }
}
