package api.db.factory;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import api.db.DBMS;

public enum Parameter {
  PREFIX(new DBMS[] { DBMS.Other }),
  HOST(new DBMS[] { DBMS.MySQL, DBMS.MicrosoftSQL, DBMS.Oracle, DBMS.PostgreSQL }),
  PORT(new DBMS[] { DBMS.MySQL, DBMS.MicrosoftSQL, DBMS.Oracle, DBMS.PostgreSQL }),
  DATABASE(new DBMS[] { DBMS.MySQL, DBMS.MicrosoftSQL, DBMS.Oracle, DBMS.PostgreSQL }),
  USERNAME(new DBMS[] { DBMS.MySQL, DBMS.MicrosoftSQL, DBMS.Oracle, DBMS.PostgreSQL }),
  PASSWORD(new DBMS[] { DBMS.MySQL, DBMS.MicrosoftSQL, DBMS.Oracle, DBMS.PostgreSQL }),
  LOCATION(new DBMS[] { DBMS.SQLite, DBMS.H2 }),
  FILENAME(new DBMS[] { DBMS.SQLite, DBMS.H2 });

  private Set<DBMS> types;

  private static Map<DBMS, Integer> count;

  static {
    count = new EnumMap<>(DBMS.class);
  }

  Parameter(DBMS... type) {
    this.types = new HashSet<>();
    for (DBMS element : type) {
      this.types.add(element);
      updateCount(element);
    }
  }

  public boolean validParam(DBMS check) {
    if (this.types.contains(DBMS.Other) || this.types.contains(check))
      return true;
    return false;
  }

  @SuppressWarnings("unused")
private static void updateCount(DBMS type) {
    Integer nb = count.get(type);
    if (nb == null) {
      nb = Integer.valueOf(1);
    } else {
      Integer integer1 = nb, integer2 = nb = Integer.valueOf(nb.intValue() + 1);
    }
    count.put(type, nb);
  }

  public static int getCount(DBMS type) {
    int nb = count.get(DBMS.Other).intValue() + count.get(type).intValue();
    return nb;
  }
}
