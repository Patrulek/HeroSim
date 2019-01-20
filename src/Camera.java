import java.awt.Dimension;

/**
 * 
 * 
 * Klasa kamery
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class Camera {
	/**
	 * Pozycja kamery (w odniesieniu do swiata)
	 */
	private MyPoint position;
	/**
	 * Rozmiar kamery (w pikselach)
	 */
	private Dimension size;
	/**
	 * Czy sledzic obiekt
	 */
	private boolean trackTarget;
	/**
	 * Referencja na swiat gry
	 */
	private World world;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param size - rozmiar kamery
	 * @param world - referencja na swiat gry
	 */
	public Camera(Dimension size, World world) {
		this.world = world;
		this.size = size;
		position = new MyPoint();
		trackTarget = false;
	}
	
	/**
	 * Funkcja aktualizujaca stan kamery
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	public void update(double dt) {
		GameObject target = world.getSimulation().getTarget();
		
		if(target != null && trackTarget) {
			position.x = (target.getPosition().x + target.getSize().width/2) - size.width/2;
			position.y = (target.getPosition().y + target.getSize().height/2) - size.height/2;
		}
		else if(target == null && trackTarget) {
			trackTarget = false;
		}
		
		Dimension worldSize = world.getSize();

		if(position.x < 0)
			position.x = 0;
		else if(position.x + size.width > worldSize.width)
			position.x = worldSize.width - size.width;
				
		if(position.y < 0)
			position.y = 0;
		else if(position.y + size.height > worldSize.height)
			position.y = worldSize.height - size.height;
	}
	
	/**
	 * Funkcja przemieszczajaca kamera o dany wektor
	 * 
	 * @param vec - wektor przesuniecia
	 */
	public void move(MyPoint vec) {
		if(!trackTarget) {
			position.addPoint(vec);
			update(0.0);
		}
	}

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

	public boolean isTrackTarget() {
		return trackTarget;
	}

	public void setTrackTarget(boolean trackTarget) {
		this.trackTarget = trackTarget;
	}
}
