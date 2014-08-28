package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 * Case in a switch structure.
 * 
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class Case extends Sequence {
    
    public Case() {
    }

    @Override
    public void apply(Record record, VariableSpace parentScope) {
    }
    
    @Override
    public Case clone() {
        return (Case) super.clone();
    }    
}
