import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Vector;

/**
 * Klasa opisujaca bohatera
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class Superhero extends Warrior {
	
	/**
	 * Referencja na miasto rodzinne (stolice w tym przypadku)
	 */
	private Town homeTown;
	/**
	 * Czy bohater wraca juz do miasta
	 */
	private boolean isReturning;
	/**
	 * Czy bohater ma czeka w miescie (na zloczynce)
	 */
	private boolean waitingForBadguy;

	/**
	 * Konsturktor klasy
	 * 
	 * @param world - referencja na swiat
	 * @param name - imie bohatera
	 */
	protected Superhero(World world, String name) {
		super(world, name);
		abilities = new Abilities(MyRandom.getDouble(15, 30), MyRandom.getDouble(15, 30), MyRandom.getDouble(15, 30),
				MyRandom.getDouble(15, 30), MyRandom.getDouble(15, 30), MyRandom.getDouble(15, 30));
		direction = Direction.NOWALK;
		size = new Dimension(16, 16);
		inTown = true;
		speed = 80.0;
		synchronized(Monitors.worldGuard) {
			actualTown = homeTown = world.getCapital(); 
		}
		enemy = null;
		nextTown = null;
		position = new MyPoint(actualTown.getPosition().x, actualTown.getPosition().y);
		isReturning = false;
		waitingForBadguy = true;
		
		timeToDelete = 0.75;
		
		sprites.put("superhero_r", new AnimatedSprite(TextureManager.getInstance().getTexture("superhero_r"), 
				new Dimension(24, 24), 0.2, 4, 1, 4, true));
		sprites.put("superhero_l", new AnimatedSprite(TextureManager.getInstance().getTexture("superhero_l"), 
				new Dimension(24, 24), 0.2, 4, 1, 4, true));
		sprites.put("superhero_d", new AnimatedSprite(TextureManager.getInstance().getTexture("superhero_d"), 
				new Dimension(24, 24), 0.2, 4, 4, 1, true));
		sprites.put("superhero_u", new AnimatedSprite(TextureManager.getInstance().getTexture("superhero_u"), 
				new Dimension(24, 24), 0.2, 4, 4, 1, true));
		sprites.put("superherodead", new AnimatedSprite(TextureManager.getInstance().getTexture("superherodead"), 
				new Dimension(24, 24), 0.15, 5, 1, 5, false));
		activeSprite = "superhero_r";
		zIndex = 3;
	}

	/**
	 * Funkcja wysylajaca bohatera w kierunku wybranego zloczyncy
	 */
	protected void goToOtherTown() {
		goToBadguy();
		leaveTown();
	}
	
	/**
	 * Funkja znajdujaca sciezke do glownego miasta
	 */
	protected void returnToCapital() {
		if(inTown && actualTown != homeTown) {
			shortestPath = world.getPathFinder().findShortestPath(world.getPathFinder().findPathsBetweenTowns(actualTown, homeTown));
			leaveTown();
		}
		else if(!inTown && steppedOnCrossroad()) {
			shortestPath = world.getPathFinder().findShortestPath(world.getPathFinder().findPathToTown(world.getGraphPointByObjectPosition(this).getId(), homeTown));
		}
	}
	
	/**
	 * Funkcja ustawiajaca pozycje podczas wychodzenia z miasta
	 */
	private void leaveTown() {
		if(shortestPath != null) {
			Vector<GraphPoint> GPs = world.getPathFinder().getGraphPoints();
			GraphPoint firstPoint = GPs.get(shortestPath.get(0));
			GraphPoint secondPoint = GPs.get(shortestPath.get(1));
			
			setStartPosition(firstPoint, secondPoint);
			inTown = false;
			actualTown = null;
		}
	}
	
	/**
	 * Funkcja wyszukujaca jakiegos zloczynce w swiecie
	 */
	public void findAnyBadguy() {
		if(!world.getBadguys().isEmpty())
			enemy = world.getBadguys().get(MyRandom.getInt(world.getBadguys().size()));
	}
	
	/**
	 * Funkcja wyboru sprite'a w zaleznosci od kierunku ruchu
	 */
	private void chooseSprite() {
		switch(direction) {
			case RIGHT:
				activeSprite = "superhero_r";
				break;
			case LEFT:
				activeSprite = "superhero_l";
				break;
			case UP:
				activeSprite = "superhero_u";
				break;
			case DOWN:
				activeSprite = "superhero_d";
				break;
		}
	}

	/**
	 * Funkcja aktualizujaca stan bohatera
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	public void update(double dt) {
		if(isAlive) {
			if(waitingForBadguy) {
				synchronized(Monitors.worldGuard) {
					for(int i = 0; i < world.getBadguys().size(); i++) {
						Badguy bg = world.getBadguys().get(i);
						synchronized(Monitors.badguyGuard) {
							if(bg != null && bg.isAlive()) {
								if(bg.getActualTown() == actualTown && bg.getFights() == 0) {
									synchronized(Monitors.superheroGuard) {
										Fight fight = new Fight(this, bg);
										world.getFights().add(fight);
									}
								}
							}
						}
					}
				}

				if(fights == 0)
					healthPoints = healthPoints + HERO_HP_REGEN * dt > MAX_HP ? MAX_HP : healthPoints + HERO_HP_REGEN * dt;
				synchronized(Monitors.simulationGuard) {
					if(world.getSimulation().getTarget() == this)
						world.getSimulation().setUpdateInfoPanel(true);
				}
			}
			else {
				if(inTown) {
					if(enemy != null && enemy.isAlive() && fights == 0 && enemy.getActualTown() != actualTown) {
						goToBadguy();
						leaveTown();
					}
					else if(fights == 0 && actualTown != homeTown) {
						synchronized(Monitors.worldGuard) {
							Vector<Badguy> badguys = world.getBadguysFromTown(actualTown);
							for(int i = 0; i < badguys.size(); i++) {
								Badguy bg = badguys.get(i);
								synchronized(Monitors.superheroGuard) {
									if(bg.getFights() == 0 || fights == 0) {
										Fight fight = new Fight(this, bg);
										world.getFights().add(fight);
									}
								}
							}
						}	
						
						if(fights == 0)
							returnToCapital();
					}
				}
				else {
					if((enemy == null || !enemy.isAlive()) && !isReturning)
						returnToCapital();
					
					if(fights == 0)
						walk(dt);
					attackBadguyInRange();
				}
			}
			
			chooseSprite();
		}
		else {
			timeToDelete -= dt;
			if(timeToDelete <= 0.0)
				running = false;
		}
		super.update(dt);
	}
	
	/**
	 * Funkcja znajdujaca sciezke do zloczyncy z aktualnej pozycji
	 */
	public void goToBadguy() {
		synchronized(Monitors.badguyGuard) {
			if(enemy != null) {
				nextTown = enemy.getActualTown();
				if(inTown) {
					if(nextTown != null)
						shortestPath = world.getPathFinder().findShortestPath(world.getPathFinder().findPathsBetweenTowns(actualTown, nextTown));
					else
						shortestPath = world.getPathFinder().findShortestPath(world.getPathFinder().findPathToPointFromTown(actualTown, enemy.getShortestPath().get(1)));
				}
				else {
					if(nextTown != null)
						shortestPath = world.getPathFinder().findShortestPath(world.getPathFinder().findPathToTown(world.getGraphPointByObjectPosition(this).getId(), nextTown));
					else {
						Vector<Vector<Integer>> allPaths = new Vector<Vector<Integer>>();
						if(shortestPath.get(0) != enemy.getShortestPath().get(1))
							world.getPathFinder().findPath(world.getGraphPointByObjectPosition(this).getId(), enemy.getShortestPath().get(1), allPaths);
						else
							world.getPathFinder().findPath(world.getGraphPointByObjectPosition(this).getId(), enemy.getShortestPath().get(0), allPaths);
						shortestPath = world.getPathFinder().findShortestPath(allPaths);
					}
				}
				synchronized(Monitors.simulationGuard) {
					Simulation sim = world.getSimulation();
						sim.setUpdateInfoPanel(true);
				}
			}
		}
	}
	
	/**
	 * Funkcja sprawdzajaca czy zloczynca jest w zasiegu ataku
	 * @param bg - zloczynca
	 * @return Zwraca true jesli tak, false w przeciwnym przypadku
	 */
	private boolean badguyInRange(Badguy bg) {
		if(!bg.isInTown()) {
			double x0 = position.x - 8;
			double y0 = position.y - 8;
			Dimension size0 = new Dimension(size.width + 16, size.height + 16);
			
			if(x0 > bg.getPosition().x + bg.getSize().width || x0 + size0.width < bg.getPosition().x ||
				y0 > bg.getPosition().y + bg.getSize().height || y0 + size0.height < bg.getPosition().y)
					return false;
			return true;
		}
		return false;
	}
	
	/**
	 * Funkcja atakujaca wszystkich zloczyncow w zasiegu
	 */
	private void attackBadguyInRange() {
		synchronized(Monitors.worldGuard) {
			Vector<Badguy> badguys = world.getBadguys();
			for(int i = 0; i < badguys.size(); i++) {
				Badguy bg = badguys.get(i);
				if(badguyInRange(bg)) {
					synchronized(Monitors.badguyGuard) {
						if(fights == 0 || bg.getFights() == 0) {
							synchronized(Monitors.superheroGuard) {
								Fight fight = new Fight(this, bg);
								world.getFights().add(fight);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Funkcja rysujaca bohatera
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 * @param cam - kamera swiata
	 */
	public void render(Graphics g, Camera cam) {
		if(!inTown) {
			sprites.get(activeSprite).render(g, cam, this);
			super.render(g, cam);
		}
	}


	/**
	 * Funkcja wywolywana przy smierci bohatera
	 */
	protected void die() {
		synchronized(Monitors.townGuard) {
			if(actualTown == homeTown)
				((Capital)homeTown).getSuperHeroes().remove(this);
		}
		activeSprite = "superherodead";
		super.die();
	}
	
	/**
	 * Funkcja wywolywana podczas wejscia bohatera do miasta
	 */
	protected void onTownEnter() {
		inTown = true;
		actualTown = world.getTownByObjectPosition(this);
		nextTown = homeTown;
		direction = Direction.NOWALK;
		
		synchronized(Monitors.townGuard) {
			if(!actualTown.isDestroyed()) {
				synchronized(Monitors.worldGuard) {
					Vector<Badguy> badguys = world.getBadguysFromTown(actualTown);
					for(int i = 0; i < badguys.size(); i++) {
						Badguy bg = badguys.get(i);
						synchronized(Monitors.superheroGuard) {
							if(bg.getFights() == 0 || fights == 0) {
								Fight fight = new Fight(this, bg);
								world.getFights().add(fight);
							}
						}
					}
				}	
			}
			else if(actualTown.isDestroyed() || (enemy != null && enemy.isAlive() && enemy.getActualTown() != actualTown)) {
				goToBadguy();
				leaveTown();
			}
			else if(enemy == null || !enemy.isAlive())
				returnToCapital();
		}
		
		if(actualTown == homeTown) {
			waitingForBadguy = true;
			synchronized(Monitors.townGuard) {
				((Capital)homeTown).getSuperHeroes().add(this);
				synchronized(Monitors.simulationGuard) {
					world.getSimulation().setUpdateInfoPanel(true);
				}
			}
		}
	}

	/**
	 * Funkcja wywolywana podczas wejscia bohatera na skrzyzowanie
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	protected void onCrossroadEnter(double dt) {
		if(enemy == null || !enemy.isAlive())
			returnToCapital();
		if(enemy != null && enemy.isAlive())
			goToBadguy(); 
	}

	/**
	 * Funkcja wywolywana podczas wejscia bohatera na graf punktu
	 */
	protected void onGraphPointEnter() {
		if(shortestPath.size() == 1)
			goToBadguy();
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
			world.getSuperHeroes().remove(this);
			world.getSuperheroNames().add(name);
		}
	}

	public Town getHomeTown() {
		return homeTown;
	}

	public void setHomeTown(Town homeTown) {
		this.homeTown = homeTown;
	}
	
	public Badguy getBadguy() {
		return (Badguy)enemy;
	}
	public boolean isReturning() {
		return isReturning;
	}

	public void setReturning(boolean isReturning) {
		this.isReturning = isReturning;
	}

	public boolean isWaitingForBadguy() {
		return waitingForBadguy;
	}

	public void setWaitingForBadguy(boolean waitingForBadguy) {
		this.waitingForBadguy = waitingForBadguy;
	}
}
