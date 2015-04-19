package hu.sztaki.ilab.longneck.process;

import hu.sztaki.ilab.longneck.process.block.BlockReference;
import hu.sztaki.ilab.longneck.process.constraint.ConstraintReference;
import hu.sztaki.ilab.longneck.process.constraint.EntityReference;
import java.io.File;
import java.nio.file.FileSystems;
import org.w3c.dom.Document;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public enum FileType {
    Block,
    Constraint,
    Entity,
    Process;
    
    public static FileType forDocument(Document doc) {
        String rootElementName = doc.getDocumentElement().getLocalName();
        
        for (FileType ft : values()) {
            if (ft.getRootElementName().equals(rootElementName)) {
                return ft;
            }
        }
        
        return null;
    }
    
    public static FileType forPath(String path) {
        if (path.endsWith(".block.xml")) {
            return Block;
        }
        else if (path.endsWith(".constraint.xml")) {
            return Constraint;
        }
        else if (path.endsWith(".entity.xml")) {
            return Entity;
        }
        
        return null;
    }
    
    public boolean isPackage() {
        switch (this) {
            case Block:
            case Constraint:
            case Entity:
                return true;
            default:
                return false;
        }
    }
    
    public static String getPackageId(String path) {
        int dotIndex  = path.lastIndexOf('.', path.lastIndexOf('.')-1);
        return path.substring(0, dotIndex).replaceAll(File.separator, "/");
    }
    
    
    public static String getFullPackageId(String defaultdirectory, String pkg) {
        return pkg.startsWith("/") ? pkg.substring(1):(defaultdirectory == null ?"":
                defaultdirectory.replace(File.separatorChar, '/'))+pkg;
    }
    
    public static String normalizePackageId(String fullpackageid, String repositoryPath, 
            AbstractReference ref) {
        return getPackageId(FileSystems.getDefault().getPath(
                repositoryPath, forReference(ref).getFileName(fullpackageid)).normalize().toString()
                .replaceFirst(repositoryPath+File.separator, ""));
    }
    
    public String getFileName(String baseName) {
        String filepath = baseName.replaceAll("/", File.separator);
        switch (this) {
            case Block:
                return String.format("%1$s.block.xml", filepath);
            case Constraint:
                return String.format("%1$s.constraint.xml", filepath);
            case Entity:
                return String.format("%1$s.entity.xml", filepath);
            case Process:
                return String.format("%1$s.process.xml", filepath);
            default:
                throw new RuntimeException("Invalid package type.");
        }
    }
    
    public static FileType forReference(AbstractReference reference) {
        if (reference instanceof BlockReference) {
            return Block;
        }
        else if (reference instanceof ConstraintReference) {
            return Constraint;            
        } 
        else if (reference instanceof EntityReference) {
            return Entity;
        }
        
        return null;
    }
    
    public String getRootElementName() {
        return this.toString().toLowerCase();
    }
    
    public Class getTargetClass() {
        switch (this) {
            case Block:
                return BlockPackage.class;
            case Constraint:
                return ConstraintPackage.class;
            case Entity:
                return EntityPackage.class;
            case Process:
                return LongneckProcess.class;
            default:
                return null;
        }
    }
}
