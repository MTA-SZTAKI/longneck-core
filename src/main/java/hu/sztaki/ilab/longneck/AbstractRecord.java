package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.Kernel.KernelState;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import java.util.*;

/**
 * A simple record implementation with providing basic functionality.
 * 
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
abstract public class AbstractRecord implements Record {

    /** Record history stack. */
    protected Stack<Map<String,Field>> history;
    /** Fields in the record. */
    protected Map<String, Field> fields;
    /** The saved kernel state. */
    protected KernelState kernelState;
    /** The list of constraint failures that occurred during processing. */
    protected List<CheckResult> constraintFailures;

    protected Stack<Integer> constraintFailureHistory;

    public AbstractRecord() {
        fields = new HashMap<String, Field>();
        constraintFailures = new ArrayList<CheckResult>();
        constraintFailureHistory = new Stack<Integer>();
    }

    @Override
    public Map<String, Field> getFields() {
        return fields;
    }

    @Override
    public void setFields(Map<String, Field> fields) {
        this.fields = fields;
    }

    @Override
    public boolean has(String fieldName) {
        return fields.containsKey(fieldName);
    }

    @Override
    public void add(Field field) {
        fields.put(field.getName(), field);
    }

    @Override
    public void remove(String fieldName) {
        fields.remove(fieldName);
    }

    @Override
    public Field get(String fieldName) {
        return fields.get(fieldName);
    }

    @Override
    public void saveState() {
        Map<String,Field> save = new HashMap<String,Field>();

        // Copy fields
        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            Field f = new Field(entry.getValue());
            save.put(entry.getKey(), f);
        }

        // Create history if needed
        if (history == null) {
            history = new Stack<Map<String, Field>>();
        }

        // Push current stack size
        history.push(save);
        constraintFailureHistory.push(constraintFailures.size());
    }

    @Override
    public void restoreState() {
        fields = history.pop();
        constraintFailures = constraintFailures.subList(0, constraintFailureHistory.pop());
    }

    @Override
    public void removeState() {
        history.pop();
        constraintFailureHistory.pop();
    }

    @Override
    public void clearHistory() {
        history.empty();
        constraintFailureHistory.empty();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{ ");
        for (Map.Entry f : fields.entrySet()) {
            result.append(f.getValue().toString());
            result.append(", ");
        }
        result.append("}");

        return result.toString();
    }

    @Override
    public List<CheckResult> getErrors() {
        return constraintFailures;
    }

    @Override
    public KernelState getKernelState() {
        return kernelState;
    }

    @Override
    public void setKernelState(KernelState kernelState) {
        this.kernelState = kernelState;
    }
}
