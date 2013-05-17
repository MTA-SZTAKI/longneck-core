package hu.sztaki.ilab.longneck.util;

import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Utility class for database related operations
 * 
 * @author Loránd Bendig Loránd
 *
 */
public class DatabaseUtils {

	private DatabaseUtils() {
		throw new AssertionError("shouldn't be instantiated");
	}

	private static final String DASH_REPLACEMENT = "__.__";
	private static final Pattern DASH_REPLACE_PATTERN =  Pattern.compile("(:[^\\)|,]*)");

	/**
	 * Replaces dash(-) characters of bind variables in a query string since Spring's JDBC handles
	 * them as delimiters
	 * @param query - the query to modify
	 * @return modified query without any dashes(-) in its bind variables or the original one if
	 * it doesn't contain any.
	 */
	public static String replaceQueryBindVariableDashes(String query) {
	       if (!query.contains("-")) {
	           return query;
	       }

	       StringBuilder sb = new StringBuilder(query);
	       Matcher m = DASH_REPLACE_PATTERN.matcher(query);
	       while (m.find()) {
	           int startPos = sb.indexOf(m.group());
	           sb.replace(startPos, startPos + m.group().length(),
	                   m.group().replace("-", DASH_REPLACEMENT));
	       }
	       return sb.toString();
	   }
	
	/**
	 * Reverts dash replacements in a query bind variable
	 * 
	 * @param bindVariable
	 * @return the original bind variable without dash replacement.
	 * @see replaceQueryBindVariableDashes
	 */
	public static String revertReplacedBindVariableDashes(String bindVariable) {
		return (bindVariable.contains(DASH_REPLACEMENT)) ? bindVariable.replaceAll(
				DASH_REPLACEMENT, "-") : bindVariable;
	}
    
    public static String sqlParameterSourceToText(MapSqlParameterSource source) {
        StringBuilder s = new StringBuilder();
        s.append("{ ");
        
        for (String key : source.getValues().keySet()) {
            s.append("\"");
            s.append(source.getValue(key) == null?"null":source.getValue(key).toString());
            s.append("\", ");
        }
        
        s.append("}");
        
        return s.toString();
    }
	
    public static int getAffectedRowsNumber(int returned) {
        if (returned >= 0) {
            return returned;
        }
        
        if (returned == Statement.SUCCESS_NO_INFO) {
            return 1;
        }
        
        return 0;
    }
}
