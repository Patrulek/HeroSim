

/**
 * Abstrakcyjna klasa opisujaca zrodlo mocy
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public abstract class PowerSource {
	
	/**
	 * Ostatni identyfikator przypisany zrodlu
	 */
	static private int lastID;
	/**
	 * Identyfikator zrodla
	 */
	protected int id;
	/**
	 * Nazwa zrodla
	 */
	protected String name;
	/**
	 * Potencjal zrodla
	 */
	protected double potential;
	/**
	 * Wlasciwosci zrodla
	 */
	protected Abilities abilities;
	/**
	 * Referencja na miasto w ktorym znajduje sie zrodlo
	 */
	protected Town town;
	/**
	 * Czy zrodlo jest aktywne (czy potencjal jest wiekszy od 0)
	 */
	protected boolean active;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param town - miasto
	 */
	protected PowerSource(Town town) {
		id = getNextID();
		name = "";
		potential = 0.0;
		abilities = null;
		this.town = town;
		active = true;
	}
	
	/**
	 * Funkcja aktualizujaca stan zrodla
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	public void update(double dt) {
		if(potential <= 0.0)
				active = false;
		if(active)
			regenerate();
	}
	
	/**
	 * Funkcja regenerujaca potencjal
	 */
	public void regenerate() {
		potential += town.getPeople() / 1000.0;
	}
	
	/**
	 * Funkcja zwracajaca nastepny identyfikator i inkrementuje go
	 * @return Zwraca kolejny identyfikator
	 */
	static int getNextID() {
		return lastID++;
	}

	public static int getLastID() {
		return lastID;
	}

	public static void setLastID(int lastID) {
		PowerSource.lastID = lastID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPotential() {
		return potential;
	}

	public void setPotential(double potential) {
		this.potential = potential;
	}

	public Abilities getAbilities() {
		return abilities;
	}

	public void setAbilities(Abilities abilities) {
		this.abilities = abilities;
	}

	public Town getTown() {
		return town;
	}

	public void setTown(Town town) {
		this.town = town;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
