package hu.sztaki.ilab.longneck.process;

import hu.sztaki.ilab.longneck.process.constraint.GenericEntity;
import java.util.HashMap;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class EntityPackage extends AbstractPackage<GenericEntity> {

    public EntityPackage() {
        map =  new HashMap<String,GenericEntity>();
    }

    public EntityPackage(String packageId) {
        this.packageId = packageId;
        map =  new HashMap<String,GenericEntity>();
    }

    public GenericEntity getEntity(String id, String version) {
        return map.get(String.format("%1$s:%2$s", id, version));
    }
    
    public void addGenericEntity(GenericEntity entity) {
        map.put(String.format("%1$s:%2$s", entity.getId(), entity.getVersion()), entity);
    }

    @Override
    public FileType getType() {
        return FileType.Entity;
    }
}
