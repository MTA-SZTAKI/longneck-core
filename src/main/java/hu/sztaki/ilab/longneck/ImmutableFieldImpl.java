package hu.sztaki.ilab.longneck;

import com.google.common.collect.ImmutableList;
import hu.sztaki.ilab.longneck.process.constraint.ConstraintFlag;
import java.util.List;

/**
 * 
 * @author Peter Molnar <molnar.peter@sztaki.mta.hu>
 */
public class ImmutableFieldImpl extends Field {
    
    private final String name;
    private final String value;
    private final ImmutableList<ConstraintFlag> flags;

    public ImmutableFieldImpl(String name, String value) {
        this.name = name;
        this.value = value;
        flags = new ImmutableList.Builder<ConstraintFlag>().build();
    }

    public ImmutableFieldImpl(String name, String value, ImmutableList<ConstraintFlag> flags) {
        this.name = name;
        this.value = value;
        this.flags = flags;
    }
    
    public ImmutableFieldImpl(Field f) {
        name = f.getName();
        value = f.getValue();
        flags = new ImmutableList.Builder<ConstraintFlag>().addAll(f.getFlags()).build();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ConstraintFlag> getFlags() {
        return flags;
    }

    @Override
    public void setFlags(List<ConstraintFlag> flags) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addFlag(ConstraintFlag flag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeFlag(ConstraintFlag flag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasFlag(ConstraintFlag flag) {
        return flags.contains(flag);
    }
    
    @Override
    public String toString() {
        return String.format("%1$s: %2$s", this.name, this.value);
    }

}
