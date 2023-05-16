package api.db;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Mongo extends HostnameDatabase {
  public enum Statements implements StatementEnum {

  }

  public Mongo(String prefix, String hostname, int port, String database, String username, String password) {
    super(prefix, DBMS.MaxDB, hostname, port, database, username, password);
  }

  public Mongo(String prefix, String database, String username, String password) {
    super(prefix, DBMS.MaxDB, "localhost", 27017, database, username, password);
  }

  public Mongo(String prefix, String database, String username) {
    super(prefix, DBMS.MaxDB, "localhost", 27017, database, username, "");
  }

  public Mongo(String prefix, String database) {
    super(prefix, DBMS.MaxDB, "localhost", 27017, database, "", "");
  }

  @Override
protected boolean initialize() {
    try {
      Class.forName("com.mongodb.jdbc.MongoDriver");
      return true;
    } catch (ClassNotFoundException e) {
      error("Mongo driver class missing: " + e.getMessage() + ".");
      return false;
    }
  }

  @Override
public boolean open() {
    if (initialize()) {
      String url = "mongodb://" + getHostname() + ":" + getPort() + "/" + getDatabase();
      try {
        this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
        return true;
      } catch (SQLException e) {
        error("Could not establish a Mongo connection, SQLException: " + e.getMessage());
        return false;
      }
    }
    return false;
  }

  @Override
protected void queryValidation(StatementEnum statement) throws SQLException {}

  @Override
public StatementEnum getStatement(String query) throws SQLException {
    String[] statement = query.trim().split(" ", 2);
    try {
      Statements converted = Statements.valueOf(statement[0].toUpperCase());
      return converted;
    } catch (IllegalArgumentException e) {
      throw new SQLException("Unknown statement: \"" + statement[0] + "\".");
    }
  }

  @Override
public boolean isTable(String table) {
    throw new UnsupportedOperationException();
  }

  @Override
public boolean truncate(String table) {
    throw new UnsupportedOperationException();
  }
}
