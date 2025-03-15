package models;

public class Instance<F, L> {
    private final F input;
    private final L output;

    public Instance(F input, L output) {
        this.input = input;
        this.output = output;
    }

    public F getInput() {
        return input;
    }

    public L getOutput() {
        return output;
    }
}
