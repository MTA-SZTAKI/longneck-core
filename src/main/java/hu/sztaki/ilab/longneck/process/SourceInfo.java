package hu.sztaki.ilab.longneck.process;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

/**
 * 
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class SourceInfo implements JSONString {
    
    /** The url where the file was loaded from. */
    private final String documentUrl;
    /** The block id, or null, if main process file. */
    private final String containerBlockId;
    /** The sequence id of the executed operation. */
    private final int sequenceId;
    /** The line where the current frame was defined. */
    private final int line;
    /** The column where the current frame was defined. */
    private final int column;
    
    public SourceInfo(String documentUrl, String containerBlockId, int sequenceId,
            int documentLine, int documentColumn) {
        this.documentUrl = documentUrl;
        this.containerBlockId = containerBlockId;
        this.sequenceId = sequenceId;
        this.line = documentLine;
        this.column = documentColumn;
    }

    public SourceInfo(JSONObject jsonObj) throws JSONException {
        documentUrl = jsonObj.getString("documentUrl");
        sequenceId = jsonObj.getInt("sequenceId");
        line = jsonObj.getInt("line");
        column = jsonObj.getInt("column");
        
        containerBlockId = 
                jsonObj.has("containerBlockId") ? jsonObj.getString("containerBlockId") : null;

    }

    public SourceInfo(SourceInfo sourceInfo) {
        documentUrl = sourceInfo.documentUrl;
        containerBlockId = sourceInfo.containerBlockId;
        sequenceId = sourceInfo.sequenceId;
        line = sourceInfo.line;
        column = sourceInfo.column;
    }
    

    public String getContainerBlockId() {
        return containerBlockId;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }

    public boolean isSameDocumentAndBlock(SourceInfo other) {
        if ((this.documentUrl == null) ? (other.documentUrl != null) : 
                ! this.documentUrl.equals(other.documentUrl)) {
            return false;
        }
        
        if ((this.containerBlockId == null) ? (other.containerBlockId != null) : 
                ! this.containerBlockId.equals(other.containerBlockId)) {
            return false;
        }
        
        return true;
    }
    
    public boolean isAfter(SourceInfo other) {
        if (this.sequenceId <= other.sequenceId) {
            return false;
        }
        
        return true;
    }
    
    public String getLocationString() {
         return String.format("in file %1$s line %2$d column %3$d: %4$s", 
                documentUrl, line, column, containerBlockId);
    }

    @Override
    public String toJSONString() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("column", column);
            jo.put("containerBlockId", containerBlockId);
            jo.put("documentUrl", documentUrl);
            jo.put("line", line);
            jo.put("sequenceId", sequenceId);
        } catch (JSONException ex) {
            throw new IllegalArgumentException("JSON encoding error.", ex);
        }
        
        return jo.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.documentUrl != null ? this.documentUrl.hashCode() : 0);
        hash = 89 * hash + (this.containerBlockId != null ? this.containerBlockId.hashCode() : 0);
        hash = 89 * hash + this.sequenceId;
        hash = 89 * hash + this.line;
        hash = 89 * hash + this.column;
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
        final SourceInfo other = (SourceInfo) obj;
        if ((this.documentUrl == null) ? (other.documentUrl != null) : !this.documentUrl.equals(other.documentUrl)) {
            return false;
        }
        if ((this.containerBlockId == null) ? (other.containerBlockId != null) : !this.containerBlockId.equals(other.containerBlockId)) {
            return false;
        }
        if (this.sequenceId != other.sequenceId) {
            return false;
        }
        if (this.line != other.line) {
            return false;
        }
        if (this.column != other.column) {
            return false;
        }
        return true;
    }
    
    
}
