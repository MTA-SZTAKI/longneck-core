package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.process.FrameAddress;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class RedirectException extends Exception {

    private final FrameAddress address;
    private final boolean subframe;
    
    public RedirectException(FrameAddress address, Throwable cause) {
        super("", cause, false, false);
        this.address = address;
        subframe = false;
    }

    public RedirectException(FrameAddress address, String message, Throwable cause) {
        super(message, cause, false, false);
        this.address = address;
        subframe = false;
    }

    public RedirectException(FrameAddress address, String message) {
        super(message, null, false, false);
        this.address = address;
        subframe = false;
    }

    public RedirectException(FrameAddress address) {
        super("", null, false, false);
        this.address = address;
        subframe = false;
    }

    public RedirectException(FrameAddress address, boolean subframe, Throwable cause) {
        super("", cause, false, false);
        this.address = address;
        this.subframe = subframe;
    }

    public RedirectException(FrameAddress address, boolean subframe, String message, Throwable cause) {
        super(message, cause, false, false);
        this.address = address;
        this.subframe = subframe;
    }

    public RedirectException(FrameAddress address, boolean subframe, String message) {
        super(message, null, false, false);
        this.address = address;
        this.subframe = subframe;
    }

    public RedirectException(FrameAddress address, boolean subframe) {
        super("", null, false, false);
        this.address = address;
        this.subframe = subframe;
    }
    
    public final FrameAddress getAddress() {
        return address;
    }

    public boolean isSubframe() {
        return subframe;
    }
}
