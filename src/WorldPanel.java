import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * 
 * Klasa opisujaca panel w ktorym rysowany jest swiat i wszystkie jego obiekty
 *
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 */
public class WorldPanel extends DBPanel{

	/**
	 * referencja na kamere
	 */
	private Camera camera;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param size - rozmiar panelu
	 */
	public WorldPanel(Dimension size) {
		super(size);
		
		setBounds(0, 0, 800, 600);
		setBackground(Color.PINK);
		setLayout(null);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(doubleBuffer, 0, 0, null);
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
}
