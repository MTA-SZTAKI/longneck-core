package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 * Matches against regular expression and replaces the first occurrence of the match.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class AddPrefix extends AbstractAtomicBlock {

    /** Replacement string. */
    private String text;
    
    @Override
    public void apply(Record record, VariableSpace parentScope) {
        for (String fName : applyTo) {
            try {
                String value = text + BlockUtils.getValue(fName, record, parentScope);
                BlockUtils.setValue(fName, value, record, parentScope);
            } catch (NullPointerException e) {
                // do nothing - operation is valid on a null value and keeps it null
            }
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public AddPrefix clone() {
        return (AddPrefix) super.clone();
    }
    

}
