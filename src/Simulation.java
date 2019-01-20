import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 * Klasa opisujaca symulacje (stan gry)
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class Simulation extends GameState implements MouseListener, MouseMotionListener, ActionListener, KeyListener, WindowListener {

	/**
	 * Panel swiata 
	 */
	private WorldPanel worldPanel;
	/**
	 * Panel informacyjny
	 */
	private InfoPanel infoPanel;
	/**
	 * Panel konstrukcyjny
	 */
	private ConstructPanel constructPanel;
	/**
	 * Czas symulacji (w sekundach)
	 */
	private double simTime;
	/**
	 * Czy trzeba zaktualizowac panel informacyjny
	 */
	private boolean updateInfoPanel;
	/**
	 * Czy trzeb zaktualizowac panel konstrukcyjny
	 */
	private boolean updateConstructPanel;
	
	/**
	 * Okno zawierajace dodatkowe informacje o swiecie
	 */
	private JFrame worldInfo;
	
	/**
	 * Swiat gry
	 */
	private World world;
	
	/**
	 * Nacelowany obiekt
	 */
	private GameObject target;
	
	/**
	 * Pozycja kursora myszki
	 */
	public MyPoint mousePos;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param app - referencja na aplikacje
	 */
	public Simulation(Application app) {
		super(app);
		
		simTime = 0.0;
		mousePos = new MyPoint(0.0, 0.0);
		
		createPanels();
		world = new World(this);
		worldPanel.setCamera(world.getCamera());
		target = null;
		updateInfoPanel = updateConstructPanel = true;
		worldInfo = null;
		app.addKeyListener(this);
		app.addWindowListener(this);
	}

	/**
	 * Funkcja aktualizujaca stan symulacji
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	public void update(double dt) {
		simTime += dt;
		world.update(dt);
		synchronized(Monitors.worldInfoGuard) {
			if(worldInfo != null) {
				updateWorldInfo();
			}
		}
	}
	
	/**
	 * Funkcja wywolywana przy zakonczeniu symulacji konczaca watki bedace cywilami, zloczyncami oraz bohaterami
	 */
	public void endSimulation() {
			for(int i = 0; i < world.getCivilians().size(); i++)
				world.getCivilians().get(i).die();
			for(int i = 0; i < world.getBadguys().size(); i++) 
				world.getBadguys().get(i).die();
			for(int i = 0; i < world.getSuperHeroes().size(); i++) 
				world.getSuperHeroes().get(i).die();
	}
	
	/**
	 * Funkcja aktualizujaca okno z dodatkowymi informacjami o swiecie
	 */
	private void updateWorldInfo() {
		synchronized(Monitors.worldInfoGuard) {
			worldInfo.getContentPane().removeAll();
			JPanel panel = new JPanel();
			panel.setBounds(0, 0, 300, 300);
			panel.setLayout(null);
				
			JLabel civilians = new JLabel("Civilians: " + world.getCivilians().size());
			civilians.setBounds(50, 20, 200, 20);
			int seconds = (int) (simTime % 60);
			int minutes = (int) (simTime / 60);
				
			JLabel time = new JLabel("Time: " + minutes + " min " + seconds + " sec");
			time.setBounds(50, 40, 200, 20);
			JLabel heroes = new JLabel("Heroes: " + world.getSuperHeroes().size() + "/" + world.getMaxHeroes());
			heroes.setBounds(50, 60, 200, 20);
			double wholePotential = (int)(world.getWholePotential() * 100)/100.0;
			JLabel maxPotential = new JLabel("MaxPotential: " + wholePotential);
			maxPotential.setBounds(50, 80, 200, 20);
				
			panel.add(civilians);
			panel.add(time);
			panel.add(heroes);
			panel.add(maxPotential);
			worldInfo.getContentPane().add(panel);
			worldInfo.repaint();
		}
	}
	
	/**
	 * Funkcja rysujaca stan gry
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 */
	public void render(Graphics g) {
		BufferedImage worldDB = worldPanel.getDoubleBuffer();
		Graphics gWorldImg = worldDB.getGraphics();
		
		gWorldImg.clearRect(0, 0, worldDB.getWidth(), worldDB.getHeight());
		
		world.render(gWorldImg);
		
		synchronized(Monitors.simulationGuard) {
			if(updateInfoPanel) {
				updateInfoPanel();
				updateInfoPanel = false;
			}
			if(updateConstructPanel) {
				updateConstructPanel();
				updateConstructPanel = false;
			}
		}
	}
	
	/**
	 * Funkcja kreujaca panele (wywolywana w konstruktorze)
	 */
	private void createPanels() {
		worldPanel = new WorldPanel(new Dimension(800, 600));
		infoPanel = new InfoPanel(new Dimension(224, 300));
		constructPanel = new ConstructPanel(new Dimension(224, 300));
		
		worldPanel.addMouseListener(this);
		worldPanel.addMouseMotionListener(this);
		worldPanel.addKeyListener(this);
		
		DrawPanel dPanel = new DrawPanel(new Dimension(app.getWindowSize()), app);
		dPanel.add(worldPanel);
		dPanel.add(infoPanel);
		dPanel.add(constructPanel);
		
		dPanel.addKeyListener(this);
		
		app.getContentPane().removeAll();
		app.add(dPanel);
		app.setDrawPanel(dPanel);
	}
	
	/**
	 * Funkcja wywolywana w funkcji {@link #render(Graphics)} gdy {@link #updateConstructPanel} jest ustawione na true
	 */
	private void updateConstructPanel() {
		DrawPanel dPanel = app.getDrawPanel();
		ConstructPanel cPanel = new ConstructPanel(new Dimension(224, 300));
		if(target != null) {
			if(target instanceof Civilian) {
				
				JButton stop = target != null && ((Civilian)target).isStopped() && target != null ? new JButton("Start") : new JButton("Stop");
				JButton kill = new JButton("Kill");
				JComboBox<String> homeTownList = new JComboBox<String>();
				JComboBox<String> nextTownList = new JComboBox<String>();
				
				stop.setBounds(20, 50, 150, 50);
				stop.addActionListener(this);
				kill.setBounds(20, 120, 150, 50);
				kill.addActionListener(this);
				
				homeTownList.setBounds(20, 180, 150, 20); nextTownList.setBounds(20, 205, 150, 20);
				homeTownList.addActionListener(this); nextTownList.addActionListener(this);
				homeTownList.addItem("Choose Hometown"); nextTownList.addItem("Choose next town");
				homeTownList.setName("HomeTown"); nextTownList.setName("NextTown");
				
				for(int i = 0; i < world.getTowns().size(); i++) {
					homeTownList.addItem(world.getTowns().get(i).getName());
					nextTownList.addItem(world.getTowns().get(i).getName());
				}
				
				cPanel.add(stop);
				cPanel.add(kill);
				cPanel.add(homeTownList);
				cPanel.add(nextTownList);
				
				dPanel.remove(constructPanel);
				constructPanel = cPanel;
				dPanel.add(constructPanel);
			}
			else if(target instanceof Town) {
				if(((Town)target).getType() == Town.TownType.CAPITAL) {
					JButton send = new JButton("Send");
					send.setBounds(37, 70, 150, 50);
					send.addActionListener(this);
					JButton superHero = new JButton("Superhero");
					superHero.setBounds(37, 140, 150, 50);
					superHero.addActionListener(this);
					cPanel.add(superHero);
					cPanel.add(send);
				}
				dPanel.remove(constructPanel);
				constructPanel = cPanel;
				dPanel.add(constructPanel);
			}
			else if(target instanceof Superhero || target instanceof Badguy) {
				dPanel.remove(constructPanel);
				constructPanel = cPanel;
				dPanel.add(constructPanel);
			}
		}
		else {
			dPanel.remove(constructPanel);
			constructPanel = new ConstructPanel(new Dimension(224, 300));
			
			JButton civil = new JButton("Civil");
			civil.setBounds(37, 70, 150, 50);
			if(world.getCivilians().size() == world.MAX_CIVILIANS)
				civil.setEnabled(false);
			else 
				civil.setEnabled(true);
			constructPanel.add(civil);
			
			JButton capital = new JButton("Capital");
			capital.setBounds(37, 170, 150, 50);
			constructPanel.add(capital);
			
			civil.addActionListener(this);
			capital.addActionListener(this);
			
			dPanel.add(constructPanel);
		}
	}
	
	/**
	 * Funkcja wywolywana w funkcji {@link #render(Graphics)} gdy {@link #updateInfoPanel} jest ustawione na true
	 */
	private void updateInfoPanel() {
		DrawPanel dPanel = app.getDrawPanel();
		InfoPanel iPanel = new InfoPanel(new Dimension(224, 300));
		
		if(target != null ){
			if(target instanceof Civilian) {
				Civilian c = (Civilian)target;
				
				JLabel nameLabel = new JLabel("Name: " + c.getName()); nameLabel.setForeground(Color.CYAN);
				JLabel surnameLabel = new JLabel("Surname: " + c.getSurname()); surnameLabel.setForeground(Color.CYAN);
				JLabel homeTownLabel = new JLabel("Hometown: " + c.getHomeTown().getName()); homeTownLabel.setForeground(Color.CYAN);
				JLabel nextTownLabel = null;
				if(c.getNextTown() != null)
					nextTownLabel = new JLabel("NextTown: " + c.getNextTown().getName()); 
				else
					nextTownLabel = new JLabel("NextTown: -----");
				nextTownLabel.setForeground(Color.CYAN);
				
				nameLabel.setBounds(20, 100, 150, 20);
				surnameLabel.setBounds(20, 130, 150, 20);
				homeTownLabel.setBounds(20, 160, 150, 20);
				nextTownLabel.setBounds(20, 190, 150, 20);
				
				iPanel.add(nameLabel);
				iPanel.add(surnameLabel);
				iPanel.add(homeTownLabel);
				iPanel.add(nextTownLabel);
				
				dPanel.remove(infoPanel);
				infoPanel = iPanel;
				dPanel.add(infoPanel);
			}
			else if(target instanceof Town){
				Town t = (Town)target;
				
				JLabel nameLabel = new JLabel("Name: " + t.getName()); nameLabel.setForeground(Color.CYAN);
				JLabel peopleLabel = new JLabel("People: " + t.getPeople()); peopleLabel.setForeground(Color.CYAN);
				JLabel capitalLabel = null; 
				JLabel superheroLabel = null;
				
				nameLabel.setBounds(20, 100, 150, 20);
				peopleLabel.setBounds(20, 150, 150, 20);
				
				iPanel.add(nameLabel);
				iPanel.add(peopleLabel);
				
				if(t.getType() == Town.TownType.CAPITAL) {
					capitalLabel = new JLabel("This is a capital!");
					capitalLabel.setBounds(20, 200, 150, 20); 
					capitalLabel.setForeground(Color.CYAN);
					
					superheroLabel = new JLabel("Superheroes: " + ((Capital)t).getSuperHeroes().size());
					superheroLabel.setBounds(20, 230, 150, 20);
					superheroLabel.setForeground(Color.CYAN);
					
					JButton moreInfo = new JButton("More Info");
					moreInfo.setBounds(20, 20, 150, 50);
					moreInfo.addActionListener(this);
					
					iPanel.add(moreInfo);
					iPanel.add(capitalLabel);
					iPanel.add(superheroLabel);
				} else if(t.getType() == Town.TownType.RUIN){
					capitalLabel = new JLabel("This is a ruin!");
					capitalLabel.setBounds(20, 200, 150, 20); 
					capitalLabel.setForeground(Color.CYAN);
					iPanel.add(capitalLabel);
				}
				dPanel.remove(infoPanel);
				infoPanel = iPanel;
				dPanel.add(infoPanel);
			}
			else if(target instanceof Superhero) {
				Superhero sh = (Superhero)target;
				
				JLabel nextTownLabel = new JLabel("NextTown: -----"); nextTownLabel.setForeground(Color.CYAN);
				JLabel nameLabel = new JLabel("Name: " + sh.getName()); nameLabel.setForeground(Color.CYAN);
				JLabel homeTownLabel = new JLabel("Hometown: " + sh.getHomeTown().getName()); homeTownLabel.setForeground(Color.CYAN);
				JLabel hpLabel = new JLabel("HP: " + (int)Math.ceil(sh.getHealthPoints())); hpLabel.setForeground(Color.CYAN);
				Abilities ab = sh.getAbilities();
				JLabel intLabel = new JLabel("Intelligence: " + (int)(100 * ab.intelligence)/100.0); intLabel.setForeground(Color.CYAN);
				JLabel initLabel = new JLabel("Initiative: " + (int)(100 * ab.initiative)/100.0); initLabel.setForeground(Color.CYAN);
				JLabel endLabel = new JLabel("Endurance: " + (int)(100 * ab.endurance)/100.0); endLabel.setForeground(Color.CYAN);
				JLabel enerLabel = new JLabel("Energy: " + (int)(100 * ab.energy)/100.0); enerLabel.setForeground(Color.CYAN);
				JLabel strLabel = new JLabel("Strength: " + (int)(100 * ab.strength)/100.0); strLabel.setForeground(Color.CYAN);
				JLabel faLabel = new JLabel("FightAbility: " + (int)(100 * ab.fightAbility)/100.0); faLabel.setForeground(Color.CYAN);
				if(sh.getNextTown() != null)
				nextTownLabel = new JLabel("NextTown: " + sh.getNextTown().getName()); nextTownLabel.setForeground(Color.CYAN);
				
				nameLabel.setBounds(20, 10, 150, 20);
				homeTownLabel.setBounds(20, 40, 150, 20);
				nextTownLabel.setBounds(20, 70, 150, 20);
				hpLabel.setBounds(20, 100, 150, 20);
				
				intLabel.setBounds(20, 130, 150, 20);
				initLabel.setBounds(20, 160, 150, 20);
				endLabel.setBounds(20, 190, 150, 20);
				enerLabel.setBounds(20, 220, 150, 20);
				strLabel.setBounds(20, 250, 150, 20);
				faLabel.setBounds(20, 280, 150, 20);
				
				
				iPanel.add(nameLabel);
				iPanel.add(homeTownLabel);
				iPanel.add(nextTownLabel);
				iPanel.add(hpLabel);
				iPanel.add(intLabel);
				iPanel.add(initLabel);
				iPanel.add(endLabel);
				iPanel.add(enerLabel);
				iPanel.add(strLabel);
				iPanel.add(faLabel);
				
				dPanel.remove(infoPanel);
				infoPanel = iPanel;
				dPanel.add(infoPanel);
			}
			else if(target instanceof Badguy) {
				Badguy bg = (Badguy)target;
				
				JLabel nextTownLabel = new JLabel("NextTown: -----"); nextTownLabel.setForeground(Color.CYAN);
				JLabel nameLabel = new JLabel("Name: " + bg.getName()); nameLabel.setForeground(Color.CYAN);
				JLabel hpLabel = new JLabel("HP: " + (int)Math.ceil(bg.getHealthPoints())); hpLabel.setForeground(Color.CYAN);
				if(bg.getNextTown() != null)
					nextTownLabel = new JLabel("NextTown: " + bg.getNextTown().getName()); nextTownLabel.setForeground(Color.CYAN);
				
				Abilities ab = bg.getAbilities();
				JLabel intLabel = new JLabel("Intelligence: " + (int)(100 * ab.intelligence)/100.0); intLabel.setForeground(Color.CYAN);
				JLabel initLabel = new JLabel("Initiative: " + (int)(100 * ab.initiative)/100.0); initLabel.setForeground(Color.CYAN);
				JLabel endLabel = new JLabel("Endurance: " + (int)(100 * ab.endurance)/100.0); endLabel.setForeground(Color.CYAN);
				JLabel enerLabel = new JLabel("Energy: " + (int)(100 * ab.energy)/100.0); enerLabel.setForeground(Color.CYAN);
				JLabel strLabel = new JLabel("Strength: " + (int)(100 * ab.strength)/100.0); strLabel.setForeground(Color.CYAN);
				
				
				JLabel faLabel = new JLabel("FightAbility: " + (int)(100 * ab.fightAbility)/100.0); faLabel.setForeground(Color.CYAN);
				nameLabel.setBounds(20, 10, 150, 20);
				nextTownLabel.setBounds(20, 40, 150, 20);
				hpLabel.setBounds(20, 70, 150, 20);
				intLabel.setBounds(20, 100, 150, 20);
				initLabel.setBounds(20, 130, 150, 20);
				endLabel.setBounds(20, 160, 150, 20);
				enerLabel.setBounds(20, 190, 150, 20);
				strLabel.setBounds(20, 220, 150, 20);
				faLabel.setBounds(20, 250, 150, 20);
				
				iPanel.add(nameLabel);
				iPanel.add(nextTownLabel);
				iPanel.add(hpLabel);
				iPanel.add(intLabel);
				iPanel.add(initLabel);
				iPanel.add(endLabel);
				iPanel.add(enerLabel);
				iPanel.add(strLabel);
				iPanel.add(faLabel);
				
				dPanel.remove(infoPanel);
				infoPanel = iPanel;
				dPanel.add(infoPanel);
			}
		}
		else {
			dPanel.remove(infoPanel);
			infoPanel = iPanel;
			dPanel.add(infoPanel);
		}
	}


	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	/**
	 * Funkcja wywolywana przy kliknieciu myszka. Gdy wcisniemy lewy przycisk myszy sprawdzamy czy nie
	 * nacelowalismy jakiegos obiektu
	 * 
	 * @param e - obiekt typu {@link MouseEvent}
	 */
	public void mousePressed(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e)) {
			int x = (int)(mousePos.x + world.getCamera().getPosition().x)/16;
			int y = (int)(mousePos.y + world.getCamera().getPosition().y)/16;
			
			int xPos = (int) ((x)*16); int yPos = (int) ((y)*16);
			int size = 1*16;

			Vector<GameObject> objects = new Vector<GameObject>();
			synchronized(Monitors.worldGuard) {
				Vector<GameObject> objectsOnScreen = world.getRenderedObjects();
				
				for(int i = 0; i < objectsOnScreen.size(); i++) {
					if(clickedOnObject(objectsOnScreen.get(i), xPos, yPos, size))
						objects.add(objectsOnScreen.get(i));
				}
			}
			
			if(!objects.isEmpty()) {
				double maxDistance = 999; int index = 0;
				
				for(int i = 0; i < objects.size(); i++) {
					double distance = getDistanceFromMousePos(objects.get(i));
					if(distance < maxDistance) {
						maxDistance = distance;
						index = i;
					}
				}
				target = objects.get(index);
				world.getCamera().setTrackTarget(true);
			}
			else 
				target = null;

			synchronized(Monitors.simulationGuard) {
				updateInfoPanel = updateConstructPanel = true;
			}
		}
	}
	
	/**
	 * Funkcja sprawdzajaca czy kliknelismy na targetowalny obiekt
	 * @param gObj - sprawdzany obiekt
	 * @param x - wartosc na osi x od ktorej sprawdzamy
	 * @param y - wartosc na osi y od ktorej sprawdzamy
	 * @param size - obszar ktor sprawdzamy
	 * @return Zwraca true jesli kliknelismy na targetowalny obiekt, false w przeciwnym przypadku
	 */
	private boolean clickedOnObject(GameObject gObj, int x, int y, int size) {
		if(gObj instanceof BackgroundObject)
			return false;
		
		if(gObj.getPosition().x > x + size || gObj.getPosition().x + gObj.getSize().width < x ||
				gObj.getPosition().y > y + size || gObj.getPosition().y + gObj.getSize().height < y)
					return false;
			return true;
	}
	
	/**
	 * Funkcja zwracajaca kwadrat odleglosci miedzy pozycja klikniecia myszki a obiektem
	 * @param gObj - obiekt
	 * @return Zwraca kwadrat odleglosci
	 */
	private double getDistanceFromMousePos(GameObject gObj) {
		double x, y;
		if(mousePos.x > gObj.getPosition().x && mousePos.x < gObj.getPosition().x + gObj.getSize().width)
			x = 0;
		else if(mousePos.x > gObj.getPosition().x + gObj.getSize().width)
			x = (int) (mousePos.x - (gObj.getPosition().x + gObj.getSize().width));
		else
			x = (int) (gObj.getPosition().x + gObj.getSize().width - mousePos.x);
		if(mousePos.y > gObj.getPosition().y && mousePos.y < gObj.getPosition().y + gObj.getSize().height)
			y = 0;
		else if(mousePos.y > gObj.getPosition().y + gObj.getSize().height)
			y = (int) (mousePos.y - (gObj.getPosition().y + gObj.getSize().height));
		else
			y = (int) (gObj.getPosition().y + gObj.getSize().height - mousePos.y);
		
		return x*x + y*y;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	/**
	 * Funkcja wywolywana podczas przesuwania myszy z wcisnietym buttonem. Dla prawego przycisku myszy
	 * funkcja steruje polozeniem kamery
	 * 
	 * @param e - obiekt typu {@link MouseEvent}
	 */
	public void mouseDragged(MouseEvent e) {
		if(SwingUtilities.isRightMouseButton(e)) {
			world.getCamera().setTrackTarget(false);
			MyPoint p = new MyPoint(e.getX(), e.getY());
			MyPoint dif = MyPoint.subPoints(mousePos, p);
			mousePos = p;
			world.getCamera().move(dif);
		}
	}

	/**
	 * Funkcja wywolywana przy przesunieciu kursora. Aktualizuje pozycje myszki
	 * 
	 * @param e - obiekt typu {@link MouseEvent}
	 */
	public void mouseMoved(MouseEvent e) {
		mousePos.x = e.getX();
		mousePos.y = e.getY();
		app.requestFocus();
	}

	public WorldPanel getWorldPanel() {
		return worldPanel;
	}

	public void setWorldPanel(WorldPanel worldPanel) {
		this.worldPanel = worldPanel;
	}

	public InfoPanel getInfoPanel() {
		return infoPanel;
	}

	public void setInfoPanel(InfoPanel infoPanel) {
		this.infoPanel = infoPanel;
	}

	public ConstructPanel getConstructPanel() {
		return constructPanel;
	}

	public void setConstructPanel(ConstructPanel constructPanel) {
		this.constructPanel = constructPanel;
	}

	public MyPoint getMousePos() {
		return mousePos;
	}

	public void setMousePos(MyPoint mousePos) {
		this.mousePos = mousePos;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public GameObject getTarget() {
		return target;
	}

	public void setTarget(GameObject target) {
		if(target == null)
			this.target = null;
		else
			this.target = target;
	}

	/**
	 * Funkcja wywolywana przy wykonaniu akcji na JButtonie badz JComboBoxie. Odpowiednie akcje aktualizuja
	 * odpowiednio stan gry.
	 * 
	 * @param e - obiekt typu {@link ActionEvent}
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		Object source = e.getSource();
		
		if(source instanceof JComboBox) {
			JComboBox<?> jCB = (JComboBox<?>)source;
			if(jCB.getName() == "HomeTown") {
				int selectedItem = jCB.getSelectedIndex();
				String itemName = (String)jCB.getItemAt(selectedItem);
				if(!itemName.equals("Choose Hometown")) {
					int townIndex = world.getTownIndexByName(itemName);
					if(!world.getTowns().get(townIndex).isDestroyed()) {
						((Civilian)target).setHomeTown(world.getTowns().get(townIndex));
						updateInfoPanel = true;
					}
				}
			}
			else if(jCB.getName() == "NextTown") {
				int selectedItem = jCB.getSelectedIndex();
				String itemName = (String)jCB.getItemAt(selectedItem);
				if(!itemName.equals("Choose NextTown")) {
					int townIndex = world.getTownIndexByName(itemName);
					if(!world.getTowns().get(townIndex).isDestroyed()) {
						((Civilian)target).setNextTown(world.getTowns().get(townIndex));
						updateInfoPanel = true;
					}
				}
			}
		}
		else {
			if(cmd.equals("Stop")){
				if(!((Civilian)target).isInTown()) {
					((Civilian)target).setStopped(true);
					updateConstructPanel = true;
				}
			}
			else if(cmd.equals("Start")){
				if(!((Civilian)target).isInTown()) {
					((Civilian)target).setStopped(false);
					updateConstructPanel = true;
				}
			}
			else if(cmd.equals("Kill")) {
				((Civilian)target).die();
			}
			else if(cmd.equals("Send")) {
				synchronized(Monitors.worldGuard) {
					synchronized(Monitors.townGuard) {
						Vector<Superhero> superheroes = ((Capital)world.getCapital()).getSuperHeroes();
						if(!world.getBadguys().isEmpty() && !superheroes.isEmpty()) {
							int shIndex = MyRandom.getInt(superheroes.size());
							Superhero sh = superheroes.get(shIndex);
							updateInfoPanel = true;
							synchronized(Monitors.superheroGuard) {
								sh.findAnyBadguy();
								Warrior enemy = sh.getEnemy();
								if(enemy != null && (enemy.getActualTown() == sh.getActualTown() || (enemy.getNextTown() == sh.getActualTown() && enemy.getShortestPath().size() == 2)))
									sh.setWaitingForBadguy(true);
								else {
									sh.setWaitingForBadguy(false);
									((Capital)sh.getHomeTown()).getSuperHeroes().remove(shIndex);
								}
							}
						}
					}
				}
			}
			else if(cmd.equals("Civil")) {
				synchronized(Monitors.worldGuard) {
					if(world.getCivilans().size() < world.MAX_CIVILIANS) {
						int randomName = MyRandom.getInt(world.getCivilNames().size());
						int randomSurname = MyRandom.getInt(world.getSurnames().size());
						Vector<Town> towns = world.getNotDestroyedTowns();
						Town t = towns.get(MyRandom.getInt(towns.size()));
						Civilian c = new Civilian(world.getCivilNames().get(randomName), world.getSurnames().get(randomSurname),
								t, world);
						world.addCivilianToWorld(c);
						world.getCivilNames().remove(randomName);
						world.getSurnames().remove(randomSurname);
						(new Thread(c)).start();
					}
				}
			}
			else if(cmd.equals("Superhero")) {
				synchronized(Monitors.worldGuard) {
					if(world.getSuperHeroes().size() < world.getMaxHeroes()) {
						updateInfoPanel = true;
						int rName = MyRandom.getInt(world.getSuperheroNames().size());
						Superhero sh = new Superhero(world, world.getSuperheroNames().get(rName));
						world.getSuperHeroes().add(sh);
						world.getSuperheroNames().remove(rName);
						((Capital)world.getCapital()).getSuperHeroes().add(sh);
						new Thread(sh).start();
					}
				}
			}
			else if(cmd.equals("Capital")) {
				synchronized(Monitors.worldGuard) {
					target = world.getCapital();
					world.getCamera().setTrackTarget(true);
					updateInfoPanel = updateConstructPanel = true;
				}
			}
			else if(cmd.equals("More Info") && worldInfo == null) {
					worldInfo = new JFrame("World info");
				synchronized(Monitors.worldInfoGuard) {
					int x = app.getX();
					int y = app.getY();
					worldInfo.setBounds(x + 362, y + 150, 300, 300);
					worldInfo.setLayout(null);
					worldInfo.setAlwaysOnTop(true);
					worldInfo.setResizable(false);
					worldInfo.setVisible(true);
					worldInfo.addWindowListener(this);
				}
			}
		}
	}

	public double getSimTime() {
		return simTime;
	}

	public void setSimTime(double simTime) {
		this.simTime = simTime;
	}

	public boolean isUpdateInfoPanel() {
		return updateInfoPanel;
	}
	
	public void setUpdateInfoPanel(boolean bool) {
		this.updateInfoPanel = bool;
	}
	
	public boolean isUpdateConstructPanel() {
		return updateConstructPanel;
	}
	
	public void setUpdateConstructPanel(boolean bool) {
		this.updateConstructPanel = bool;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(app.getGameState() == this) {
			if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				world.getCamera().setTrackTarget(true);
			}
			else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				endSimulation();
				app.startGameOver();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		synchronized(Monitors.worldInfoGuard) {
			if(e.getSource() == worldInfo){
				if(worldInfo != null) {
					worldInfo.dispose();
					worldInfo = null;
					
				}
			}
			else if(e.getSource() == app) {
				if(worldInfo != null) {
					worldInfo.dispose();
					worldInfo = null;
				}
				endSimulation();
				app.breakLoop();
			}
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

	public JFrame getWorldInfo() {
		return worldInfo;
	}

	public void setWorldInfo(JFrame worldInfo) {
		this.worldInfo = worldInfo;
	}
}
