package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.AbstractSourceInfoContainer;
import hu.sztaki.ilab.longneck.process.FilterException;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 * A filter is responsible for dropping records based on a filtering criteria.
 * 
 * @author Csaba Sidló
 */
public class Filter extends AbstractSourceInfoContainer implements Block {

    @Override
    public void apply(Record record, VariableSpace parentScope) throws FilterException {
        throw new FilterException("Record was filtered out.");
    }

    @Override
    public Filter clone() {
        return (Filter) super.clone();
    }
}
