import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Vector;

/**
 *
 * Klasa przestawiajaca cywila
 * @author Patryk Lewandowski
 * @version 1.0
 * 
 *
 */
public class Civilian extends Person {
	
	/**
	 * Nazwisko cywila
	 */
	private String surname;
	
	/**
	 * Miasto rodzinne
	 */
	private Town homeTown;
	/**
	 * Czas po ktorym cywil bedzie chcial opuscic miasto (w sekundach)
	 */
	private double maxTimeInTown;
	/**
	 * Czy cywil moze wyjsc z miasta
	 */
	private boolean canLeaveTown;
	/**
	 * Referencja na skrzyzowanie na ktorym aktualnie sie znajduje (null, gdy na zadnym)
	 */
	private Crossroad crossroad;
	/**
	 * Czy cywil sie zatrzymal
	 */
	private boolean stopped;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param name - imie cywila
	 * @param surname - nazwisko cywila
	 * @param homeTown - referencja na miasto rodzinne
	 * @param world - referencja na swiat
	 */
	public Civilian(String name, String surname, Town homeTown, World world) {
		super(world, name);
		this.surname = surname;
		this.homeTown = actualTown = homeTown;
		
		synchronized(Monitors.townGuard) {
			actualTown.addPerson();
			actualTown.addCivilianToQueue(this);
		}
		
		canLeaveTown = false;
		inTown = true;
		
		direction = Direction.NOWALK;
		shortestPath = null;
		crossroad = null;
		stopped = false;
		speed = 48.0;
		
		size = new Dimension(16, 16);
		position = new MyPoint(homeTown.getPosition().x, homeTown.getPosition().y);
		calculateTilePosition();
		
		maxTimeInTown =  MyRandom.getDouble(5, 15);
		
		timeToDelete = 5.0;
		running = true;
		
		sprites.put("civilian_u", new AnimatedSprite(TextureManager.getInstance().getTexture("civilian_u"), new Dimension(24, 16), 0.1, 8, 1, 8, true));
		sprites.put("civilian_d", new AnimatedSprite(TextureManager.getInstance().getTexture("civilian_d"), new Dimension(24, 16), 0.1, 8, 1, 8, true));
		sprites.put("civilian_r", new AnimatedSprite(TextureManager.getInstance().getTexture("civilian_r"), new Dimension(16, 24), 0.1, 8, 8, 1, true));
		sprites.put("civilian_l", new AnimatedSprite(TextureManager.getInstance().getTexture("civilian_l"), new Dimension(16, 24), 0.1, 8, 8, 1, true));
		sprites.put("dead", new Sprite(TextureManager.getInstance().getTexture("citizenDead"), false));
		activeSprite = "civilian_u";
	}
	
	/**
	 * Funkcja aktualizujaca stan cywila
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	public void update(double dt) {
		if(isAlive) {
			if(inTown) {
				maxTimeInTown -= dt;
				
				if(maxTimeInTown <= 0.0 && canLeaveTown) {
					maxTimeInTown = MyRandom.getDouble(5, 15) ;
					goToOtherTown();
				}
			}
			else {
				if(MyRandom.getDouble() <= 0.0001) {
					stopped = true;
					synchronized(Monitors.simulationGuard) {
						if(world.getSimulation().getTarget() == this)
							world.getSimulation().setUpdateConstructPanel(true);
					}
				} 
				if(!stopped && shortestPath != null)
					walk(dt);
				if(findNewTown() == null)
					die();
				
				synchronized(Monitors.townGuard) {
					if(homeTown.isDestroyed())
						homeTown = findNewTown();
					synchronized(Monitors.simulationGuard) {
						if(world.getSimulation().getTarget() == this)
							world.getSimulation().setUpdateInfoPanel(true);
					}
				}
			}
			switch(direction) {
				case RIGHT:
					activeSprite = "civilian_r";
					break;
				case LEFT:
					activeSprite = "civilian_l";
					break;
				case UP:
					activeSprite = "civilian_u";
					break;
				case DOWN:
					activeSprite = "civilian_d";
					break;
			}
		}
		else {
			timeToDelete -= dt;
			if(timeToDelete <= 0.0)
				running = false;
			activeSprite = "dead";
			zIndex = 0;
		}
		super.update(dt);
	}
	
	/**
	 * Funkcja wysylajaca cywila do innego miasta. Wyszukuje droge niezniszczonego jeszcze miasta
	 */
	protected void goToOtherTown() {
		if(actualTown == homeTown) {
			Vector<Town> towns = new Vector<Town>(); towns.add(homeTown);
			nextTown = findNewTown(towns);
		}
		else
			nextTown = homeTown;
		
		if(nextTown != null && nextTown != actualTown) {
			shortestPath = world.getPathFinder().findShortestPath(world.getPathFinder().findPathsBetweenTowns(actualTown, nextTown));
			leaveTown();
		}
		
		synchronized(Monitors.simulationGuard) {
			Simulation sim = world.getSimulation();
			if(sim.getTarget() == this)
				sim.setUpdateInfoPanel(true);
		}
	}
	
