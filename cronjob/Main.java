package cronjob;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import org.yaml.snakeyaml.Yaml;

public class Main {
	
	/***/ // TICKERS
	public static Timer ticker;
	public static TimerTask stick;
	public static TimerTask mtick;
	public static TimerTask htick;
	
	public static boolean stickState = false;
	public static boolean mtickState = false;
	public static boolean htickState = false;
	
	/***/ // FILES
	public static File configFile;
	public static InputStream configStream;
	public static Yaml configYaml = new Yaml();
	
	/***/ // DATABASE CONNECTION
	public static String dbName;
	public static String dbPort;
	public static String dbUser;
	public static String dbPass;
	public static String dbServer;
	
	public static Database database;
	
	/***/ // CONFIGURATION
	public static String dateFormat = "yyyy-MM-dd HH:mm:ss";
	
	/***/ // COLORS
	public static final String RESET = "\u001B[0m";
	public static final String BLACK = "\u001B[30m";
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String BLUE = "\u001B[34m";
	public static final String PURPLE = "\u001B[35m";
	public static final String CYAN = "\u001B[36m";
	public static final String WHITE = "\u001B[37m";

	/***/ // OTHER
	public static HashMap<String, Object> config;
	public static List<String> params;
	
	public static boolean debug;
	
    public static void main(String[] args) {
    	
    	/**/ // 0.0 Startup
    	params = new ArrayList<String>();  
    	
    	if(args.length > 0)
    		for(String key : args)
    			params.add(key);
    	
    	ticker = new Timer();
    	
    	if(params.contains("--debug"))
    		debug = true;
    	debug = true;
    	
    	/**/ // 1.0 -- Loading config file and defining variables
    	log("startup", "Loading confing file...");
    	try {
    		configFile = new File("config.yml");
    		configStream = new FileInputStream(configFile);
    		config = configYaml.load(configStream);
		} catch (Exception ex) {
			error("startup", "Config file not found.", ex);
		}
    	log("startup", "Loaded config file.");
    	
    	log("startup", "Loading variables...");
    	try {
    		dbName = config("dbName");
    		dbPort = config("dbPort");
    		dbUser = config("dbUser");
    		dbPass = config("dbPass");
    		dbServer = config("dbServer");
    		
    		if(dbServer == null)
    			dbServer = "localhost";
    		if(dbPort == null)
    			dbPort = "3306";
    		
    		dateFormat = config("date");
    	} catch(Exception ex) {
    		error("startup", "Failed to load variables.", ex);
    	}
    	log("startup", "Loaded variables.");

    	/**/ // 2.0 -- Connect to database
    	log("startup", "Connecting to database...");
    	try {
    		database = new Database(dbServer, Integer.parseInt(dbPort), dbUser, dbPass, dbName);
    	} catch(Exception ex) {
    		error("startup", "Failed to connect to database.", ex);
    	}
    	log("startup", "Connected to database.");
    	
    	/**/ // 3.0 -- Loading tickers
        stick = new TimerTask() {
            @Override
            public void run() {
                if(!stickState) {
                	log("startup", "Ticker:SECOND started.");
                	stickState = true;
                }
            }
        };
        
        mtick = new TimerTask() {
            @Override
            public void run() {
            	if(!mtickState) {
                	log("startup", "Ticker:MINUTE started.");
                	mtickState = true;
                }
            }
        };
        
        htick = new TimerTask() {
            @Override
            public void run() {
            	if(!htickState) {
                	log("startup", "Ticker:HOUR started.");
                	htickState = true;
                }
            }
        };

        
        /***/ // 4.0 -- Starting tickers
        long currentTime = System.currentTimeMillis();
        long initialDelay;
        
        initialDelay = 1000 - (currentTime % 1000); /**/ initialDelay = 1000 - (currentTime % 1000);
        log("startup", "Syncing up second ticker (" + initialDelay + "ms).");
        ticker.scheduleAtFixedRate(stick, initialDelay, 1000);
        
        initialDelay = (60 - (currentTime / 1000) % 60) * 1000; /**/ initialDelay = 1000 - (currentTime % 1000);
        log("startup", "Syncing up minute ticker (" + (initialDelay/1000) + "s).");
        ticker.scheduleAtFixedRate(mtick, initialDelay, 60 * 1000);
        
        initialDelay = (60 - (currentTime / (60 * 1000)) % 60) * 60 * 1000; /**/ initialDelay = 1000 - (currentTime % 1000);
        log("startup", "Syncing up hour ticker (" + (initialDelay/1000/60) + "m).");
        ticker.scheduleAtFixedRate(htick, initialDelay, 60 * 60 * 1000);
        
        /***/ // 5.0 -- Loop
        Scanner scanner = new Scanner(System.in);
        String userInput;

        while(true) {
            userInput = scanner.nextLine();

            if(userInput.equalsIgnoreCase("quit") || userInput.equalsIgnoreCase("exit"))
                break;
        }

        scanner.close();
    }
    
    public static String config(String key) {
    	try {
    		return (String) config.get(key);
    	} catch(Exception ex) {
    		return null;
    	}
    }
    
    public static String date() {
    	LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return now.format(formatter);
    }
    
    public static void log(String sender, String str) {
    	System.out.println(WHITE + date() + " [" + sender.toUpperCase() + "] :: " + str + BLACK);
    }
    
    public static void error(String str) {
    	error("LOGGER", str, null, false);
    }
    public static void error(String sender, String str) {
    	error(sender, str, null, false);
    }
    public static void error(String sender, String str, Exception ex) {
    	error(sender, str, ex, false);
    }
    public static void error(String sender, String str, Exception ex, boolean exit) {
    	System.out.println(RED + date() + " [" + sender.toUpperCase() + "] :: " + str + BLACK);
    	
    	if(debug && ex != null)
    		ex.printStackTrace();
    	
    	if(exit)
    		System.exit(0);
    }
    
}
