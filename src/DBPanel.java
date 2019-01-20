import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


/**
 * 
 * Klasa opisujaca panel z dodatkowym obrazem 
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class DBPanel extends JPanel {
	
	/**
	 * Bufor do rysowania, zanim obraz zostanie wyswietlony
	 */
	protected BufferedImage doubleBuffer;
	/**
	 * Rozmiar panelu (w pikselach)
	 */
	protected Dimension size;
	
	/**
	 * Konstruktor klasy (rozmiar bufora jest rowny rozmiarowi panelu)
	 * 
	 * @param size - rozmiar panelu (w pikselach)
	 */
	public DBPanel(Dimension size) {
		super();
		this.size = size;
		
		doubleBuffer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);

		setVisible(true);
		setSize(size.width, size.height);
	}
	
	/**
	 * Funkcja rysujaca panel
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	public BufferedImage getDoubleBuffer() {
		return doubleBuffer;
	}

	public void setDoubleBuffer(BufferedImage doubleBuffer) {
		this.doubleBuffer = doubleBuffer;
	}

	public Dimension getSize() {
		return size;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}
}
