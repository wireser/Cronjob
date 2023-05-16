package api.db.factory;

import api.db.Database;
import api.db.MySQL;
import api.db.SQLite;

public class DatabaseFactory {
  @SuppressWarnings("incomplete-switch")
public static Database createDatabase(DatabaseConfig config) throws InvalidConfigurationException {
    if (!config.isValid())
      throw new InvalidConfigurationException("The configuration is invalid, you don't have enought parameters for that DB : " + config.getType());
    switch (config.getType()) {
      case MySQL:
        return new MySQL(config.getParameter(Parameter.PREFIX), config.getParameter(Parameter.HOST), Integer.parseInt(config.getParameter(Parameter.PORT)), config.getParameter(Parameter.DATABASE), config.getParameter(Parameter.USERNAME), config.getParameter(Parameter.PASSWORD));
      case SQLite:
        return new SQLite(config.getParameter(Parameter.PREFIX), config.getParameter(Parameter.LOCATION), config.getParameter(Parameter.FILENAME));
    }
    return null;
  }
}
