package hu.sztaki.ilab.longneck.process;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public interface SourceInfoContainer {    
    
    public SourceInfo getSourceInfo();
    public void setSourceInfo(SourceInfo sourceInfo);
    public FrameAddress getFrameAddress();
    public void setFrameAddress(FrameAddress address);
    
}
