package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.AbstractSourceInfoContainer;
import hu.sztaki.ilab.longneck.process.CheckError;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.constraint.AndOperator;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import hu.sztaki.ilab.longneck.process.constraint.Constraint;
import java.util.List;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class Check extends AbstractSourceInfoContainer implements ContextualBlock {

    /** The summary of implemented checks as a short text. */
    private String summary;
    /** The record field optionally containing the context to be logged when an error occurs */
    private String contextField;
    /* The checked field, what we want to test in the check box.*/
    private String checkedField;
    /** The contained constraints. */
    private AndOperator constraints = new AndOperator();
    /** */
    private String context ;

    public Check() {
    }

    @Override
    public void apply(Record record, VariableSpace parentScope) throws CheckError {
        String localContext ;
        if (contextField != null && BlockUtils.exists(contextField, record, parentScope)) {
            localContext = BlockUtils.getValue(contextField, record, parentScope) ;
        }
        else {
            localContext = this.context ;
        }
        CheckResult res;
        if (checkedField == null || !BlockUtils.exists(checkedField, record, parentScope)) {
            res = new CheckResult(constraints.check(record, parentScope), this, summary, localContext);
        } else {
            CheckResult andresult = constraints.check(record, parentScope);
            res = new CheckResult(this, andresult.isPassed(), checkedField,
                    BlockUtils.getValue(checkedField, record, parentScope), summary, localContext,
                    andresult.getCauses());
        }
        if (! res.isPassed()) {
            throw new CheckError(res);
        }
    }

    @Override
    public Check clone() {
        Check copy = (Check) super.clone();
        copy.constraints = this.constraints.clone();
        copy.setContext(context);
        return copy;
    }

    public void setConstraints(List<Constraint> constraints) {
        this.constraints.setConstraints(constraints);
    }

    public List<Constraint> getConstraints() {
        return constraints.getConstraints();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCheckedField() {
        return checkedField;
    }

    public void setCheckedField(String checkedfield) {
        this.checkedField = checkedfield;

    }

    public String getContextField() {
        return this.contextField;
    }

    public void setContextField(String checkedfield) {
        this.contextField = checkedfield;

    }

    @Override
    public String getContext() {
        return this.context ;
    }

    @Override
    public void setContext(String context) {
        this.context = context ;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.summary != null ? this.summary.hashCode() : 0);
        hash = 67 * hash + (this.checkedField != null ? this.checkedField.hashCode() : 0);
        hash = 67 * hash + (this.constraints != null ? this.constraints.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Check other = (Check) obj;
        if (super.equals(obj) == false) {
            return false;
        }
        if ((this.summary == null) ? (other.summary != null) : !this.summary.equals(other.summary)) {
            return false;
        }
        if ((this.checkedField == null) ? (other.checkedField != null) : !this.checkedField.equals(other.checkedField)) {
            return false;
        }
        if (this.constraints != other.constraints && (this.constraints == null || !this.constraints.equals(other.constraints))) {
            return false;
        }
        return true;
    }


}
