package hu.sztaki.ilab.longneck.bootstrap;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class DecimalKeyGenerator implements KeyGenerator {
    /** Number of bits representing the node. */
    private int nodePlaces = 0;
    /** Number of bits representing the current timestamp. */
    private int timestampPlaces = 13;
    /** Counter for records processed at the same millisecond. */
    private int serialPlaces = 5;
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

    public DecimalKeyGenerator() {
    }
    
    public DecimalKeyGenerator(long nodeId) {
        //assert nodePlaces >= 1;
        //assert (nodeId < IMath.lpow(10, nodePlaces));
        
        // Assign node id and check, if it fits into node bits
        this.maxSerial = lpow(10, serialPlaces) - 1;
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
    public DecimalKeyGenerator(long nodeId, int nodePlaces, int timestampPlaces, int serialPlaces) {
        this(nodeId);
        
        //assert nodePlaces >= 1;
        //assert (nodeId < IMath.lpow(10, nodePlaces));
        assert timestampPlaces >= 1;
        assert calendar.getTimeInMillis() < lpow(10, timestampPlaces);
        assert serialPlaces >= 1;
        assert ((timestampPlaces + serialPlaces) <= 18);

        // Assign node id and check, if it fits into node bits
        this.nodeId = nodeId;
        // this.nodePlaces = nodePlaces;
        this.timestampPlaces = timestampPlaces;
        this.serialPlaces = serialPlaces;
        
        this.maxSerial = lpow(10, serialPlaces) - 1;
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
                //(IMath.lpow(10, timestampPlaces + serialPlaces) * nodeId) +
                (lpow(10, serialPlaces) * lastTimestamp) +
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
    
    
    public static long lpow(long base, long exp) {
        int result = 1;
        while (exp > 0) {
            if ((exp & 1) > 0)
                result *= base;
            exp >>= 1;
            base *= base;
        }
        return result;
    }    

}
