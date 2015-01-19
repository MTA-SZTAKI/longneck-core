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

    /** The summary of implemented checks as a short text. */
    private String summary;
    /* The checked field, what we want to test in the check box.*/
    private String faildField;

    @Override
    public void apply(Record record, VariableSpace parentScope) throws FailException {
        record.addError(new CheckResult(this, false,
                faildField == null || !BlockUtils.exists(faildField, record, parentScope)?null:faildField,
                faildField == null? null:BlockUtils.getValue(faildField, record, parentScope),
                summary == null ? "Intentional failure." : summary));

        throw new FailException(summary == null?"Intentional failure.":summary);
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getFaildField() {
        return faildField;
    }

    public void setFaildField(String faildField) {
        this.faildField = faildField;
    }

    @Override
    public Fail clone() {
        return (Fail) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Fail other = (Fail) obj;
        if (super.equals(obj) == false) {
            return false;
        }
        if ((this.summary == null) ? (other.summary != null) : !this.summary.equals(other.summary)) {
            return false;
        }
        if ((this.faildField == null) ? (other.faildField != null) : !this.faildField.equals(other.faildField)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.summary != null ? this.summary.hashCode() : 0);
        hash = 67 * hash + (this.faildField != null ? this.faildField.hashCode() : 0);
        return hash;
    }



}
