package hu.sztaki.ilab.longneck.bootstrap;

import hu.sztaki.ilab.longneck.process.*;
import hu.sztaki.ilab.longneck.process.block.BlockReference;
import hu.sztaki.ilab.longneck.process.block.GenericBlock;
import hu.sztaki.ilab.longneck.process.constraint.ConstraintReference;
import hu.sztaki.ilab.longneck.process.constraint.EntityReference;
import hu.sztaki.ilab.longneck.process.constraint.GenericConstraint;
import hu.sztaki.ilab.longneck.process.constraint.GenericEntity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class Repository {
    /** Log object. */
    protected final Logger LOG = Logger.getLogger(Repository.class);
    
    /** Entity packages. */
    protected final Map<String,EntityPackage> entities;
    /** Constraint packages. */
    protected final Map<String,ConstraintPackage> constraints;
    /** Block packages. */
    protected final Map<String,BlockPackage> blocks;
    
    public Repository() {
        entities = new HashMap<String,EntityPackage>();
        constraints = new HashMap<String,ConstraintPackage>();
        blocks = new HashMap<String,BlockPackage>();        
    }
    
    public Repository(List<LongneckPackage> packages) {
        this();
        
        for (LongneckPackage pkg : packages) {
            addPackage(pkg);
        }
    }
    
    public final void addPackage(LongneckPackage pkg) {
        switch (pkg.getType()) {
            case Block:
                blocks.put(pkg.getPackageId(), (BlockPackage) pkg);
                break;
                
            case Constraint:
                constraints.put(pkg.getPackageId(), (ConstraintPackage) pkg);
                break;
                
            case Entity:
                entities.put(pkg.getPackageId(), (EntityPackage) pkg);
                break;
        }
    }
    
    public GenericEntity getEntity(String packageid, String id, String version) {
        GenericEntity entity = null;
        try {
            entity = entities.get(packageid).getEntity(id, version);
        } catch (NullPointerException ex) {
            throw new RuntimeException("Entity package not found: " + id + ":" + version, ex);
        }
        
        if (entity == null) {
            throw new RuntimeException("Entity not found: " + id + ":" + version);
        }
        
        return entity;
    }
    
    public GenericConstraint getConstraint(String packageid, String id, String version) {
        GenericConstraint constraint = null;
        try {
            constraint = constraints.get(packageid).getConstraint(id, version);
        } catch (NullPointerException ex) {
            throw new RuntimeException("Constraint package not found: " + id + ":" + version, ex);
        }
        
        if (constraint == null) {
            throw new RuntimeException("Constraint not found: " + id + ":" + version);
        }
        
        return constraint;
    }
    
    public GenericBlock getBlock(String packageid, String id, String version) {
        GenericBlock block = null;
        try {
             block = blocks.get(packageid).getBlock(id, version);
        } catch (NullPointerException ex) {
            throw new RuntimeException("Block package not found: " + id + ":" + version, ex);
        }
        
        if (block == null) {
            throw new RuntimeException("Block not found: " + id + ":" + version);
        }
        
        return block;
    }
    
    public boolean isLoaded(FileType type, String pkg) {
        switch (type) {
            case Block:
                return blocks.containsKey(pkg);
            case Constraint:
                return constraints.containsKey(pkg);
            case Entity:
                return entities.containsKey(pkg);
            default:
                return false;                
        }
    }
    
    public void updateReferences(List<RefToDirPair> refdirlist, String repositoryPath) throws IOException {
        for (RefToDirPair refdir : refdirlist) {
            AbstractReference ref = refdir.getRef();
            SplitId splitId = new SplitId(ref.getId());
            String pkg = splitId.pkg;
            String id = splitId.id;
            String packageid = FileType.normalizePackageId(
                    FileType.getFullPackageId(refdir.getDefaultdirectory(), pkg), repositoryPath, ref);
            if (ref instanceof BlockReference) {
                ((BlockReference) ref).setReferredBlock(getBlock(packageid, id, ref.getVersion()));
            }
            else if (ref instanceof ConstraintReference) {
                ((ConstraintReference) ref).setReferredConstraint(
                        getConstraint(packageid, id, ref.getVersion()));
            }
            else if (ref instanceof EntityReference) {
                ((EntityReference) ref).setReferredEntity(getEntity(packageid, id, ref.getVersion()));
            }
        }
    }
    
    public List<LongneckPackage> getSources() {
        List<LongneckPackage> sources = new ArrayList<LongneckPackage>(
                blocks.size() + constraints.size() + entities.size());
        for (BlockPackage pak : blocks.values()) {
            sources.add(pak);
        }
        for (ConstraintPackage pak : constraints.values()) {
            sources.add(pak);
        }
        for (EntityPackage pak : entities.values()) {
            sources.add(pak);
        }
        
        return sources;
    }
}
