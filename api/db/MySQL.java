package api.db;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL extends HostnameDatabase {
  public enum Statements implements StatementEnum {
    SELECT("SELECT"),
    INSERT("INSERT"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    DO("DO"),
    REPLACE("REPLACE"),
    LOAD("LOAD"),
    HANDLER("HANDLER"),
    CALL("CALL"),
    CREATE("CREATE"),
    ALTER("ALTER"),
    DROP("DROP"),
    TRUNCATE("TRUNCATE"),
    RENAME("RENAME"),
    START("START"),
    COMMIT("COMMIT"),
    SAVEPOINT("SAVEPOINT"),
    ROLLBACK("ROLLBACK"),
    RELEASE("RELEASE"),
    LOCK("LOCK"),
    UNLOCK("UNLOCK"),
    PREPARE("PREPARE"),
    EXECUTE("EXECUTE"),
    DEALLOCATE("DEALLOCATE"),
    SET("SET"),
    SHOW("SHOW"),
    DESCRIBE("DESCRIBE"),
    EXPLAIN("EXPLAIN"),
    HELP("HELP"),
    USE("USE");

    private String string;

    Statements(String string) {
      this.string = string;
    }

    @Override
	public String toString() {
      return this.string;
    }
  }

  @Deprecated
  public MySQL(String prefix, String hostname, String port, String database, String username, String password) {
    super(prefix, DBMS.MySQL, hostname, Integer.parseInt(port), database, username, password);
  }

  public MySQL(String prefix, String hostname, int port, String database, String username, String password) {
    super(prefix, DBMS.MySQL, hostname, port, database, username, password);
  }

  public MySQL(String prefix, String database, String username, String password) {
    super(prefix, DBMS.MySQL, "localhost", 3306, database, username, password);
  }

  public MySQL(String prefix, String database, String username) {
    super(prefix, DBMS.MySQL, "localhost", 3306, database, username, "");
  }

  public MySQL(String prefix, String database) {
    super(prefix, DBMS.MySQL, "localhost", 3306, database, "", "");
  }

  @Override
protected boolean initialize() {
    try {
      Class.forName("com.mysql.cj.jdbc.MysqlDataSource");
      return true;
    } catch (ClassNotFoundException e) {
      warning("MySQL DataSource class missing: " + e.getMessage() + ".");
      return false;
    }
  }

  @Override
public boolean open() {
    try {
      String url = "jdbc:mysql://" + getHostname() + ":" + getPort() + "/" + getDatabase() + "?useSSL=false";
      if (initialize()) {
        this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
        return true;
      }
      return false;
    } catch (SQLException e) {
      error("Could not establish a MySQL connection, SQLException: " + e.getMessage());
      return false;
    }
  }

  @Override
@SuppressWarnings("incomplete-switch")
protected void queryValidation(StatementEnum statement) throws SQLException {
    switch ((Statements)statement) {
      case USE:
        warning("Please create a new connection to use a different database.");
        throw new SQLException("Please create a new connection to use a different database.");
      case PREPARE:
      case EXECUTE:
      case DEALLOCATE:
        warning("Please use the prepare() method to prepare a query.");
        throw new SQLException("Please use the prepare() method to prepare a query.");
    }
  }

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

  @Deprecated
  public boolean createTable(String query) {
    Statement statement = null;
    if (query == null || query.equals("")) {
      error("Could not create table: query is empty or null.");
      return false;
    }
    try {
      statement = this.connection.createStatement();
      statement.execute(query);
      statement.close();
    } catch (SQLException e) {
      error("Could not create table, SQLException: " + e.getMessage());
      return false;
    }
    return true;
  }

  @Override
public boolean isTable(String table) {
    Statement statement;
    try {
      statement = this.connection.createStatement();
    } catch (SQLException e) {
      error("Could not create a statement in checkTable(), SQLException: " + e.getMessage());
      return false;
    }
    try {
      statement.executeQuery("SELECT * FROM " + table);
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  @Override
public boolean truncate(String table) {
    Statement statement = null;
    String query = null;
    try {
      if (!isTable(table)) {
        error("Table \"" + table + "\" does not exist.");
        return false;
      }
      statement = this.connection.createStatement();
      query = "DELETE FROM " + table + ";";
      statement.executeUpdate(query);
      statement.close();
      return true;
    } catch (SQLException e) {
      error("Could not wipe table, SQLException: " + e.getMessage());
      return false;
    }
  }
}
