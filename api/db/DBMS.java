package api.db;

import java.util.HashMap;
import java.util.Map;

public enum DBMS {
  Other("[Other] "),
  Firebird("[Firebird] "),
  FrontBase("[FrontBase] "),
  DB2("[DB2] "),
  H2("[H2] "),
  Informix("[Informix] "),
  Ingres("[Ingres] "),
  MaxDB("[MaxDB] "),
  MicrosoftSQL("[MicrosoftSQL] "),
  Mongo("[Mongo] "),
  mSQL("[mSQL] "),
  MySQL("[MySQL] "),
  Oracle("[Oracle] "),
  PostgreSQL("[PostgreSQL] "),
  SQLite("[SQLite] ");

  private String prefix;

  private static Map<String, DBMS> prefixes;

  DBMS(String prefix) {
    this.prefix = prefix;
  }

  @Override
public String toString() {
    return this.prefix;
  }

  static {
    prefixes = new HashMap<>();
    for (DBMS dbms : prefixes.values())
      prefixes.put(dbms.toString(), dbms);
  }

  public static DBMS getDBMS(String prefix) {
    return prefixes.get(prefix);
  }
}
