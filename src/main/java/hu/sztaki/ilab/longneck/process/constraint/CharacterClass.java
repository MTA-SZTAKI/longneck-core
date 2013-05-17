package hu.sztaki.ilab.longneck.process.constraint;

/**
 * Character classes used by the alphabet constraint.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public enum CharacterClass {    
    
    /** Character is a letter. */
    Letter,
    /** Character is a number. */
    Number,
    /** Character is a space. */
    Space;
    
    public boolean isMember(Character c) {
        switch (this) {
            case Letter:
                return Character.isLetter(c);
            case Number:
                return Character.isDigit(c);
            case Space:
                return Character.isSpaceChar(c);                
        }
        
        return false;
    }
    
    
}
