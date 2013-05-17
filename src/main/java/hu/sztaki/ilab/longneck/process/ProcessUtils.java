package hu.sztaki.ilab.longneck.process;

import hu.sztaki.ilab.longneck.process.block.*;
import hu.sztaki.ilab.longneck.process.constraint.AndOperator;
import hu.sztaki.ilab.longneck.process.constraint.CompoundConstraint;
import hu.sztaki.ilab.longneck.process.constraint.Constraint;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class ProcessUtils {
    
    public static void postProcess(Sequence topLevelSequence, PostProcessor postProcessor) {
        for (Block b : getBlockList(topLevelSequence)) {
            postProcessor.processBlock(b);
            
            if (b instanceof Check) {
                postProcess(new AndOperator(((Check) b).getConstraints()), postProcessor);
            }
            else if (b instanceof If) {
                postProcess(((If) b).getCondition(), postProcessor);
            }
        }
    }
    
    public static void postProcess(CompoundConstraint topLevelConstraint, PostProcessor postProcessor) {
        for (Constraint c : getConstraintList(topLevelConstraint)) {
            postProcessor.processConstraint(c);
        }
    }
    
    public static List<Block> getBlockList(CompoundBlock topLevelSequence) {
        List<CompoundBlock> compoundBlocks = new ArrayList<CompoundBlock>();
        List<Block> blockList = new ArrayList<Block>();
        
        compoundBlocks.add(topLevelSequence);
        for (int i = 0; i < compoundBlocks.size(); ++i) {
            CompoundBlock compoundBlock = compoundBlocks.get(i);

            for (Block block : compoundBlock.getBlocks()) {
                blockList.add(block);
                
                if (block instanceof CompoundBlock) {
                    compoundBlocks.add((CompoundBlock) block);
                }
            }
        }
        
        return blockList;
    }
    
    public static List<Constraint> getConstraintList(CompoundConstraint topLevelConstraint) {
        List<CompoundConstraint> compoundConstraints = new ArrayList<CompoundConstraint>();
        List<Constraint> constraintList = new ArrayList<Constraint>();
        
        compoundConstraints.add(topLevelConstraint);
        for (int i = 0; i < compoundConstraints.size(); ++i) {
            CompoundConstraint compoundConstraint = compoundConstraints.get(i);

            for (Constraint constraint : compoundConstraint.getConstraints()) {
                constraintList.add(constraint);
                
                if (constraint instanceof CompoundConstraint) {
                    compoundConstraints.add((CompoundConstraint) constraint);
                }
            }
        }
        
        return constraintList;
    }

    public static List<Constraint> getConstraintList(List<Constraint> constraints) {
        CompoundConstraint topLevelConstraint = new AndOperator();
        topLevelConstraint.setConstraints(constraints);
        
        return getConstraintList(topLevelConstraint);
    }
    
}
