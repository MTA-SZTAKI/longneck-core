package hu.sztaki.ilab.longneck.process.task;

/**
 *
 * @author Peter Molnar <molnar.peter@sztaki.mta.hu>
 */
public enum TaskType {
    Reader,
    Writer,
    Worker;
    
    public static TaskType forClass(Class clazz) {
        if (SourceReader.class.equals(clazz)) {
            return Reader;
        }
        else if (TargetWriter.class.equals(clazz)) {
            return Writer;
        }
        
        return Worker;
    }
}
