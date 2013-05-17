package hu.sztaki.ilab.longneck.process;

import hu.sztaki.ilab.longneck.bootstrap.Repository;
import hu.sztaki.ilab.longneck.process.block.Block;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class FrameAddressResolver {
    
    protected final Logger LOG = Logger.getLogger(FrameAddressResolver.class);
    
    /** The file id counter. */
    private int fileIdCounter = 0;
    /** The file url to id map. */
    private Map<String,Integer> fileIdMap = new HashMap<String,Integer>();
    /** The address -> per file map -> frame map. */
    private Map<Integer, Map<Integer,Block>> fileMap = 
            new HashMap<Integer,Map<Integer,Block>>();
    
    public void put(Block block) {
        FrameAddress address = getAddress(block);
        
        Map<Integer,Block> blockMap = fileMap.get(address.getFileId());
        if (blockMap == null) {
            blockMap = new HashMap<Integer, Block>();            
            fileMap.put(address.getFileId(), blockMap);
        }
        
        block.setFrameAddress(address);        
        blockMap.put(block.getSourceInfo().getSequenceId(), block);
    }
    
    public Block get(FrameAddress address) {
        try {
            return fileMap.get(address.getFileId()).get(address.getSequenceId());
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public FrameAddress getAddress(Block block) {
        String url = null;
        try {
            url = block.getSourceInfo().getDocumentUrl();
        } catch (Exception e) {
            LOG.error("Reading block source failed, block: " + block.toString() + ", sourceInfo: " 
                    + block.getSourceInfo(), e) ;
        }
        if (url == null || "".equals(url)) {
            throw new RuntimeException("Specified url was null or empty on block: " + 
                    block.getClass().getName());
        }
        
        int fileId = getFileId(url);
        return new FrameAddress(fileId, block.getSourceInfo().getSequenceId());
    }
    
    private int getFileId(String url) {
        Integer id = fileIdMap.get(url);
        if (id == null) {
            ++fileIdCounter;
            id = fileIdCounter;
            
            fileIdMap.put(url, fileIdCounter);
        }
        
        return id;
    }
    
}
