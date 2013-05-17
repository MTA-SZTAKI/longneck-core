package hu.sztaki.ilab.longneck.process;

import hu.sztaki.ilab.longneck.process.constraint.GenericConstraint;
import java.util.HashMap;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class ConstraintPackage extends AbstractPackage<GenericConstraint> {

    public ConstraintPackage() {
        this.map = new HashMap<String, GenericConstraint>();
    }

    public ConstraintPackage(String packageId) {
        this.packageId = packageId;
    }

    public GenericConstraint getConstraint(String id, String version) {
        return map.get(String.format("%1$s:%2$s", id, version));
    }

    public void addGenericConstraint(GenericConstraint constraint) {
        map.put(String.format("%1$s:%2$s", constraint.getId(), constraint.getVersion()), constraint);
    }

    @Override
    public FileType getType() {
        return FileType.Constraint;
    }
}
