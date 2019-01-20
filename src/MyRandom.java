import java.util.Random;


/**
 * 
 * Klasa implementujaca generator liczb pseudolosowych
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class MyRandom {
	
	/**
	 * Obiekt generujacy liczby
	 */
	public static Random rand = new Random();
	
	/**
	 * Ustawienie ziarna generatora
	 * 
	 * @param seed - ziarno generatora
	 */
	public static void setSeed(long seed) {
		rand.setSeed(seed);
	}
	
	/**
	 * Funkcja zwracaja liczbe typu int
	 * 
	 * @return Zwraca liczbe typu int od 0 do MAX_RAND {@link Random} po wiecej informacji
	 */
	public static int getInt() {
		return rand.nextInt();
	}
	
	/**
	 * Funkcja zwracajaca liczbe typu int
	 * 
	 * @param n - liczba wieksza od 0
	 * 
	 * @return Zwraca liczbe z zakresu od 0 do n
	 */
	public static int getInt(int n) {
		if(n > 0)
			return rand.nextInt(n);
		return 0;
	}
	
	/**
	 * Funkcja zwracajaca liczbe typu int
	 * 
	 * @param min - poczatek przedzialu
	 * @param max - koniec przedzialu
	 * 
	 * @return Zwraca liczbe z zakresu od min do max
	 */
	public static int getInt(int min, int max) {
		return min + rand.nextInt(max - min);
	}
	
	/**
	 * Funkcja zwracajaca liczbe typu double
	 * 
	 * @return Zwraca liczbe rzeczywista od 0 do 1
	 */
	public static double getDouble() {
		return rand.nextDouble();
	}
	
	/**
	 * Funkcja zwracaja liczbe typu double
	 * 
	 * @param d - gorna granica przedzialu
	 * 
	 * @return Zwraca liczbe rzeczywista z zakresu od 0 do d
	 */
	public static double getDouble(double d) {
		return rand.nextDouble() * d;
	}
	
	/**
	 * Funkcja zwracajaca liczbe typu double
	 * 
	 * @param min - dolna granica przedzialu
	 * @param max - gorna granica przedzialu
	 * 
	 * @return Zwraca liczbe rzeczywista z zakresu od min do value max
	 */
	public static double getDouble(double min, double max) {
		return min + getDouble(max - min);
	}
}
