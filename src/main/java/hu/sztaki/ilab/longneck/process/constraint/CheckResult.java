package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.process.SourceInfoContainer;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class CheckResult {

    /** The constraint that did the test. */
    private final SourceInfoContainer sourceInfoContainer;
    /** The result of the check. */
    private final boolean passed;
    /** The name of the failed field or variable. */
    private final String field;
    /** The value of the failed field or variable. */
    private final String value;
    /** The details of the failure. */
    private final String details;
    /** The context of the given error, aiming to help finding the exact location it occurred */
    private final String context;
    /** The causes of this constraint failure, if any. */
    private final List<CheckResult> causes;

    public CheckResult(SourceInfoContainer sourceInfoContainer, boolean passed,
            String field, String value, String details, String context) {
        this.sourceInfoContainer = sourceInfoContainer;
        this.passed = passed;
        this.field = field;
        this.value = value;
        this.details = details;
        this.causes = null;
        this.context = context;
    }

    public CheckResult(SourceInfoContainer sourceInfoContainer, boolean passed,
            String field, String value, String details) {
        this.sourceInfoContainer = sourceInfoContainer;
        this.passed = passed;
        this.field = field;
        this.value = value;
        this.details = details;
        this.causes = null;
        this.context = null;
    }

    public CheckResult(SourceInfoContainer sourceInfoContainer, boolean passed,
            String field, String value, String details, String context, List<CheckResult> causes) {
        this.sourceInfoContainer = sourceInfoContainer;
        this.passed = passed;
        this.field = field;
        this.value = value;
        this.details = details;
        this.context = context;
        this.causes = Collections.unmodifiableList(causes);
    }

    public CheckResult(SourceInfoContainer sourceInfoContainer, boolean passed,
            String field, String value, String details, List<CheckResult> causes) {
        this.sourceInfoContainer = sourceInfoContainer;
        this.passed = passed;
        this.field = field;
        this.value = value;
        this.details = details;
        this.context = null;
        this.causes = Collections.unmodifiableList(causes);
    }

    public CheckResult(CheckResult result, SourceInfoContainer sourceInfoContainer,
            String details, String context) {
        this.sourceInfoContainer = sourceInfoContainer;
        this.details = details;
        this.context = context ;
        this.passed = result.passed;
        this.field = result.field;
        this.value = result.value;
        this.causes = result.causes;
    }

    public CheckResult(CheckResult result, SourceInfoContainer sourceInfoContainer,
            String details) {
        this.sourceInfoContainer = sourceInfoContainer;
        this.details = details;
        this.context = null ;
        this.passed = result.passed;
        this.field = result.field;
        this.value = result.value;
        this.causes = result.causes;
    }

    /**
     * Returns if the check was passed.
     *
     * @return The check result.
     */
    public boolean isPassed() {
        return passed;
    }

    /**
     * Returns the failure details.
     *
     * The details contain the nature of the test performed, that allows the test to be
     * reconstructed in full.
     * @return The failure details.
     */
    public String getDetails() {
        return details;
    }

    /**
     * Returns the failure context.
     *
     * The details contain the nature of the test performed, that allows the test to be
     * reconstructed in full.
     * @return The failure details.
     */
    public String getContext() {
        return context;
    }

    /**
     * Returns the field that was tested.
     *
     * It contains the field or variable name under test.
     *
     * @return The name of the field or variable being tested.
     */
    public String getField() {
        return field;
    }

    /**
     * Returns the value of the field being tested.
     *
     * @return The value of the field being tested.
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the causes.
     * @param causes The previously created constraint results.
     */
    public List<CheckResult> getCauses() {
        return causes == null ? null : Collections.unmodifiableList(causes);
    }

    public SourceInfoContainer getSourceInfoContainer() {
        return sourceInfoContainer;
    }
}
