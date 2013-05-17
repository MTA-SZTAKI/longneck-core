package hu.sztaki.ilab.longneck.process.task;

import hu.sztaki.ilab.longneck.bootstrap.KeyGenerator;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Peter Molnar <molnar.peter@sztaki.mta.hu>
 */
public class CheckTreeItem {
    
    /** The tree item wrapped. */
    private CheckResult result;
    /** The node key assigned to this result. */
    private long checkId;
    /** The key of the parent event caused by this. */
    private long checkParentId;
    /** The key of the parent event caused by this. */
    private long checkTreeId;
    /** The level of this constraint check. */
    private int checkLevel;

    public CheckTreeItem() {
    }

    public CheckTreeItem(CheckResult result) {
        this.result = result;
    }

    public CheckResult getResult() {
        return result;
    }

    public void setResult(CheckResult result) {
        this.result = result;
    }

    public long getCheckId() {
        return checkId;
    }

    public void setCheckId(long checkId) {
        this.checkId = checkId;
    }

    public int getCheckLevel() {
        return checkLevel;
    }

    public void setCheckLevel(int checkLevel) {
        this.checkLevel = checkLevel;
    }

    public long getCheckParentId() {
        return checkParentId;
    }

    public void setCheckParentId(long checkParentId) {
        this.checkParentId = checkParentId;
    }

    public long getCheckTreeId() {
        return checkTreeId;
    }

    public void setCheckTreeId(long checkTreeId) {
        this.checkTreeId = checkTreeId;
    }
    
    private static void flatten(CheckTreeItem item, KeyGenerator keyGenerator, 
            List<CheckTreeItem> outList, long checkParentId, long checkTreeId, 
            int checkLevel, int maxLevel) {
        
        item.setCheckId(keyGenerator.getNext());        
        item.setCheckParentId(checkParentId);        
        item.setCheckLevel(checkLevel);

        // Assign check tree id if this is the topmost node
        checkTreeId = checkTreeId > 0 ? checkTreeId : item.getCheckId();
        item.setCheckTreeId(checkTreeId);
        
        outList.add(item);
        
        if (item.getResult().getCauses() != null && (maxLevel < 0 || maxLevel >= (checkLevel + 1))) {
            for (CheckResult child : item.getResult().getCauses()) {
                
                flatten(new CheckTreeItem(child), keyGenerator, outList, item.getCheckId(), 
                        checkTreeId, checkLevel + 1, maxLevel);
            }
        }
    }
    
    public static List<CheckTreeItem> flatten(CheckResult result, 
            KeyGenerator keyGenerator, int maxLevel) {
        List<CheckTreeItem> outList = new ArrayList<CheckTreeItem>();
        
        CheckTreeItem item = new CheckTreeItem(result);
        
        if (maxLevel != 0) {
            flatten(item, keyGenerator, outList, 0, 0, 1, maxLevel);
        }
        
        return outList;
    }

    
}
