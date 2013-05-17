package hu.sztaki.ilab.longneck.process.constraint;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public enum CharacterTarget {
    
    ALL,
    FIRST,
    TOKEN_INITIALS,
    TOKEN_INITIALS_NON_ALNUM;
    
    public String apply(String text, CharacterCase characterCase) {
        
        char[] characters = text.toCharArray();
        
        switch (this) {
            case ALL:
                for (int i = 0; i < characters.length; ++i) {
                    characters[i] = characterCase.getCharacter(characters[i]);
                }
                return new String(characters);
                
            case FIRST:
                return characterCase.getCharacter(text.charAt(0)) + text.substring(1);
                
            case TOKEN_INITIALS:
                for (int i = 0; i < characters.length; ++i) {
                    if (Character.isLetter(characters[i]) && (i == 0 || Character.isSpaceChar(characters[i-1]))) {
                        characters[i] = characterCase.getCharacter(characters[i]);
                    }
                }
                return new String(characters);
                                
            case TOKEN_INITIALS_NON_ALNUM:
                for (int i = 0; i < characters.length; ++i) {
                    if (Character.isLetter(characters[i]) && (i == 0 || !Character.isLetterOrDigit(characters[i-1]))) {
                        characters[i] = characterCase.getCharacter(characters[i]);
                    }
                }
                return new String(characters);

            default:
                return text;
                
        }
    }
}
