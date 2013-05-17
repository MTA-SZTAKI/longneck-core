package hu.sztaki.ilab.longneck.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class LongneckStringUtils {
    
    public static String implode(String glue, List<String> haystack) {
        return implode(glue, haystack, false);
    }
    
    public static String implode(String glue, List<String> haystack, boolean skipEmptyStrings) {
        StringBuilder sb = new StringBuilder();
        
        int len = haystack.size();
        int added = 0;
        for (int i = 0; i < len; ++i) {
            try {
                String value = haystack.get(i);

                if (value != null && ! "".equals(value)) {
                    // Check if there is a previous element
                    if (added > 0) {
                        sb.append(glue);
                    }

                    // Add current element and increment added
                    sb.append(value);
                    ++added;
                } else if (! skipEmptyStrings) {
                    sb.append(glue);
                }

            } catch (NullPointerException ex) {
                Logger.getLogger(LongneckStringUtils.class.getName()).warn(
                        String.format("Implode: source field %1$s is undefined or null.", 
                        haystack.get(i)));
            }
        }
        
        return sb.toString();
        
    }
    
    public static String getEnumList(List<? extends Enum> classes) {
        List<String> strClasses = new ArrayList<String>(classes.size());
        for (int i = 0; i < classes.size(); ++i) {
            strClasses.add(classes.get(i).toString());
        }
        
        return LongneckStringUtils.implode(", ", strClasses);
    }
    
    public static String camelCaseToUnderscore(String in) {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < in.length(); ++i) {
            if (Character.isLetter(in.charAt(i)) && Character.isUpperCase(in.charAt(i))) {
                sb.append('_');
                sb.append(Character.toLowerCase(in.charAt(i)));
            } else {
                sb.append(in.charAt(i));
            }
        }
        
        return sb.toString();
    }
    
    public static String underscoreToCamelCase(String in) {
        StringBuilder sb = new StringBuilder();
        
        boolean previousIsUnderscore = false;
        
        for (int i = 0; i < in.length(); ++i) {
            if (in.charAt(i) == '_') {
                previousIsUnderscore = true;                
            } else {
                if (previousIsUnderscore && Character.isLetter(in.charAt(i))) {
                    sb.append(Character.toUpperCase(in.charAt(i)));
                } else {
                    sb.append(in.charAt(i));
                }
                previousIsUnderscore = false;
            }
            
        }
        
        return sb.toString();        
    }
    
}
