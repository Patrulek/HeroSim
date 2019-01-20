import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * Klasa opisujaca graf polaczen miedzy sciezkami
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class Graph {
	/**
	 * Polaczenia
	 */
	int connections[][];
	
	/**
	 * Konstruktor klasy inicjalizujacy zmienna {@link #connections} wartosciami z pliku "graph.xml"
	 */
	public Graph() {
		NodeList nl = XmlParser.parseDocument(XmlParser.parseXML("graph.xml"), "point");
		int size = nl.getLength();
		connections = new int[size][size];
		for(int i = 0; i < size; i++)
			for(int j = 0; j < size; j++)
				connections[i][j] = 0;
		
		for(int i = 0; i < size; i++) {
			Element point = (Element)nl.item(i);
			int id = XmlParser.getIntValue(point, "id");
			
			NodeList paths = point.getElementsByTagName("path");
			for(int j = 0; j < paths.getLength(); j++) {
				Element path = (Element)paths.item(j);
				int connection = Integer.parseInt(path.getFirstChild().getNodeValue());
				
				connections[id][connection] = 1;
			}
		}
	}

	public int[][] getConnections() {
		return connections;
	}
}
