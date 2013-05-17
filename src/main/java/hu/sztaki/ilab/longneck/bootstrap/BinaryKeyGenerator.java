package hu.sztaki.ilab.longneck.bootstrap;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Generates keys for the error writer.
 * 
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class BinaryKeyGenerator implements KeyGenerator {
    /** Number of bits representing the node. */
    private int nodeBits = 3;
    /** Number of bits representing the current timestamp. */
    private int timestampBits = 44;
    /** Counter for records processed at the same millisecond. */
    private int serialBits = 16;
    /** The last timestamp. */
    private long lastTimestamp;
    /** Last issued serial. */
    private long lastSerial = 0;
    /** The highest serial number available. */
    private long maxSerial;
    /** The calendar to query milliseconds from. */
    private Calendar calendar = new GregorianCalendar();
    /** The id of the current node. */
    private long nodeId = 0;

    public BinaryKeyGenerator() {
    }
    
    public BinaryKeyGenerator(long nodeId) {
        assert nodeId >= 0;
        assert (nodeId < (1 << nodeBits));
        
        // Assign node id and check, if it fits into node bits
        this.nodeId = nodeId;
        this.maxSerial = (1 << serialBits) - 1;
    }
    
    /**
     * Initializes the key generator.
     * 
     * @param nodeId The numeric id of the node.
     * @param nodeBits The number of bits for the node id.
     * @param timestampBits The number of bits used for the timestamp.
     * @param serialBits The number of bits used for the serial number.
     * @throws AssertionError If the parts of the key do not fit into the bits specified.
     */
    public BinaryKeyGenerator(long nodeId, int nodeBits, int timestampBits, int serialBits) {
        this(nodeId);
        
        assert nodeBits >= 1;
        assert timestampBits >= 1;
        assert serialBits >= 1;
        assert ((nodeBits + timestampBits + serialBits) <= 63);
        assert calendar.getTimeInMillis() < (1 << timestampBits);

        // Assign node id and check, if it fits into node bits
        this.nodeId = nodeId;
        assert (nodeId < (1 << nodeBits));
        this.nodeBits = nodeBits;
        this.timestampBits = timestampBits;
        this.serialBits = serialBits;
        
        this.maxSerial = (1 << serialBits) - 1;
        
    }
    
    /**
     * Returns the next key.
     * 
     * @return The generated key.
     * @throws AssertionError If serial incrementation results in overflow.
     */
    @Override
    public long getNext() {
        long timestamp = calendar.getTimeInMillis();
        
        if (lastTimestamp < timestamp) {
            lastTimestamp = timestamp;
            lastSerial = 0;
        } else {
            ++lastSerial;
        }
        
        assert (lastSerial <= maxSerial);
        
        return (
                (nodeId  << (timestampBits + serialBits)) | 
                (lastTimestamp << serialBits) | 
                lastSerial);
    }
    
    /**
     * Thread-safe implementation of next key.
     * 
     * @return The generated key.
     * @throws AssertionError If serial incrementation results in overflow.
     */
    @Override
    public synchronized long getNextThreadSafe() {
        return getNext();
    }
}
