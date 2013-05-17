package hu.sztaki.ilab.longneck.process;

import hu.sztaki.ilab.longneck.process.block.GenericBlock;
import java.util.HashMap;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class BlockPackage extends AbstractPackage<GenericBlock> {

    public BlockPackage() {
        this.map = new HashMap<String,GenericBlock>();
    }
    
    public BlockPackage(String packageId) {
        this();
        
        this.packageId = packageId;
    }

    public GenericBlock getBlock(String id, String version) {
        return map.get(String.format("%1$s:%2$s", id, version));        
    }

    public void addGenericBlock(GenericBlock block) {
        map.put(String.format("%1$s:%2$s", block.getId(), block.getVersion()), block);
    }

    @Override
    public FileType getType() {
        return FileType.Block;
    }
}
