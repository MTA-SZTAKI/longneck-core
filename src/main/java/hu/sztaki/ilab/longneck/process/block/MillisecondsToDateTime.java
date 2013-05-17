package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.CheckError;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Converts a date and time string to milliseconds.
 * 
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class MillisecondsToDateTime extends AbstractAtomicBlock {

    /** To read the date from. */
    private String from;
    /** The pattern to convert the text to a date object. */
    private String pattern;
    /** The date formatter class that parses the date. */
    private DateTimeFormatter dateFormat;

    public void afterPropertiesSet() {
        dateFormat = DateTimeFormat.forPattern(pattern);
    }
    
    @Override
    public void apply(Record record, VariableSpace parentScope) throws CheckError {
        long dateValue = 0;
        String date;
        
        try {
            dateValue = Long.valueOf(BlockUtils.getValue(from, record, parentScope));
            date = dateFormat.print(dateValue);
            
            for (String fieldName : applyTo) {
                BlockUtils.setValue(fieldName, date, record, parentScope);
            }
        } catch (IllegalArgumentException ex) {
            log.warn(String.format("Field %1$s content '%2$s' is not a valid timestamp.", 
                    from, dateValue), ex);
        }        
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public MillisecondsToDateTime clone() {
        return (MillisecondsToDateTime) super.clone();
    }   
}
