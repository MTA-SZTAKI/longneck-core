package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.access.SimpleDatabaseTarget;
import hu.sztaki.ilab.longneck.process.block.BlockUtils;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Checks whether the input falls between the given margins.
 * 
 * @author hajdu
 */
public class IntervalCheckConstraint extends AbstractAtomicConstraint{
    
    private String marginFrom, marginTo, inputFormat;
    private List<String> applyTo;

    public String getMarginFrom() {
        return marginFrom;
    }

    public void setMarginFrom(String marginFrom) {
        this.marginFrom = marginFrom;
    }

    public String getMarginTo() {
        return marginTo;
    }

    public void setMarginTo(String marginTo) {
        this.marginTo = marginTo;
    }

    @Override
    public void setApplyTo(List<String> fieldNames) {
        this.applyTo = fieldNames;
    }

    public List<String> getApplyTo() {
        return applyTo;
    }
    
    public void setApplyTo(String applyTo) {
        // Assign filtered list
        this.applyTo = BlockUtils.splitIdentifiers(applyTo);
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }
    
    @Override
    public IntervalCheckConstraint clone() {
        IntervalCheckConstraint copy = (IntervalCheckConstraint) super.clone();
        if (applyTo != null) {
            copy.applyTo = new ArrayList<String>(applyTo.size());
            copy.applyTo.addAll(applyTo);
        }
        
        return copy;
    }
    

    @Override
    public CheckResult check(Record record, VariableSpace scope) {
    
        // Prepare result variable
        List<CheckResult> results = new ArrayList<CheckResult>(applyTo.size());
       
        //processing input fields
        DateFormat formatter = new SimpleDateFormat(inputFormat);
        Date fromDate;
        Date toDate;
        try {
            fromDate = formatter.parse(marginFrom);
            toDate = formatter.parse(marginTo);
        
            //processing each examined field
            for (String examineeField : applyTo) {
                //Fetching date value
                String examineeValue = BlockUtils.getValue(examineeField, record, scope);

                try { 
                    Date examineeDate = formatter.parse(examineeValue);

                    // Details
                    String details = String.format("Date Values: form: %12s; to: %12s; examinee: %12s; format: %12s", fromDate, toDate, examineeDate, inputFormat);

                    if (examineeDate.after(fromDate) && examineeDate.before(toDate)) {
                        //examined date falls in the intervall
                       results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                    } else {
                        //examined date falls out of the intervall
                        results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                    }
                } catch (ParseException ex) {
                    //Parsing Error: Examinee
                    String eDetails = String.format("Parsing Error - Examinee: examinee: %12s; format: %12s", examineeValue, inputFormat);
                    return new CheckResult(this, false, null, null, null, eDetails);
                }
            }
        
        } catch (ParseException ex) {
            //Parsing Error: Margins
            String eDetails = String.format("Parsing Error - Margins: form: %12s; to: %12s; format: %12s", marginFrom, marginTo, inputFormat);
            return new CheckResult(this, false, null, null, null, eDetails);
        }
        
        return new CheckResult(this, true, null, null, null, results);

    }
    
}
