package api.db;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DB2 extends HostnameDatabase {
  public enum Statements implements StatementEnum {

  }

  public DB2(String prefix, String hostname, int port, String database, String username, String password) {
    super(prefix, DBMS.DB2, hostname, port, database, username, password);
  }

  public DB2(String prefix, String database, String username, String password) {
    super(prefix, DBMS.DB2, "localhost", 523, database, username, password);
  }

  public DB2(String prefix, String database, String username) {
    super(prefix, DBMS.DB2, "localhost", 523, database, username, "");
  }

  public DB2(String prefix, String database) {
    super(prefix, DBMS.DB2, "localhost", 523, database, "", "");
  }

  @Override
protected boolean initialize() {
    try {
      Class.forName("com.ibm.db2.jcc.DB2Driver");
      return true;
    } catch (ClassNotFoundException e) {
      error("DB2 driver class missing: " + e.getMessage() + ".");
      return false;
    }
  }

  @Override
public boolean open() {
    if (initialize()) {
      String url = "jdbc:derby:net://" + getHostname() + ":" + getPort() + "/" + getDatabase();
      try {
        this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
        return true;
      } catch (SQLException e) {
        error("Could not establish a DB2 connection, SQLException: " + e.getMessage());
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
