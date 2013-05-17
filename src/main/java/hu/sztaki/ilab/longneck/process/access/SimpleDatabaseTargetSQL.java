package hu.sztaki.ilab.longneck.process.access;

/**
 * An SQL operation representation for simple database targets. 
 * 
 * Possibly caused by bug (http://jira.codehaus.org/browse/CASTOR-655), a
 * separate class must be used to marshal both insert query and the numeric
 * fields attributes.
 * 
 * @author Csaba Sidló <sidlo@sztaki.mta.hu>
 */

public class SimpleDatabaseTargetSQL {

    private String sql;
    private String numericFields;

    public void setNumericFields(String numericFields) {
        this.numericFields = numericFields;
    }

    public String getNumericFields() {
        return numericFields;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

}

