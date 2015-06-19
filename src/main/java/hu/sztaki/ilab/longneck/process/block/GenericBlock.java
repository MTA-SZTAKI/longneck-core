package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.SourceInfo;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.bootstrap.RepositoryItem;

/**
 * Generic transformation block.
 *
 * Transformation block with custom transformation steps, that has an id and version, and
 * can be retrieved from repository.
 *
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class GenericBlock extends Sequence implements RepositoryItem {

    /** The id of this block in the repository. */
    private String id;
    /** The block version. */
    private String version;
    /** Allow error propagation. */
    private boolean propagateFailure = false;

    /** The input constraints. */
    private Check inputConstraints;
    /** The output constraints. */
    private Check outputConstraints;
    
    @Override
    public void apply(Record record, VariableSpace parentScope) {
        // do nothing
    }

    public Check getInputConstraints() {
        return inputConstraints;
    }

    public void setInputConstraints(Check inputConstraints) {
        this.inputConstraints = inputConstraints;
    }

    public Check getOutputConstraints() {
        return outputConstraints;
    }

    public void setOutputConstraints(Check outputConstraints) {
        this.outputConstraints = outputConstraints;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    public String getKey() {
        return String.format("%1$s:%2$s", id, version);
    }

    @Override
    public GenericBlock clone() {
        GenericBlock copy = (GenericBlock) super.clone();

        // Version and Id are immutable strings

        // Copy input and output constraints
        if (inputConstraints != null) {
            copy.inputConstraints = inputConstraints.clone();
        }

        if (outputConstraints != null) {
            copy.outputConstraints = outputConstraints.clone();
        }

        return copy;
    }

    @Override
    public SourceInfo getSourceInfo() {
        return sourceInfo;
    }

    public boolean isPropagateFailure() {
        return propagateFailure;
    }

    public void setPropagateFailure(boolean propagateFailure) {
        this.propagateFailure = propagateFailure;
    }


}

