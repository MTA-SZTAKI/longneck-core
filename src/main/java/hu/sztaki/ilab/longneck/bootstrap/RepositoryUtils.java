package hu.sztaki.ilab.longneck.bootstrap;

import hu.sztaki.ilab.longneck.process.AbstractReference;
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

    public static Set<String> getPackageNames(List<AbstractReference> references) {
        Set<String> retval = new HashSet<String>();
        
        for (AbstractReference ref : references) {
            SplitId splitId = new SplitId(ref.getId());
            retval.add(FileType.forReference(ref).getFileName(splitId.pkg));
        }
        
        return retval;
    }
}
