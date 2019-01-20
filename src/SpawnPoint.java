
/**
 * Klasa opisujaca punkt spawnu zloczyncow
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class SpawnPoint extends MyPoint {
	/**
	 * Identyfikator punktu grafu jaki zawiera ten punkt
	 */
	private int graphPointID;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param x - pozycja w swiecie (w pikselach)
	 * @param y - pozycja w swiecie (w pikselach)
	 * @param gpID - identyfikator punktu w grafie
	 */
	public SpawnPoint(double x, double y, int gpID) {
		super(x, y);
		graphPointID = gpID;
	}

	public int getGraphPointID() {
		return graphPointID;
	}

	public void setGraphPointID(int graphPointID) {
		this.graphPointID = graphPointID;
	}
}
