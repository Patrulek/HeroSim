import java.awt.Dimension;
import java.awt.Graphics;

/**
 * 
 * Klasa opisujaca punkt grafu
 *
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 */
public class GraphPoint extends GameObject{
	
	/**
	 * Identyfikator punktu w grafie
	 */
	private int id;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param id - identyfikator punktu
	 * @param position - pozycja w swiecie (w pikselach)
	 * @param size - rozmiar obiektu (w pikselach)
	 * @param world - referencja na swiat
	 */
	public GraphPoint(int id, MyPoint position, Dimension size, World world) {
		super(position, size, world);
		this.id = id;
	}
	
	/**
	 * Funkcja aktualizujaca obiekt
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	public void update(double dt) {
		
	}
	
	/**
	 * Funkcja rysujaca punkt
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 * @param cam - kamera swiata
	 */
	public void render(Graphics g, Camera cam) {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}