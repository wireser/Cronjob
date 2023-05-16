package cronjob;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import api.db.MySQL;

/***
 *
 * @author wireser
 *
 */
public class Database {

	public static MySQL database;

	public static Boolean paused = false;
	private static Boolean defined = false;

	private static String serverName;
	private static String userName;
	private static String userPass;
	private static String databaseName;
	private static Integer serverPort;

	/***
	 *
	 * @param serverName
	 * @param serverPort
	 * @param userName
	 * @param userPass
	 * @param databaseName
	 */
	public Database(String serverName, Integer serverPort, String userName, String userPass, String databaseName) {
		defineConnection(serverName, serverPort, userName, userPass, databaseName);
	}

	/***
	 *
	 * @param serverName
	 * @param serverPort
	 * @param userName
	 * @param userPass
	 * @param databaseName
	 * @return
	 */
	public static boolean defineConnection(String serverName, Integer serverPort, String userName, String userPass, String databaseName) {

		Database.defined = false;

		if(serverName.equals(null))
			log("DB :: You must enter the server name.");
		else if(serverPort.equals(null))
			log("DB :: You must enter the server port.");
		else if(serverPort < 1 || serverPort > 65536)
			log("DB :: You must enter a valid server port (between 1 and 65535).");
		else if(userName.equals(null))
			log("DB :: You must enter the server user name.");
		else if(userPass.equals(null))
			log("DB :: You must enter the server user password.");
		else if(databaseName.equals(null))
			log("DB :: You must enter the server database name.");
		else {

			if(serverName.equalsIgnoreCase("localhost") || serverName.equalsIgnoreCase("127.0.0.1"))
				log("DB :: Defining a local mysql connection.");
			else
				log("DB :: Defining an external mysql connection.");

			if(databaseName.length() > 64)
				log("DB :: Database name or table name is too long. Try using an shorter one.");
			else {
				try {
					Database.serverName = serverName;
					Database.serverPort = serverPort;
					Database.userName = userName;
					Database.userPass = userPass;
					Database.databaseName = databaseName;

					Database.database = new MySQL("TheMeetCraftPlugin: ", Database.serverName, Database.serverPort, Database.databaseName, Database.userName, Database.userPass);
					Database.defined = true;
				}
				catch(Exception e) {
					log("DB :: Error: " + e.getMessage());
				}
				finally {
					if(Database.defined && serverName.equalsIgnoreCase("localhost"))
						log("DB :: Defined a local mysql connection.");
					else if(Database.defined && !serverName.equalsIgnoreCase("localhost"))
						log("DB :: Defined an external mysql connection.");
					else
						log("DB :: Error: Failed to define a mysql connection.");
				}
			}
		}

		return connect();
	}

	/***
	 *
	 * @return
	 */
	public static boolean connect() {
		int tries = 1;

		while(tries < 32 && !Database.database.isOpen())
			try {
				Database.database.open();
			}
			catch(Exception e) {
				log("DB :: Error: " + e.getMessage());
			}
			finally {
				tries++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log("DB :: Error: " + e.getMessage());
				}
			}

		if(Database.database.isOpen())
		{
			if(Database.serverName.equalsIgnoreCase("localhost"))
				log("DB :: We have connected to a local mysql connection.");
			else if(Database.defined && !serverName.equalsIgnoreCase("localhost"))
				log("DB :: We have connected to an external mysql connection.");

			return true;
		}

