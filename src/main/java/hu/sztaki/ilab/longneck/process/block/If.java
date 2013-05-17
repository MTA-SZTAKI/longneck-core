package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.constraint.AndOperator;
import hu.sztaki.ilab.longneck.process.constraint.Constraint;
import java.util.ArrayList;
import java.util.List;

/**
 * If-then-else control structure.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class If extends Sequence {
    
    /** The condition constraints. */
    private AndOperator condition = new AndOperator();
    /** The then branch. */
    private Sequence thenBranch;
    /** Blocks, that are executed, if the condition constraint fails. */
    private Sequence elseBranch;
    
    public If() {
        blocks = new ArrayList<Block>(2);
    }

    public AndOperator getCondition() {
        return condition;
    }

    public void setCondition(AndOperator condition) {
        this.condition = condition;
    }

    public List<Constraint> getConstraints() {
        return condition.getConstraints();
    }
    
    public void setConstraints(List<Constraint> constraints) {
        condition.setConstraints(constraints);
    }

    public Sequence getElseBranch() {
        return elseBranch;
    }

    public void setElseBranch(Sequence elseBranch) {
        ((List<Block>) blocks).add(elseBranch);
        this.elseBranch = elseBranch;
    }

    public Sequence getThenBranch() {        
        return thenBranch;
    }

    public void setThenBranch(Sequence thenBranch) {
        ((List<Block>) blocks).add(thenBranch);
        this.thenBranch = thenBranch;
    }
    
    @Override
    public void apply(Record record, VariableSpace parentScope) {}

    @Override
    public If clone() {
        If copy = (If) super.clone();
        if (condition != null) {
            copy.condition = condition.clone(); 
        }
        
        copy.blocks = new ArrayList<Block>(2);
        if (thenBranch != null) {            
            copy.thenBranch = thenBranch.clone();
            ((List<Block>) copy.blocks).add(copy.thenBranch);
        }
        
        if (elseBranch != null) {
            copy.elseBranch = elseBranch.clone();
            ((List<Block>) copy.blocks).add(copy.elseBranch);
        }
        
        return copy;       
    }
}
