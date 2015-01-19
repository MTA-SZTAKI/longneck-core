package hu.sztaki.ilab.longneck.process.block;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for multiple case branching control structures.
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
abstract public class AbstractSwitch extends Sequence {
    /** List of cases. */
    protected List<Case> cases;

    public AbstractSwitch() {
        cases = new ArrayList<Case>();
        blocks = cases;
    }

    public List<Case> getCases() {
        return cases;
    }

    public void setCases(List<Case> cases) {
        for (Case c: cases) {
            c.setContext(this.context);
        }
        this.blocks = cases;
        this.cases = cases;
    }

    @Override
    public List<Block> getBlocks() {
        return (List<Block>) blocks;
    }

    @Override
    public boolean hasPosition(int pos) {
        if (cases == null) {
            return false;
        }

        return (pos >= 0 && pos < cases.size());
    }

    @Override
    public AbstractSwitch clone() {
        AbstractSwitch copy = (AbstractSwitch) super.clone();

        if (cases != null) {
            copy.cases = new ArrayList<Case>(cases.size());
            copy.blocks = copy.cases;

            for (final Case c : cases) {
                Case cloneCase = c.clone();
                cloneCase.setContext(this.context);
                copy.cases.add(cloneCase);
            }
        }
        copy.setContext(this.context) ;
        return copy;
    }
}
