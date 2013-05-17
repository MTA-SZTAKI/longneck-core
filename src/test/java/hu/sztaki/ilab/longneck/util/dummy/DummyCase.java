package hu.sztaki.ilab.longneck.util.dummy;

import hu.sztaki.ilab.longneck.process.block.Case;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class DummyCase extends Case  {
    private int value = 0;

    public DummyCase(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DummyCase other = (DummyCase) obj;
        if (this.value != other.value) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + this.value;
        return hash;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
