package api.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import api.db.builders.Builder;

public abstract class Database {
  protected final String prefix;

  protected DBMS driver;

  protected Connection connection;

  protected Map<PreparedStatement, StatementEnum> preparedStatements = new HashMap<>();

  protected int lastUpdate;

  public Database(String prefix, DBMS dbms) throws DatabaseException {
    this.prefix = prefix;
    this.driver = dbms;
  }

  protected final String prefix(String message) {
    return this.prefix + this.driver + message;
  }

  public final void info(String info) {
    //if (info != null && !info.isEmpty())
    	//Main.b(prefix(info));
  }

  public final void warning(String warning) {
    //if (warning != null && !warning.isEmpty())
    //	Main.b(prefix(warning));
  }

  public final void error(String error) {
    //if (error != null && !error.isEmpty())
    //  Main.b(prefix(error));
  }

  protected abstract boolean initialize();

  public final DBMS getDriver() {
    return getDBMS();
  }

  public final DBMS getDBMS() {
    return this.driver;
  }

  public abstract boolean open();

  public final boolean close() {
    if (this.connection != null)
      try {
        this.connection.close();
        return true;
      } catch (SQLException e) {
        error("Could not close connection, SQLException: " + e.getMessage());
        return false;
      }
    error("Could not close connection, it is null.");
    return false;
  }

  public final Connection getConnection() {
    return this.connection;
  }

  public final boolean isOpen() {
    return isOpen(1);
  }

  public final boolean isOpen(int seconds) {
    if (this.connection != null)
      try {
        if (this.connection.isValid(seconds))
          return true;
      } catch (SQLException e) {}
    return false;
  }

  public final int getLastUpdateCount() {
    return this.lastUpdate;
  }

  protected abstract void queryValidation(StatementEnum paramStatementEnum) throws SQLException;

  public final ResultSet query(String query) throws SQLException {
    queryValidation(getStatement(query));
    Statement statement = getConnection().createStatement();
    if (statement.execute(query))
      return statement.getResultSet();
    int uc = statement.getUpdateCount();
    this.lastUpdate = uc;
    return getConnection().createStatement().executeQuery("SELECT " + uc);
  }

  protected final ResultSet query(PreparedStatement ps, StatementEnum statement) throws SQLException {
    queryValidation(statement);
    if (ps.execute())
      return ps.getResultSet();
    int uc = ps.getUpdateCount();
    this.lastUpdate = uc;
    return this.connection.createStatement().executeQuery("SELECT " + uc);
  }

  public final ResultSet query(PreparedStatement ps) throws SQLException {
    ResultSet output = query(ps, this.preparedStatements.get(ps));
    this.preparedStatements.remove(ps);
    return output;
  }

  public final PreparedStatement prepare(String query) throws SQLException {
    StatementEnum s = getStatement(query);
    PreparedStatement ps = this.connection.prepareStatement(query);
    this.preparedStatements.put(ps, s);
    return ps;
  }

  public ArrayList<Long> insert(String query) throws SQLException {
    ArrayList<Long> keys = new ArrayList<>();
    PreparedStatement ps = this.connection.prepareStatement(query, 1);
    this.lastUpdate = ps.executeUpdate();
    ResultSet key = ps.getGeneratedKeys();
    if (key.next())
      keys.add(Long.valueOf(key.getLong(1)));
    return keys;
  }

  public ArrayList<Long> insert(PreparedStatement ps) throws SQLException {
    this.lastUpdate = ps.executeUpdate();
    this.preparedStatements.remove(ps);
    ArrayList<Long> keys = new ArrayList<>();
    ResultSet key = ps.getGeneratedKeys();
    if (key.next())
      keys.add(Long.valueOf(key.getLong(1)));
    return keys;
  }

  public final ResultSet query(Builder builder) throws SQLException {
    return query(builder.toString());
  }

  public abstract StatementEnum getStatement(String paramString) throws SQLException;

  public abstract boolean isTable(String paramString);

  public abstract boolean truncate(String paramString);
}
