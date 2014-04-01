package hu.sztaki.ilab.longneck.util;

import java.util.Map;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;

/**
 * 
 * @author Geszler Döme <gdome@ilab.sztaki.hu>
 */

public class TestUtil {

    // observed Record fits expected one, if observed contains all fields of
    // expected with same values

    public static boolean fit(Record expected, Record observed) {
        Map<String, Field> expectedFields = expected.getFields();
        Map<String, Field> observedFields = observed.getFields();
        for (String name : expectedFields.keySet()) {
            if (!observedFields.containsKey(name)) {
                return false;
            } else {
                String expectedValue = expectedFields.get(name).getValue();
                String observedValue = observedFields.get(name).getValue();
                if (!(equal(expectedValue, observedValue)))
                    return false;
            }
        }
        return true;
    }

    private static boolean equal(Object o1, Object o2) {
        if (o1 == null && o2 == null)
            return true;
        if (o1 == null || o2 == null)
            return false;
        return o1.equals(o2);
    }

}
