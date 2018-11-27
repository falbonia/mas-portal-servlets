package sg.mas.servlet.activemq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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
    // Path tempFile = Files.createTempFile(_tempDirectory.toAbsolutePath(), "reply", count.toString() + ".zip");
    Path tempFile = Files.createTempFile(_tempDirectory.toAbsolutePath(), "reply", filename);
    
    try (OutputStream os = new FileOutputStream(tempFile.toFile())) {
    	System.out.println("Path Defined is : " + tempFile.toString());
      os.write(bytes, 0, bytes.length);
      os.flush();
      LOGGER.info("Message downloaded to " + tempFile.toString());
    }
    
    TemporaryFile temporaryFile = new TemporaryFile(tempFile.toFile(), filename);
    _files.put(count, temporaryFile);
    try {
    	String truncateName = "reply" + filename;
    String getFormName = tempFile.toString().substring(tempFile.toString().lastIndexOf("reply"), tempFile.toString().lastIndexOf("."));
         
    String destPath = "/home/virtuser/logs/htmlfiles/" + getFormName + "/";
    String zipPath = tempFile.toString();
    //String zipPath = "/home/virtuser/logs/incoming/" + temporaryFile.getFilename();
    System.out.println("Printing Destination Path: " + destPath);
    System.out.println("Printing Zip Path: " + zipPath);
    
    unzip(zipPath,destPath);
    } catch(Exception e){
    	System.out.println("Unzip Error: " + e.getMessage());
    }
    return temporaryFile;
  }

  public boolean deleteFile(final Integer index) {
    TemporaryFile temporaryFile = _files.remove(index);
    if (temporaryFile != null) {
      return temporaryFile.getFile().delete();
    }
    return false;
  }
  
  private static void unzip(String zipFilePath, String destDir) {
	  File dir = new File(destDir);
      // create output directory if it doesn't exist
      
      if(!dir.exists()) dir.mkdirs();
      FileInputStream fis;
      //buffer for read and write data to file
      byte[] buffer = new byte[1024];
      try {
          fis = new FileInputStream(zipFilePath);
          ZipInputStream zis = new ZipInputStream(fis);
          ZipEntry ze = zis.getNextEntry();
          while(ze != null){
              String fileName = ze.getName();
              File newFile = new File(destDir + fileName);
              System.out.println("Unzipping to "+newFile.getAbsolutePath());
              //create directories for sub directories in zip
              new File(newFile.getParent()).mkdirs();
              FileOutputStream fos = new FileOutputStream(newFile);
              int len;
              while ((len = zis.read(buffer)) > 0) {
              fos.write(buffer, 0, len);
              }
              fos.close();
              //close this ZipEntry
              zis.closeEntry();
              ze = zis.getNextEntry();
          }
          //close last ZipEntry
          zis.closeEntry();
          zis.close();
          fis.close();
      } catch (IOException e) {
          e.printStackTrace();
      }
      
  }
	

}
