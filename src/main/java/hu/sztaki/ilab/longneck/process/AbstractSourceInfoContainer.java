package hu.sztaki.ilab.longneck.process;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
abstract public class AbstractSourceInfoContainer implements SourceInfoContainer, Cloneable {
    
    protected SourceInfo sourceInfo;
    protected FrameAddress frameAddress;
    protected final Logger log = Logger.getLogger(this.getClass());
    
    public AbstractSourceInfoContainer() {
    }

    @Override
    public SourceInfo getSourceInfo() {
        return sourceInfo;
    }

    @Override
    public void setSourceInfo(SourceInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
    }
    
    public void setSourceInfo(String jsonSource) {
        try {
            JSONObject jsonObj = new JSONObject(jsonSource);
            sourceInfo = new SourceInfo(jsonObj);
        } catch (JSONException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public FrameAddress getFrameAddress() {
        return frameAddress;
    }

    @Override
    public void setFrameAddress(FrameAddress frameAddress) {
        this.frameAddress = frameAddress;
    }
    

    @Override
    public String toString() {
        return this.getClass().getName();
    }
    
    @Override
    protected AbstractSourceInfoContainer clone() {
        AbstractSourceInfoContainer clone;
        try {
            clone = (AbstractSourceInfoContainer) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError(ex);
        }
        if (sourceInfo != null) {
            clone.sourceInfo = new SourceInfo(sourceInfo);
        }
        if (frameAddress != null) {
            clone.frameAddress = new FrameAddress(frameAddress.getFileId(), frameAddress.getSequenceId());
        }
        return clone;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.sourceInfo != null ? this.sourceInfo.hashCode() : 0);
        hash = 23 * hash + (this.frameAddress != null ? this.frameAddress.hashCode() : 0);
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
        final AbstractSourceInfoContainer other = (AbstractSourceInfoContainer) obj;
        if (this.sourceInfo != other.sourceInfo && (this.sourceInfo == null || !this.sourceInfo.equals(other.sourceInfo))) {
            return false;
        }
        if (this.frameAddress != other.frameAddress && (this.frameAddress == null || !this.frameAddress.equals(other.frameAddress))) {
            return false;
        }
        return true;
    }
}
