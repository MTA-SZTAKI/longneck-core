package hu.sztaki.ilab.longneck.bootstrap;

import hu.sztaki.ilab.longneck.process.FileType;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;

/**
 * A Type for path map to default search directory.
 * 
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
public class PathToDirPair {
    private String path;
    private String directory;

    public PathToDirPair(String  pkgid, String defaultdirectory, FileType filetype) {
        // assume deafultdirectory is a valid directoty path in the given operationsytem
        init(pkgid.startsWith("/") ? filetype.getFileName(pkgid.substring(1)):
                (defaultdirectory == null ?"":defaultdirectory)+filetype.getFileName(pkgid));
    }
    
    public PathToDirPair normalizePath(String repositoryPath) throws IOException {
        String cannonicalpath = FileSystems.getDefault().getPath(repositoryPath, path).normalize()
                .toString().replaceFirst(repositoryPath+File.separator, "");
        
        if(cannonicalpath.equals(path)) return this;
        init(cannonicalpath);
        return this;
    }

    public String getPath() {
        return path;
    }

    public String getDirectory() {
        return directory;
    }
    
    private void init(String path) {
        this.path = path;
        this.directory = path.substring(0, path.lastIndexOf(File.separatorChar)+1);
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
