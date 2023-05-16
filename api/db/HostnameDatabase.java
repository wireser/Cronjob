package api.db;

public abstract class HostnameDatabase extends Database {
  private String hostname = "localhost";

  private int port = 0;

  private String database = "minecraft";

  private String username = "minecraft";

  private String password = "";

  public HostnameDatabase(String prefix, DBMS dbms, String hostname, int port, String database, String username, String password) {
    super(prefix, dbms);
    setHostname(hostname);
    setPort(port);
    setUsername(username);
    setPassword(password);
    setDatabase(database);
  }

  public String getHostname() {
    return this.hostname;
  }

  public void setHostname(String hostname) {
    if (hostname == null || hostname.length() == 0)
      throw new DatabaseException("Hostname cannot be null or empty.");
    this.hostname = hostname;
  }

  public int getPort() {
    return this.port;
  }

  public void setPort(int port) {
    if (port < 0 || 65535 < port)
      throw new DatabaseException("Port number cannot be below 0 or greater than 65535.");
    this.port = port;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    if (username == null)
      throw new DatabaseException("Username cannot be null.");
    this.username = username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    if (password == null)
      throw new DatabaseException("Password cannot be null.");
    this.password = password;
  }

  public String getDatabase() {
    return this.database;
  }

  public void setDatabase(String database) {
    if (database == null || database.length() == 0)
      throw new DatabaseException("Database cannot be null or empty.");
    this.database = database;
  }
}
