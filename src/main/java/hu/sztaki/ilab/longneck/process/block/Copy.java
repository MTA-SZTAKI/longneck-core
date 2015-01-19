package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.constraint.ConstraintFlag;
import hu.sztaki.ilab.longneck.util.LongneckStringUtils;
import java.util.List;

/**
 * Copies the content of another field.
 *
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class Copy extends AbstractAtomicBlock {

    /** The name of the field to copy from. */
    private String from;
    /** Also copy source flags if any. */
    private boolean withFlags = false;

    /**
     * Sets the field's name, where the data is copied from.
     *
     * @param from The source field's name.
     */
    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public boolean isWithFlags() {
        return withFlags;
    }

    public void setWithFlags(boolean withFlags) {
        this.withFlags = withFlags;
    }

    @Override
    public void apply(Record record, VariableSpace parentScope) {
        try {
            String value;
            List<ConstraintFlag> flags = null;
            value = BlockUtils.getValue(from, record, parentScope);
            if (withFlags) {
                flags = record.get(from).getFlags();
            }


            for (String fieldName : this.applyTo) {
                BlockUtils.setValue(fieldName, value, record, parentScope);

                if (withFlags) {
                    record.get(fieldName).setFlags(flags);
                }
            }
        } catch (NullPointerException ex) {
               log.error(String.format("%1$s %2$s", this, sourceInfo.getLocationString()), ex);
        }
    }

    @Override
    public Copy clone() {
        return (Copy) super.clone();
    }

    @Override
    public String toString() {
        return String.format("<copy apply-to=\"%1$s\" from=\"%2$s\", with-flags=\"%3$b\">",
                LongneckStringUtils.implode(" ", applyTo), from, withFlags);
    }
}