		log("DB :: Error: We are not able to connect to your database.");
		return false;
	}

	/***
	 * 
	 */
	public static void log(String str) {
		String[] data = str.split(" :: ");
		Main.log(data[0], data[1]);
	}
	
	/***
	 * Disconnect from the server.
	 */
	public static void disconnect() {
		Database.database.close();
	}

	/***
	 * Check if the connection has already been defined.
	 * @return
	 */
	public static boolean isDefined() {
		return Database.defined;
	}

	/***
	 * Check if the connection is alive.
	 */
	public static boolean isConnected() {
		return Database.database.isOpen();
	}

	/***
	 * Check if the table is exists.
	 * @param table The name of the table.
	 * @return boolean
	 */
	public static boolean isTable(String table) {
		return Database.database.isTable(table);
	}

	/***
	 *
	 * @param value
	 */
	public static void setPause(boolean value) {
		if(value) {
			Database.paused = true;
			log("DB :: Paused your database connection.");
			return;
		}

		Database.paused = false;
		log("DB :: Disabled your pause on your database connection.");
	}

	/***
	 *
	 * @param table
	 * @param updates
	 * @param where
	 */
	public static void update(String table, String updates, String where) {
		try {
			database.query("UPDATE " + table + " SET " + updates + " WHERE " + where);
		} catch (SQLException e) {
			log("DB :: Update -> Exception: " + e.getMessage());
		}
	}

	/***
	 *
	 * @param table
	 * @param where
	 * @return
	 */
	public static boolean delete(String table, String where) {
		try {
			database.query("DELETE FROM " + table + " WHERE " + where);
			return true;
		} catch (SQLException e) {
			log("DB :: Delete -> Exception: " + e.getMessage());
			return false;
		}
	}

	/***
	 *
	 * @param table
	 * @param columns
	 * @param values
	 */
	public static void insert(String table, String columns, String values) {
		try {
			database.query("INSERT INTO " + table + " ( " + columns + " ) VALUES " + " ( " + values + " )");
		} catch (SQLException e) {
			log("DB :: Insert -> Exception: " + e.getMessage());
		}
	}

	/***
	 *
	 * @param table
	 */
	public static void empty(String table) {
		try {
			database.query("TRUNCATE TABLE " + table);
		} catch (SQLException e) {
			log("DB :: Empty -> Exception: " + e.getMessage());
		}
	}

	/***
	 *
	 * @param table
	 * @param columns
	 * @param where
	 * @return
	 */
	public static ResultSet getRs(String table, String columns, String where) {
		return getRs(table, columns, where, null, null, null);
	}

	/***
	 *
	 * @param table
	 * @param columns
	 * @param where
	 * @param limit
	 * @param order
	 * @param offset
	 * @return
	 */
	public static ResultSet getRs(String table, String columns, String where, Integer limit, String order, Integer offset) {
		ResultSet rs;
		String query = "SELECT " + columns + " FROM " + table;

		if(Strings.checkString(where))
			query = query + " WHERE " + where;

		if(Strings.checkString(order))
			query = query + " ORDER BY " + order;

		if(limit != null)
			query = query + " LIMIT " + limit;

		if(offset != null)
			query = query + " OFFSET " + offset;

		if(Database.paused)
			return null;

		try {
			rs = database.query(query);
			return rs;
		} catch(Exception ex) {
			log("DB :: getRs -> Exception: " + ex.getMessage());
            ex.printStackTrace();
            return null;
		}
	}

	/***
	 *
	 * @param table
	 * @param column
	 * @param condition
	 * @return
	 */
	public static String gets(String table, String column, String condition) {
		return (String) grab(String.class, table, column, condition, null);
	}

	/***
	 *
	 * @param table
	 * @param column
	 * @param condition
	 * @return
	 */
	public static Integer geti(String table, String column, String condition) {
		return (Integer) grab(Integer.class, table, column, condition, null);
	}

	/***
	 *
	 * @param table
	 * @param column
	 * @param condition
	 * @return
	 */
	public static Double getd(String table, String column, String condition) {
		return (Double) grab(Double.class, table, column, condition, null);
	}

	/***
	 *
	 * @param table
	 * @param column
	 * @param condition
	 * @return
	 */
	public static Boolean getb(String table, String column, String condition) {
		return (Boolean) grab(Boolean.class, table, column, condition, null);
	}

	/***
	 *
	 * @param table
	 * @param column
	 * @param condition
	 * @return
	 */
	public static Float getf(String table, String column, String condition) {
		return (Float) grab(Float.class, table, column, condition, null);
	}

	/***
	 *
	 * @param <T>
	 * @param type
	 * @param table
	 * @param columns
	 * @param condition
	 * @param defaultReturn
	 * @return
	 */
	public static <T> Object grab(T type, String table, String columns, String condition, Object defaultReturn) {

		if(Database.paused)
			return null;

		try {
			ResultSet rs = database.query("SELECT " + columns + " FROM " + table + " WHERE " + condition);
            while(rs.next()) {
            	if(type.equals(String.class))
                	return rs.getString(columns);
            	else if(type.equals(Boolean.class))
                	return rs.getBoolean(columns);
            	else if(type.equals(Integer.class))
                	return rs.getInt(columns);
            	else if(type.equals(Float.class))
                	return rs.getFloat(columns);
            	else if(type.equals(Double.class))
                	return rs.getDouble(columns);
            }

        } catch (Exception ex) {
            log("DB :: grab -> Exception: " + ex.getMessage());
            ex.printStackTrace();
        }

		return defaultReturn;
	}

	/***
	 *
	 * @param <T>
	 * @param type
	 * @param table
	 * @param columns
	 * @param defaultReturn
	 * @return
	 */
	public static <T> Object random(T type, String table, String columns, Object defaultReturn) {
		if(Database.paused)
			return null;

		try {
			ResultSet rs = database.query("SELECT " + columns + " FROM " + table + " ORDER BY RAND() LIMIT 1");
            while(rs.next()) {
            	if(type.equals(String.class))
                	return rs.getString(columns);
            	else if(type.equals(Boolean.class))
                	return rs.getBoolean(columns);
            	else if(type.equals(Integer.class))
                	return rs.getInt(columns);
            	else if(type.equals(Float.class))
                	return rs.getFloat(columns);
            	else if(type.equals(Double.class))
                	return rs.getDouble(columns);
            }

        } catch (Exception ex) {
            log("DB :: random -> Exception: " + ex.getMessage());
            ex.printStackTrace();
        }

		return defaultReturn;
	}

	/**
	 *
	 * @param <T>
	 * @param type
	 * @param table
	 * @param columns
	 * @param where
	 * @param defaultReturn
	 * @return
	 */
	public static <T> Object random(T type, String table, String columns, String where, Object defaultReturn) {
		if(Database.paused)
			return null;

		try {
			ResultSet rs = database.query("SELECT " + columns + " FROM " + table + " WHERE " + where + " ORDER BY RAND() LIMIT 1");
            while(rs.next()) {
            	if(type.equals(String.class))
                	return rs.getString(columns);
            	else if(type.equals(Boolean.class))
                	return rs.getBoolean(columns);
            	else if(type.equals(Integer.class))
                	return rs.getInt(columns);
            	else if(type.equals(Float.class))
                	return rs.getFloat(columns);
            	else if(type.equals(Double.class))
                	return rs.getDouble(columns);
            }

        } catch (Exception ex) {
            log("DB :: random -> Exception: " + ex.getMessage());
            ex.printStackTrace();
        }

		return defaultReturn;
	}

	/***
	 * Count any rows in the given table.
	 * @param table
	 * @return
	 */
	public static Integer count(String table) {
		return count(table, null);
	}

	/**
	 *
	 * @param table
	 * @param where
	 * @return
	 */
	public static Integer count(String table, String where) {
		if(Database.paused)
			return null;

		try {
			ResultSet rs;

			if(where == null)
				rs = database.query("select count(0) FROM " + table);
			else
				rs = database.query("select count(0) FROM " + table + " WHERE " + where);

            while(rs.next())
                return rs.getInt("count(0)");
        } catch (Exception ex) {
            log("DB :: count -> Exception: " + ex.getMessage());
            ex.printStackTrace();
        }

		return null;
	}

	/***
	 *
	 * @param table
	 * @param row
	 * @param where
	 * @return
	 */
	public static Integer countAll(String table, String row, String where) {
		if(Database.paused)
			return null;

		try {
			ResultSet rs;
			int num = 0;

			if(where == null)
				rs = database.query("select " + row + "FROM " + table);
			else
				rs = database.query("select " + row + " FROM " + table + " WHERE " + where);

            while(rs.next())
                num = num + rs.getInt(row);

            return num;
        } catch (Exception ex) {
            log("DB :: countall -> Exception: " + ex.getMessage());
            ex.printStackTrace();
        }

		return null;
	}

	/***
	 *
	 * @param table
	 * @param column
	 * @param where
	 * @return
	 */
	public static List<String> getList(String table, String column, String where) {
		ResultSet rs = null;
		List<String> list = new ArrayList<>();

		try {
			rs = getRs(table, column, where);
			while(rs.next())
				list.add(rs.getString(column));
			return list;
		} catch(Exception ex) {
			// error
			return null;
		}
	}

	/***
	 *
	 * @param table
	 * @param column
	 * @param where
	 * @return
	 */
	public static List<String> AJAX_LIST(String table, String column, String where, String where2) {
		ResultSet rs = null;
		List<String> list = new ArrayList<>();

		try {
			if(where2 == null)
				rs = database.query("SELECT " + column + " FROM " + table + " WHERE " + column + " LIKE '%" + where + "%'");
			else
				rs = database.query("SELECT " + column + " FROM " + table + " WHERE " + column + " LIKE '%" + where + "%' AND " + where2);

			while(rs.next())
				list.add(rs.getString(column));
			return list;
		} catch(Exception ex) {
			// error
			return null;
		}
	}

}