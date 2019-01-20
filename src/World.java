import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Klasa opisujaca swiat symulacji
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class World {
	/**
	 * Maksymalna liczba cywili w swiecie
	 */
	public static final int MAX_CIVILIANS = 100;
	/** 
	 * Maksymalna liczba zloczyncow w swiecie
	 */
	public static final int MAX_BADGUYS = 5;
	/**
	 * Maksymalna liczba bohaterow w swiecie
	 */
	public static final int MAX_SUPERHEROES = 15;
	
	/**
	 * Wielkosc swiata (w pikselach)
	 */
	private Dimension size;
	/**
	 * Referencja na symulacje (stan gry)
	 */
	private Simulation simulation;
	/**
	 * Kamera swiata
	 */
	private Camera camera;
	
	/**
	 * Mapa z kafelkow
	 */
	private TileLayer tileLayer;
	/**
	 * Rozmiar kafelka
	 */
	private int tileSize;
	/**
	 * Mapa drog w swiecie
	 */
	private int pathLayer[][];
	/**
	 * Obiekt znajdujacy sciezki
	 */
	private PathFinder pathFinder;
	
	/**
	 * Miasta w swiecie
	 */
	private Vector<Town> towns;
	/**
	 * Skrzyzowania w swiecie
	 */
	private Vector<Crossroad> crossroads;
	/**
	 * Cywile w swiecie
	 */
	private Vector<Civilian> civilians;
	/**
	 * Punkty spawnu zloczyncow w swiecie
	 */
	private Vector<SpawnPoint> spawnPoints;
	/**
	 * Zloczyncy w swiecie
	 */
	private Vector<Badguy> badguys;
	/**
	 * Bohaterowie w swiecie
	 */
	private Vector<Superhero> superHeroes;
	/**
	 * Obecne walki w swiecie
	 */
	private Vector<Fight> fights;
	/**
	 * Stale obiekty symulacji
	 */
	private Vector<BackgroundObject> mapObjects;
	
	/**
	 * Obiekty ktore nalezy wyswietlic
	 */
	private Vector<GameObject> renderedObjects;
	
	/**
	 * Maksymalna liczba herosow w danym momencie
	 */
	private int maxHeroes;
	/**
	 * Czas po jakim nalezy zespawnowac zloczynce
	 */
	private double timeToSpawnBadguy;
	
	/**
	 * Lista imion cywili
	 */
	private List<String> civilNames;
	/**
	 * Lista nazwisk cywili
	 */
	private List<String> surnames;
	/**
	 * Lista imion zloczyncow
	 */
	private List<String> badguyNames;
	/**
	 * Lista imion bohaterow
	 */
	private List<String> superheroNames;
	
	/**
	 * Czy stolica zostala zniszczona i nalezy wybrac inne miasto
	 */
	private boolean needToChangeCapital;
	
	/**
	 * Funkcja ladujaca potrzebne dane z plikow
	 */
	private void loadFiles() {
		civilNames = IO.readAllLines("civilnames.txt");
		surnames = IO.readAllLines("surnames.txt");
		badguyNames = IO.readAllLines("badguyNames.txt");
		superheroNames = IO.readAllLines("heroNames.txt");
		loadPathLayer("mapadrog.txt");
	}
	
	/**
	 * Funkcja znajdujaca niezniszczone jeszcze miasta
	 * 
	 * @return Zwraca wektor miast, ktore nie sa jeszcze zniszczone
	 */
	public Vector<Town> getNotDestroyedTowns() {
		Vector<Town> nTowns = new Vector<Town>(towns);
		for(int i = 0; i < nTowns.size(); i++) {
			synchronized(Monitors.townGuard) {
				if(nTowns.get(i).isDestroyed()) {
					nTowns.remove(i);
					i--;
				}
			}
		}
		return nTowns;
	}
	
	/**
	 * Funkcja zwracajaca indeks z wektora miast na podstawie jego nazwy (obecnie chyba nie wykorzystywana)
	 * 
	 * @param townName - nazwa miasta
	 * 
	 * @return Zwraca indeks podanego przez parametr miasta z wektora {@link #towns} lub -1 gdy podane miasto nie istnieje
	 */
	public int getTownIndexByName(String townName) {
		for(int i = 0; i < towns.size(); i++) {
			if(townName.equals(towns.get(i).getName()))
				return i;
		}
		return -1;
	}
	
	/**
	 * Funkcja zwracajaca indeks z wektora miast na podstawie identyfikatora punktu grafu
	 * 
	 * @param gpID - identyfikator punktu grafu
	 * 
	 * @return Zwraca indeks z wektora miast {@link #towns} lub -1 gdy podany identyfikator nie jest zawarty
	 * w zadnym z miast
	 */
	public int getTownIndexByGraphPointID(int gpID) {
		for(int i = 0; i < towns.size(); i++) 
			if(towns.get(i).hasGraphPointOfID(gpID))
				return i;
		return -1;
	}
	
	/**
	 * Funkcja zwracajaca identyfikator punktu grafu na podstawie skrzyzowania
	 * 
	 * @param c - skrzyzowanie
	 * 
	 * @return Funkcja zwraca identyfikator punktu grafu na podstawie podanego skrzyzowania
	 */
	public int getGraphPointIDByCrossroad(Crossroad c) {
		int index = -1;
		Vector<GraphPoint> GPs = pathFinder.getGraphPoints();
		for(int i = 0; i < GPs.size(); i++)
			if(c.getPosition().x == GPs.get(i).getPosition().x && c.getPosition().y == GPs.get(i).getPosition().y) {
				index = i;
				break;
			}
		return index;
	}
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param sim - referencja na symulacje
	 */
	public World(Simulation sim) {
		simulation = sim;
		
		loadFiles();
		
		camera = new Camera(new Dimension(simulation.getWorldPanel().getWidth(), simulation.getWorldPanel().getHeight()), this);
		tileLayer = new TileLayer("mapa.txt");
		tileLayer.setCamera(camera);
		tileSize = tileLayer.getTileset().getTileSize();
		size = new Dimension(tileLayer.getTileIDs()[0].length * tileLayer.getTileset().getTileSize(), 
				tileLayer.getTileIDs().length * tileLayer.getTileset().getTileSize());
		pathFinder = new PathFinder(this);
		renderedObjects = new Vector<GameObject>();
		
		generateTowns();
		generateCrossroads();
		generateCivilians();
		generateSpawnPoints();
		generateMapObjects();
		
		timeToSpawnBadguy = MyRandom.getDouble(5, 10);
	
		badguys = new Vector<Badguy>();
		superHeroes = new Vector<Superhero>();
		fights = new Vector<Fight>();
		
		needToChangeCapital = false;
		maxHeroes = 0;
	}
	
	/**
	 * Funkcja generujaca stale obiekty swiata
	 */
	private void generateMapObjects() {
		mapObjects = new Vector<BackgroundObject>();
		BackgroundObject obj = new BackgroundObject(new MyPoint(0, 0), new Dimension(800, 575), this, "frame");
		obj.setzIndex((short)7);
		mapObjects.add(obj);
		obj = new BackgroundObject(new MyPoint(0, 0), new Dimension(1600, 1600), this, "map");
		obj.setzIndex((short)6);
		mapObjects.add(obj);  
	}
	
	/**
	 * Funkcja sprawdzajaca czy dwa obiekty koliduja ze soba
	 * 
	 * @param go1 - pierwszy obiekt
	 * @param go2 - drugi obiekt
	 * 
	 * @return Zwraca true, gdy wystapi kolizja oraz false w przeciwnym przypadku
	 */
	public boolean objectsCollide(GameObject go1, GameObject go2) {
		if(go1.getPosition().x > go2.getPosition().x + go2.getSize().width || go1.getPosition().x + go1.getSize().width < go2.getPosition().x 
			|| go1.getPosition().y > go2.getPosition().y + go2.getSize().height || go1.getPosition().y + go1.getSize().height < go2.getPosition().y)
				return false;
		return true;
	}
	
	/**
	 * Funkcja sprawdzajaca czy obiekt znajduje sie w widoku kamery
	 * 
	 * @param go - obiekt
	 * 
	 * @return Zwraca true, gdy obiekt znajduje sie w widoku kamery oraz false w przeciwnym przypadku
	 */
	public boolean objectOnView(GameObject go) {
		if(camera.getPosition().x > go.getPosition().x + go.getSize().width || camera.getPosition().x + camera.getSize().width < go.getPosition().x 
			|| camera.getPosition().y > go.getPosition().y + go.getSize().height || camera.getPosition().y + camera.getSize().height < go.getPosition().y)
				return false;
		return true;
	}
	
	/**
	 * Funkcja zwracajaca skrzyzowanie na podstawie pozycji obiektu
	 * 
	 * @param go - obiekt
	 * 
	 * @return Zwraca skrzyzowanie, gdy obiekt koliduje z nim lub null gdy nie koliduje z zadnym skrzyzowaniem
	 */
	public Crossroad getCrossroadByObjectPosition(GameObject go) {
		for(int i = 0; i < crossroads.size(); i++)
			if(objectsCollide(go, crossroads.get(i)))
				return crossroads.get(i);
		return null;
	}
	
	/**
	 * Funkcja zwracajaca wektor bohaterow znajdujacych sie aktualnie w podanym miescie
	 * 
	 * @param t - miasto
	 * 
	 * @return Zwraca wektor bohaterow znajdujacych sie w miescie podanym przez parametr (gdy w miescie brak 
	 * bohaterow, wektor jest pusty)
	 */
	public Vector<Superhero> getSuperheroesFromTown(Town t) {
		Vector<Superhero> superheroes = new Vector<Superhero>();
		for(int i = 0; i < superHeroes.size(); i++)
			if(superHeroes.get(i).getActualTown() == t)
				superheroes.add(superHeroes.get(i));
		return superheroes;
	}
	
	/**
	 * 
	 * To samo co {@link #getSuperheroesFromTown(Town)} tyle, ze dla zloczyncow
	 * 	 
	 */
	public Vector<Badguy> getBadguysFromTown(Town t) {
		Vector<Badguy> nBadguys = new Vector<Badguy>();
		for(int i = 0; i < badguys.size(); i++)
			if(badguys.get(i).getActualTown() == t)
				nBadguys.add(badguys.get(i));
		return nBadguys;
	}
	
	/**
	 * Funkcja zwracajaca miasto na podstawie pozycji obiektu
	 * 
	 * @param go - obiekt
	 * 
	 * @return Zwraca miasto, gdy obiekt koliduje z jakims lub null, gdy nie koliduje z zadnym miastem
	 */
	public Town getTownByObjectPosition(GameObject go) {
		for(int i = 0; i < towns.size(); i++)
			if(objectsCollide(go, towns.get(i)))
				return towns.get(i);
		return null;
	}
	
	/**
	 * Funkcja zwracajaca punkt grafu na podstawie pozycji obiektu
	 * 
	 * @param go - obiekt
	 * 
	 * @return Zwraca punkt grafu gdy obiekt koliduje z jakims lub null, gdy nie koliduje z zadnym punktem w grafie
	 */
	public GraphPoint getGraphPointByObjectPosition(GameObject go) {
		for(int i = 0; i < pathFinder.getGraphPoints().size(); i++)
			if(objectsCollide(go, pathFinder.getGraphPoints().get(i)))
				return pathFinder.getGraphPoints().get(i);
		return null;
	}
	
	/**
	 * Funkcja spawnujaca zloczynce
	 */
	private void spawnBadguy() {
		synchronized(Monitors.worldGuard) {
			synchronized(Monitors.badguyGuard) {
				int rName = MyRandom.getInt(badguyNames.size());
				Badguy bg = new Badguy(this, badguyNames.get(rName));
				badguyNames.remove(rName);
				badguys.add(bg);
				(new Thread(bg)).start();
			}
		}
	}
	
	/**
	 * Funkcja aktualizujaca stan swiata
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	public void update(double dt) {
		camera.update(dt);
		tileLayer.update();
		
		if(needToChangeCapital)
			changeCapital();

		if(timeToSpawnBadguy <= 0.0 && badguys.size() < MAX_BADGUYS) {
			spawnBadguy();
			timeToSpawnBadguy = MyRandom.getDouble(5, 10);
		}
		else
			timeToSpawnBadguy -= dt;
			
			
		for(int i = 0; i < towns.size(); i++)
			towns.get(i).update(dt);
			
		for(int i = 0; i < crossroads.size(); i++)
			crossroads.get(i).update(dt);
			
		for(int i = 0; i < fights.size(); i++) {
			fights.get(i).update(dt);
			if(fights.get(i).isEnded()) {
				fights.remove(i);
				i--;
			} 
		}
		
		maxHeroes = (int) Math.ceil((getWholePotential() / 100.0)) >= MAX_SUPERHEROES ? MAX_SUPERHEROES : (int) Math.ceil((getWholePotential() / 150.0)); 

		if(getNotDestroyedTowns().isEmpty()) {
			if(simulation.getWorldInfo() != null) {
				simulation.getWorldInfo().dispose();
				simulation.setWorldInfo(null);
			}
			simulation.endSimulation();
			simulation.app.startGameOver();
		}
		
		synchronized(Monitors.worldGuard) {
			renderedObjects.clear();
			for(int i = 0; i < civilians.size(); i++)
				renderedObjects.add(civilians.get(i));
			for(int i = 0; i < superHeroes.size(); i++)
				renderedObjects.add(superHeroes.get(i));
			for(int i = 0; i < badguys.size(); i++)
				renderedObjects.add(badguys.get(i));
			for(int i = 0; i < towns.size(); i++)
				renderedObjects.add(towns.get(i));
			for(int i = 0; i < mapObjects.size(); i++)
				renderedObjects.add(mapObjects.get(i));
			
			sortRenderedObjects();
		}
	}
	
	/**
	 * Funkcja selekcyjna i sortujaca. Sprawdza czy obiekty sa w widoku kamery a nastepnie sortuje je wzgledem
	 * parametru {@link GameObject#zIndex}
	 */
	private void sortRenderedObjects() {
		for(int i = 0; i < renderedObjects.size(); i++) {
			for(int j = 0; j < renderedObjects.size() - 1; j++) {
				GameObject ro = renderedObjects.get(j);
				GameObject ro2 = renderedObjects.get(j+1);
				if(!objectOnView(ro) && !ro.getSprites().get(ro.getActiveSprite()).isGuiObject()) {
					renderedObjects.remove(j);
					j--; 
					continue;
				}
		
				if(ro.getzIndex() > ro2.getzIndex()) {
					GameObject temp = ro;
					renderedObjects.set(j, ro2);
					renderedObjects.set(j+1, temp);
				}
			}
		}
	}
	
	/**
	 * Funkcja rysujaca swiat
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 */
	public void render(Graphics g) {
		synchronized(Monitors.worldGuard) {
			tileLayer.render(g);
			for(int i = 0; i < renderedObjects.size(); i++)
				renderedObjects.get(i).render(g, camera);
		}
	}
	
	/**
	 * Funkcja usuwajaca danego cywila ze swiata
	 * 
	 * @param c - cywil
	 */
	public void removeCivilianFromWorld(Civilian c) {
		civilians.remove(c);
	}
	
	/**
	 * Funkcja dodajaca cywila do swiata
	 * 
	 * @param c - cywil
	 */
	public void addCivilianToWorld(Civilian c) {
		civilians.add(c);
	}
	
	/**
	 * Funkcja wyszukujaca nowa stolice
	 */
	private void changeCapital() {
		synchronized(Monitors.townGuard) {
			Vector<Town> nTowns = getNotDestroyedTowns();
			Town nT = nTowns.get(MyRandom.getInt(nTowns.size()));
			for(int i = 0; i < towns.size(); i++) {
				Town t = towns.get(i);
				if(t == nT) {
					Town newT = new Capital(t);
					needToChangeCapital = false;
					for(int j = 0; j < civilians.size(); j++) {
						synchronized(Monitors.civilianGuard) {
							Civilian c = civilians.get(j);
							if(c.getHomeTown() == t)
								c.setHomeTown(newT);
							if(c.getActualTown() == t)
								c.setActualTown(newT);
							if(c.getNextTown() == t)
								c.setNextTown(newT);
						}
					}
					for(int j = 0; j < superHeroes.size(); j++) {
						synchronized(Monitors.superheroGuard) {
							Superhero sh = superHeroes.get(j);
							sh.setHomeTown(newT);
							if(sh.getActualTown() == t)
								sh.setActualTown(newT);
							if(sh.getNextTown() == t)
								sh.setNextTown(newT);
						}
					}
					for(int j = 0; j < badguys.size(); j++) {
						synchronized(Monitors.badguyGuard) {
							Badguy bg = badguys.get(j);
							if(bg.getActualTown() == t)
								bg.setActualTown(newT);
							if(bg.getNextTown() == t)
								bg.setNextTown(newT);
						}
					}
					towns.setElementAt(newT, i);
					break;
				}
			}
		}
	}
	
	/**
	 * Funkcja zwracajaca stolice
	 * 
	 * @return Zwraca stolice lub null gdy nie znaleziono miasta bedacego stolica
	 */
	public Town getCapital() {
		for(int i = 0; i < towns.size(); i++)
			synchronized(towns.get(i)) {
				if(towns.get(i).getType() == Town.TownType.CAPITAL)
					return towns.get(i);
			}
		return null;
	}
	
	/**
	 * Funkcja generujaca miasta z pliku "objects.xml"
	 */
	private void generateTowns() {
		if(towns == null)
			towns = new Vector<Town>();
		NodeList nl = XmlParser.parseDocument(XmlParser.parseXML("objects.xml"), "Town");
		
			if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				
				Element el = (Element)nl.item(i);
	
				Town t = ObjectCreator.getInstance(this).createTown(el);
				towns.add(t);
			}
		} 
	}
	
	/**
	 * Funkcja generujaca cywili
	 */
	private void generateCivilians() {
		civilians = new Vector<Civilian>();
		
		for(int i = 0; i < MAX_CIVILIANS; i++) {
			int randomName = MyRandom.getInt(civilNames.size());
			int randomSurname = MyRandom.getInt(surnames.size());
			
			Civilian c = new Civilian(civilNames.get(randomName), surnames.get(randomSurname), towns.get(i/10), this);
			civilians.add(c);
			civilNames.remove(randomName); surnames.remove(randomSurname);
			(new Thread(c)).start();
		}
	}
	
	/**
	 * Funkcja generujaca skrzyzowania z pliku "objects.xml"
	 */
	private void generateCrossroads() {
		if(crossroads == null) 
			crossroads = new Vector<Crossroad>();
		
		NodeList nl = XmlParser.parseDocument(XmlParser.parseXML("objects.xml"), "Crossroad");
		
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				
				Element el = (Element)nl.item(i);
	
				Crossroad gp = ObjectCreator.getInstance(this).createCrossroad(el);
				crossroads.add(gp);
			}
		} 
	}
	
	/**
	 * Funkcja generujaca spawn pointy z pliku "objects.xml"
	 */
	private void generateSpawnPoints() {
		if(spawnPoints == null)
			spawnPoints = new Vector<SpawnPoint>();
		
		NodeList nl = XmlParser.parseDocument(XmlParser.parseXML("objects.xml"), "SpawnPoint");
		
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				Element el = (Element)nl.item(i);
				
				SpawnPoint sp = ObjectCreator.getInstance(this).createSpawnPoint(el);
				spawnPoints.add(sp);
			}
		}
	}
	
	/**
	 * Funkcja wczytujaca mape drog z pliku
	 * 
	 * @param fileName - sciezka do pliku
	 */
	private void loadPathLayer(String fileName) {
		if(pathLayer == null)
			pathLayer = new int[100][100];
		
		List<String> tilemap = IO.readAllLines(fileName);
		
		for(int i = 0; i < 100; i++) {
			String sLine = tilemap.get(i);
			
			for(int j = 0, k = 0; k < 100; j++) {
				if(!TileLayer.isInteger(sLine.substring(j, j + 1)))
					continue;
				
				String number = "";
				while(j < sLine.length() && TileLayer.isInteger(sLine.substring(j, j + 1))) {
					number += sLine.substring(j, j + 1);
					j++;
				}
				
				pathLayer[i][k] = Integer.parseInt(number);
				k++;
			}
		}
	}
	
	/**
	 * Funkcja zwracajaca sume potencjalow ze wszystkich miast
	 * 
	 * @return Zwraca sume potencjalow zrodel mocy ze wszystkich miast
	 */
	public double getWholePotential() {
		double sum = 0.0;
		for(int i = 0; i < towns.size(); i++) {
			Town t = towns.get(i);
			synchronized(Monitors.townGuard) {
				for(int j = 0; j < t.getPowerSources().size(); j++)
					sum += t.getPowerSources().get(j).getPotential();
			}
		}
		return sum;
	}
	
	public Simulation getSimulation() {
		return simulation;
	}

	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public TileLayer getTileLayer() {
		return tileLayer;
	}

	public void setTileLayer(TileLayer tileLayer) {
		this.tileLayer = tileLayer;
	}

	public Vector<Town> getTowns() {
		return towns;
	}

	public void setTowns(Vector<Town> towns) {
		this.towns = towns;
	}

	public Vector<Crossroad> getCrossroads() {
		return crossroads;
	}

	public void setCrossroads(Vector<Crossroad> crossroads) {
		this.crossroads = crossroads;
	}

	public Vector<Civilian> getCivilans() {
		return civilians;
	}

	public void setCivilans(Vector<Civilian> civilans) {
		this.civilians = civilans;
	}

	public int[][] getPathLayer() {
		return pathLayer;
	}

	public void setPathLayer(int[][] pathLayer) {
		this.pathLayer = pathLayer;
	}
	
	public Vector<Civilian> getCivilians() {
		return civilians;
	}

	public void setCivilians(Vector<Civilian> civilians) {
		this.civilians = civilians;
	}

	public int getTileSize() {
		return tileSize;
	}

	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	public Vector<SpawnPoint> getSpawnPoints() {
		return spawnPoints;
	}

	public void setSpawnPoints(Vector<SpawnPoint> spawnPoints) {
		this.spawnPoints = spawnPoints;
	}

	public List<String> getCivilNames() {
		return civilNames;
	}

	public void setCivilNames(List<String> civilNames) {
		this.civilNames = civilNames;
	}

	public List<String> getSurnames() {
		return surnames;
	}

	public void setSurnames(List<String> surnames) {
		this.surnames = surnames;
	}

	public Vector<Badguy> getBadguys() {
		return badguys;
	}

	public void setBadguys(Vector<Badguy> badguys) {
		this.badguys = badguys;
	}

	public boolean isChangeCapital() {
		return needToChangeCapital;
	}

	public void setChangeCapital(boolean changeCapital) {
		this.needToChangeCapital = changeCapital;
	}

	public PathFinder getPathFinder() {
		return pathFinder;
	}

	public void setPathFinder(PathFinder pathFinder) {
		this.pathFinder = pathFinder;
	}

	public Dimension getSize() {
		return size;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public Vector<Superhero> getSuperHeroes() {
		return superHeroes;
	}

	public void setSuperHeroes(Vector<Superhero> superHeroes) {
		this.superHeroes = superHeroes;
	}

	public int getMaxHeroes() {
		return maxHeroes;
	}

	public void setMaxHeroes(int maxHeroes) {
		this.maxHeroes = maxHeroes;
	}

	public boolean isNeedToChangeCapital() {
		return needToChangeCapital;
	}

	public void setNeedToChangeCapital(boolean needToChangeCapital) {
		this.needToChangeCapital = needToChangeCapital;
	}

	public Vector<Fight> getFights() {
		return fights;
	}

	public void setFights(Vector<Fight> fights) {
		this.fights = fights;
	}

	public Vector<GameObject> getRenderedObjects() {
		return renderedObjects;
	}

	public void setRenderedObjects(Vector<GameObject> renderedObjects) {
		this.renderedObjects = renderedObjects;
	}

	public Vector<BackgroundObject> getMapObjects() {
		return mapObjects;
	}

	public void setMapObjects(Vector<BackgroundObject> mapObjects) {
		this.mapObjects = mapObjects;
	}

	public double getTimeToSpawnBadguy() {
		return timeToSpawnBadguy;
	}

	public void setTimeToSpawnBadguy(double timeToSpawnBadguy) {
		this.timeToSpawnBadguy = timeToSpawnBadguy;
	}

	public List<String> getBadguyNames() {
		return badguyNames;
	}

	public void setBadguyNames(List<String> badguyNames) {
		this.badguyNames = badguyNames;
	}

	public List<String> getSuperheroNames() {
		return superheroNames;
	}

	public void setSuperheroNames(List<String> superheroNames) {
		this.superheroNames = superheroNames;
	}
}
