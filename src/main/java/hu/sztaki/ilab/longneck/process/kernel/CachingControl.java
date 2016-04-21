package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.CheckError;
import hu.sztaki.ilab.longneck.process.FrameAddress;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.BlockUtils;
import hu.sztaki.ilab.longneck.process.block.Caching;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
class CachingControl implements StartHandler, EndHandler {
        /** The logger instance. */
        private final Logger LOG = Logger.getLogger(CachingControl.class);
        /** The caching instance from the project. */
        private final Caching caching;
        /** True, if the currently processed record was a hit. */
        private boolean hit = false;
        /** The key of the cache. */
        private String cacheKey;

        public CachingControl(Caching caching) {
            this.caching = caching;
        }

        @Override
        public void beforeChildren(KernelState kernelState, Record record) 
                throws RedirectException {
            
            VariableSpace variables = kernelState.getLastExecutionFrame().getVariables();
            cacheKey = BlockUtils.getValue(caching.getApplyTo().get(0), record, variables);
            List<Field> cacheValue = caching.getCacheElement(cacheKey);
                    
            //cache hit
            if (cacheValue != null) {
                for (Field f : cacheValue) {
                    record.add(new Field(f));
                }
                
                hit = true;
                LOG.debug("Cache hit.");
                throw new RedirectException(FrameAddress.RETURN);
            }
            
            LOG.debug("Cache miss.");
        }

        @Override
        public void afterChildren(KernelState kernelState, Record record) throws CheckError {
            if (hit == false) {
                
                List<Field> cacheValue = new ArrayList<Field>(caching.getOutputFields().size());
                
                for (String s : caching.getOutputFields()) {

                    Field f = new Field(record.get(s));
                    if (f == null) {
                        f = new Field(s);
                    }
                    cacheValue.add(f);
                }
                
                caching.putCacheElement(cacheKey, cacheValue);
            }
        }
        
        @Override
        public CachingControl clone() {
            try {
                return (CachingControl) super.clone();
            } catch (CloneNotSupportedException ex) {
                throw new AssertionError(ex);
            }
        }
    }
