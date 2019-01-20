import java.awt.Graphics;

/**
 * 
 * Klasa opisujaca walke pomiedzy wojownikami
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class Fight extends GameObject {
	/**
	 * Referencja na zloczynce
	 */
	private Badguy badguy;
	/**
	 * Referencja na superbohatera
	 */
	private Superhero superhero;
	/**
	 * Czy zakonczono walke
	 */
	private boolean ended;

	/**
	 * Konstruktor klasy
	 * 
	 * @param sh - referencja na superbohatera
	 * @param bg - referencja na zloczynce
	 */
	public Fight(Superhero sh, Badguy bg) {
		super(GameObject.NO_POSITION, GameObject.NO_DIMENSION, sh.getWorld());
		if(bg != null) {
			if(bg.isAlive() && sh.isAlive()) {
				sh.addFight();
				bg.addFight();
				if(sh.getFight() == null)
					sh.setFight(this);
				if(bg.getFight() == null)
					bg.setFight(this);
				if(bg.getEnemy() == null)
					bg.setEnemy(sh);
						
				badguy = bg;
				superhero = sh;
						
				ended = false;
			}
			else
				ended = true;
		}
			
		synchronized(Monitors.simulationGuard) {
			world.getSimulation().setUpdateInfoPanel(true);
		}
	}
	
	/**
	 * Funkcja wywolywana przy smierci bohatera
	 */
	private void onHeroDead() {
		superhero.die();
		superhero = null;
		badguy.subFight();
		badguy.setFight(null);
		ended = true;
	}
	
	/**
	 * Funkcja wywolywana przy smierci zloczyncy
	 */
	private void onBadguyDead() {
		badguy.die();
		badguy = null;
		superhero.subFight();
		superhero.setFight(null);
		superhero.getAbilities().increase(MyRandom.getDouble(1, 3));
		synchronized(Monitors.superheroGuard) {
			if(!superhero.isWaitingForBadguy() && superhero.getActualTown() == superhero.getHomeTown()) {
				synchronized(Monitors.townGuard) {
					((Capital)superhero.getHomeTown()).getSuperHeroes().add(superhero);
				}
			}
		}
		if(superhero.getEnemy() == badguy)
			superhero.setEnemy(null);
		ended = true;
	}

	/**
	 * Funkcja aktualizujaca stan walczacych
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	public void update(double dt) {
		if(!ended) {
			synchronized(Monitors.badguyGuard) {
				synchronized(Monitors.superheroGuard) {
					if(badguy != null && superhero != null && badguy.isAlive() && superhero.isAlive()) {
						if(badguy.getFight() == null)
							badguy.setFight(this);
						if(superhero.getFight() == null)
							superhero.setFight(this);
						
						if(badguy.getAbilities().initiative > superhero.getAbilities().initiative) {
							if(badguy.getFight() == this)
								badguy.attack(superhero);
							if(superhero.getHealthPoints() <= 0.0) 
								onHeroDead();
							else {
								if(superhero.getFight() == this)
									superhero.attack(badguy);
								if(badguy.getHealthPoints() <= 0.0) 
									onBadguyDead();
							}
						}
						else {
							if(superhero.getFight() == this)
								superhero.attack(badguy);
							if(badguy.getHealthPoints() <= 0.0) 
								onBadguyDead();
							else {
								if(badguy.getFight() == this)
									badguy.attack(superhero);
								if(superhero.getHealthPoints() <= 0.0)
									onHeroDead();
							}
						}
					}
					else {
						if(badguy != null && badguy.isAlive()) 
							if(badguy.getHealthPoints() <= 0.0) {
								badguy.die();
								badguy = null;
								if(superhero != null && superhero.getEnemy() == badguy)
									superhero.setEnemy(null);
							}
							else {
								badguy.subFight();
								badguy.setFight(null);
							}
						
						if(superhero != null && superhero.isAlive()) 
							if(superhero.getHealthPoints() <= 0.0) {
								superhero.die();
								superhero = null;
							}
							else {
								superhero.subFight();
								superhero.setFight(null);
								superhero.getAbilities().increase(MyRandom.getDouble(1, 3));
							}
						ended = true;
					}
				}
			}
			synchronized(Monitors.simulationGuard) {
				world.getSimulation().setUpdateInfoPanel(true);
			}
		}
	}

	/**
	 * Funkcja rysujaca walke
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 * @param cam - kamera swiata
	 */
	public void render(Graphics g, Camera cam) {}

	public boolean isEnded() {
		return ended;
	}

	public void setEnded(boolean ended) {
		this.ended = ended;
	}
}