	/**
	 * Funkcja ustawiajaca cywila na odpowiednia pozycje podczas wychodzenia z miasta
	 */
	private void leaveTown() {
		if(shortestPath != null) {
			Vector<GraphPoint> GPs = world.getPathFinder().getGraphPoints();
			GraphPoint firstPoint = GPs.get(shortestPath.get(0));
			GraphPoint secondPoint = GPs.get(shortestPath.get(1));
				
			setStartPosition(firstPoint, secondPoint);
			inTown = canLeaveTown = false;
			synchronized(Monitors.townGuard) {
				actualTown.removeCivilianFromQueue(this);
				actualTown.resetTimeToLetCivilianGo();
				actualTown.subPerson();
				actualTown = null;
			}
		}
	}
	
	/**
	 * Funkcja wywolywana przy smierci cywila
	 */
	protected void die() {
		if(inTown) {
			synchronized(Monitors.townGuard) {
				actualTown.subPerson();
				actualTown.removeCivilianFromQueue(this);
			}
			running = false;
		}
		synchronized(Monitors.crossroadGuard) {
			if(crossroad != null) {
				crossroad.unlock();
				crossroad = null;
			}
		}
		super.die();
	}
	
	/**
	 * Funkcja wyswietlajaca cywila
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 * @param cam - kamera swiata
	 */
	public void render(Graphics g, Camera cam) {
		if(!inTown) {
			sprites.get(activeSprite).render(g, cam, this);
		}
	}
	
