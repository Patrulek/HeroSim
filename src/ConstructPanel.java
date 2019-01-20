import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 
 * Klasa opisujaca panel konstrukcyjny w symulacji (zawierajacy buttony do akcji dla obiektow)
 * 
 * @author Patryk Lewandowsk
 * @version 1.0
 * 
 */
public class ConstructPanel extends DBPanel {

	/**
	 * Konstruktor klasy
	 * 
	 * @param size - rozmiar panelu (w pikselach)
	 * 
	 */
	public ConstructPanel(Dimension size) {
		super(size);
	
		setBounds(800, 300, 224, 300);
		setBackground(Color.BLUE);
		setLayout(null);
	}

	/**
	 * Funkcja rysujaca panel
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics dbGraphics = doubleBuffer.getGraphics();
		dbGraphics.clearRect(0, 0, 224, 300);
		dbGraphics.drawImage(TextureManager.getInstance().getTexture("panel_bg"), 0, 0, null);
		g.drawImage(doubleBuffer, 0, 0, null);
	}
}
