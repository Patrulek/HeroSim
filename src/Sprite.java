import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Klasa opisujaca sprite'a
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class Sprite {
	/**
	 * Referencja na teksture do wyswietlania
	 */
	protected BufferedImage texture;
	/**
	 * Obszar tekstury ktory ma byc rysowany
	 */
	protected Rectangle textureRegion; 
	/**
	 * Czy rysowany obiekt ma byc traktowany jak gui (pozycja stala, niezalezna od kamery)
	 */
	protected boolean guiObject;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param txt - referencja na teksture
	 * @param guiObject - czy obiekt jest traktowany jako gui
	 */
	public Sprite(BufferedImage txt, boolean guiObject) {

		this.texture = txt;
		this.guiObject = guiObject;
		textureRegion = new Rectangle(0, 0, txt.getWidth(), txt.getHeight());
	}
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param txt - referencja na teksture
	 * @param txtRegion - obszar rysowanej tekstury
	 * @param guiObject - czy obiekt jest trkatowany jako gui
	 */
	public Sprite(BufferedImage txt, Rectangle txtRegion, boolean guiObject) {
		this.texture = txt;
		this.textureRegion = txtRegion;
		this.guiObject = guiObject;
	}
	
	/** 
	 * Funkcja aktualizujaca stan sprite'a
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	public void update(double dt) {
		
	}
	
	/**
	 * Funkcja rysujaca sprite'a
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 * @param cam - kamera swiata
	 * @param go - referencja na obiekt dla ktorego ma byc rysowany sprite
	 */
	public void render(Graphics g, Camera cam, GameObject go) {
		int dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2;
		if(!guiObject) {
			dx1 = (int)(go.getPosition().x - cam.getPosition().x);
			dy1 = (int)(go.getPosition().y - cam.getPosition().y);
		}
		else {
			dx1 = (int)(go.getPosition().x);
			dy1 = (int)(go.getPosition().y);
		}
		
		
		if(textureRegion.width > cam.getSize().width) {		// taki trick ¿eby nie wyœwietlaæ ca³ego obrazka 1600x1600 (obiekt musi mieæ sta³¹ pozycjê 0, 0)
			sx1 = (int)(cam.getPosition().x);
			sx2 = sx1 + cam.getSize().width;
			dx1 = 0;
			dx2 = dx1 + cam.getSize().width;
		}
		else {
			sx1 = textureRegion.x;
			sx2 = sx1 + textureRegion.width;
			dx2 = dx1 + go.getSize().width;
		}
		if(textureRegion.height > cam.getSize().height) {
			sy1 = (int)(cam.getPosition().y);
			sy2 = sy1 + cam.getSize().height;
			dy1 = 0;
			dy2 = dy1 + cam.getSize().height;
		}
		else {
			sy1 = textureRegion.y;
			sy2 = sy1 + textureRegion.height;
			dy2 = dy1 + go.getSize().height;
		}
		
		g.drawImage(texture, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}


	public BufferedImage getTexture() {
		return texture;
	}

	public void setTexture(BufferedImage texture) {
		this.texture = texture;
	}

	public Rectangle getTextureRegion() {
		return textureRegion;
	}

	public void setTextureRegion(Rectangle textureRegion) {
		this.textureRegion = textureRegion;
	}

	public boolean isGuiObject() {
		return guiObject;
	}

	public void setGuiObject(boolean guiObject) {
		this.guiObject = guiObject;
	}
}
