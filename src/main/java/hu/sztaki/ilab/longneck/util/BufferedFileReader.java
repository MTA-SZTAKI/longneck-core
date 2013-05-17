package hu.sztaki.ilab.longneck.util;

import hu.sztaki.ilab.longneck.process.access.NoMoreRecordsException;
import java.io.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Reads input stream to Strings by using data segments (i.e a fixed size List)
 * one at a time until the end of the stream.
 * Compressed (Gzip) streams are identified and uncompressed on the fly
 * <br>
 * Usage:
 * <pre>
 * try {
 *   for (;;) {
 *     List<Records> records = source.getRecords(); //a data segment
 *     ...
 *   }
 * }
 * catch (NoMoreRecordsException ex) {
 *   ...
 * }      
 * <pre>
 * @author Loránd Bendig
 * 
 */
public class BufferedFileReader {

    private static final Logger LOG = Logger.getLogger(BufferedFileReader.class);
    
    //default parameters
    private static final int DEFAULT_SEGMENT_SIZE = 4096;
    
    private static final String DEFAULT_ENCODING = "UTF8";
    
    private BufferedReader br;
    
    /** True if reading is performed from stdin */
    private boolean stdinEnabled = false;

    /** BufferedReader's buffer size */
    private Integer inputBufferSize; 
    
    /** InputStreamReader's encoding */
    private String encoding;
    
    private LineIterator it;
    
    /** List of files to be processed */
    private List<File> fileList;

    /** file being read */
    private File currFile;

    /** configurable segment size */
    private int segmentSize;
    
    /** Class that manages constructor parameters*/
    public static class Builder {
        
        //required parameters
        private List<String> path;
        private boolean stdinEnabled;
        
        //optional parameters
        private String encoding;
        private int segmentSize;
        private Integer inputBufferSize;
        
        public Builder(List<String> path, boolean stdinEnabled) {
          
            this.path = path;
            this.stdinEnabled = stdinEnabled;
        }
        
        public Builder encoding(String val) {
            encoding = val;
            return this;
        }
        
        public Builder segmentSize(int val) {
            segmentSize = val;
            return this;
        }
        
        public Builder inputBufferSize(Integer val) {
            inputBufferSize = val;
            return this;
        }
        
        public BufferedFileReader build() throws IOException {
            return new BufferedFileReader(this);
        }
    }
    
    /**
     * Builds a BufferedFileReader instance
     * 
     * @param builder - holds parameters
     * @throws IOException
     */
    private BufferedFileReader(Builder builder) throws IOException {
        
        this.stdinEnabled = builder.stdinEnabled;
        initFileResource(builder.path);
        encoding = builder.encoding;
        segmentSize = builder.segmentSize;
        inputBufferSize = builder.inputBufferSize;
        initHandlers();
    }
    
    /**
     * Reads file contents to a fixed size data segment
     * 
     * @return
     * @throws NoMoreRecordsException - when there are no more data to read
     */
    public Deque<String> getDataSegment() throws NoMoreRecordsException {
        Deque<String> result = new LinkedList<String>();

        manageResources();
        
        int segments = (segmentSize == 0) ? DEFAULT_SEGMENT_SIZE : segmentSize;
        long lineCount = 0;
        while (it.hasNext() && (lineCount != segments)) {
            result.addLast(it.nextLine());
            lineCount++;
        }
        //if (compressed) file is empty then move to the next file in the queue if there's any 
        if (result.isEmpty() && fileList != null && ! fileList.isEmpty()) {
            result = getDataSegment();
        }
        
        return result;
    }
    
    private void initFileResource(List<String> path) throws FileNotFoundException,
            UnsupportedEncodingException {

        if (stdinEnabled) {
            return;
        }
        
        fileList = new ArrayList<File>();
        for (String p :path) {
            
            File source = new File(p);
            if (source.isFile()) {
                fileList.add(source);
            }
            else if (source.isDirectory()) {
                for (File f : source.listFiles()) {
                    if (f.isFile() && f.length() != 0) {
                        fileList.add(f);
                    }
                }
            }
        }
        
        if (fileList == null || fileList.isEmpty()) {
            throw new FileNotFoundException("No files to read");
        }

        currFile = fileList.remove(0);
        LOG.info("Processing file: " + currFile.getAbsolutePath());

    }
    
    private void initHandlers() throws IOException {
        InputStream is = (stdinEnabled) ? System.in : new FileInputStream(currFile);
        
        InputStreamReader isr = new InputStreamReader(decompressStream(is), 
                (StringUtils.isEmpty(encoding) ? DEFAULT_ENCODING : encoding));
    
        br = (inputBufferSize == null) ? 
                new BufferedReader(isr) : new BufferedReader(isr, inputBufferSize);
        it = IOUtils.lineIterator(br);

    }
    
    private void manageResources() throws NoMoreRecordsException {

        if (!it.hasNext()) {

            IOUtils.closeQuietly(br);
            it.close();
            
            if (stdinEnabled) {
                return;
            }
            // no more file / files in directory
            if (fileList.isEmpty()) {
                currFile = null;
                throw new NoMoreRecordsException();
            }

            currFile = fileList.remove(0);
            LOG.info("Processing file: " + currFile.getAbsolutePath());
            try {
                initHandlers();
            } catch (IOException e) {
                LOG.error("Couldn't initialize BufferedFileReader resources");
            }

        }
    }
    
    /**
     * Determines whether the input stream is GZIP compressed by checking its first two bytes.
     * @param input - the input stream to be checked
     * @return the input stream wrapped into a GZIPInputStream if it is compressed
     * @throws IOException
     */
    private InputStream decompressStream(InputStream input) throws IOException {
        PushbackInputStream pb = new PushbackInputStream(input, 2);
        byte[] header = new byte[2];
        pb.read(header);
        pb.unread(header);
        
        if( header[0] == (byte) 0x1f && header[1] == (byte) 0x8b ) {
            return new GZIPInputStream(pb);
        }
        else {
            return pb;
        }
    }
  
}
