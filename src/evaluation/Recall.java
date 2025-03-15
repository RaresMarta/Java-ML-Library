package evaluation;

import models.Instance;
import java.util.List;

public class Recall<F, L> implements EvaluationMeasure<F, L> {
    @Override
    public double evaluate(List<Instance<F, L>> instances, List<L> predictions) {
        int truePositives = 0;
        int falseNegatives = 0;

        for (int i = 0; i < instances.size(); i++) {
            if (predictions.get(i).equals(instances.get(i).getOutput())) {
                if (predictions.get(i).equals("1")) {
                    truePositives++;
                }
            } else {
                if (instances.get(i).getOutput().equals("1")) {
                    falseNegatives++;
                }
            }
        }

        if (truePositives + falseNegatives == 0) {
            return 0.0;
        }

        return (double) truePositives / (truePositives + falseNegatives);
    }
}
