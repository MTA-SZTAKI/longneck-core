package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.AbstractSourceInfoContainer;
import hu.sztaki.ilab.longneck.process.FailException;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class Fail extends AbstractSourceInfoContainer implements Block {
    
    @Override
    public void apply(Record record, VariableSpace parentScope) throws FailException {
        record.getErrors().add(new CheckResult(this, false,
                null, null, "Intentional failure."));
        
        throw new FailException("Fail.");
    }

    @Override
    public Fail clone() {
        return (Fail) super.clone();
    }
}
