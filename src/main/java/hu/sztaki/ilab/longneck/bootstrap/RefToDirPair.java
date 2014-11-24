package hu.sztaki.ilab.longneck.bootstrap;

import hu.sztaki.ilab.longneck.process.AbstractReference;

/**
 * A Type for AbstractReference map to default search directory.
 * 
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
public class RefToDirPair {
    private final AbstractReference ref;
    private final String defaultdirectory;

    public RefToDirPair(AbstractReference ref, String defaultdirectory) {
        this.ref = ref;
        this.defaultdirectory = defaultdirectory;
    }

    public AbstractReference getRef() {
        return ref;
    }

    public String getDefaultdirectory() {
        return defaultdirectory;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.ref != null ? this.ref.hashCode() : 0);
        hash = 97 * hash + (this.defaultdirectory != null ? this.defaultdirectory.hashCode() : 0);
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
        final RefToDirPair other = (RefToDirPair) obj;
        if (this.ref != other.ref && (this.ref == null || !this.ref.equals(other.ref))) {
            return false;
        }
        if ((this.defaultdirectory == null) ? (other.defaultdirectory != null) : !this.defaultdirectory.equals(other.defaultdirectory)) {
            return false;
        }
        return true;
    }
    
    
}
