package hu.sztaki.ilab.longneck.util;

/**
 *
 * @author Molnár Péter <molnar.peter@sztaki.mta.hu>
 */
public enum OsType {
    Linux,
    Windows,
    Other;

    public static OsType getCurrent() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return Windows;
        }
        else if (System.getProperty("os.name").startsWith("Linux")) {
            return Linux;
        }

        return Other;
    }
}
