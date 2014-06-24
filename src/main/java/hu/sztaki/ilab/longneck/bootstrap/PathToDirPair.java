package hu.sztaki.ilab.longneck.bootstrap;

import hu.sztaki.ilab.longneck.process.FileType;
import java.io.File;

/**
 * A Type for path map to default search directory.
 * 
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
public class PathToDirPair {
    private final String path;
    private final String directory;

    public PathToDirPair(String  pkgid, String defaultdirectory, FileType filetype) {
        // assume deafultdirectory is a valid directoty path in the given operationsytem
        this.path = pkgid.startsWith("/") ? filetype.getFileName(pkgid.substring(1)):
                (defaultdirectory == null ?"":defaultdirectory)+filetype.getFileName(pkgid);;
        this.directory = path.substring(0, path.lastIndexOf(File.separatorChar)+1);
    }

    public String getPath() {
        return path;
    }

    public String getDirectory() {
        return directory;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.path != null ? this.path.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PathToDirPair other = (PathToDirPair) obj;
        if (this.path != other.path && (this.path == null || !this.path.equals(other.path))) {
            return false;
        }
        return true;
    }
    
    
}
