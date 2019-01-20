import java.awt.Dimension;
import java.awt.Graphics;


/**
 * 
 * Klasa opisujaca skrzyzowanie
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class Crossroad extends GameObject {
	/**
	 * Identyfikator skrzyzowania (obecnie chyba do niczego nie wykorzystywany)
	 */
	private int id;
	/**
	 * Czy skrzyzowanie jest zajete przez cywila
	 */
	private boolean occupied;
	/**
	 * Referencja na cywila okupujacego skrzyzowanie
	 */
	private Civilian civilian;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param id - identyfikator skrzyzowania
	 * @param position - pozycja w swiecie (w pikselach)
	 * @param size - rozmiar skrzyzowania (w pikselach)
	 * @param world
	 */
	public Crossroad(int id, MyPoint position, Dimension size, World world) {
		super(position, size, world);
		this.id = id;
		occupied = false;
		civilian = null;
	}
	
	/**
	 * Funkcja aktualizujaca stan skrzyzowania
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	public void update(double dt) {
		synchronized(Monitors.crossroadGuard) {
			if((civilian != null && (!civilian.isAlive() || !world.objectsCollide(this, civilian))) || civilian == null) {
				unlock();
			}
		}
	}

	/**
	 * Funkcja wyswietlajaca skrzyzowanie
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 * @param cam - kamera swiata
	 * 
	 */
	public void render(Graphics g, Camera cam) {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Funkcja blokujaca skrzyzowania dla danego cywila
	 * 
	 * @param c - referencja na cywila
	 */
	public void lock(Civilian c) {
		civilian = c;
		occupied = true;
	}
	
	/**
	 * Funkcja zwalniajaca skrzyzowanie
	 */
	public void unlock() {
		civilian = null;
		occupied = false;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	public Civilian getCivilian() {
		return civilian;
	}

	public void setCivilian(Civilian civilian) {
		this.civilian = civilian;
	}
}
