package hu.sztaki.ilab.longneck.process;

import com.google.common.collect.ImmutableMap;
import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.ImmutableFieldImpl;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.kernel.KernelState;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import hu.sztaki.ilab.longneck.process.task.CheckTreeItem;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class ImmutableErrorRecordImpl implements Record {

    private final ImmutableMap<String,Field> fields;

    public ImmutableErrorRecordImpl(Record record, CheckTreeItem item) {
        ImmutableMap.Builder<String,Field> fieldsBuilder = new ImmutableMap.Builder<String,Field>();

        // special fields:
        fieldsBuilder.put("class_name", new ImmutableFieldImpl("class_name",
                item.getResult().getSourceInfoContainer().getClass().getName()));
        fieldsBuilder.put("field",
                new ImmutableFieldImpl("field", item.getResult().getField()));
        fieldsBuilder.put("field_value",
                new ImmutableFieldImpl("field_value", item.getResult().getValue()));
        fieldsBuilder.put("details",
                new ImmutableFieldImpl("details", item.getResult().getDetails()));
        fieldsBuilder.put("context",
                new ImmutableFieldImpl("context", item.getResult().getContext()));

        //        if (item.getResult().getSourceInfoContainer().getSourceInfo() == null) {
        //            System.out.println(record.getFields().values().toString());
        //        }
        fieldsBuilder.put("document_url", new ImmutableFieldImpl("document_url",
                item.getResult().getSourceInfoContainer().getSourceInfo().getDocumentUrl()));
        fieldsBuilder.put("document_line", new ImmutableFieldImpl("document_line",
                Integer.toString(item.getResult().getSourceInfoContainer()
                        .getSourceInfo().getLine())));
        fieldsBuilder.put("document_column", new ImmutableFieldImpl("document_column",
                Integer.toString(item.getResult().getSourceInfoContainer()
                        .getSourceInfo().getColumn())));
        fieldsBuilder.put("check_result",
                new ImmutableFieldImpl("check_result",
                        Boolean.toString(item.getResult().isPassed())));
        fieldsBuilder.put("check_parent_id", new ImmutableFieldImpl("check_parent_id",
                (item.getCheckParentId() > 0) ?
                        Long.toString(item.getCheckParentId()) : null));
        fieldsBuilder.put("check_id",
                new ImmutableFieldImpl("check_id", Long.toString(item.getCheckId())));
        fieldsBuilder.put("check_tree_id",
                new ImmutableFieldImpl("check_tree_id", Long.toString(item.getCheckTreeId())));
        fieldsBuilder.put("check_level",
                new ImmutableFieldImpl("check_level", Integer.toString(item.getCheckLevel())));

        // fields of the record:
        for (Map.Entry<String,Field> e : record.getFields().entrySet()) {
            String f = e.getKey() ;
            // special fields override record fields:
            if (! (f.equals("class_name") || f.equals("field") || f.equals("field_value") || f.equals("details") ||
                   f.equals("context") || f.equals("document_url") || f.equals("document_url") || f.equals("document_column") ||
                   f.equals("check_result") || f.equals("check_parent_id") || f.equals("check_id") || f.equals("check_tree_id") ||
                   f.equals("check_level")) ) {
                fieldsBuilder.put(e.getKey(), new ImmutableFieldImpl(e.getValue()));
            }
        }

        fields = fieldsBuilder.build();
    }

    @Override
    public boolean has(String fieldName) {
        return fields.containsKey(fieldName);
    }

    @Override
    public Field get(String fieldName) {
        return fields.get(fieldName);
    }

    @Override
    public void add(Field field) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<CheckResult> getErrors() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(String fieldName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void restoreState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clearHistory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, Field> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{ ");
        if (fields != null) {
            for (Map.Entry f : fields.entrySet()) {
                result.append(f.getValue().toString());
                result.append(", ");
            }
        }
        result.delete(result.length() - 2, result.length());
        result.append("}");

        return result.toString();
    }


    @Override
    public void setFields(Map<String, Field> fields) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public KernelState getKernelState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setKernelState(KernelState kernelState) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Record clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addError(CheckResult error) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
