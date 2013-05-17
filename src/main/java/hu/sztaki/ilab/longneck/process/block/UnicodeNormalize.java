package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import java.text.Normalizer;
import java.text.Normalizer.Form;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class UnicodeNormalize extends AbstractAtomicBlock {
    /** The form of normalization, see http://unicode.org/reports/tr15/#Norm_Forms */
    private Normalizer.Form form;

    @Override
    public void apply(Record record, VariableSpace parentScope) {
        for (String fieldName : applyTo) {
            String value = BlockUtils.getValue(fieldName, record, parentScope);
            
            if (value == null || "".equals(value)) {
                continue;
            }
            
            value = Normalizer.normalize(value, form);
            
            BlockUtils.setValue(fieldName, value, record, parentScope);
        }
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }
    
    public void setForm(String form) {
        this.form = Normalizer.Form.valueOf(form);
    }

    @Override
    public UnicodeNormalize clone() {
        return (UnicodeNormalize) super.clone();
    }
    
    
}
