import java.awt.Graphics;

/**
 * 
 * Klasa absktrakcyjna opisujaca stan gry
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public abstract class GameState {
	/**
	 * Referencja na aplikacje
	 */
	protected Application app;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param app - referencja na aplikacje
	 */
	public GameState(Application app) {
		this.app = app;
	}
	
	/**
	 * Funkcja aktualizujaca stan gry
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	public abstract void update(double dt);
	
	/**
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 */
	public abstract void render(Graphics g);
}
