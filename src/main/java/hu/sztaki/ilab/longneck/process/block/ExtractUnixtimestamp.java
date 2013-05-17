package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 
 * Extracts input Unix timestamp into distinct date element fields (year,month,day,hour,min,sec)
 * 
 * @author Loránd Bendig <lbendig@ilab.sztaki.hu>
 *
 */
public class ExtractUnixtimestamp extends AbstractAtomicBlock implements CompoundBlock {
	
	/** The list of inside blocks. */
	private List<? extends Block> blocks;

	@Override
    public void apply(Record record, VariableSpace variables) {

        try {
            // Execute regular expression
            for (String fName : applyTo) {
                // Get source value
                String value = BlockUtils.getValue(fName, record, variables);

                long unixTimestamp = Long.parseLong(value);

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(unixTimestamp * 1000);

                variables.setVariable("year", cal.get(Calendar.YEAR) + "");
                variables.setVariable("month", cal.get(Calendar.MONTH) + 1 + "");
                variables.setVariable("day", cal.get(Calendar.DAY_OF_MONTH) + "");
                variables.setVariable("hour", cal.get(Calendar.HOUR_OF_DAY) + "");
                variables.setVariable("min", cal.get(Calendar.MINUTE) + "");
                variables.setVariable("sec", cal.get(Calendar.SECOND) + "");

                variables.setVariable("unixTimestamp", Integer.toString(6));
            }
        } catch (NumberFormatException ex) {
            log.error(String.format("%1$s %2$s", this, sourceInfo.getLocationString()), ex);                    
        }
	}

	@Override
	public List<Block> getBlocks() {
		return (List<Block>) blocks;
	}

	@Override
	public void setBlocks(List<? extends Block> blocks) {
		this.blocks = blocks;
	}

	@Override
	public ExtractUnixtimestamp clone() {
		ExtractUnixtimestamp copy = (ExtractUnixtimestamp) super.clone();

		if (blocks != null) {
			copy.blocks = new ArrayList<Block>(blocks.size());
			for (Block b : blocks) {
				((List<Block>) copy.blocks).add(b.clone());
			}
		}

		return copy;
	}

    @Override
    public boolean hasPosition(int pos) {
        return (blocks.size() - 1 >= pos);
    }
}