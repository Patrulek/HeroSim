import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * Klasa wykorzystywana do wyswietlania animowanych spriteow.
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 * 
 */
public class AnimatedSprite extends Sprite {

	/**
	 * Numer aktualnie wyswietlanej klatki. Przyjmuje wartosci od 0 do {@link #maxFrames} - 1
	 */
	private int currentFrame;
	/**
	 * Maksymalna liczba klatek animacji.
	 */
	private int maxFrames;
	/**
	 * Rozmiar pojedynczej klatki (w pikselach)
	 */
	private Dimension frameSize;
	/**
	 * Czas, jaki bedzie wyswietlana jedna klatka (w sekundach)
	 */
	private double animSpeed;
	/**
	 * Czas, ktory okresla jak dlugo dana klatka juz jest wyswietlana (w sekundach)
	 */
	private double currentTime;
	/**
	 * Liczba wierszy, z ktorych sklada sie tekstura animacji.
	 */
	private int rows;
	/**
	 * Liczba kolumn, z ktorych sklada sie tekstura animacji.
	 */
	private int columns;
	/**
	 * Okresla, czy animacja ma byc zapetlona.
	 */
	private boolean loop;
	
	
	public static final int INFINITY_PER_FRAME = 1 << 31;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param txt - referencja na teksture z animacja
	 * @param frameSize - rozmiar pojedynczej klatki
	 * @param animSpeed - dlugosc klatki
	 * @param maxFrames - ilosc klatek
	 * @param rows - liczba wierszy tekstury
	 * @param columns liczba kolumn tekstury
	 * @param loop - czy animacja ma byc zapetlona
	 */
	public AnimatedSprite(BufferedImage txt, Dimension frameSize, double animSpeed, int maxFrames, 
			int rows, int columns, boolean loop) {
		super(txt, false);
		
		this.frameSize = frameSize;
		this.animSpeed = animSpeed;
		this.maxFrames = maxFrames;
		this.rows = rows;
		this.columns = columns;
		this.currentTime = 0.0;
		this.currentFrame = 0;
		this.loop = loop;
	}
	
	/**
	 * Funkcja aktualizujaca stan obiektu
	 * 
	 * @param dt - czas przebiegu jednej klatki (w sekundach)
	 */
	public void update(double dt) {
		if(animSpeed != INFINITY_PER_FRAME) {
			currentTime += dt;
			
			if(currentTime > animSpeed) {
				currentFrame++;
				currentTime -= animSpeed;
			}
			
			if(currentFrame >= maxFrames) {
				if(loop)
					currentFrame = 0;
				else
					currentFrame = maxFrames - 1;
			}
		}
	}
	
	/**
	 * Funkcja rysujaca obiekt.
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 * @param cam - kamera swiata gry
	 * @param go - referencja do obiektu, dla ktorego ma byc narysowany sprite
	 */
	public void render(Graphics g, Camera cam, GameObject go) {
		int dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2;
		dx1 = (int)(go.getPosition().x - cam.getPosition().x);
		dy1 = (int)(go.getPosition().y - cam.getPosition().y);
		dx2 = dx1 + frameSize.width;
		dy2 = dy1 + frameSize.height;
		
		sx1 = (currentFrame % columns) * frameSize.width;
		sy1 = (currentFrame % rows) * frameSize.height;
		sx2 = sx1 + frameSize.width;
		sy2 = sy1 + frameSize.height;
		
		g.drawImage(texture, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}
}
