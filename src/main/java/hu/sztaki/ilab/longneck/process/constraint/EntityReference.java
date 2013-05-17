package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.AbstractReference;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.mapping.MappedRecord;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class EntityReference extends AbstractReference implements Entity  {
    
    /** Referred entity instance. */
    private Entity referredEntity = null;

    @Override
    public CheckResult check(Record record, VariableSpace scope) {
        if (mapping.hasRules()) {
            Record mapped = new MappedRecord(record, mapping);
            return referredEntity.check(mapped, scope);
        }
        
        return referredEntity.check(record, scope);
        
    }

    @Override
    public EntityReference clone() {
        EntityReference copy = (EntityReference) super.clone();
        copy.mapping = mapping.clone();

        return copy;
    }

    public Entity getReferredEntity() {
        return referredEntity;
    }

    public void setReferredEntity(Entity referredEntity) {
        this.referredEntity = referredEntity;
    }
}
