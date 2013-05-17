package hu.sztaki.ilab.longneck.process.mapping;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
abstract public class AbstractFixRule extends AbstractMappingRule {
    public String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    @Override
    public AbstractFixRule clone() {
        return (AbstractFixRule) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (this.text != null ? this.text.hashCode() : 0);
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
        final AbstractFixRule other = (AbstractFixRule) obj;
        if ((this.text == null) ? (other.text != null) : !this.text.equals(other.text)) {
            return false;
        }
        return true;
    }
}
