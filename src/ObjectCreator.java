import java.awt.Dimension;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * Klasa (Singleton) tworzaca obiekty z pliku
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class ObjectCreator {
	/**
	 * Referencja na swiat gry
	 */
	private World world;
	/**
	 * Referencja na obiekt klasy
	 */
	private static ObjectCreator s_instance;
	/**
	 * Konstruktor klasy
	 * 
	 * @param world - referencja na swiat
	 */
	private ObjectCreator(World world) { this.world = world; };
	
	/**
	 * Funkcja dostepu do singleton'a
	 * 
	 * @param world - referencja na swiat (przekazywana do konstruktora)
	 * 
	 * @return Zwraca referencje na obiekt klasy
	 */
	public static ObjectCreator getInstance(World world) {
		if(s_instance == null)
			s_instance = new ObjectCreator(world);
		return s_instance;
	}
	/**
	 * Funkcja tworzaca {@link SpawnPoint}
	 * 
	 * @param el - obiekt przechowujacy dane na temat tworzonego obiektu
	 * 
	 * @return Zwraca nowy {@link SpawnPoint}
	 */
	public SpawnPoint createSpawnPoint(Element el) {
		int x = XmlParser.getIntValue(el, "x");
		int y = XmlParser.getIntValue(el, "y");
		int gpID = XmlParser.getIntValue(el, "point");
		
		return new SpawnPoint(x, y, gpID);
	}
	/**
	 * Funkcja tworzaca {@link Town}
	 * 
	 * @param el - obiekt przechowujacy dane na temat tworzonego obiektu
	 * 
	 * @return Zwraca nowy {@link Town}
	 */
	public Town createTown(Element el) {
		String name = XmlParser.getTextValue(el,"name");
		Town.TownType type = XmlParser.getTextValue(el, "type").equals("Normal") ? Town.TownType.NORMAL : Town.TownType.CAPITAL;
		int x = XmlParser.getIntValue(el,"x");
		int y = XmlParser.getIntValue(el, "y");
		int width = XmlParser.getIntValue(el,"width");
		int height = XmlParser.getIntValue(el, "height");
		
		Vector<Integer> gpIDs = new Vector<Integer>();
		
		NodeList points = el.getElementsByTagName("point");
		if(points != null && points.getLength() > 0) 
			for(int j = 0; j < points.getLength(); j++) {
				Element p = (Element)points.item(j);
				gpIDs.add(Integer.parseInt(p.getFirstChild().getNodeValue()));
			}
			
		Town t;
		if(type == Town.TownType.NORMAL)
			t = new Town(new MyPoint(x, y), new Dimension(width, height), world, name, gpIDs);
		else
			t = new Capital(new MyPoint(x, y), new Dimension(width, height), world, name, gpIDs);
		
		return t;
	}
	/**
	 * Funkcja tworzaca {@link Crossroad}
	 * 
	 * @param el - obiekt przechowujacy dane na temat tworzonego obiektu
	 * 
	 * @return Zwraca nowy {@link Crossroad}
	 */
	public Crossroad createCrossroad(Element el) {
		int id = XmlParser.getIntValue(el, "id");
		int x = XmlParser.getIntValue(el, "x");
		int y = XmlParser.getIntValue(el, "y");
		int width = XmlParser.getIntValue(el, "width");
		int height = XmlParser.getIntValue(el, "height");
		
		Crossroad cr = new Crossroad(id, new MyPoint(x, y), new Dimension(width, height), world);
		
		return cr;
	}
	/**
	 * Funkcja tworzaca nowy obiekt typu {@link GraphPoint}
	 * 
	 * @param el - obiekt przechowujacy dane na temat tworzonego obiektu
	 * 
	 * @return Zwraca nowy {@link GraphPoint}
	 */
	public GraphPoint createGraphPoint(Element el) {
		int id = XmlParser.getIntValue(el, "id");
		int x = XmlParser.getIntValue(el, "x");
		int y = XmlParser.getIntValue(el, "y");
		int width = XmlParser.getIntValue(el, "width");
		int height = XmlParser.getIntValue(el, "height");
		
		GraphPoint gp = new GraphPoint(id, new MyPoint(x, y), new Dimension(width, height), world);
		
		return gp;
	}
}
