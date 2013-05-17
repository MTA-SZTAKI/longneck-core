package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.constraint.CharacterCase;
import hu.sztaki.ilab.longneck.process.constraint.CharacterTarget;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class SetCharacterCase extends AbstractAtomicBlock {
    
    /** The character case to apply. */
    private CharacterCase characterCase;
    /** The characters to change. */
    private CharacterTarget characters;
    

    @Override
    public void apply(Record record, VariableSpace parentScope) {
        
        for (String fieldName : applyTo) {
            String value = BlockUtils.getValue(fieldName, record, parentScope);
            
            // Skip null or empty fields
            if (value == null || "".equals(value)) {
                continue;
            }
            
            BlockUtils.setValue(fieldName, characters.apply(value, characterCase), record, parentScope);
        }
    }

    public CharacterCase getCase() {
        return characterCase;
    }

    public void setCase(CharacterCase characterCase) {
        this.characterCase = characterCase;
    }

    public CharacterTarget getCharacters() {
        return characters;
    }

    public void setCharacters(CharacterTarget characters) {
        this.characters = characters;
    }

    @Override
    public SetCharacterCase clone() {
        return (SetCharacterCase) super.clone();
    }
}
