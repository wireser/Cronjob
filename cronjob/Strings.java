package cronjob;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class Strings {

	/***
	 *
	 * @param string
	 * @return
	 */
	public static String clearString(String string) {
		if(!checkString(string))
			return "";

		string = string.replaceAll("'", "");
		string = string.replaceAll("\\\\", "");
		return string;
	}

	/***
	 *
	 * @param input
	 * @return
	 */
	public static Integer parseDuration(String input) {
		int multiplier = 1;

		try {
			switch(input.charAt(input.length() - 1)) {
				case 'm': multiplier = 1; break;
				case 'h': multiplier = 60; break;
				case 'd': multiplier = 60 * 24; break;
				case 'w': multiplier = 60 * 24 * 7; break;
				default: return Lib.forceInt(input) * multiplier;
			}

			return Lib.forceInt(removeLastChar(1, input)) * multiplier;
		} catch(Exception ex) {
			return null;
		}
	}

	/***
	 *
	 * @param input
	 * @param duration
	 * @return
	 */
	public static String durationText(String input, Integer duration) {
		if(input.endsWith("h"))
			if(duration < 2) return "hour";
			else return "hours";
		if(input.endsWith("d"))
			if(duration < 2) return "day";
			else return "days";
		if(input.endsWith("w"))
			if(duration < 2) return "week";
			else return "weeks";
		else if(input.endsWith("m"))
			if(duration < 2) return "month";
			else return "months";
		else if(input.endsWith("y"))
			if(duration < 2) return "year";
			else return "years";
		else
			if(duration < 2) return "minute";
			else return "minutes";
	}

	/***
	 *
	 * @param number
	 * @return
	 */
	public static String numberToRoman(Integer number) {
		int[] values = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
        String[] romanLiterals = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};

        StringBuilder roman = new StringBuilder();

        for(int i = 0; i < values.length; i++)
            while(number >= values[i]) {
            	number -= values[i];
                roman.append(romanLiterals[i]);
            }

        return roman.toString();
	}

	/***
	 *
	 * @param msg
	 * @return
	 */
	public static boolean checkString(String msg) {
		if(msg == null || msg == "" || msg.length() == 0 || msg.isEmpty())
			return false;
		return true;
	}

	/***
	 *
	 * @param i
	 * @param str
	 * @return
	 */
	public static String removeLastChar(Integer i, String str) {
	    if(str != null && str.length() > i)
	        str = str.substring(0, str.length() - i);
	    return str;
	}

	/***
	 * Convert a string variable to a string array with one index.
	 * @param value The string that needs to be put in an array.
	 * @return new String[1] array
	 */
	public static String[] stringToArray(String value) {
		return new String[] { value };
	}

	/***
	 *
	 * @param value
	 * @return
	 */
	public static String[] convertToArray(String value) {
		return value.split("@");
	}

	/***
	 *
	 * @param value
	 * @return
	 */
	public static String color(String value) {
		return value.replaceAll("&", "\247");
	}

	/***
	 *
	 * @return
	 */
	public static String getDate() {
		Calendar now = Calendar.getInstance();

		String yer = now.get(Calendar.YEAR) + "";
		String mon = (now.get(Calendar.MONTH)+1)  + "";
		String day = now.get(Calendar.DAY_OF_MONTH) + "";
		String hor = now.get(Calendar.HOUR_OF_DAY) + "";
		String min = now.get(Calendar.MINUTE) + "";
		String sec = now.get(Calendar.SECOND) + "";

		if(Integer.parseInt(mon) < 10) mon = "0" + mon;
		if(Integer.parseInt(day) < 10) day = "0" + day;
		if(Integer.parseInt(hor) < 10) hor = "0" + hor;
		if(Integer.parseInt(min) < 10) min = "0" + min;
		if(Integer.parseInt(sec) < 10) sec = "0" + sec;

		return yer + "-" + mon + "-" + day + " " + hor + ":" + min + ":" + sec;
	}

	/***
	 *
	 * @return
	 */
	public static String getJustDate() {
		Calendar now = Calendar.getInstance();

		String yer = now.get(Calendar.YEAR) + "";
		String mon = (now.get(Calendar.MONTH)+1)  + "";
		String day = now.get(Calendar.DAY_OF_MONTH) + "";

		if(Integer.parseInt(mon) < 10) mon = "0" + mon;
		if(Integer.parseInt(day) < 10) day = "0" + day;

		return yer + "-" + mon + "-" + day;
	}

	/***
	 *
	 * @return
	 */
	public static Integer getJustMinute() {
		return Calendar.getInstance().get(Calendar.MINUTE);
	}

	/***
	 *
	 * @param args
	 * @param start
	 * @return
	 */
	public static String fm(String args[], int start)
	{
		String value = new String("");
		for(int i = start; i < args.length; i++)
			value = value + args[i] + " ";
		return removeLastChar(1, value);
	}

	/***
	 *
	 * @param input
	 * @return
	 */
	public static String niceName(String input) {
		return capitalizeFirst(input.toLowerCase().replace("_", " "));
	}

	/**
	 *
	 * @param str
	 * @return
	 */
	public static String capitalizeFirst(String str) {
		if(str == null)
	    	return str;
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/***
	 * 
	 * @param str
	 * @return
	 */
	public static String capitalizeFirstLetters(String str) {
		String[] words = null;
		StringBuilder value = new StringBuilder();

		if(str == null)
	    	return str;

		if(!str.contains(" "))
			return capitalizeFirst(str);

		words = str.split(" ");

		for(String word : words)
			value.append( word.substring(0, 1).toUpperCase() + word.substring(1) + " " );

		return removeLastChar(1, value.toString());
	}

	/**
	 *
	 * @param str
	 * @param maxWidth
	 * @return
	 */
	public static List<String> wrap(String str, int maxWidth) {
		if(!checkString(str))
			return new ArrayList<>();

        List<String> lines = splitIntoLines(str);
        if (lines.isEmpty()) {
            return lines;
        }

        ArrayList<String> strings = new ArrayList<>();
        for(Iterator<String> iter = lines.iterator(); iter.hasNext();) {
            wrapLineInto(iter.next(), strings, maxWidth);
        }
        return strings;
    }

	/**
	 *
	 * @param str
	 * @return
	 */
	public static List<String> splitIntoLines(String str) {
        ArrayList<String> strings = new ArrayList<>();
        if (str != null) {
            int len = str.length();
            if (len == 0) {
                strings.add("");
                return strings;
            }

            int lineStart = 0;

            for (int i = 0; i < len; ++i) {
                char c = str.charAt(i);
                if (c == '\r') {
                    int newlineLength = 1;
                    if ((i + 1) < len && str.charAt(i + 1) == '\n') {
                        newlineLength = 2;
                    }
                    strings.add(str.substring(lineStart, i));
                    lineStart = i + newlineLength;
                    if (newlineLength == 2) // skip \n next time through loop
                    {
                        ++i;
                    }
                } else if (c == '\n') {
                    strings.add(str.substring(lineStart, i));
                    lineStart = i + 1;
                }
            }
            if (lineStart < len) {
                strings.add(str.substring(lineStart));
            }
        }
        return strings;
    }

	/**
	 *
	 * @param line
	 * @param list
	 * @param maxWidth
	 */
    public static void wrapLineInto(String line, List<String> list, int maxWidth) {
        int len = line.length();
        while (len > maxWidth) {
            // Guess where to split the line. Look for the next space before
            // or after the guess.
            int pos;
            if (len > maxWidth) // Too long
            {
                pos = findBreakBefore(line, maxWidth);
            } else { // Too short or possibly just right
                pos = len;
            }
            list.add(line.substring(0, pos).trim());
            line = line.substring(pos).trim();
            len = line.length();
        }
        if (len > 0) {
            list.add(line);
        }
    }

    /**
     *
     * @param line
     * @param start
     * @return
     */
    public static int findBreakBefore(String line, int start) {
        for (int i = start; i >= 0; --i) {
            char c = line.charAt(i);
            if (Character.isWhitespace(c) || c == '-') {
                return i;
            }
        }
        return -1;
    }

}
