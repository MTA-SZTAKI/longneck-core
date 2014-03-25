package hu.sztaki.ilab.longneck.process;

import hu.sztaki.ilab.longneck.TestCase;
import hu.sztaki.ilab.longneck.process.access.NullTarget;
import hu.sztaki.ilab.longneck.process.access.Source;
import hu.sztaki.ilab.longneck.process.access.Target;
import hu.sztaki.ilab.longneck.process.block.Block;
import hu.sztaki.ilab.longneck.process.block.Sequence;

import java.util.List;

import org.w3c.dom.Document;


/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class LongneckProcess implements LongneckSource {
    
    /** The source to read records from. */
    private Source source;
    /** The target to write records to. */
    private Target target;
    /** The error target to write record processing errors to. */
    private Target errorTarget = new NullTarget();
    /** The blocks. */
    private Sequence topLevelBlocks;
    /** The source dom document. */
    private Document domDocument;
        
    private List<TestCase> testCases;
    
    public List<TestCase> getTestCases() {
      return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
      this.testCases = testCases;
    }

    public LongneckProcess() {
        topLevelBlocks = new Sequence();
    }

    public void setBlocks(List<Block> blocks) {
        topLevelBlocks.setBlocks(blocks);
    }

    public List<Block> getBlocks() {
        return topLevelBlocks.getBlocks();
    }

    public Sequence getTopLevelBlocks() {
        return topLevelBlocks;
    }

    public void setTopLevelBlocks(Sequence topLevelBlocks) {
        this.topLevelBlocks = topLevelBlocks;
    }
    
    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public Target getErrorTarget() {
        return errorTarget;
    }

    public void setErrorTarget(Target errorTarget) {
        this.errorTarget = errorTarget;
    }

    @Override
    public Document getDomDocument() {
        return domDocument;
    }

    @Override
    public void setDomDocument(Document domDocument) {
        this.domDocument = domDocument;
    }

    @Override
    public FileType getType() {
        return FileType.Process;
    }
    
    
}
