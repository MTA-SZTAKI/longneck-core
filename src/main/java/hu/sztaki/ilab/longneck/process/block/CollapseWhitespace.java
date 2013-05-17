package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 * Collapses whitespace characters between tokens to single space characters. This block does not
 * remove whitespace from the beginning and the end of a string (use trim for that).
 * 
 * @see Trim
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class CollapseWhitespace extends AbstractAtomicBlock {

    @Override
    public void apply(Record record, VariableSpace parentScope) {
        for (String fieldName : applyTo) {
            String value = BlockUtils.getValue(fieldName, record, parentScope);
            if (value == null || "".equals(value)) {
                continue;
            }
            
            StringBuilder collapsed = new StringBuilder(value.length());
            boolean prev = false;
            for (int i = 0; i < value.length(); ++i) {
                char ch = value.charAt(i);
                if (Character.isWhitespace(ch)) {
                    if (! prev) {
                        collapsed.append(' ');
                    }
                    
                    prev = true;
                } else {
                    collapsed.append(ch);
                    prev = false;
                }
            }
            
            BlockUtils.setValue(fieldName, collapsed.toString(), record, parentScope);
        }
    }
    
}
