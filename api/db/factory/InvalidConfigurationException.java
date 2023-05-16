package api.db.factory;

public class InvalidConfigurationException extends Exception {
  private static final long serialVersionUID = 7939781253235805155L;

  public InvalidConfigurationException(String message) {
    super(message);
  }

  public InvalidConfigurationException(Exception e) {
    super(e);
  }
}
