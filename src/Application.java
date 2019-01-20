import java.awt.Dimension;

import javax.swing.JFrame;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * 
 * Klasa glowna aplikacji. Ona inicjalizuje okno gry i rozpoczyna przetwarzanie.
 * @author Patryk Lewandowski
 * @version 1.0
 */
public class Application extends JFrame {
	/**
	 * Stala okreslajaca ile czasu ma byc poswiecone na jedna klatke gry (w sekundach)
	 */
	public static final double FRAME_STEP = 0.02;
	/**
	 * Rozmiar okna (w pikselach)
	 */
	private final Dimension windowSize;
	
	/**
	 * Okresla czy petla glowna programu ma byc przetwarzana
	 */
	private boolean running;
	
	/**
	 * Glowny panel do rysowania w oknie
	 */
	private DrawPanel drawPanel;
	
	/**
	 * Aktualnie przetwarzany stan gry
	 */
	private GameState gameState;
	
	/**
	 * Konstruktor klasy. Tworzy okno, inicjalizuje ziarno generator liczba pseudolosowych, laduje potrzebne zasoby
	 * ustawia poczatkowy stan gry i rozpoczyna przetwarzanie.
	 */
	public Application() {
		super("Programowanie Obiektowe - Projekt");
		
		MyRandom.setSeed(System.nanoTime());
		
		windowSize = new Dimension(1024, 600);
		running = true;
		
		setLayout(null);
		setSize(windowSize);
		setVisible(true);
		setResizable(false);
		setIgnoreRepaint(true);
		
		loadResources();

		startMainMenu();
		
		appLoop();
	}
	
	/**
	 * Glowna petla gry
	 */
	public void appLoop() {
		double start, elapsed;
		start = System.nanoTime();
		
		while(running) {
			elapsed = System.nanoTime() - start;
			update(elapsed/1000000000);
			start = System.nanoTime();
			render();
			
			try {
				if((System.nanoTime() - start)/(1000000 * 1000) < FRAME_STEP)
				{
					int toSleep = (int)((FRAME_STEP - ((System.nanoTime() - start)/1000000000.0)) * 1000);
						if(toSleep > 0)
					Thread.sleep(toSleep);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		dispose();
	}
	
	/**
	 * Funkcja aktualizujaca obecny stan gry
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	public void update(double dt) {
		gameState.update(dt);
	}
	
	/**
	 * Odswiezenie obrazu okna
	 */
	public void render() {
		repaint();
	}
	
	/**
	 * Zmiana stanu na glowne menu gry
	 */
	public void startMainMenu() {
		gameState = new MainMenu(this);
		requestFocus();
	}
	
	/**
	 * Zmiana stanu na symulacje
	 */
	public void startSimulation() {
		gameState = new Simulation(this);
		requestFocus();
	}
	
	/**
	 * Zmiana stanu na ekran koncowy gry
	 */
	public void startGameOver() {
		gameState = new GameOver(this, ((Simulation)gameState).getSimTime(), getWorstScore());
		requestFocus();
	}
	
	/**
	 * Pobranie najgorszego wyniku zapisanego w pliku "results.xml"
	 * 
	 * @return Zwraca najgorszy zapisany wynik
	 */
	private double getWorstScore() {
		NodeList nl = XmlParser.parseDocument(XmlParser.parseXML("results.xml"), "Time");
		Element el = (Element)nl.item(nl.getLength() - 1);
		String s = el.getFirstChild().getNodeValue();
		try {
			return Double.parseDouble(s); 
		} catch(NumberFormatException e) {
			return 0.0;
		}
	}
	
	/**
	 * Funkcja ladujaca potrzebne zasoby do pamieci (tekstury)
	 */
	private void loadResources() {
		TextureManager.getInstance().loadTexture("panel_bg", "assets/panel_bg.png");
		TextureManager.getInstance().loadTexture("frame", "assets/frame.png");
		TextureManager.getInstance().loadTexture("go_screen", "assets/go_screen.png");
		TextureManager.getInstance().loadTexture("tlo_menu", "assets/tlomenu.png");
		TextureManager.getInstance().loadTexture("tileset1", "tilesetnew.png");
		TextureManager.getInstance().loadTexture("civilian_r", "assets/civilian_r.png");
		TextureManager.getInstance().loadTexture("civilian_d", "assets/civilian_d.png");
		TextureManager.getInstance().loadTexture("civilian_u", "assets/civilian_u.png");
		TextureManager.getInstance().loadTexture("civilian_l", "assets/civilian_l.png");
		TextureManager.getInstance().loadTexture("citizenDead", "assets/dead_citizen.png");
		TextureManager.getInstance().loadTexture("town", "assets/town.png");
		TextureManager.getInstance().loadTexture("townattacked", "assets/townattacked.png");
		TextureManager.getInstance().loadTexture("capital", "assets/capital.png");
		TextureManager.getInstance().loadTexture("capitalattacked", "assets/capitalattacked.png");
		TextureManager.getInstance().loadTexture("towndestroyed", "assets/towndestroyed.png");
		TextureManager.getInstance().loadTexture("superhero_r", "assets/superhero_r.png");
		TextureManager.getInstance().loadTexture("superhero_d", "assets/superhero_d.png");
		TextureManager.getInstance().loadTexture("superhero_u", "assets/superhero_u.png");
		TextureManager.getInstance().loadTexture("superhero_l", "assets/superhero_l.png");
		TextureManager.getInstance().loadTexture("superherodead", "assets/superherodead.png");
		TextureManager.getInstance().loadTexture("badguy", "assets/badguy.png");
		TextureManager.getInstance().loadTexture("badguydead", "assets/badguydead.png");
		TextureManager.getInstance().loadTexture("map", "assets/map.png");
	}

	public DrawPanel getDrawPanel() {
		return drawPanel;
	}

	public void setDrawPanel(DrawPanel drawPanel) {
		this.drawPanel = drawPanel;
	}
	
	/**
	 * Zmienia zmienna {@link #running} na false, co konczy przetwarzanie glownej petli gry
	 */
	public void breakLoop() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public Dimension getWindowSize() {
		return windowSize;
	}
}
