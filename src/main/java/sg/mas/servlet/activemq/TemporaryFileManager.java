package sg.mas.servlet.activemq;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;

/**
 * Manager for temporary files.
 *
 * @copyright
 * @author prc
 */
public class TemporaryFileManager {

  private final DateFormat _dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss-SSS");

  private Map<Integer, TemporaryFile> _files = Maps.newLinkedHashMap();

  private Path _tempDirectory;

  private Integer _count = 0;

  private static final Logger LOGGER = Logger.getLogger(TemporaryFileManager.class);

  public TemporaryFileManager() throws IOException {
    _tempDirectory = FileSystems.getDefault().getPath("/home/virtuser/logs/incoming/");
  }

  public TemporaryFile storeFile(final byte[] bytes) throws IOException {
    Integer count = null;
    synchronized (_count) {
      count = _count++;
    }
    // Store File
    String filename = _dateFormat.format(new Date()) + ".zip";
    String name = _tempDirectory.toString() + "response" + filename;
   // Path tempFile = Files.createTempFile(_tempDirectory.toAbsolutePath(), "reply", count.toString() + ".zip");
    System.out.println(name);
    Path tempFile = Files.createTempFile(_tempDirectory.toAbsolutePath(), "reply", filename);
    try (FileOutputStream fos = new FileOutputStream(name)) {
     	System.out.println("Path Defined is for fos : " + name);
        fos.write(bytes, 0, bytes.length);
    	fos.flush();
        LOGGER.info("Message downloaded to " + tempFile.toString());
        System.out.println("Message downloaded to " + tempFile.toString());
        
      }
    try (OutputStream os = new FileOutputStream(tempFile.toFile())) {
    	System.out.println("Path Defined is : " + tempFile.toString());
      os.write(bytes, 0, bytes.length);
      os.flush();
      LOGGER.info("Message downloaded to " + tempFile.toString());
    }
    
    TemporaryFile temporaryFile = new TemporaryFile(tempFile.toFile(), filename);
    _files.put(count, temporaryFile);
    return temporaryFile;
  }

  public boolean deleteFile(final Integer index) {
    TemporaryFile temporaryFile = _files.remove(index);
    if (temporaryFile != null) {
      return temporaryFile.getFile().delete();
    }
    return false;
  }

}
