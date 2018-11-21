package sg.mas.servlet.activemq;

import java.io.File;

/**
 * Temporary file.
 *
 * @copyright
 * @author prc
 */
public class TemporaryFile {

  private File _file;

  private String _filename;

  public TemporaryFile(final File file, final String filename) {
    _file = file;
    _filename = filename;
  }

  public File getFile() {
    return _file;
  }

  public String getFilename() {
    return _filename;
  }
}
