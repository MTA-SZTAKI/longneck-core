package hu.sztaki.ilab.longneck.process.mapping;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class Map implements MappingRule {
    
    private String from;
    private String to;

    @Override
    public java.util.Map<String, String> getNames() {
        java.util.Map<String, String> retval = new java.util.HashMap<String, String>(1);
        retval.put(to, from);
        
        return retval;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
    
    @Override
    public Map clone() {
        try {
            return (Map) super.clone();            
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError(ex);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.from != null ? this.from.hashCode() : 0);
        hash = 79 * hash + (this.to != null ? this.to.hashCode() : 0);
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
        final Map other = (Map) obj;
        if ((this.from == null) ? (other.from != null) : !this.from.equals(other.from)) {
            return false;
        }
        if ((this.to == null) ? (other.to != null) : !this.to.equals(other.to)) {
            return false;
        }
        return true;
    }
    
}
