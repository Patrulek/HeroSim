import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Abstrakcyjna klasa przedstawiajaca podstawowy obiekt w grze.
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public abstract class GameObject {
	/**
	 * Stala okreslajaca niezdefiniowana wielkosc (oznacza to np. ze obiekt jest nieskonczenie wielki lub maly)
	 */
	static final public int UNDEFINED_VALUE = 1 << 31;
	/**
	 * Stala okreslajaca rozmiar obiektu (nieskonczenie wielki lub maly)
	 */
	static final public Dimension NO_DIMENSION = new Dimension(UNDEFINED_VALUE, UNDEFINED_VALUE);
	/**
	 * Stala okreslajaca pozycje obiektu (obiekt nie posiada pozycji, jest wszedzie lub nigdzie)
	 */
	static final public MyPoint NO_POSITION = new MyPoint(UNDEFINED_VALUE, UNDEFINED_VALUE);
	
	/**
	 * Pozycja obiektu (w pikselach)
	 */
	protected MyPoint position;
	/**
	 * Rozmiar obiektu (w pikselach)
	 */
	protected Dimension size;
	/**
	 * Referencja na swiat
	 */
	protected World world;
	/**
	 * Mapa spriteow, ktore posiada obiekt
	 */
	protected Map<String, Sprite> sprites;
	/**
	 * Obecnie wyswietlany sprite
	 */
	protected String activeSprite;
	/**
	 * Okresla na ktorej warstwie znajduje sie obiekt
	 */
	protected short zIndex;
	
	/**
	 * Konstruktor obiektu
	 * 
	 * @param position - pozycja w swiecie (w pikselach)
	 * @param dimension - rozmiar obiektu (w pikselach)
	 * @param world - referencja na swiat gry
	 */
	public GameObject(MyPoint position, Dimension dimension, World world) {
		this.position = position;
		this.size = dimension;
		this.world = world;
		sprites = new HashMap<String, Sprite>();
		activeSprite = "";
		zIndex = 1;
	}
	
	/**
	 * Funkcja aktualizujaca stan obiektu
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	public void update(double dt) {
		if(!sprites.isEmpty())
			sprites.get(activeSprite).update(dt);
	}
	
	/**
	 * Funkcja abstrakcyjna rysujaca obiekt
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 * @param cam - kamera swiata
	 * 
	 */
	public abstract void render(Graphics g, Camera cam);

	public MyPoint getPosition() {
		return position;
	}

	public void setPosition(MyPoint position) {
		this.position = position;
	}

	public Dimension getSize() {
		return size;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Map<String, Sprite> getSprites() {
		return sprites;
	}

	public void setSprites(Map<String, Sprite> sprites) {
		this.sprites = sprites;
	}

	public String getActiveSprite() {
		return activeSprite;
	}

	public void setActiveSprite(String activeSprite) {
		this.activeSprite = activeSprite;
	}

	public short getzIndex() {
		return zIndex;
	}

	public void setzIndex(short zIndex) {
		this.zIndex = zIndex;
	}
}
