package hu.sztaki.ilab.longneck.bootstrap;

import hu.sztaki.ilab.longneck.process.FileType;

/**
 *
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public enum SchemaPath {
    Block("classpath:META-INF/longneck/schema/longneck-block.xsd"), 
    Constraint("classpath:META-INF/longneck/schema/longneck-constraint.xsd"), 
    Entity("classpath:META-INF/longneck/schema/longneck-entity.xsd"),
    Process("classpath:META-INF/longneck/schema/longneck-process.xsd");
    
    private final String path;

    private SchemaPath(String url) {
        this.path = url;
    }

    public String getPath() {
        return path;
    }

    public static String forType(FileType type) {
        switch (type) {
            case Block:
                return Block.getPath();
            case Constraint:
                return Constraint.getPath();
            case Entity:
                return Entity.getPath();
            case Process:
                return Process.getPath();
            default:
                return null;
        }
    }
}
