package api.db.factory;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

import api.db.DBMS;

public class DatabaseConfig {
  private final Map<Parameter, String> config = new EnumMap<>(Parameter.class);

  private DBMS type;

  private Logger log;

  public DBMS getType() {
    return this.type;
  }

  public Logger getLog() {
    return this.log;
  }

  public DatabaseConfig setType(DBMS type) throws IllegalArgumentException {
    if (type == DBMS.Other)
      throw new IllegalArgumentException("You can't set your database type to Other");
    this.type = type;
    return this;
  }

  public DatabaseConfig setLog(Logger log) {
    this.log = log;
    return this;
  }

  public DatabaseConfig setParameter(Parameter param, String value) throws NullPointerException, InvalidConfigurationException {
    if (this.type == null)
      throw new NullPointerException("You must set the type of the database first");
    if (!param.validParam(this.type))
      throw new InvalidConfigurationException(param.toString() + " is invalid for a database type of : " + this.type.toString());
    this.config.put(param, value);
    return this;
  }

  public String getParameter(Parameter param) {
    return this.config.get(param);
  }

  public boolean isValid() throws InvalidConfigurationException {
    if (this.log == null)
      throw new InvalidConfigurationException("You need to set the logger.");
    return (this.config.size() == Parameter.getCount(this.type));
  }
}
