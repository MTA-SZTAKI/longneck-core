package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.Kernel.KernelState;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public interface Record {

    /**
     * Adds a field to the record.
     * 
     * @param field The field to add.
     */
    void add(Field field);

    /**
     * Clears record history.
     * 
     * Removes all history entries from the record.
     */
    void clearHistory();

    /**
     * Returns the specified field.
     * 
     * @param fieldName The name of the field.
     * @return The field specified or null, if not found.
     */
    Field get(String fieldName);

    /**
     * Returns all fields as a map.
     * 
     * @return The fields contained by this record.
     */
    Map<String, Field> getFields();

    /**
     * Checks, if this record has the specified field.
     * 
     * @param fieldName The name of the field to check.
     * @return True, if the record has the specified field.
     */
    boolean has(String fieldName);

    /**
     * Removes the specified field.
     * 
     * @param fieldName The name of the field to remove.
     */
    void remove(String fieldName);

    /**
     * Removes the last added historic state.
     * 
     * This method removes the historic state that was added last.
     */
    void removeState();

    /**
     * Restores the last historic state.
     * 
     * This method restores the fields to the state of the last historic state and removes that
     * state from history.
     */
    void restoreState();

    /**
     * Saves the current state into history.
     * 
     * Creates a new historic state with the current fields
     */
    void saveState();

    /**
     * Sets all fields from the specified map.
     * 
     * @param fields The fields to set.
     */
    void setFields(Map<String, Field> fields);
    
    /**
     * Returns the saved kernel state.
     * 
     * The kernel state preserves processing state from a different session.
     */
    public KernelState getKernelState();
    
    /**
     * Sets a saved kernel state.
     * 
     * @param kernelState The kernel state to preserve.
     */
    public void setKernelState(KernelState kernelState);
    
    /**
     * Returns the list of constraint failures, that occurred during processing.
     * 
     * @return The list of constraint failures.
     */
    public List<CheckResult> getErrors();
}
