import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JTextField;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * Klasa opisujaca stan koncowy po symulacji
 *
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class GameOver extends GameState implements FocusListener, KeyListener {
	/**
	 * Referencja na tlo
	 */
	private BufferedImage image;
	/**
	 * Pole tekstowe
	 */
	private JTextField tf;
	/**
	 * Uzyskany wynik w symulacji
	 */
	private double score;
	/**
	 * Lista wynikow  
	 */
	private List<String> listDouble;
	/**
	 * Lista imion
	 */
	private List<String> listString;
	/**
	 * Czy wynik zalicza sie do najlepszych wynikow
	 */
	private boolean bestScores;
	/**
	 * Czas zmiany stanu gry (w sekundach)
	 */
	private double timeToChangeState;

	/**
	 * Konstruktor klasy
	 * 
	 * @param app - referencja na aplikacje
	 * @param score - uzyskany wynik
	 * @param worstScore - najgorszy zapisany wynik
	 */
	public GameOver(Application app, double score, double worstScore) {
		super(app);
		
		this.score = score;
		
		DrawPanel dPanel = new DrawPanel(new Dimension(app.getWindowSize()), app);

		image = TextureManager.getInstance().getTexture("go_screen");
		
		if(score > worstScore) {
			tf = new JTextField("Podaj swoje imie:");
			tf.setBounds(400, 300, 200, 50);
			tf.addFocusListener(this);
			tf.addKeyListener(this);
			dPanel.add(tf);
			listDouble = new LinkedList<String>();
			listString = new LinkedList<String>();
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
			bestScores = true;
		}
		else {
			bestScores = false;
			timeToChangeState = 3.5;
		}
		
		app.getContentPane().removeAll();
		app.setDrawPanel(dPanel);
		app.add(dPanel);
	}

	/**
	 * Funkcja aktualizujaca stan gry
	 * 
	 * @param dt - czas przebiegu klatki (w sekundach)
	 */
	public void update(double dt) {
		if(!bestScores) {
			timeToChangeState -= dt;
			if(timeToChangeState <= 0.0)
				app.startMainMenu();
		}
	}

	/**
	 * Funkcja rysujaca stan gry
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 */
	public void render(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}

	/**
	 * Funkcja sprawdzajaca czy pole tekstowe zdobylo focus i usuwajaca z niego tekst
	 * 
	 * @param arg0 - obiekt typu {@link FocusEvent}
	 */
	public void focusGained(FocusEvent arg0) {
		if(arg0.getSource() == tf) {
			tf.setText("");
		}
	}

	@Override
	public void focusLost(FocusEvent arg0) {}

	/**
	 * Funkcja sprawdzajaca wcisniecie klawisza (w tym przypadku entera).
	 * Po wcisnieciu zapisuje wynik do pliku i przechodzi do glownego menu.
	 * 
	 * @param arg0 - obiekt typu {@link KeyEvent}
	 */
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			if(!(tf.getText().equals("Podaj swoje imie:") || tf.getText().equals(""))) {
				
				List<Double> results = new LinkedList<Double>();
				listDouble.add(((Double)((int)(score * 1000)/1000.0)).toString());
				for(int i = 0; i < 5; i++) {
					try {
						results.add(Double.parseDouble(listDouble.get(i)));
					} catch(NumberFormatException e) {
						results.add(0.0);
					}
				}
				results.add((int)(score * 1000)/1000.0);
				listString.add(tf.getText());
				for(int i = 0; i < results.size(); i++) {
					for(int j = 0; j < results.size() - 1; j++) {
						if(results.get(j) < results.get(j+1)) {
							double temp = results.get(j);
							results.set(j, results.get(j+1));
							results.set(j+1, temp);
							String tempStr = listString.get(j);
							listString.set(j, listString.get(j+1));
							listString.set(j+1, tempStr);
							String tempStr2 = listDouble.get(j);
							listDouble.set(j, listDouble.get(j+1));
							listDouble.set(j+1, tempStr2);
						}
					}
				}
				results.remove(5);
				listString.remove(5);
				listDouble.remove(5);
				XmlParser.writeResultsXML("results.xml", listDouble, listString);
				app.startMainMenu();
			}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
}
