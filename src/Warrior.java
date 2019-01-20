import java.awt.Color;
import java.awt.Graphics;



/**
 * Abstrakcyjna klasa opisujaca wojownika
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public abstract class Warrior extends Person {
	
	/**
	 * Umiejetnosci wojownika
	 */
	protected Abilities abilities;
	/**
	 * Liczba zdrowia
	 */
	protected double healthPoints;
	/**
	 * Liczba walk w ktorych bierze udzial
	 */
	protected int fights;
	/**
	 * Aktualny przeciwnik (wykorzystywane chyba tylko przez bohatera)
	 */
	protected Warrior enemy;
	/**
	 * Aktualnie prowadzona walka
	 */
	protected Fight fight;
	
	/** 
	 * Maksymalna liczba zdrowia
	 */
	public final static int MAX_HP = 100;
	/**
	 * Szybkosc regeneracji zdrowia bohatera (HP/sek)
	 */
	public final static double HERO_HP_REGEN = 1.0;
	/**
	 * Szybkosc regeneracji zdrowia zloczyncy (HP/sek)
	 */
	public final static double BADGUY_HP_REGEN = 0.25;

	/**
	 * Konstruktor klasy
	 * 
	 * @param world - referencja na swiat
	 * @param name - imie wojownika
	 */
	protected Warrior(World world, String name) {
		super(world, name);
		abilities = null;
		healthPoints = MAX_HP;
		enemy = null;
		fights = 0;
		fight = null;
	}
	
	/**
	 * Funkcja symulujaca atak wojownika
	 * @param enemy - przeciwnik ktorego atakujemy
	 */
	protected void attack(Warrior enemy) {
		if(enemy != null) {
			synchronized(Monitors.superheroGuard) {
				int typeAttack = MyRandom.getInt(3);
				double damage = 0.0;
				double damageRange = MyRandom.getDouble(0.0005, 0.0025);
				double critChance = 0.05;
				double critical = MyRandom.getDouble(1.0);
				switch(typeAttack) {
					case 0:
						damage = abilities.strength * abilities.fightAbility;
						break;
					case 1:
						damage = abilities.intelligence * abilities.fightAbility;
						break;
					case 2:
						damage = abilities.energy * abilities.fightAbility;
						break;
				}
				damage = (damage - abilities.endurance) * damageRange <= 0.1 ? 0.1 : (damage - abilities.endurance) * damageRange;
				if(critical <= critChance) {
					damage *= 2;
				}
				enemy.setHealthPoints(enemy.getHealthPoints() - damage);
			}
		}
	}
	
	/**
	 *  Funkcja rysujaca wojownika
	 *  @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 *  @param cam - kamera swiata
	 */
	public void render(Graphics g, Camera cam) {
		
		if(healthPoints > 75)
			g.setColor(Color.GREEN);
		else if(healthPoints > 50 && healthPoints <= 75)
			g.setColor(Color.YELLOW);
		else if(healthPoints > 25 && healthPoints <= 50)
			g.setColor(Color.ORANGE);
		else
			g.setColor(Color.RED);
		
		int x = (int)(position.x - cam.getPosition().x);
		int y = (int)(position.y - cam.getPosition().y) - 6;
		int width = (int)(healthPoints / 100.0 * 24);
		final int height = 4;
		g.fillRect(x, y, width, height);
	}
	protected void walk(double dt) {
		super.walk(dt);
	}

	public Abilities getAbilities() {
		return abilities;
	}

	public void setAbilities(Abilities abilities) {
		this.abilities = abilities;
	}

	public double getHealthPoints() {
		return healthPoints;
	}

	public void setHealthPoints(double healthPoints) {
		this.healthPoints = healthPoints;
	}

	public Warrior getEnemy() {
		return enemy;
	}

	public void setEnemy(Warrior enemy) {
		this.enemy = enemy;
	}
	
	public void addFight() {
		fights++;
	}
	
	public void subFight() {
		fights--;
	}
	
	public int getFights() {
		return fights;
	}

	public Fight getFight() {
		return fight;
	}

	public void setFight(Fight fight) {
		this.fight = fight;
	}

	public void setFights(int fights) {
		this.fights = fights;
	}
}
