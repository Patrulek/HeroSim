import java.util.Vector;


/**
 * Abstrakcyjna klasa opisujaca czlowieka
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public abstract class Person extends GameObject implements Runnable{

	/**
	 * Enum okreslajacy kierunek ruchu 
	 *
	 */
	enum Direction {
		UP, RIGHT, DOWN, LEFT, NOWALK;
	}
	
	/**
	 * Imie czlowieka
	 */
	protected String name;
	/**
	 * Wektor przechowujacy identyfikatory punktow grafu (sciezke po ktorej bedzie sie poruszac)
	 */
	protected Vector<Integer> shortestPath;
	/**
	 * Kierunek ruchu
	 */
	protected Direction direction;
	/**
	 * Zajmowany kafel (kolumna)
	 */
	protected int tileX;
	/**
	 * Zajmowany kafel (wiersz)
	 */
	protected int tileY;
	/**
	 * Czy zyje
	 */
	protected boolean isAlive;
	/**
	 * Referencja na miasto w ktorym sie aktualnie znajduje
	 */
	protected Town actualTown;
	/**
	 * Referencja na miasto do ktorego podaza
	 */
	protected Town nextTown;
	/**
	 * Czy jest w miescie
	 */
	protected boolean inTown;
	/**
	 * Predkosc przemieszczania sie (w pikselach)
	 */
	protected double speed;
	/**
	 * Czy glowna petla watku ma byc przetwarzana
	 */
	protected boolean running;
	/**
	 * Czas po smierci po ktorym nalezy zatrzymac przetwarzanie petli
	 */
	protected double timeToDelete;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param world - referencja na swiat
	 * @param name - imie osoby
	 */
	protected Person(World world, String name) {
		super(GameObject.NO_POSITION, GameObject.NO_DIMENSION, world);
		this.name = name;
		isAlive = true;
		inTown = false;
		nextTown = null;
		speed = 32.0;
		running = true;
	}
	
	/**
	 * Abstrakcyjna funkcja wysylajaca czlowieka do innego miasta
	 */
	protected abstract void goToOtherTown();
	
	/**
	 * Abstrakcyjna funkcja wywolywana gdy osoba wejdzie do miasta
	 */
	protected abstract void onTownEnter();
	
	/**
	 * Abstrakcyjna funkcja wywolywana gdy osoba wejdzie na skrzyzowanie
	 * Parametr potrzebny dla cywili
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	protected abstract void onCrossroadEnter(double dt);
	
	/**
	 * Abstrakcyjna funkcja wywolywana gdy osoba wejdzie na punkt w grafie
	 */
	protected abstract void onGraphPointEnter();
	
	/**
	 * Funkcja wywolywana gdy osoba zginie
	 */
	protected void die() {
		isAlive = false;
	}
	
	/**
	 * Funkcja symulujaca ruch osoby
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	protected void walk(double dt) {

		calculateTilePosition();
		GraphPoint firstPoint = null;
		GraphPoint secondPoint = null;
		Vector<GraphPoint> GPs = world.getPathFinder().getGraphPoints();
		
		if(shortestPath != null) {
			if(shortestPath.size() > 1) {
				firstPoint = GPs.get(shortestPath.get(0));
				secondPoint = GPs.get(shortestPath.get(1));
			}
			else {
				firstPoint = GPs.get(shortestPath.get(0));
				secondPoint = null;
			}
			chooseDirection(firstPoint, secondPoint);
		}
		
		move(dt);
		
		if(steppedOnGraphPoint()) {
			if(shortestPath.get(1) == world.getGraphPointByObjectPosition(this).getId())
				shortestPath.remove(0);
			
			if(steppedOnCrossroad())
				onCrossroadEnter(dt);
			else if(reachedTown())
				onTownEnter();
			else
				onGraphPointEnter();
		}
	}
	
	/**
	 * Funkcja przemieszczajaca osobe o odpowiedni wektor
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	protected void move(double dt) {
		switch(direction) {
			case RIGHT:
				position.x += speed * dt;
				break;
			case LEFT:
				position.x -= speed * dt;
				break;
			case UP:
				position.y -= speed * dt;
				break;
			case DOWN:
				position.y += speed * dt;
				break;
		}
	}
	
	/**
	 * Obliczenie pozycji kafelka na ktorym znajduje sie osoba
	 */
	protected void calculateTilePosition() {
		switch(direction) {
			case RIGHT:
				tileX = (int)Math.floor(position.x / world.getTileSize());
				tileY = (int)Math.floor(position.y / world.getTileSize());
				break;
			case LEFT:
				tileX = (int)Math.ceil(position.x / world.getTileSize());
				tileY = (int)Math.floor(position.y / world.getTileSize());
				break;
			case UP:
				tileX = (int)Math.floor(position.x / world.getTileSize());
				tileY = (int)Math.ceil(position.y / world.getTileSize());
				break;
			case DOWN:
				tileX = (int)Math.floor(position.x / world.getTileSize());
				tileY = (int)Math.floor(position.y / world.getTileSize());
				break;
			default:
				tileX = (int)Math.floor(position.x / world.getTileSize());
				tileY = (int)Math.floor(position.y / world.getTileSize());
				break;
		}
		int maxX = world.getSize().width / world.getTileSize();
		int maxY = world.getSize().height / world.getTileSize();
		
		tileX = tileX >= maxX ? maxX - 1 : tileX < 0 ? 0 : tileX;
		tileY = tileY >= maxY ? maxY - 1 : tileY < 0 ? 0 : tileY;
	}
	
	/**
	 * Wybranie odpowiedniego kierunku ruchu na podstawie odcinka sciezki na jakim znajduje sie osoba
	 * @param firstPoint - pierwszy punkt odcinka
	 * @param secondPoint - drugi punkt odcinka
	 */
	protected void chooseDirection(GraphPoint firstPoint, GraphPoint secondPoint) {
			int pathLayer[][] = world.getPathLayer();
			Direction oldDirection = direction;
			
			switch(pathLayer[tileY][tileX]) {
				case 1:
					direction = Direction.UP;
					break;
				case 2:
					direction = Direction.RIGHT;
					break;
				case 3:
					direction = Direction.DOWN;
					break;
				case 4:
					direction = Direction.LEFT;
					break;
				case 5:
					if(secondPoint != null) {
						if(secondPoint.getPosition().x > firstPoint.getPosition().x)
							direction = Direction.RIGHT;
						else
							direction = Direction.UP;
						break;
					}
				case 6:
					if(secondPoint != null) {
						if(secondPoint.getPosition().y < firstPoint.getPosition().y)
							direction = Direction.UP;
						else
							direction = Direction.LEFT;
						break;
					}
				case 7:
					if(secondPoint != null) {
						if(secondPoint.getPosition().y > firstPoint.getPosition().y)
							direction = Direction.DOWN;
						else
							direction = Direction.RIGHT;
						break;
					}
				case 8:
					if(secondPoint != null) {
						if(secondPoint.getPosition().x < firstPoint.getPosition().x)
							direction = Direction.LEFT;
						else
							direction = Direction.DOWN;
						break;
					}
			}
			
			if(!oldDirection.equals(direction)) {
				position.x = tileX * world.getTileSize();
				position.y = tileY * world.getTileSize();
			}
	}

	/**
	 * Ustawienie poczatkowej pozycji podczas wychodzenia z miasta
	 * 
	 * @param firstPoint - pierwszy odcinek sciezki
	 * @param secondPoint - drugi odcinek sciezki
	 */
	protected void setStartPosition(GraphPoint firstPoint, GraphPoint secondPoint) {
		if(secondPoint.getPosition().x > firstPoint.getPosition().x) {
			position.x = firstPoint.getPosition().x + 2 * world.getTileSize(); 
			position.y = firstPoint.getPosition().y + world.getTileSize();
		} else if(secondPoint.getPosition().x < firstPoint.getPosition().x)  {
			position.x = firstPoint.getPosition().x - world.getTileSize();
			position.y = firstPoint.getPosition().y;
		} else if(secondPoint.getPosition().y > firstPoint.getPosition().y) {
			position.x = firstPoint.getPosition().x;
			position.y = firstPoint.getPosition().y + 2 * world.getTileSize();
		} else if(secondPoint.getPosition().y < firstPoint.getPosition().y){
			position.x = firstPoint.getPosition().x + world.getTileSize();
			position.y = firstPoint.getPosition().y - world.getTileSize();
		}
	}
	
	/**
	 * Funkcja sprawdzajaca czy osoba weszla do miasta
	 * 
	 * @return Zwraca true gdy tak, false w przeciwnym przypadku
	 */
	protected boolean reachedTown() {
		Vector<Town> towns = world.getTowns();
		for(int i = 0; i < towns.size(); i++) {
			if(world.objectsCollide(this, towns.get(i)))
				return true;
		}
		return false;
	}
	
	/** 
	 * Funkcja sprawdzajaca czy osoba weszla na skrzyzowanie
	 * 
	 * @return Zwraca true gdy tak, false w przeciwnym przypadku
	 */
	protected boolean steppedOnCrossroad() {
		Vector<Crossroad> crossroads = world.getCrossroads();
		for(int i = 0; i < crossroads.size(); i++) {
			if(world.objectsCollide(this, crossroads.get(i)))
				return true;
		}
		return false;
	}
	
	/**
	 * Funkcja sprawdzajaca czy osoba weszla na punkt grafu
	 * 
	 * @return Zwraca true, gdy tak, else w przeciwnym przypadku
	 */
	protected boolean steppedOnGraphPoint() {
		Vector<GraphPoint> graphPoints = world.getPathFinder().getGraphPoints();
		for(int i = 0; i < graphPoints.size(); i++) {
			if(world.objectsCollide(this, graphPoints.get(i)))
				return true;
		}
		return false;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vector<Integer> getShortestPath() {
		return shortestPath;
	}

	public void setShortestPath(Vector<Integer> shortestPath) {
		this.shortestPath = shortestPath;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public int getTileX() {
		return tileX;
	}

	public void setTileX(int tileX) {
		this.tileX = tileX;
	}

	public int getTileY() {
		return tileY;
	}

	public void setTileY(int tileY) {
		this.tileY = tileY;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public Town getActualTown() {
		return actualTown;
	}

	public void setActualTown(Town actualTown) {
		this.actualTown = actualTown;
	}

	public Town getNextTown() {
		return nextTown;
	}

	public void setNextTown(Town nextTown) {
		if(shortestPath != null && shortestPath.size() > 2)
			this.nextTown = nextTown;
	}

	public boolean isInTown() {
		return inTown;
	}

	public void setInTown(boolean inTown) {
		this.inTown = inTown;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public double getTimeToDelete() {
		return timeToDelete;
	}

	public void setTimeToDelete(double timeToDelete) {
		this.timeToDelete = timeToDelete;
	}
}