	/**
	 * Funkcja przemieszczajaca cywila
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	protected void move(double dt) {
		if(canMove(dt)) 
			super.move(dt);
	}
	
	/**
	 * Funkcja okreslajaca czy cywil moze sie przemiescic
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 * 
	 * @return Zwraca true, jesli moze sie ruszyc, false jesli nie
	 */
	private boolean canMove(double dt) {
		Vector<Civilian> vC = null;
		synchronized(Monitors.worldGuard) {
			vC = world.getCivilans();
			for(int i = 0; i < vC.size(); i++) {
				Civilian c = vC.get(i);
				if(this == c || !c.isAlive()) continue;
				else if(direction == c.direction) {
					switch(direction) {
						case RIGHT:
							if(tileY == c.tileY && c.getPosition().x > position.x && Math.abs(c.getPosition().x - position.x) < 24)
								return false;
							break;
						case LEFT:
							if(tileY == c.tileY && c.getPosition().x < position.x && Math.abs(c.getPosition().x - position.x) < 24)
								return false;
							break;
						case UP:
							if(tileX == c.tileX && c.getPosition().y < position.y && Math.abs(c.getPosition().y - position.y) < 24)
								return false;
							break;
						case DOWN:
							if(tileX == c.tileX && c.getPosition().y > position.y && Math.abs(c.getPosition().y - position.y) < 24)
								return false;
							break;
						case NOWALK:
							break;
						default:
							break;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Funkcja znajdujaca niezniszczone jeszcze miasto
	 * 
	 * @return Zwraca niezniszczone miasto lub null, gdy takie nie istnieje
	 */
	protected Town findNewTown() {
		Vector<Town> towns = null; 
		towns = world.getNotDestroyedTowns();
		
		if(towns == null)
			return null;
		return towns.get(MyRandom.getInt(towns.size()));
	}
	
	/**
	 * Funkcja podobna do {@link #findNewTown()}
	 * 
	 * @param t - parametr okreslajacy ktore miasta maja byc wykluczone z wyszukiwania
	 * 
	 * @return Zwraca niezniszczone miasto lub null, gdy takie nie istnieje
	 */
	protected Town findNewTown(Vector<Town> t) { // z wyjatkiem miast ...
		Vector<Town> towns = null;
		towns = world.getNotDestroyedTowns();
		
		if(towns == null)
			return null;
		
		for(int i = 0; i < towns.size(); i++)
			for(int j = 0; j < t.size(); j++)
				if(towns.get(i) == t.get(j)) {
					towns.remove(i);
					i--;
				}
		if(!towns.isEmpty())
			return towns.get(MyRandom.getInt(towns.size()));
		return null;
	}
	
	/**
	 * Funkcja z glowna petla watku
	 */
	public void run() {
		double start, elapsed;
		start = System.nanoTime();
		while(running) {
			
			elapsed = System.nanoTime() - start;
			update(elapsed/1000000000);
			start = System.nanoTime();
				
			try {
				if((System.nanoTime() - start)/(1000000 * 1000) < Application.FRAME_STEP)
				{
					int toSleep = (int)((Application.FRAME_STEP - ((System.nanoTime() - start)/1000000000.0)) * 1000);
						if(toSleep > 0)
					Thread.sleep(toSleep);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		synchronized(Monitors.simulationGuard) {
			Simulation sim = world.getSimulation();
			if(sim.getTarget() == this) {
				sim.setTarget(null);
				sim.setUpdateInfoPanel(true);
				sim.setUpdateConstructPanel(true);
			}
		}
		
		synchronized(Monitors.worldGuard) {
			world.removeCivilianFromWorld(this);
			world.getCivilNames().add(name);
			world.getSurnames().add(surname);
		}
	}
	
	/**
	 * Funkcja wywolywana podczas wejscia do miasta
	 */
	protected void onTownEnter() {
		actualTown = world.getTownByObjectPosition(this);
		nextTown = null;
		inTown = true;
		direction = Direction.NOWALK;
		synchronized(Monitors.townGuard) {
			actualTown.addCivilianToQueue(this);
			actualTown.addPerson();
			
			if(actualTown.isDestroyed()) {
				canLeaveTown = true;
				maxTimeInTown = 0.0;
			}
		}
		shortestPath.clear(); shortestPath = null;
		position.x = actualTown.getPosition().x;
		position.y = actualTown.getPosition().y;
		calculateTilePosition();
	}

	/**
	 * Funkcja wywolywana podczas wejscia na skrzyzowanie
	 */
	protected void onCrossroadEnter(double dt) {
		synchronized(Monitors.crossroadGuard) {
			crossroad = world.getCrossroadByObjectPosition(this);
			if(!crossroad.isOccupied()) 
				crossroad.lock(this);
			else if(crossroad.isOccupied() && crossroad.getCivilian() != this) {
				super.move(-dt);
			}
		}
		
		if(nextTown.isDestroyed())
			nextTown = homeTown;
		
		shortestPath = world.getPathFinder().findShortestPath(world.getPathFinder().findPathToTown(world.getGraphPointIDByCrossroad(crossroad), nextTown));
	}

	/**
	 * Funkcja wywolywana podczas wejscia na punkt grafu
	 */
	protected void onGraphPointEnter() {}
	
	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Town getHomeTown() {
		return homeTown;
	}

	public void setHomeTown(Town homeTown) {
		this.homeTown = homeTown;
	}

	public Town getActualTown() {
		return actualTown;
	}

	public void setActualTown(Town actualTown) {
		this.actualTown = actualTown;
	}

	public double getMaxTimeInTown() {
		return maxTimeInTown;
	}

	public void setMaxTimeInTown(double maxTimeInTown) {
		this.maxTimeInTown = maxTimeInTown;
	}

	public boolean isInTown() {
		return inTown;
	}

	public void setInTown(boolean inTown) {
		this.inTown = inTown;
	}

	public boolean isCanLeaveTown() {
		return canLeaveTown;
	}

	public void setCanLeaveTown(boolean canLeaveTown) {
		this.canLeaveTown = canLeaveTown;
	}

	public Crossroad getCrossroad() {
		return crossroad;
	}

	public void setCrossroad(Crossroad crossroad) {
		this.crossroad = crossroad;
	}

	public boolean isStopped() {
		return stopped;
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}
}
