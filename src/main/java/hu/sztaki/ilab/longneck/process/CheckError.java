package hu.sztaki.ilab.longneck.process;

import hu.sztaki.ilab.longneck.process.constraint.CheckResult;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class CheckError extends BlockError {
    
    /** The result of the check, that has caused the failure. */
    private final CheckResult checkResult;

    
    public CheckError(CheckResult checkResult) {
        super(checkResult.getDetails());
        
        this.checkResult = checkResult;
    }
    
    public CheckError(CheckResult checkResult, String blockName) {
        super(checkResult.getDetails(), blockName);
        this.checkResult = checkResult;
    }

    public CheckError(CheckResult checkResult, String blockName, Throwable cause) {
        super(checkResult.getDetails(), blockName, cause);
        this.checkResult = checkResult;
    }
    
    public CheckResult getCheckResult() {
        return checkResult;
    }
}
