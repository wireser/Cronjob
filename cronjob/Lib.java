package cronjob;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/***
 * Functions that are used globally.
 * @author wireser
 *
 */
public class Lib {

	/***
	 *
	 * @param ar
	 * @return
	 */
	public static Object[] shuffleArray(Object[] ar)
	{
	    Random rnd = ThreadLocalRandom.current();

	    for(int i = ar.length - 1; i > 0; i--)
	    {
	    	int index = rnd.nextInt(i + 1);
	    	Object a = ar[index];
	    	ar[index] = ar[i];
	    	ar[i] = a;
	    }

	    return ar;
	}

	/***
	 *
	 * @param number
	 * @return
	 */
	public static Integer forceInt(String number) {
		try {
			return Integer.parseInt(number);
		} catch(Exception ex) {
			return null;
		}
	}

	/***
	 *
	 * @param number
	 * @return
	 */
	public static Double forceDouble(String number) {
		try {
			return Double.parseDouble(number);
		} catch(Exception ex) {
			return null;
		}
	}

}
