package hu.sztaki.ilab.longneck.util.dummy;

import hu.sztaki.ilab.longneck.process.AbstractSourceInfoContainer;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
abstract public class AbstractDummy extends AbstractSourceInfoContainer {

    private int value = 0;

    public AbstractDummy() {
    }

    public AbstractDummy(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractDummy && this.value == ((AbstractDummy) obj).value) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.value;
        return hash;
    }

}
