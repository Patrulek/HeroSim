import java.awt.Dimension;
import java.awt.Graphics;

/**
 * 
 * Klasa, tylko na potrzeby wyswietlania obiektu 
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class BackgroundObject extends GameObject {

	/**
	 * Konstruktor klasy
	 * 
	 * @param position - pozycja w swiecie (w pikselach)
	 * @param dimension - rozmiar obiektu (w pikselach)
	 * @param world - referencja na swiat
	 * @param textureKey - klucz pod jakim znajduje sie tekstura obiektu
	 */
	public BackgroundObject(MyPoint position, Dimension dimension, World world, String textureKey) {
		super(position, dimension, world);
		sprites.put("main", new Sprite(TextureManager.getInstance().getTexture(textureKey), true));
		activeSprite = "main";
	}

	/**
	 * Funkcja wyswietlajaca obiekt
	 */
	public void render(Graphics g, Camera cam) {
		sprites.get(activeSprite).render(g, cam, this);
	}

}
