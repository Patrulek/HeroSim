import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * 
 * 
 * Klasa opisujaca glowne menu gry
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class MainMenu extends GameState implements ActionListener, KeyListener, MouseListener {
	/**
	 * Przycisk start
	 */
	private JButton start;
	/**
	 * Przycisk exit
	 */
	private JButton exit;
	/**
	 * Przycisk scores
	 */
	private JButton scores;
	/**
	 * Referencja na teksture tla
	 */
	private BufferedImage image = null;
	/**
	 * Labele wykorzystywane w sekcji scores
	 */
	private JLabel scoreLabels[];
	
	/**
	 * Czy znajdujemy sie w sekcji scores
	 */
	private boolean scoreScene;

	/**
	 * Konstruktor klasy
	 * 
	 * @param app - referencja na aplikacje
	 */
	public MainMenu(Application app) {
		super(app);
		
		createButtons();
		
		DrawPanel dPanel = new DrawPanel(new Dimension(app.getWindowSize()), app);
		dPanel.add(start);
		dPanel.add(exit);
		dPanel.add(scores);
		
		image = TextureManager.getInstance().getTexture("tlo_menu");
		
		scoreScene = false;
		
		app.getContentPane().removeAll();
		app.setDrawPanel(dPanel);
		app.add(dPanel);
	}
	
	/**
	 * Funkcja obslugujaca menu
	 * 
	 * @param e - obiekt typu {@link ActionEvent}
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		
		if(cmd.equals("Exit"))
			app.breakLoop();
		else if(cmd.equals("Start"))
			app.startSimulation();
		else if(cmd.equals("Scores")) {
			scoreScene = true;
			app.getContentPane().removeAll();
			app.requestFocus();
			scoreLabels = new JLabel[5];
			DrawPanel dPanel = new DrawPanel(new Dimension(app.getWindowSize()), app);
			app.addKeyListener(this);
			app.addMouseListener(this);
			LinkedList<String>listDouble = new LinkedList<String>();
			LinkedList<String>listString = new LinkedList<String>();
			NodeList nl = XmlParser.parseDocument(XmlParser.parseXML("results.xml"), "Time");
			for(int i = 0; i < nl.getLength(); i++) {
				Element el = (Element)nl.item(i);
				listDouble.add(el.getFirstChild().getNodeValue());
			}
			NodeList nl2 = XmlParser.parseDocument(XmlParser.parseXML("results.xml"), "Name");
			for(int i = 0; i < nl2.getLength(); i++) {
				Element el = (Element)nl2.item(i);
				listString.add(el.getFirstChild().getNodeValue());
			}
			
			for(int i = 0; i < 5; i++) {
				scoreLabels[i] = new JLabel();
				scoreLabels[i].setText("Imie: " + listString.get(i) +  "     Wynik: " + listDouble.get(i));
				scoreLabels[i].setBounds(416, 200 + i * 30, 400, 20);
				scoreLabels[i].setForeground(Color.RED);
				dPanel.add(scoreLabels[i]);
			}
			app.setDrawPanel(dPanel);
			app.add(dPanel);
		}
		
	}
	
	public void update(double dt) {}
	
	public void render(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
	
	/**
	 * Funkcja inicjalizujaca przyciski
	 */
	private void createButtons() {
		start = new JButton("Start");
		exit = new JButton("Exit");
		scores = new JButton("Scores");
		
		start.setBounds(412, 160, 200, 50);
		exit.setBounds(412, 360, 200, 50);
		scores.setBounds(412, 260, 200, 50);
		
		start.addActionListener(this);
		exit.addActionListener(this);
		scores.addActionListener(this);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(app.getGameState() == this) {
			if(scoreScene) {
				scoreScene = false;
				app.getContentPane().removeAll();
				createButtons();
				DrawPanel dPanel = new DrawPanel(new Dimension(app.getWindowSize()), app);
				dPanel.add(start);
				dPanel.add(exit);
				dPanel.add(scores);
				app.setDrawPanel(dPanel);
				app.add(dPanel);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if(scoreScene) {
			scoreScene = false;
			app.getContentPane().removeAll();
			createButtons();
			DrawPanel dPanel = new DrawPanel(new Dimension(app.getWindowSize()), app);
			dPanel.add(start);
			dPanel.add(exit);
			dPanel.add(scores);
			app.setDrawPanel(dPanel);
			app.add(dPanel);
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
