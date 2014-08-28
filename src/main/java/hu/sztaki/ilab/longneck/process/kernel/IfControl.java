package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.FrameAddress;
import hu.sztaki.ilab.longneck.process.block.If;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;

/**
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
class IfControl implements StartHandler, SuccessHandler {

    /**
     * The If block that is controlled.
     */
    private final If ifObj;

    public IfControl(If ifObj) {
        this.ifObj = ifObj;
    }

    @Override
    public void beforeChildren(KernelState kernelState, Record record) throws RedirectException {
        CheckResult result = ifObj.getCondition().check(
                record, kernelState.getLastExecutionFrame().getVariables());

        if (result.isPassed() && ifObj.getThenBranch() != null) {
            throw new RedirectException(ifObj.getThenBranch().getFrameAddress(), false);
        } else if (!result.isPassed() && ifObj.getElseBranch() != null) {
            throw new RedirectException(ifObj.getElseBranch().getFrameAddress(), false);
        } else {
            throw new RedirectException(FrameAddress.RETURN, false);
        }
    }

    @Override
    public void onSuccess(KernelState kernelState, Record record) throws RedirectException {
        throw new RedirectException(FrameAddress.RETURN, false);
    }

    @Override
    public IfControl clone() {
        try {
            return (IfControl) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError(ex);
        }
    }

}
