package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.CheckError;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Converts a date and time string to the given format.
 * 
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class DateTimeToFormat extends AbstractAtomicBlock {

    /** To read the date from. */
    private String from;
    /** The pattern to convert the text to a date object from. */
    private String fromPattern;
    /** The pattern to convert the text to a date object to. */
    private String toPattern;
    
    /** The date formatter class that parses the from date. */
    private DateTimeFormatter fromFormat;
    /** The date formatter class that parses the to date. */
    private DateTimeFormatter toFormat;

    public void afterPropertiesSet() {
        fromFormat = DateTimeFormat.forPattern(fromPattern);
        toFormat = DateTimeFormat.forPattern(toPattern);
    }
    
    @Override
    public void apply(Record record, VariableSpace parentScope) throws CheckError {
        String dateValue = "";
        try {
            dateValue = BlockUtils.getValue(from, record, parentScope);
            LocalDateTime date = fromFormat.parseLocalDateTime(dateValue);
            
            for (String fieldName : applyTo) {
                BlockUtils.setValue(fieldName, date.toString(toFormat), record, parentScope);
            }
        } catch (IllegalArgumentException ex) {
            throw new CheckError(new CheckResult(this, false, from, dateValue, 
                    String.format("Field '%1$s' content '%2$s' does not match date pattern '%3$s'.", 
                    from, dateValue, fromPattern)));
        } catch (UnsupportedOperationException ex) {
            log.error("joda-time pattern-based parsing is unsupported.", ex);
        }
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromPattern() {
        return fromPattern;
    }

    public void setFromPattern(String fromPattern) {
        this.fromPattern = fromPattern;
    }

    public String getToPattern() {
        return toPattern;
    }

    public void setToPattern(String toPattern) {
        this.toPattern = toPattern;
    }

    public DateTimeFormatter getFromFormat() {
        return fromFormat;
    }

    public void setFromFormat(DateTimeFormatter fromFormat) {
        this.fromFormat = fromFormat;
    }

    public DateTimeFormatter getToFormat() {
        return toFormat;
    }

    public void setToFormat(DateTimeFormatter toFormat) {
        this.toFormat = toFormat;
    }

    
    
    @Override
    public DateTimeToFormat clone() {
        return (DateTimeToFormat) super.clone();
    }   
}
