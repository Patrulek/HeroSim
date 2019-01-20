import java.awt.Dimension;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Klasa opisujaca miasto
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class Town extends GameObject {
	
	/**
	 * Enum okreslajacy typ miasta:
	 * NORMAL - zwykle miasto
	 * CAPITAL - stolica
	 * RUIN - miasto zniszczone
	 *
	 */
	public enum TownType {
		NORMAL,
		CAPITAL,
		RUIN
	}

	/**
	 * Nazwa miasta
	 */
	protected String name;
	/**
	 * Liczba mieszkancow
	 */
	protected int people;
	/**
	 * Typ miasta
	 */
	protected TownType type;
	
	/**
	 * Punkty grafu ktore posiada miasto
	 */
	protected Vector<Integer> graphPointIDs;
	/**
	 * Zrodla mocy jakie posiada miasto
	 */
	protected Vector<PowerSource> powerSources;
	/**
	 * Kolejka cywili chcacych wyjsc z miasta
	 */
	protected LinkedList<Civilian> civilianQueue;
	
	/**
	 * Czas pomiedzy kolejnymi wyjsciami mieszkancow z miasta
	 */
	protected double timeToLetCivilianGo;
	/**
	 * Czy pozwolic mieszkancowi wyjsc z miasta
	 */
	protected boolean lettingCivilianGo;
	/**
	 * Czy miasto jest zniszczone
	 */
	protected boolean destroyed;
	/**
	 * Przez ilu zloczyncow miasto jest atakowane
	 */
	protected int attacked;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param t - referencja na miasto
	 */
	public Town(Town t) {
		super(t.position, t.size, t.world);
		
		people = t.people;
		name = t.name;
		type = t.type;
		
		graphPointIDs = t.graphPointIDs;
		civilianQueue = t.civilianQueue;
		powerSources = t.powerSources;
		
		timeToLetCivilianGo = t.timeToLetCivilianGo;
		destroyed = t.destroyed;
		lettingCivilianGo = t.lettingCivilianGo;
		attacked = 0;
		
		generatePowerSources();
		
		sprites.put("town", new Sprite(TextureManager.getInstance().getTexture("town"), false));
		sprites.put("townattacked", new AnimatedSprite(TextureManager.getInstance().getTexture("townattacked"), 
				new Dimension(96, 96), 0.1, 5, 1, 5, true));
		sprites.put("capital", new Sprite(TextureManager.getInstance().getTexture("capital"), false));
		sprites.put("capitalattacked", new AnimatedSprite(TextureManager.getInstance().getTexture("capitalattacked"),
				new Dimension(96, 96), 0.1, 5, 1, 5, true));
		sprites.put("destroyed", new AnimatedSprite(TextureManager.getInstance().getTexture("towndestroyed"), 
				new Dimension(96, 96), 0.1, 5, 1, 5, true));
		activeSprite = "town";
		zIndex = 5;
	}
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param pos - pozycja w swiecie (w pikselach)
	 * @param size - rozmiar miasta (w pikselach)
	 * @param world - referencja na swiat
	 * @param name - nazwa miasta
	 * @param gpIDs - punkty grafu ktore miasto bedzie posiadac 
	 */
	public Town(MyPoint pos, Dimension size, World world, String name, Vector<Integer> gpIDs) {
		super(pos, size, world);
		
		people = 0;
		this.name = name;
		type = TownType.NORMAL;
		
		graphPointIDs = gpIDs;
		civilianQueue = new LinkedList<Civilian>();
		powerSources = null;
		
		timeToLetCivilianGo = 1.0;
		destroyed = false;
		lettingCivilianGo = true;
		attacked = 0;
		
		generatePowerSources();
		
		sprites.put("town", new Sprite(TextureManager.getInstance().getTexture("town"), false));
		sprites.put("townattacked", new AnimatedSprite(TextureManager.getInstance().getTexture("townattacked"), 
				new Dimension(96, 96), 0.1, 5, 1, 5, true));
		sprites.put("capital", new Sprite(TextureManager.getInstance().getTexture("capital"), false));
		sprites.put("capitalattacked", new AnimatedSprite(TextureManager.getInstance().getTexture("capitalattacked"),
				new Dimension(96, 96), 0.1, 5, 1, 5, true));
		sprites.put("destroyed", new AnimatedSprite(TextureManager.getInstance().getTexture("towndestroyed"), 
				new Dimension(96, 96), 0.1, 5, 1, 5, true));
		activeSprite = "town";
		
		zIndex = 5;
	}
	
	/**
	 * Funkcja wybierajaca odpowiedni sprite na podstawie typu miasta
	 */
	private void chooseSprite() {
		if(attacked != 0)
			if(type == TownType.CAPITAL)
				activeSprite = "capitalattacked";
			else
				activeSprite = "townattacked";
		else {
			if(type == TownType.CAPITAL)
				activeSprite = "capital";
			else
				activeSprite = "town";
		}
	}
	
	/**
	 * Funkcja aktualizujaca stan miasta
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	public void update(double dt) {
		if(!destroyed) {
			synchronized(Monitors.townGuard) {
				if(!civilianQueue.isEmpty()) {
					timeToLetCivilianGo -= dt;
					if(timeToLetCivilianGo <= 0.0) {
						if(lettingCivilianGo == true) {
							lettingCivilianGo = false;
							letCivilianGo(civilianQueue.getFirst());
						}
					}
				}
			}
			
			chooseSprite();
		}
		
		for(int i = 0; i < powerSources.size(); i++) {
			PowerSource ps = powerSources.get(i);
			ps.update(dt);
			if(ps.isActive() == false) {
				powerSources.remove(i);
				i--;
			}
		}
		
		if(!destroyed && people == 0 && powerSources.size() == 0) {
			destroyed = true;
			if(type == TownType.CAPITAL)
				world.setChangeCapital(true);
			
			type = TownType.RUIN;
			activeSprite = "destroyed";
			
			synchronized(Monitors.simulationGuard) {
				if(world.getSimulation().getTarget() == this) {
					world.getSimulation().setUpdateInfoPanel(true);
					world.getSimulation().setUpdateConstructPanel(true);
				}
			}
		}
		super.update(dt);
	}
	
	/**
	 * Funkcja rysujaca miasto
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 * @param cam - kamera swiata
	 */
	public void render(Graphics g, Camera cam) {
		sprites.get(activeSprite).render(g, cam, this);
	}
	
	/**
	 * Funkcja resetujaca czas pomiedzy wyjsciem kolejnych mieszkancow
	 */
	public void resetTimeToLetCivilianGo() {
		timeToLetCivilianGo = 1.0;
		lettingCivilianGo = true;
	}
	
	/**
	 * Funkcja zmieniajaca cywilowi {@link Civilian#canLeaveTown} na true
	 * @param c - cywil
	 */
	public void letCivilianGo(Civilian c) {
		c.setCanLeaveTown(true);
	}
	
	/**
	 * Funkcja generujaca zrodla mocy
	 */
	private void generatePowerSources() {
		if(powerSources != null)
			return;
		else
			powerSources = new Vector<PowerSource>();
		
		int typesOfPowerSources = MyRandom.getInt(1, 5);		// liczba typów od 1 do 5 (maksymlanie 6, ale tyle nie mo¿e mieæ miasto)
		Vector<Integer> types = new Vector<Integer>();
		for(int i = 0; i < 6; i++)
			types.add(i);
		
		for(int i = 0; i < typesOfPowerSources; i++) {
			int toAdd = MyRandom.getInt(types.size());		// wybieramy jeden z typów
			int type = types.get(toAdd);
			types.remove(toAdd);
			int number = MyRandom.getInt(1, 3);			// tworzymy od 1 do 3 Ÿróde³ wybranego typu
			for(int j = 0; j < number; j++)
				addPowerSource(type);
		}
	}
	
	/**
	 * Funkcja dodajaca zrodlo mocy do miasta
	 * 
	 * @param type - typ zrodla mocy
	 * 0 - {@link MoonWell}
	 * 1 - {@link ThunderTower}
	 * 2 - {@link ObsidianObelisk}
	 * 3 - {@link CrystalBall}
	 * 4 - {@link ThorsHammer}
	 * 5 - {@link BurningStatue}
	 */
	private void addPowerSource(int type) {
		switch(type) {
			case 0:
				powerSources.add(new MoonWell(this));
				break;
			case 1:
				powerSources.add(new ThunderTower(this));
				break;
			case 2:
				powerSources.add(new ObsidianObelisk(this));
				break;
			case 3:
				powerSources.add(new CrystalBall(this));
				break;
			case 4:
				powerSources.add(new ThorsHammer(this));
				break;
			case 5:
				powerSources.add(new BurningStatue(this));
				break;
		}
	}
	
	/**
	 * Funkcja sprawdzajaca czy miasto posiada punkt grafu o podanym id
	 * 
	 * @param id - identyfikator punktu grafu
	 * 
	 * @return Zwraca true, gdy posiada, false w przeciwnym przypadku
	 */
	public boolean hasGraphPointOfID(int id) {
		for(int i = 0; i < graphPointIDs.size(); i++)
			if(id == graphPointIDs.get(i))
				return true;
		return false;
	}
	public int getPeople() {
		return people;
	}
	public void setPeople(int people) {
		this.people = people;
	}
	/**
	 * Funkcja inkrementujaca zmienna {@link #people} o 1
	 */
	public void addPerson() {
		synchronized(Monitors.townGuard) {
			people++;
		}
		synchronized(Monitors.simulationGuard) {
			if(world.getSimulation().getTarget() == this) {
				world.getSimulation().setUpdateInfoPanel(true);
			}
		}
	}
	/**
	 * Funkcja dekrementujaca zmienna {@link #people} o 1
	 */
	public void subPerson() {
		synchronized(Monitors.townGuard) {
			people--;
		}
		synchronized(Monitors.simulationGuard) {
			if(world.getSimulation().getTarget() == this)
				world.getSimulation().setUpdateInfoPanel(true);
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public TownType getType() {
		return type;
	}

	public void setType(TownType type) {
		this.type = type;
	}

	public Vector<Integer> getGraphPointIDs() {
		return graphPointIDs;
	}

	public void setGraphPointIDs(Vector<Integer> graphPointIDs) {
		this.graphPointIDs = graphPointIDs;
	}

	public LinkedList<Civilian> getCivilianQueue() {
		return civilianQueue;
	}
	
	public void removeCivilianFromQueue(Civilian c) {
		civilianQueue.remove(c);
	}
	
	public void addCivilianToQueue(Civilian c) {
		civilianQueue.addLast(c);
	}

	public void setCivilianQueue(LinkedList<Civilian> civilianQueue) {
		this.civilianQueue = civilianQueue;
	}

	public Vector<PowerSource> getPowerSources() {
		return powerSources;
	}

	public void setPowerSources(Vector<PowerSource> powerSources) {
		this.powerSources = powerSources;
	}

	public double getTimeToLetCivilianGo() {
		return timeToLetCivilianGo;
	}

	public void setTimeToLetCivilianGo(double timeToLetCivilianGo) {
		this.timeToLetCivilianGo = timeToLetCivilianGo;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}

	public boolean isLettingCivilianGo() {
		return lettingCivilianGo;
	}

	public void setLettingCivilianGo(boolean lettingCivilianGo) {
		this.lettingCivilianGo = lettingCivilianGo;
	}

	public void subAttackCounter() {
		attacked--;
	}

	public void addAttackCounter() {
		attacked++;
	}
}
