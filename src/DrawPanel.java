import java.awt.Dimension;
import java.awt.Graphics;


/**
 *
 * 
 * Klasa opisujaca glowny panel aplikacji
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class DrawPanel extends DBPanel {
	/**
	 * Referencja na aplikacje
	 */
	private Application app;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param size - rozmiar panelu (w pikselach)
	 * @param app - referencja na aplikacje
	 */
	public DrawPanel(Dimension size, Application app) {
		super(size);
		
		this.app = app;
		
		setLayout(null);
	}
	
	/**
	 * Funkcja rysujaca panel
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		app.getGameState().render(g);
	}
}
