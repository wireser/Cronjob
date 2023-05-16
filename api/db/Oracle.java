package api.db;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Oracle extends HostnameDatabase {
  protected enum Statements implements StatementEnum {
    ALTER("ALTER"),
    CREATE("CREATE"),
    DROP("DROP"),
    GRANT("GRANT"),
    REVOKE("REVOKE"),
    TRUNCATE("TRUNCATE"),
    DELETE("DELETE"),
    EXPLAIN("EXPLAIN"),
    INSERT("INSERT"),
    SELECT("SELECT"),
    UPDATE("UPDATE"),
    COMMIT("COMMIT"),
    ROLLBACK("ROLLBACK"),
    SET("SET"),
    CONSTRAINT("CONSTRAINT"),
    CURRVAL("CURRVAL"),
    NEXTVAL("NEXTVAL"),
    ROWNUM("ROWNUM"),
    LEVEL("LEVEL"),
    OL_ROW_STATUS("OL_ROW_STATUS"),
    ROWID("ROWID");

    private String string;

    Statements(String string) {
      this.string = string;
    }

    @Override
	public String toString() {
      return this.string;
    }
  }

  public Oracle(String prefix, String hostname, int port, String database, String username, String password) throws SQLException {
    super(prefix, DBMS.Oracle, hostname, port, database, username, password);
  }

  public Oracle(String prefix, String database, String username, String password) throws SQLException {
    super(prefix, DBMS.Oracle, "localhost", 1521, database, username, password);
  }

  public Oracle(String prefix, String database, String username) throws SQLException {
    super(prefix, DBMS.Oracle, "localhost", 1521, database, username, "");
  }

  public Oracle(String prefix, String database) throws SQLException {
    super(prefix, DBMS.Oracle, "localhost", 1521, database, "", "");
  }

  @Override
public boolean initialize() {
    try {
      Class.forName("oracle.jdbc.driver.OracleDriver");
      return true;
    } catch (ClassNotFoundException e) {
      error("Oracle driver class missing: " + e.getMessage() + ".");
      return false;
    }
  }

  @Override
public boolean open() {
    if (initialize()) {
      String url = "";
      url = "jdbc:oracle:thin:@" + getHostname() + ":" + getPort() + ":" + getDatabase();
      try {
        this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
        return true;
      } catch (SQLException e) {
        error("Could not establish an Oracle connection, SQLException: " + e.getMessage());
        return false;
      }
    }
    return false;
  }

  @Override
protected void queryValidation(StatementEnum statement) throws SQLException {}

  @Override
public Statements getStatement(String query) throws SQLException {
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
