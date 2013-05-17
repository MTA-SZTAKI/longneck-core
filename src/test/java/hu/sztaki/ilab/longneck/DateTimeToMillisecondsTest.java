package hu.sztaki.ilab.longneck;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Péter Molnár  <molnarp@sztaki.mta.hu>
 */
public class DateTimeToMillisecondsTest {

    @Test
    public void testConversion() {
        String input = "21/Jan/2012:02:34:42 +0100";
        String pattern = "dd/MMM/yyyy:HH:mm:ss Z";
        
        DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
        DateTime actual = dtf.parseDateTime(input);
        
        DateTime expected = new DateTime(2012, 1, 21, 2, 34, 42, DateTimeZone.forOffsetHours(1));
        Assert.assertTrue(expected.isEqual(actual));
    }
}
