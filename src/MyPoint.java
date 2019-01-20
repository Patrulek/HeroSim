
/**
 * 
 * 
 * Klasa opisujaca punkt/wektor
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 */
public class MyPoint {
	/**
	 * Wartosc x
	 */
	public double x;
	/**
	 * Wartosc y
	 */
	public double y;
	
	/**
	 * Konstruktor klasy
	 */
	public MyPoint() {
		x = y = 0;
	}
	/**
	 * Konstruktor klasy
	 * 
	 * @param x - wartosc x
	 * @param y - wartosc y
	 */
	public MyPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param position - punkt
	 */
	public MyPoint(MyPoint position) {
		this.x = position.x;
		this.y = position.y;
	}
	
	/**
	 * Funkcja dodajaca dwa punkty i zwracaja nowy punkt
	 * 
	 * @param p1 - punkt pierwszy
	 * @param p2 - punkt drugi
	 * 
	 * @return Zwraca nowy punkt
	 */
	public static MyPoint addPoints(MyPoint p1, MyPoint p2) {
		return new MyPoint(p1.x + p2.x, p1.y + p2.y);
	}
	/**
	 * Funkcja odejmujaca dwa punkty i zwracaja nowy punkt
	 * 
	 * @param p1 - punkt pierwszy
	 * @param p2 - punkt drugi
	 * 
	 * @return Zwraca nowy punkt
	 */
	public static MyPoint subPoints(MyPoint p1, MyPoint p2) {
		return new MyPoint(p1.x - p2.x, p1.y - p2.y);
	}
	
	/**
	 * Funkcja zwracajaca kwadrat odleglosci miedzy dwoma punktami
	 * 
	 * @param p1 - punkt pierwszy
	 * @param p2 - punkt drugi
	 * 
	 * @return Zwraca kwadrat odleglosci miedzy punktami
	 */
	public static double getDistanceSquared(MyPoint p1, MyPoint p2) {
		return ((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y));
	}
	
	/**
	 * Funkcja dodajaca do obecnego punktu kolejny punkt
	 * 
	 * @param p2 - punkt
	 */
	public void addPoint(MyPoint p2) {
		this.x += p2.x;
		this.y += p2.y;
	}
	
	/**
	 * Funkcja mnozaca punkt przez liczbe
	 * 
	 * @param scalar - liczba rzeczywista
	 * 
	 * @return Zwraca ten sam punkt
	 */
	public MyPoint scalarPoint(double scalar) {
		this.x *= scalar;
		this.y *= scalar;
		return this;
	}
}
