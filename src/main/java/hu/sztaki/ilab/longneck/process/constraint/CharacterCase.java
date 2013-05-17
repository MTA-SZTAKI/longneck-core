package hu.sztaki.ilab.longneck.process.constraint;

/**
 *
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public enum CharacterCase {
    
    /** The character must be uppercase. */
    Uppercase, 
    /** The character must be lowercase. */
    Lowercase, 
    /** The string must be capitalized. */
    Capitalized;
    
    public boolean check(String input) {
        char[] characters = input.toCharArray();
        switch (this) {
            case Uppercase:
                for (int i = 0; i < characters.length; ++i) {
                    if (Character.isLetter(characters[i]) && ! Character.isUpperCase(characters[i])) {
                        return false;
                    }
                }
                break;
            case Lowercase:
                for (int i = 0; i < characters.length; ++i) {
                    if (Character.isLetter(characters[i]) && ! Character.isLowerCase(characters[i])) {
                        return false;
                    }
                }
                break;
            case Capitalized:
                char prev = ' ';
                for (int i = 0; i < characters.length; ++i) {
                    if (Character.isSpaceChar(prev) &&
                        (Character.isLetter(characters[i]) && ! Character.isUpperCase(characters[i]))) {
                        return false;
                    }
                    
                    prev = characters[i];
                }
                break;
        }
        
        return true;
    }
    
    public char getCharacter(char c) {
        if (Character.isLetter(c)) {
            switch (this) {
                case Uppercase:
                    return Character.toUpperCase(c);
                case Lowercase:
                    return Character.toLowerCase(c);
                default:
                    return c;
                    
            }
        }
        
        return c;    
    }    
}


