package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.BlockError;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.SetCharacterCase;
import hu.sztaki.ilab.longneck.process.constraint.CharacterCase;
import hu.sztaki.ilab.longneck.process.constraint.CharacterTarget;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class SetCharacterCaseTest {
    
    @Test
    public void testCase1() throws BlockError {
        Record r = new RecordImpl();
        VariableSpace scope = new VariableSpace();
        
        r.add(new Field("test"));
        r.get("test").setValue("NAgy HAjnalka");
        
        SetCharacterCase scc = new SetCharacterCase();
        scc.setCase(CharacterCase.Lowercase);
        scc.setCharacters(CharacterTarget.ALL);
        scc.setApplyTo(Arrays.asList(new String[] { "test" }));
        
        scc.apply(r, scope);
        
        scc.setCase(CharacterCase.Uppercase);
        scc.setCharacters(CharacterTarget.TOKEN_INITIALS_NON_ALNUM);
        scc.setApplyTo(Arrays.asList(new String[] { "test" }));
        
        scc.apply(r, scope);
        
        Assert.assertEquals("Nagy Hajnalka", r.get("test").getValue());
    }
}
