package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.util.LongneckStringUtils;

/**
 * Cut the length of field(s) to the given value (inclusive).
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
public class Cut extends AbstractAtomicBlock {
    /** The acceptable length. */
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void apply(Record record, VariableSpace parentScope) {
        if (value < 0) {
            value = 0;
        }
        try {
            for (String fieldName : this.applyTo) {
                String s = BlockUtils.getValue(fieldName, record, parentScope);
                if (s != null && s.length() > value) {
                    BlockUtils.setValue(fieldName, s.substring(0, value), record, parentScope);
                }
            }
        } catch (Exception ex) {
            log.error(String.format("Cut: failed to cut field. %1$s %2$s", this, sourceInfo.getLocationString()), ex);
        }
    }

    @Override
    public Cut clone() {
        return (Cut) super.clone();
    }

    @Override
    public String toString() {
        return String.format("<cut apply-to=\"%1$s\" value=\"%2$d\">",
                LongneckStringUtils.implode(" ", applyTo), value);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + this.value;
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
        final Cut other = (Cut) obj;
        if (super.equals(obj) == false) {
            return false;
        }
        if (this.value != other.value) {
            return false;
        }
        return true;
    }
    
    
}
