package api.db;

import java.io.File;

public abstract class FilenameDatabase extends Database {
  private String directory;

  private String filename;

  private File file;

  private String extension = ".db";

  public FilenameDatabase(String prefix, DBMS dbms) {
    super(prefix, dbms);
    setFile();
  }

  public FilenameDatabase(String prefix, DBMS dbms, String directory, String filename) {
    super(prefix, dbms);
    setFile(directory, filename);
  }

  public FilenameDatabase(String prefix, DBMS dbms, String directory, String filename, String extension) {
    super(prefix, dbms);
    setFile(directory, filename, extension);
  }

  public String getDirectory() {
    return this.directory;
  }

  public void setDirectory(String directory) {
    if (directory == null || directory.length() == 0)
      throw new DatabaseException("Directory cannot be null or empty.");
    this.directory = directory;
  }

  public String getFilename() {
    return this.filename;
  }

  public void setFilename(String filename) {
    if (filename == null || filename.length() == 0)
      throw new DatabaseException("Filename cannot be null or empty.");
    if (filename.contains("/") || filename.contains("\\") || filename.endsWith(".db"))
      throw new DatabaseException("The database filename cannot contain: /, \\, or .db.");
    this.filename = filename;
  }

  public String getExtension() {
    return this.extension;
  }

  public void setExtension(String extension) {
    if (extension == null || extension.length() == 0)
      throw new DatabaseException("Extension cannot be null or empty.");
    if (extension.charAt(0) != '.')
      throw new DatabaseException("Extension must begin with a period");
  }

  public File getFile() {
    return this.file;
  }

  public void setFile() {
    this.file = null;
  }

  public void setFile(String directory, String filename) throws DatabaseException {
    setDirectory(directory);
    setFilename(filename);
    File folder = new File(getDirectory());
    if (!folder.exists())
      folder.mkdir();
    this.file = new File(folder.getAbsolutePath() + File.separator + getFilename() + getExtension());
  }

  public void setFile(String directory, String filename, String extension) throws DatabaseException {
    setExtension(extension);
    setFile(directory, filename);
  }
}
