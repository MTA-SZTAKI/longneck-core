package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.access.SimpleDatabaseTarget;
import hu.sztaki.ilab.longneck.process.block.BlockUtils;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
 * @author hajdu - 2015 febr
 */
public class IntervalCheckConstraint extends AbstractAtomicConstraint{
    
    private String lowerBound, upperBound, dateFormat, numberFormat, intervalType;
    private List<String> applyTo;

    public String getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(String numberFormat) {
        this.numberFormat = numberFormat;
    }
    
    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    public String getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }

    public String getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(String lowerBound) {
        this.lowerBound = lowerBound;
    }

    public String getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(String upperBound) {
        this.upperBound = upperBound;
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
        
        //Determining function mode: date or number
        if (dateFormat != null && numberFormat != null) {
//            conflicting parameters: Throw error
            String eDetails = String.format("FunctionModeError - Both date-format and number-format not allowed");
            return new CheckResult(this, false, null, null, null, eDetails);
            
        } else if (dateFormat == null && numberFormat != null) {
//            function mode - number
            return parseNumInterval(record, scope, numberFormat, lowerBound, upperBound, intervalType);
        } else if (dateFormat != null && numberFormat == null) {
//            function mode - date
            return parseDateInterval(record, scope, dateFormat, lowerBound, upperBound, intervalType);
        } else {
//            function mode -  default-number
            return parseDefaultNumInterval(record, scope, lowerBound, upperBound, intervalType);
        }
    }

    private CheckResult parseNumInterval(Record record, VariableSpace scope, String numberFormat, String lowerBound, String upperBound, String intervalType) {
//          Prepare result variable
        List<CheckResult> results = new ArrayList<>(applyTo.size());
        
//        Determining optional parameters
        boolean lowerSet = (lowerBound != null);
        boolean upperSet = (upperBound != null);
        
        Double lower = null;
        Double upper = null;
        try {
            if(lowerSet)
                lower = formatNumber(lowerBound, numberFormat).doubleValue();
            if (upperSet) 
                upper = formatNumber(upperBound, numberFormat).doubleValue();
        
//        main flow control: intervalType
        switch (intervalType) {
            case "closed":
//                            processing each examined field as CLOSED interval type
                for (String examineeField : applyTo) {
//                    Fetching date value
                    String examineeValue = BlockUtils.getValue(examineeField, record, scope);
                    try { 
                        double examinee = formatNumber(examineeValue, numberFormat).doubleValue();

//                         Details
                        String details = String.format("Interval - %s Number - format: %12s from: %12s to: %12s examinee: %12s"
                                , intervalType, numberFormat, lower, upper, examinee);

                        if (!upperSet && !lowerSet) {
//                            None ot the margins set: ERROR
                            String eDetails = String.format("InsufficientArgumentNumberError - margin-from or margin-to required.");
                            return new CheckResult(this, false, null, null, null, eDetails);

                        } else if (upperSet && !lowerSet) {
//                            Upper margin set
                            
                            if (examinee <= upper) {
//                                examined date is before the upper margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else if (!upperSet && lowerSet) {
//                            Lower margin set;
                            
                            if (examinee >= lower) {
                                //examined date is after the margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else {
//                            both margins set
                            
                            if ((examinee >= lower) && (examinee <= upper)) {
//                                examined date falls in the intervall
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }
                        }


                    } catch (Exception ex) {
//                        Parsing Error: Examinee
                        String eDetails = String.format("Parsing Error - Examinee: examinee: %12s", examineeValue);
                        return new CheckResult(this, false, null, null, null, eDetails);
                    }
                }
                break;
            case "open-closed":
//                            processing each examined field as OPEN-CLOSED interval type
                for (String examineeField : applyTo) {
//                    Fetching date value
                    String examineeValue = BlockUtils.getValue(examineeField, record, scope);
                    try { 
                        double examinee = formatNumber(examineeValue, numberFormat).doubleValue();

//                         Details
                        String details = String.format("Interval - %s Number - format: %12s from: %12s to: %12s examinee: %12s"
                                , intervalType, numberFormat, lower, upper, examinee);

                        if (!upperSet && !lowerSet) {
//                            None ot the margins set: ERROR
                            String eDetails = String.format("InsufficientArgumentNumberError - margin-from or margin-to required.");
                            return new CheckResult(this, false, null, null, null, eDetails);

                        } else if (upperSet && !lowerSet) {
//                            Upper margin set
                            
                            if (examinee <= upper) {
//                                examined date is before the upper margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else if (!upperSet && lowerSet) {
//                            Lower margin set;
                            
                            if (examinee > lower) {
                                //examined date is after the margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else {
//                            both margins set
                            
                            if ((examinee > lower) && (examinee <= upper)) {
//                                examined date falls in the intervall
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }
                        }


                    } catch (Exception ex) {
//                        Parsing Error: Examinee
                        String eDetails = String.format("Parsing Error - Examinee: examinee: %12s", examineeValue);
                        return new CheckResult(this, false, null, null, null, eDetails);
                    }
                }
                break;
            case "closed-open":
//                            processing each examined field as CLOSED-OPEN interval type
                for (String examineeField : applyTo) {
//                    Fetching date value
                    String examineeValue = BlockUtils.getValue(examineeField, record, scope);
                    try { 
                        double examinee = formatNumber(examineeValue, numberFormat).doubleValue();

//                         Details
                        String details = String.format("Interval - %s Number - format: %12s from: %12s to: %12s examinee: %12s"
                                , intervalType, numberFormat, lower, upper, examinee);

                        if (!upperSet && !lowerSet) {
//                            None ot the margins set: ERROR
                            String eDetails = String.format("InsufficientArgumentNumberError - margin-from or margin-to required.");
                            return new CheckResult(this, false, null, null, null, eDetails);

                        } else if (upperSet && !lowerSet) {
//                            Upper margin set
                            
                            if (examinee < upper) {
//                                examined date is before the upper margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else if (!upperSet && lowerSet) {
//                            Lower margin set;
                            
                            if (examinee >= lower) {
                                //examined date is after the margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else {
//                            both margins set
                            
                            if ((examinee >= lower) && (examinee < upper)) {
//                                examined date falls in the intervall
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }
                        }


                    } catch (Exception ex) {
//                        Parsing Error: Examinee
                        String eDetails = String.format("Parsing Error - Examinee: examinee: %12s", examineeValue);
                        return new CheckResult(this, false, null, null, null, eDetails);
                    }
                }    
                break;
            case "open":
//                            processing each examined field as OPEN interval type
                for (String examineeField : applyTo) {
//                    Fetching date value
                    String examineeValue = BlockUtils.getValue(examineeField, record, scope);
                    try { 
                        double examinee = formatNumber(examineeValue, numberFormat).doubleValue();

//                         Details
                        String details = String.format("Interval - %s Number - format: %12s from: %12s to: %12s examinee: %12s"
                                , intervalType, numberFormat, lower, upper, examinee);

                        if (!upperSet && !lowerSet) {
//                            None ot the margins set: ERROR
                            String eDetails = String.format("InsufficientArgumentNumberError - margin-from or margin-to required.");
                            return new CheckResult(this, false, null, null, null, eDetails);

                        } else if (upperSet && !lowerSet) {
//                            Upper margin set
                            
                            if (examinee < upper) {
//                                examined date is before the upper margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else if (!upperSet && lowerSet) {
//                            Lower margin set;
                            
                            if (examinee > lower) {
                                //examined date is after the margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else {
//                            both margins set
                            
                            if ((examinee > lower) && (examinee < upper)) {
//                                examined date falls in the intervall
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }
                        }


                    } catch (Exception ex) {
//                        Parsing Error: Examinee
                        String eDetails = String.format("Parsing Error - Examinee: examinee: %12s", examineeValue);
                        return new CheckResult(this, false, null, null, null, eDetails);
                    }
                }
                break;
            default:
//                invalid parameter
                String eDetails = String.format("InvalidIntervalTypeError - interval-type possible values: closed, open-closed, closed-open, open");
                return new CheckResult(this, false, null, null, null, eDetails);             
        }
       

        

        
        } catch (Exception ex) {
            //Parsing Error: Margins
            String eDetails = String.format("Parsing Error - Margins: from: %12s; to: %12s; format: %12s", lowerBound, upperBound, dateFormat);
            return new CheckResult(this, false, null, null, null, eDetails);
        }
        
        return new CheckResult(this, true, null, null, null, results);
    }

    private CheckResult parseDateInterval(Record record, VariableSpace scope, String dateFormat, String lowerBound, String upperBound, String intervalType) {
        
//                 Prepare result variable
        List<CheckResult> results = new ArrayList<>(applyTo.size());
        
//        Determining optional parameters
        boolean lowerSet = (lowerBound != null);
        boolean upperSet = (upperBound != null);
        
        DateFormat formatter = new SimpleDateFormat(dateFormat);
        Date lowerDate = null;
        Date upperDate = null;
        try {
            if(lowerSet)
                lowerDate = formatter.parse(lowerBound);
            if (upperSet) 
                upperDate = formatter.parse(upperBound);
        
//        main flow control: intervalType
        switch (intervalType) {
            case "closed":
//                            processing each examined field as CLOSED interval type
                for (String examineeField : applyTo) {
//                    Fetching date value
                    String examineeValue = BlockUtils.getValue(examineeField, record, scope);
                    try { 
                        Date examineeDate = formatter.parse(examineeValue);

//                         Details
                        String details = String.format("Interval - %s Date -  format: %12s from: %12s to: %12s examinee: %12s"
                                , intervalType, dateFormat, lowerDate, upperDate, examineeDate);

                        if (!upperSet && !lowerSet) {
//                            None ot the margins set: ERROR
                            String eDetails = String.format("InsufficientArgumentNumberError - margin-from or margin-to required.");
                            return new CheckResult(this, false, null, null, null, eDetails);

                        } else if (upperSet && !lowerSet) {
//                            Upper margin set
                            
                            if (examineeDate.before(upperDate) || examineeDate.equals(upperDate)) {
//                                examined date is before the upper margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else if (!upperSet && lowerSet) {
//                            Lower margin set;
                            
                            if (examineeDate.after(lowerDate) || examineeDate.equals(lowerDate)) {
                                //examined date is after the margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else {
//                            both margins set
                            
                            if ((examineeDate.after(lowerDate) || examineeDate.equals(lowerDate)) && (examineeDate.before(upperDate) || examineeDate.equals(upperDate))) {
//                                examined date falls in the intervall
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }
                        }


                    } catch (ParseException ex) {
//                        Parsing Error: Examinee
                        String eDetails = String.format("Parsing Error - Examinee: examinee: %12s; format: %12s", examineeValue, dateFormat);
                        return new CheckResult(this, false, null, null, null, eDetails);
                    }
                }
                break;
            case "open-closed":
//                            processing each examined field as OPEN-CLOSED interval type
                for (String examineeField : applyTo) {
//                    Fetching date value
                    String examineeValue = BlockUtils.getValue(examineeField, record, scope);
                    try { 
                        Date examineeDate = formatter.parse(examineeValue);

//                         Details
                        String details = String.format("Interval - %s Date -  format: %12s from: %12s to: %12s examinee: %12s"
                                , intervalType, dateFormat, lowerDate, upperDate, examineeDate);

                        if (!upperSet && !lowerSet) {
//                            None ot the margins set: ERROR
                            String eDetails = String.format("InsufficientArgumentNumberError - margin-from or margin-to required.");
                            return new CheckResult(this, false, null, null, null, eDetails);

                        } else if (upperSet && !lowerSet) {
//                            Upper margin set
                            
                            if (examineeDate.before(upperDate) || examineeDate.equals(upperDate)) {
//                                examined date is before the upper margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else if (!upperSet && lowerSet) {
//                            Lower margin set;
                            
                            if (examineeDate.after(lowerDate)) {
                                //examined date is after the margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else {
//                            both margins set
                            
                            if ((examineeDate.after(lowerDate)) && (examineeDate.before(upperDate) || examineeDate.equals(upperDate))) {
//                                examined date falls in the intervall
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }
                        }


                    } catch (ParseException ex) {
//                        Parsing Error: Examinee
                        String eDetails = String.format("Parsing Error - Examinee: examinee: %12s; format: %12s", examineeValue, dateFormat);
                        return new CheckResult(this, false, null, null, null, eDetails);
                    }
                }                
                break;
            case "closed-open":
//                            processing each examined field as CLOSED-OPEN interval type
                for (String examineeField : applyTo) {
//                    Fetching date value
                    String examineeValue = BlockUtils.getValue(examineeField, record, scope);
                    try { 
                        Date examineeDate = formatter.parse(examineeValue);

//                         Details
                        String details = String.format("Interval - %s Date -  format: %12s from: %12s to: %12s examinee: %12s"
                                , intervalType, dateFormat, lowerDate, upperDate, examineeDate);

                        if (!upperSet && !lowerSet) {
//                            None ot the margins set: ERROR
                            String eDetails = String.format("InsufficientArgumentNumberError - margin-from or margin-to required.");
                            return new CheckResult(this, false, null, null, null, eDetails);

                        } else if (upperSet && !lowerSet) {
//                            Upper margin set
                            
                            if (examineeDate.before(upperDate)) {
//                                examined date is before the upper margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else if (!upperSet && lowerSet) {
//                            Lower margin set;
                            
                            if (examineeDate.after(lowerDate) || examineeDate.equals(lowerDate)) {
                                //examined date is after the margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else {
//                            both margins set
                            
                            if ((examineeDate.after(lowerDate) || examineeDate.equals(lowerDate)) && (examineeDate.before(upperDate))) {
//                                examined date falls in the intervall
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }
                        }


                    } catch (ParseException ex) {
//                        Parsing Error: Examinee
                        String eDetails = String.format("Parsing Error - Examinee: examinee: %12s; format: %12s", examineeValue, dateFormat);
                        return new CheckResult(this, false, null, null, null, eDetails);
                    }
                }         
                break;
            case "open":
//            processing each examined field as OPEN interval type
                for (String examineeField : applyTo) {
//                    Fetching date value
                    String examineeValue = BlockUtils.getValue(examineeField, record, scope);
                    try { 
                        Date examineeDate = formatter.parse(examineeValue);

//                         Details
                        String details = String.format("Interval - %s Date -  format: %12s from: %12s to: %12s examinee: %12s"
                                , intervalType, dateFormat, lowerDate, upperDate, examineeDate);

                        if (!upperSet && !lowerSet) {
//                            None ot the margins set: ERROR
                            String eDetails = String.format("InsufficientArgumentNumberError - margin-from or margin-to required.");
                            return new CheckResult(this, false, null, null, null, eDetails);

                        } else if (upperSet && !lowerSet) {
//                            Upper margin set
                            
                            if (examineeDate.before(upperDate)) {
//                                examined date is before the upper margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else if (!upperSet && lowerSet) {
//                            Lower margin set;
                            
                            if (examineeDate.after(lowerDate)) {
                                //examined date is after the margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else {
//                            both margins set
                            
                            if (examineeDate.after(lowerDate) && examineeDate.before(upperDate)) {
//                                examined date falls in the intervall
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }
                        }


                    } catch (ParseException ex) {
//                        Parsing Error: Examinee
                        String eDetails = String.format("Parsing Error - Examinee: examinee: %12s; format: %12s", examineeValue, dateFormat);
                        return new CheckResult(this, false, null, null, null, eDetails);
                    }
                }
                break;
            default:
//                invalid parameter
                String eDetails = String.format("InvalidIntervalTypeError - interval-type possible values: closed, open-closed, closed-open, open");
                return new CheckResult(this, false, null, null, null, eDetails);             
        }
       

        

        
        } catch (ParseException ex) {
            //Parsing Error: Margins
            String eDetails = String.format("Parsing Error - Margins: from: %12s; to: %12s; format: %12s", lowerBound, upperBound, dateFormat);
            return new CheckResult(this, false, null, null, null, eDetails);
        }
        
        return new CheckResult(this, true, null, null, null, results);
    }

    private CheckResult parseDefaultNumInterval(Record record, VariableSpace scope, String lowerBound, String upperBound, String intervalType) {
//          Prepare result variable
        List<CheckResult> results = new ArrayList<>(applyTo.size());
        
//        Determining optional parameters
        boolean lowerSet = (lowerBound != null);
        boolean upperSet = (upperBound != null);
        
        Double lower = null;
        Double upper = null;
        try {
            if(lowerSet)
                lower = Double.parseDouble(lowerBound);
            if (upperSet) 
                upper = Double.parseDouble(upperBound);
        
//        main flow control: intervalType
        switch (intervalType) {
            case "closed":
//                            processing each examined field as CLOSED interval type
                for (String examineeField : applyTo) {
//                    Fetching date value
                    String examineeValue = BlockUtils.getValue(examineeField, record, scope);
                    try { 
                        double examinee = Double.parseDouble(examineeValue);

//                         Details
                        String details = String.format("Interval - %s Numbers -  from: %12s to: %12s examinee: %12s"
                                , intervalType, lower, upper, examinee);

                        if (!upperSet && !lowerSet) {
//                            None ot the margins set: ERROR
                            String eDetails = String.format("InsufficientArgumentNumberError - margin-from or margin-to required.");
                            return new CheckResult(this, false, null, null, null, eDetails);

                        } else if (upperSet && !lowerSet) {
//                            Upper margin set
                            
                            if (examinee <= upper) {
//                                examined date is before the upper margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else if (!upperSet && lowerSet) {
//                            Lower margin set;
                            
                            if (examinee >= lower) {
                                //examined date is after the margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else {
//                            both margins set
                            
                            if ((examinee >= lower) && (examinee <= upper)) {
//                                examined date falls in the intervall
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }
                        }


                    } catch (Exception ex) {
//                        Parsing Error: Examinee
                        String eDetails = String.format("Parsing Error - Examinee: examinee: %12s", examineeValue);
                        return new CheckResult(this, false, null, null, null, eDetails);
                    }
                }
                break;
            case "open-closed":
//                            processing each examined field as OPEN-CLOSED interval type
                for (String examineeField : applyTo) {
//                    Fetching date value
                    String examineeValue = BlockUtils.getValue(examineeField, record, scope);
                    try { 
                        double examinee = Double.parseDouble(examineeValue);

//                         Details
                        String details = String.format("Interval - %s Numbers -  from: %12s to: %12s examinee: %12s"
                                , intervalType, lower, upper, examinee);

                        if (!upperSet && !lowerSet) {
//                            None ot the margins set: ERROR
                            String eDetails = String.format("InsufficientArgumentNumberError - margin-from or margin-to required.");
                            return new CheckResult(this, false, null, null, null, eDetails);

                        } else if (upperSet && !lowerSet) {
//                            Upper margin set
                            
                            if (examinee <= upper) {
//                                examined date is before the upper margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else if (!upperSet && lowerSet) {
//                            Lower margin set;
                            
                            if (examinee > lower) {
                                //examined date is after the margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else {
//                            both margins set
                            
                            if ((examinee > lower) && (examinee <= upper)) {
//                                examined date falls in the intervall
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }
                        }


                    } catch (Exception ex) {
//                        Parsing Error: Examinee
                        String eDetails = String.format("Parsing Error - Examinee: examinee: %12s", examineeValue);
                        return new CheckResult(this, false, null, null, null, eDetails);
                    }
                }
                break;
            case "closed-open":
//                            processing each examined field as CLOSED-OPEN interval type
                for (String examineeField : applyTo) {
//                    Fetching date value
                    String examineeValue = BlockUtils.getValue(examineeField, record, scope);
                    try { 
                        double examinee = Double.parseDouble(examineeValue);

//                         Details
                        String details = String.format("Interval - %s Numbers -  from: %12s to: %12s examinee: %12s"
                                , intervalType, lower, upper, examinee);

                        if (!upperSet && !lowerSet) {
//                            None ot the margins set: ERROR
                            String eDetails = String.format("InsufficientArgumentNumberError - margin-from or margin-to required.");
                            return new CheckResult(this, false, null, null, null, eDetails);

                        } else if (upperSet && !lowerSet) {
//                            Upper margin set
                            
                            if (examinee < upper) {
//                                examined date is before the upper margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else if (!upperSet && lowerSet) {
//                            Lower margin set;
                            
                            if (examinee >= lower) {
                                //examined date is after the margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else {
//                            both margins set
                            
                            if ((examinee >= lower) && (examinee < upper)) {
//                                examined date falls in the intervall
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }
                        }


                    } catch (Exception ex) {
//                        Parsing Error: Examinee
                        String eDetails = String.format("Parsing Error - Examinee: examinee: %12s", examineeValue);
                        return new CheckResult(this, false, null, null, null, eDetails);
                    }
                }    
                break;
            case "open":
//                            processing each examined field as OPEN interval type
                for (String examineeField : applyTo) {
//                    Fetching date value
                    String examineeValue = BlockUtils.getValue(examineeField, record, scope);
                    try { 
                        double examinee = Double.parseDouble(examineeValue);

//                         Details
                        String details = String.format("Interval - %s Numbers -  from: %12s to: %12s examinee: %12s"
                                , intervalType, lower, upper, examinee);

                        if (!upperSet && !lowerSet) {
//                            None ot the margins set: ERROR
                            String eDetails = String.format("InsufficientArgumentNumberError - margin-from or margin-to required.");
                            return new CheckResult(this, false, null, null, null, eDetails);

                        } else if (upperSet && !lowerSet) {
//                            Upper margin set
                            
                            if (examinee < upper) {
//                                examined date is before the upper margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else if (!upperSet && lowerSet) {
//                            Lower margin set;
                            
                            if (examinee > lower) {
                                //examined date is after the margin
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }

                        } else {
//                            both margins set
                            
                            if ((examinee > lower) && (examinee < upper)) {
//                                examined date falls in the intervall
                               results.add(new CheckResult(this, true, examineeField, examineeValue, details));

                            } else {
//                                examined date falls out of the intervall
                                results.add(new CheckResult(this, false, examineeField, examineeValue, details));
                                return new CheckResult(this, false, null, null, null, results);
                            }
                        }


                    } catch (Exception ex) {
//                        Parsing Error: Examinee
                        String eDetails = String.format("Parsing Error - Examinee: examinee: %12s", examineeValue);
                        return new CheckResult(this, false, null, null, null, eDetails);
                    }
                }
                break;
            default:
//                invalid parameter
                String eDetails = String.format("InvalidIntervalTypeError - interval-type possible values: closed, open-closed, closed-open, open");
                return new CheckResult(this, false, null, null, null, eDetails);             
        }
       

        

        
        } catch (Exception ex) {
            //Parsing Error: Margins
            String eDetails = String.format("Parsing Error - Margins: from: %12s; to: %12s; format: %12s", lowerBound, upperBound, dateFormat);
            return new CheckResult(this, false, null, null, null, eDetails);
        }
        
        return new CheckResult(this, true, null, null, null, results);
    }
 
    private Number formatNumber(String value, String format) throws ParseException {
        DecimalFormat nf = new DecimalFormat(format);
        return nf.parse(value);
    } 
}
