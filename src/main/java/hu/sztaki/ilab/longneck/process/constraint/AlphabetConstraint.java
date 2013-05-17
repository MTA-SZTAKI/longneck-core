package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.BlockUtils;
import hu.sztaki.ilab.longneck.util.LongneckStringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Checks input against an alphabet.
 * 
 * The alphabet defines the characters, that are checked. The policy defines
 * if the alphabet specified is allowed in the input, or denied.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class AlphabetConstraint extends AbstractAtomicConstraint {
    
    /** Allow or deny policy for character containment. */
    private Policy policy = Policy.Allow;
    /** Character classes checked in this constraint. */
    private List<CharacterClass> classes;

    public List<CharacterClass> getClasses() {
        return classes;
    }

    public void setClasses(List<CharacterClass> classes) {
        this.classes = classes;
    }
    
    public void setClasses(String classes) {
        List<CharacterClass> cls = new ArrayList<CharacterClass>();
        
        for (String c : Arrays.asList(classes.split("\\s+"))) {
            cls.add(CharacterClass.valueOf(c));
        }
        
        this.classes = cls;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    @Override
    public CheckResult check(Record record, VariableSpace scope) {        
        String details = String.format("Policy %1$s, Character classes: %2$s", 
                policy.toString(), LongneckStringUtils.getEnumList(classes));
        
        // Prepare result variable
        List<CheckResult> results = new ArrayList<CheckResult>(applyTo.size());
        
        for (String fieldName : applyTo) {
            // Perform check
            String s = BlockUtils.getValue(fieldName, record, scope);
            if (s == null || "".equals(s)) {
                continue;
            }
            char[] characters = s.toCharArray();
            
            // Allow policy
            if (policy == Policy.Allow) {
                for (int i = 0; i < characters.length; ++i) {

                    boolean ok = false;

                    for (CharacterClass cc : classes) {

                        if (cc.isMember(characters[i])) {
                            ok = true;
                            break;
                        }
                    }

                    if (ok) { 
                        results.add(new CheckResult(this, true, fieldName, s, details));
                    } else {
                        results.add(new CheckResult(this, false, fieldName, s, details));
                        return new CheckResult(this, false, null, null, null, results);
                    }
                }                
            } else {
                // Deny policy
                for (int i = 0; i < characters.length; ++i) {

                    boolean ok = true;

                    for (CharacterClass cc : classes) {

                        if (cc.isMember(characters[i])) {
                            ok = false;
                            break;
                        }
                    }

                    if (ok) { 
                        results.add(new CheckResult(this, true, fieldName, s, details));
                    } else {
                        results.add(new CheckResult(this, false, fieldName, s, details));
                        return new CheckResult(this, false, null, null, null, results);
                    }
                }
            }
        }
        
        return new CheckResult(this, true, null, null, null, results);
    }

    @Override
    public AlphabetConstraint clone() {
        AlphabetConstraint copy = (AlphabetConstraint) super.clone();
        if (classes != null) {
            copy.classes = new ArrayList<CharacterClass>(classes.size());
            copy.classes.addAll(classes);
        }
        
        return copy;
    }    
}
