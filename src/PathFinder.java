import java.awt.Dimension;
import java.util.LinkedList;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * Klasa korzystajaca z grafu, znajdujaca sciezki w swiecie po ktorych poruszaja sie ludzie
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class PathFinder {

	/**
	 * Punkty grafu
	 */
	private Vector<GraphPoint> graphPoints;
	/**
	 * Polaczenia miedzy punktami w grafie
	 */
	private Graph graph;
	/**
	 * Referencja na swiat gry
	 */
	private World world;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param world - referencja na swiat gry
	 */
	public PathFinder(World world) {
		this.world = world;
		generateGraphPoints();
		graph = new Graph();
	}
	
	/**
	 * Funkcja generujaca obiekty typu {@link GraphPoint} z pliku "objects.xml"
	 */
	private void generateGraphPoints() {
		if(graphPoints == null) 
			graphPoints = new Vector<GraphPoint>();
		
		NodeList nl = XmlParser.parseDocument(XmlParser.parseXML("objects.xml"), "GraphPoint");
		
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				
				Element el = (Element)nl.item(i);
	
				GraphPoint gp = ObjectCreator.getInstance(world).createGraphPoint(el);
				graphPoints.add(gp);
			}
		} 
	}
	
	/**
	 * Funkcja przechodzaca przez graf rekurencyjnie algorytmem BFS
	 * 
	 * @param startPoint - poczatek sciezki
	 * @param endPoint - koniec sciezki
	 * @param allPaths - referencja na wszystkie znalezione sciezki miedzy punktami
	 * @param queue - zakolejkowane punkty grafu
	 * @param path - aktualnie wygenerowana sciezka
	 * @param tab2 - tablica poprzednikow punktow
	 * @param visited - tablica odwiedzonych punktow
	 */
	private void BFS(int startPoint, int endPoint, Vector<Vector<Integer>> allPaths, LinkedList<Integer> queue, Vector<Integer> path, int tab2[], int visited[]) {
		while(!queue.isEmpty()) {
			int tab[][] = graph.getConnections();
			int vertex = queue.getFirst();
			queue.removeFirst();
			
			if(vertex == endPoint) {
				int i = vertex;
				while(tab2[i] != -1) {
					path.add(i);
					i = tab2[i];
				}
				allPaths.add(path);
			}
			
			for(int i = 0; i < tab[0].length; i++) {
				if(vertex == i || visited[i] == 1) continue;
				if(tab[vertex][i] == 1) {
					visited[i] = 1;
					tab2[i] = vertex;
					queue.addFirst(i);
				}
			}
			
			BFS(vertex, endPoint, allPaths, queue, path, tab2, visited);
		}
	}
	
	/**
	 * Funkcja inicjalizujaca algorytm BFS
	 * 
	 * @param startPoint - poczatek sciezki
	 * @param endPoint - koniec sciezki 
	 * @param allPaths - referencja na wszystkie znalezione sciezki
	 */
	private void BFSStart(int startPoint, int endPoint, Vector<Vector<Integer>> allPaths) {
		
		LinkedList<Integer> queue = new LinkedList<Integer>();
		Vector<Integer> path = new Vector<Integer>();
		int tab2[] = new int[graph.getConnections().length];
		int visited[] = new int[graph.getConnections().length];
		
		for(int i = 0; i < tab2.length; i++) {
			tab2[i] = -1;
			visited[i] = 0;
		}
		
		queue.addFirst(startPoint);
		visited[startPoint] = 1;
		BFS(startPoint, endPoint, allPaths, queue, path, tab2, visited);
		path.add(startPoint);
	}
	
	/**
	 * Funkcja znajdujaca najkrotsza sciezke z podanych przez parametr
	 * 
	 * @param allPaths - sciezki do przeszukania
	 * 
	 * @return Zwraca najkrotsza sciezke lub null gdy nie podano zadnych sciezek na wejsciu
	 */
	public Vector<Integer> findShortestPath(Vector<Vector<Integer>> allPaths) {
		
		if(allPaths == null)
			return null;
		
		double minDistance = 999999;
		int index = -1;
		
		for(int i = 0; i < allPaths.size(); i++) {
			Vector<Integer> vec = allPaths.get(i);
			
			double distance = 0.0;
			for(int j = 0; j < vec.size() - 1; j++) {
				GraphPoint gp1 = graphPoints.get(vec.get(j));
				GraphPoint gp2 = graphPoints.get(vec.get(j+1));
				
				if(gp1.getPosition().x == gp2.getPosition().x)
					distance += Math.abs(gp2.getPosition().y - gp1.getPosition().y);
				else
					distance += Math.abs(gp2.getPosition().x - gp1.getPosition().x);
			}
			if(distance < minDistance) {
				minDistance = distance;
				index = i;
			}
		}
		
		if(index == -1)
			return null;
		
		Vector<Integer> shortestPath = new Vector<Integer>();
		Vector<Integer> indexedPath = allPaths.get(index);
		
		for(int i = 0; i < indexedPath.size(); i++) {
			shortestPath.add(indexedPath.get(indexedPath.size() - 1 - i));
		}
		
		return shortestPath;
	}
	
	/**
	 * Funkcja znajdujaca sciezki pomiedzy miastami
	 * 
	 * @param t1 - miasto pierwsze
	 * @param t2 - miasto drugie
	 * 
	 * @return Zwraca wszystkie sciezki pomiedzy miastami lub null gdy podane miasta sa takie same lub jedno
	 * z miast nie istnieje
	 */
	public Vector<Vector<Integer>> findPathsBetweenTowns(Town t1, Town t2) {
		if(t1 == t2 || t1 == null || t2 == null)
			return null;
		
		Vector<Integer> startPoints = t1.getGraphPointIDs();
		Vector<Integer> endPoints = t2.getGraphPointIDs();
		Vector<Vector<Integer>> allPaths = new Vector<Vector<Integer>>();
		
		for(int i = 0; i < startPoints.size(); i++)
			for(int j = 0; j < endPoints.size(); j++)
				findPath(startPoints.get(i), endPoints.get(j), allPaths);
		
		return allPaths;
	}
	
	/**
	 * Funkcja znajdujaca sciezke do miasta z podanego miejsca
	 * 
	 * @param startPoint - poczatek sciezki
	 * @param t - miasto
	 * 
	 * @return Zwraca wszystkie sciezki od punktu do miasta lub null gdy miasto nie istnieje, lub aktualnie 
	 * znajdujemy sie juz w miescie
	 */
	public Vector<Vector<Integer>> findPathToTown(int startPoint, Town t) {
		if(t == null)
			return null;
		
		Vector<Integer> endPoints = t.getGraphPointIDs();
		for(int i = 0; i < endPoints.size(); i++)
			if(startPoint == endPoints.get(i))
				return null;
		
		Vector<Vector<Integer>> allPaths = new Vector<Vector<Integer>>();
		
		for(int i = 0; i < endPoints.size(); i++)
			findPath(startPoint, endPoints.get(i), allPaths);
		return allPaths;
	}
	
	/**
	 * Funkcja znajdujaca sciezke do punktu z miasta
	 * 
	 * @param startTown - miasto z ktorego wychodzimy
	 * @param endPoint - punkt do ktorego idziemy
	 * 
	 * @return Zwraca wszystkie sciezki pomiedzy miastem a punktem lub null gdy miasto nie istnieje, lub
	 * chcemy wyjsc z miasta do punktu ktore znajduje sie w tym samym miescie
	 */
	public Vector<Vector<Integer>> findPathToPointFromTown(Town startTown, int endPoint) {
		if(startTown == null)
			return null;
		
		Vector<Integer> startPoints = startTown.getGraphPointIDs();
		for(int i = 0; i < startPoints.size(); i++)
			if(startPoints.get(i) == endPoint)
				return null;
		
		Vector<Vector<Integer>> allPaths = new Vector<Vector<Integer>>();
		
		for(int i = 0; i < startPoints.size(); i++)
			findPath(startPoints.get(i), endPoint, allPaths);
		return allPaths;
	}
	
	/**
	 * Funkcja znajdujaca sciezke miedzy punktami
	 * 
	 * @param startPoint - poczatek sciezki
	 * @param endPoint - koniec sciezki
	 * @param allPaths - referencja na wszystkie sciezki (tam sa dopisywane znalezione sciezki)
	 */
	public void findPath(int startPoint, int endPoint, Vector<Vector<Integer>> allPaths) {
		if(allPaths == null)
			allPaths = new Vector<Vector<Integer>>();
		
		if(startPoint == endPoint)
			return;
		
		BFSStart(startPoint, endPoint, allPaths);
	}


	public Vector<GraphPoint> getGraphPoints() {
		return graphPoints;
	}


	public void setGraphPoints(Vector<GraphPoint> graphPoints) {
		this.graphPoints = graphPoints;
	}


	public Graph getGraph() {
		return graph;
	}


	public void setGraph(Graph graph) {
		this.graph = graph;
	}


	public World getWorld() {
		return world;
	}


	public void setWorld(World world) {
		this.world = world;
	}
}
