package hu.sztaki.ilab.longneck.process;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class FrameAddress {

    private int fileId;
    private int sequenceId;
    
    public static final FrameAddress NEXT = new FrameAddress(-1, -1);
    public static final FrameAddress RETURN = new FrameAddress(-2, -2);

    public FrameAddress(int fileId, int elementId) {
        this.fileId = fileId;
        this.sequenceId = elementId;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int elementId) {
        this.sequenceId = elementId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }
    
    @Override
    public boolean equals(Object o) {        
        if (! (o instanceof FrameAddress)) {
            return false;
        }
        
        FrameAddress other = (FrameAddress) o;
        if (fileId != other.fileId || sequenceId != other.sequenceId) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.fileId;
        hash = 97 * hash + this.sequenceId;
        return hash;
    }
}
