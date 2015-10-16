package hu.sztaki.ilab.longneck.process;

/**
 * Exception raised when a block execution fails.
 * 
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
abstract public class BlockError extends Exception {
    
    /** The name of the block that has failed. */
    private String blockName;
    /** The block count. */
    private int blockCounter = 0;

    public BlockError(Throwable cause) {
        super(cause);
    }

    public BlockError(String message, Throwable cause) {
        super(message, cause, false, false);
    }

    public BlockError(String message) {
        super(message, null, false, false);
    }

    public BlockError() {
    }
    
    public BlockError(String message, String blockName) {
        super(message, null, false, false);
        this.blockName = blockName;
    }
    
    public BlockError(String message, String blockName, Throwable cause) {
        super(message, cause, false, false);
        this.blockName = blockName;
    }
    

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public int getBlockCounter() {
        return blockCounter;
    }

    public void setBlockCounter(int blockCounter) {
        this.blockCounter = blockCounter;
    }    
}
