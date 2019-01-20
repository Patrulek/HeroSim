import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Vector;

/**
 * 
 * Klasa zloczyncy
 *
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class Badguy extends Warrior {
	
	/**
	 * Czas jaki potrzebuje zloczynca na zabicie cywila, podczas ataku miasta (w sekundach)
	 */
	private double timeBetweenKills;
	/**
	 * Czy aktualnie atakuje miasto
	 */
	private boolean attackingTown;
	
	/**
	 * Konstruktor klasy (inicjalizuje potrzebne zmienne oraz tworzy sprite'y)
	 * 
	 * @param world - referencja na swiat
	 * @param name - imie zloczyncy
	 */
	public Badguy(World world, String name) {
		super(world, name);
		abilities = new Abilities(MyRandom.getDouble(15, 30), MyRandom.getDouble(15, 30), MyRandom.getDouble(15, 30), 
				MyRandom.getDouble(15, 30), MyRandom.getDouble(15, 30), MyRandom.getDouble(15, 30));
		actualTown = null;
		direction = Direction.NOWALK;
		size = new Dimension(16, 16);
		timeBetweenKills = 2.0;
		inTown = false;
		speed = 64.0;
		attackingTown = false;
		timeToDelete = 1.0;
		
		sprites.put("badguy", new AnimatedSprite(TextureManager.getInstance().getTexture("badguy"), 
				new Dimension(24, 24), 0.5, 4, 1, 4, true));
		sprites.put("badguydead", new AnimatedSprite(TextureManager.getInstance().getTexture("badguydead"), 
				new Dimension(24, 24), 0.2, 5, 1, 5, false));
		activeSprite = "badguy";
		zIndex = 2;
		
		spawn();
	}
	
	/**
	 * Funkcja ustawiajaca zloczynce na jednym ze {@link SpawnPoint}, wywolywana na koncu konstruktora 
	 */
	private void spawn() {
		SpawnPoint sp = world.getSpawnPoints().get(MyRandom.getInt(4));
		position = new MyPoint(sp.x, sp.y);
		
		nextTown = findClosestTown();
		shortestPath = world.getPathFinder().findShortestPath(world.getPathFinder().findPathToTown(sp.getGraphPointID(), nextTown));
	}

	/**
	 * Funkcja znajdujaca najblizsze niezniszczone miasto. Wywolywana w metodzie {@link #spawn()}
	 * 
	 * @return Zwraca referencje na najblizsze, niezniszczone miasto. Jesli takiego nie ma, zwraca {@link Person#nextTown}
	 */
	private Town findClosestTown() {
		Vector<Town> towns = null;
		synchronized(Monitors.worldGuard) {
			towns = world.getNotDestroyedTowns();
		}
		if(!towns.isEmpty()) {
			Town closestTown = null;
			double maxDistance = 9999999;
			for(int i = 0; i < towns.size(); i++) {
				Town t = towns.get(i);
				MyPoint myCenter = MyPoint.addPoints(position, new MyPoint(size.width, size.height));
				MyPoint townCenter = MyPoint.addPoints(t.getPosition(), new MyPoint(t.getSize().width, t.getSize().height));
				double distSq = MyPoint.getDistanceSquared(myCenter, townCenter);
				if(distSq < maxDistance) {
					maxDistance = distSq;
					closestTown = t;
				}
			}
			return closestTown;
		}
		return nextTown;
	}
	
	/**
	 * Funkcja wysylajaca zloczynce do innego miasta. Wyszukuje droge oraz
	 * uzywa funkcji {@link #findClosestTown()} i {@link #leaveTown()} do odpowiedniego ustawienia pozycji
	 */
	protected void goToOtherTown() {
		nextTown = findClosestTown();
		if(actualTown != nextTown) {
			shortestPath = world.getPathFinder().findShortestPath(world.getPathFinder().findPathsBetweenTowns(actualTown, nextTown));
			leaveTown();
		}
	}
	
	/**
	 * Funkcja ustawiajaca zloczynce na odpowiedniej pozycji podczas wychodzenia z miasta.
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
	 * Funkcja aktualizujaca stan zloczyncy.
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	public void update(double dt) {
		if(isAlive) {
			if(!inTown) {
				if(fights == 0) {
					walk(dt);
					killCiviliansInRange();
					healthPoints = healthPoints + BADGUY_HP_REGEN * dt > MAX_HP ? MAX_HP : healthPoints + BADGUY_HP_REGEN * dt;
					synchronized(Monitors.simulationGuard) {
						if(world.getSimulation().getTarget() == this)
							world.getSimulation().setUpdateInfoPanel(true);
					}
				}
			}
			else {
				if(fights == 0) {
					synchronized(Monitors.townGuard) {
						if(!actualTown.isDestroyed())
							attackTown(dt);
						else {
							if(attackingTown) {
								actualTown.subAttackCounter();
								attackingTown = false;
							}
							goToOtherTown();
						}
					}
				}
			}
		}
		else {
			timeToDelete -= dt;
			position.y -= 50 * dt;
			if(MyRandom.getInt() % 2 == 0)
				position.x += MyRandom.getInt(-30, -15) * dt;
			else
				position.x += MyRandom.getInt(15, 30) * dt;
			if(timeToDelete <= 0.0)
				running = false;
		}
		super.update(dt);
	}


	/**
	 * Funkcja wyswietlajaca zloczynce
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 * @param cam - kamera swiata
	 * 
	 */
	public void render(Graphics g, Camera cam) {
		if(!inTown) {
			sprites.get(activeSprite).render(g, cam, this);
			super.render(g, cam);
		}
	}
	
	/**
	 * Funkcja aktualizujaca stan zloczyncy i innych obiektow, gdy ten atakuje miasto
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	private void attackTown(double dt) {
		timeBetweenKills -= dt;
		if(timeBetweenKills <= 0.0) {
			timeBetweenKills = 2.0;
			if(!actualTown.getCivilianQueue().isEmpty()) {
				Civilian c = actualTown.getCivilianQueue().getFirst();
				if(c != null)
					c.die();
			}
		}
		else {
			if(actualTown.getPowerSources().size() > 1 || (actualTown.getPowerSources().size() == 1 && actualTown.getCivilianQueue().isEmpty())) {
				PowerSource ps = actualTown.getPowerSources().get(0);
				absorbPotential(ps);
			}
		}
	}
	
	/**
	 * Funkcja, ktora zwieksza statystki zloczyncy oraz absorbuje potencjal zrodel mocy.
	 * Wykorzystywana w {@link #attackTown(double)}
	 * 
	 * @param ps - referencja na zrodlo mocy
	 */
	private void absorbPotential(PowerSource ps) {
		if(ps.potential > 0) {
			Abilities psAbi = ps.getAbilities();
			
			if(psAbi.intelligence > 0) {
				abilities.intelligence += psAbi.intelligence / 100.0;
			}
			if(psAbi.initiative > 0) {
				abilities.initiative += psAbi.initiative / 100.0;
			}
			if(psAbi.endurance > 0) {
				abilities.endurance += psAbi.endurance / 100.0;
			}
			if(psAbi.energy > 0) {
				abilities.energy += psAbi.energy / 100.0;
			}
			if(psAbi.strength > 0) {
				abilities.strength += psAbi.strength / 100.0;
			}
			if(psAbi.fightAbility > 0) {
				abilities.fightAbility += psAbi.fightAbility / 100.0;
			}
			
			ps.potential = ps.potential - 0.75 > 0 ? ps.potential - 0.75 : 0.0;
			
			synchronized(Monitors.simulationGuard) {
				if(world.getSimulation().getTarget() == this)
					world.getSimulation().setUpdateInfoPanel(true);
			}
		}
	}
	
	/**
	 * Funkcja sprawdzajaca czy dany cywil jest w zasiegu ataku
	 * 
	 * @param c - referencja na cywila
	 * 
	 * @return Zwraca true jesli jest w zasiegu, false jesli nie
	 */
	private boolean civilianInRange(Civilian c) {
		if(c.isAlive()) {
			double x0 = position.x - 8;
			double y0 = position.y - 8;
			Dimension size0 = new Dimension(size.width + 16, size.height + 16);
			
			if(x0 > c.getPosition().x + c.getSize().width || x0 + size0.width < c.getPosition().x ||
				y0 > c.getPosition().y + c.getSize().height || y0 + size0.height < c.getPosition().y)
					return false;
			return true;
		}
		return false;
	}
	
	/**
	 * Funkcja, w ktorej zloczynca zabija wszystkich cywili w zasiegu
	 */
	private void killCiviliansInRange() {
		Vector<Civilian> civilians = null;
		synchronized(Monitors.worldGuard) {
			civilians = new Vector<Civilian>(world.getCivilans());
		}
		for(int i = 0; i < civilians.size(); i++) {
			Civilian c = civilians.get(i);
			if(civilianInRange(c))
				c.die();
		}
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
			world.getBadguys().remove(this);
			world.getBadguyNames().add(name);
		}
	}

	/**
	 * Funkcja wywolywana przy smierci zloczyncy
	 */
	protected void die() {
		if(inTown) {
			synchronized(Monitors.townGuard) {
				actualTown.subAttackCounter();
			}
		}
		activeSprite = "badguydead";
		super.die();
	}

	/**
	 * Funkcja wywolywana gdy zloczynca wejdzie do miasta
	 */
	protected void onTownEnter() {
		actualTown = world.getTownByObjectPosition(this);
		nextTown = null;
		direction = Direction.NOWALK;
		inTown = true;
		shortestPath = null;

		synchronized(Monitors.townGuard) {
			if(!attackingTown && !actualTown.isDestroyed()) {
				attackingTown = true;
				actualTown.addAttackCounter();
			}
		}
			
		synchronized(Monitors.worldGuard) {
			Vector<Superhero> superheroes = world.getSuperheroesFromTown(actualTown);
			if(!superheroes.isEmpty()) {
				synchronized(Monitors.superheroGuard) {
					for(int i = 0; i < superheroes.size(); i++) {
						Superhero sh = superheroes.get(i);
						if(fights == 0 || sh.getFights() == 0) {
							Fight fight = new Fight(sh, this);
							world.getFights().add(fight);
						}
					}
				}
			}
		}
	}

	/**
	 * Funkcja wywolywana gdy zloczynca wejdzie na skrzyzowanie
	 */
	protected void onCrossroadEnter(double dt) {
		if(nextTown.isDestroyed()) {
			nextTown = findClosestTown();
			shortestPath = world.getPathFinder().findShortestPath(world.getPathFinder().findPathToTown(shortestPath.get(0), nextTown));
		}
	}

	/**
	 * Funkcja wywolywana gdy zloczynca wejdzie na punkt grafu
	 */
	protected void onGraphPointEnter() {}
}
