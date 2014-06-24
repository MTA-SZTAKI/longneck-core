package hu.sztaki.ilab.longneck.bootstrap;

import hu.sztaki.ilab.longneck.process.FileType;
import hu.sztaki.ilab.longneck.process.SplitId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Péter Molnár <molnar.peter@sztaki.mta.hu>
 */
public class RepositoryUtils {

    public static Set<PathToDirPair> getPackageNames(List<RefToDirPair> references) {
        Set<PathToDirPair> retval = new HashSet<PathToDirPair>();
        for (RefToDirPair reftodir : references) {
            retval.add(new PathToDirPair((new SplitId(reftodir.getRef().getId())).pkg, 
                    reftodir.getDefaultdirectory(),FileType.forReference(reftodir.getRef())));
        }
        return retval;
    }
}
