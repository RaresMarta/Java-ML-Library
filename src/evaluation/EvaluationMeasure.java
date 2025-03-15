package evaluation;

import models.Instance;
import java.util.List;

public interface EvaluationMeasure<F, L> {
    double evaluate(List<Instance<F, L>> instances, List<L> predictions);
}