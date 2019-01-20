import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 
 * Klasa opisujaca panel informacyjny w symulacji
 * 
 * @author Patryk Lewandowsk
 * @version 1.0
 *
 */
public class InfoPanel extends DBPanel {
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param size - rozmiar panelu
	 */
	public InfoPanel(Dimension size) {
		super(size);
		
		setBounds(800, 0, size.width, size.height);
		setBackground(Color.RED);
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
