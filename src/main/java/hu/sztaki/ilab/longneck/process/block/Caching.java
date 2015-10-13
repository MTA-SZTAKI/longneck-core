package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.LRUMap;



/**
 * Block that caches block outputs as key-value pairs it contains.
 *
 * Each thread has its own internal cache (LRUMap). Cache key is the field
 * value defined in the "apply-to" parameter. Note that only one field name
 * is permitted here! Cache value is a list of fields defined in the
 * "output-fields" parameter
 *
 * @author Bendig Loránd <lbendig@ilab.sztaki.hu>
 */
public class Caching extends AbstractAtomicBlock implements CompoundBlock {

    /** The list of inside blocks. */
    private List<? extends Block> blocks;
    /** The thread-local cache. */
//    private CacheMapBuilder localCache;
    /** Internal cache */
//    transient private Map<String, List<Field>> cache = null;
    private Map<String, List<Field>> cache;
    /** Maximum size. */
    private int size = 100;
    /** Value of the cache */
    private List<String> outputFields;

    public Caching() {
//        localCache = new CacheMapBuilder(size);
        cache = (Map<String, List<Field>>) (Collections.synchronizedMap(new LRUMap(size)));
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public List<Block> getBlocks() {
        return (List<Block>) blocks;
    }

    @Override
    public void setBlocks(List<? extends Block> blocks) {
        this.blocks = blocks;
    }

    @Override
    public boolean hasPosition(int pos) {
        return (blocks.size() - 1 >= pos);
    }

    public void setOutputFields(List<String> outputFields) {
        this.outputFields = outputFields;
    }

    public List<String> getOutputFields() {
        return outputFields;
    }

    public void setOutputFields(String outputFields) {
        // Assign filtered list
        this.outputFields = BlockUtils.splitIdentifiers(outputFields);
    }

    @Override
    public void apply(Record record, VariableSpace parentScope) {
    }

    public List<Field> getCacheElement(String key) {
//        if (cache == null) {
//            cache = localCache.get();
//        }

//        return cache.get(sourceInfo.getLine() + "_" + key);
        return cache.get(sourceInfo.getLine() + "_" + key);
    }

    public void putCacheElement(String key, List<Field> value) {
//        if (cache == null) {
//            cache = localCache.get();
//        }

        cache.put((sourceInfo.getLine() + "_" + key) , value);
    }

    @Override
    public Caching clone() {
        Caching copy = (Caching) super.clone();

        if (blocks != null) {
            copy.blocks = new ArrayList<Block>(blocks.size());
            for (Block b : blocks) {
                ((List<Block>) copy.blocks).add(b.clone());
            }
        }

//        copy.localCache = new CacheMapBuilder(size);
//        copy.cache = null;
        copy.cache = (Map<String, List<Field>>) (Collections.synchronizedMap(new LRUMap(size)));
        copy.cache.putAll(cache);

        if (outputFields != null) {
            copy.outputFields =  new ArrayList<String>(outputFields.size());
            copy.outputFields.addAll(outputFields);
        }

        return copy;
    }

//    public static class CacheMapBuilder extends ThreadLocal<Map> {
//        private final int size;
//
//        public CacheMapBuilder(int size) {
//            this.size = size;
//        }
//
//        public int getSize() {
//            return size;
//        }
//
//        @Override
//        protected Map initialValue() {
//            return new LRUMap(size);
//        }
//    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.blocks != null ? this.blocks.hashCode() : 0);
        hash = 59 * hash + this.size;
        hash = 59 * hash + (this.outputFields != null ? this.outputFields.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Caching other = (Caching) obj;
        if (super.equals(obj) == false) {
            return false;
        }
        if (this.blocks != other.blocks && (this.blocks == null || !this.blocks.equals(other.blocks))) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        if (this.outputFields != other.outputFields && (this.outputFields == null || !this.outputFields.equals(other.outputFields))) {
            return false;
        }
        return true;
    }


}
