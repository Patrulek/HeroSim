import java.awt.Graphics;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


/**
 * Klasa opisujaca mape kafelkowa
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class TileLayer {
	
	/**
	 * Tileset mapy kafelkowej
	 */
	private Tileset tileset;
	/**
	 * Mapa identyfikatorow {@link Chunk}ow
	 */
	private int tileIDs[][];
	/**
	 * Referencja na kamere swiata
	 */
	private Camera camera;
	
	/**
	 * Numer pierwszej renderowanej kolumny
	 */
	private int leftTile;
	/**
	 * Numer ostatniej renderowanej kolumny
	 */
	private int rightTile;
	/**
	 * Numer pierwszego renderowanego wiersza
	 */
	private int topTile;
	/**
	 * Numer ostatniego renderowanego wiersza
	 */
	private int bottomTile;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param fileName - sciezka do pliku z mapa (w projekcie jest to "mapa.txt")
	 */
	public TileLayer(String fileName) {
		loadLayer(fileName);
		camera = null;
	}
	
	/**
	 * Funkcja aktualizujaca stan mapy kafelkowej
	 */
	public void update() {
		if(camera != null) {
	      	
	      	int tileColumns = (int)(Math.ceil(camera.getSize().getWidth()) / tileset.getTileSize());
	      	int tileRows = (int) (Math.ceil(camera.getSize().getHeight()) / tileset.getTileSize());
	      	
	      	leftTile =  camera.getPosition().x < 0 ? 0 : (int) Math.floor(camera.getPosition().x / tileset.getTileSize());
	      	leftTile = leftTile - 2 >= 0 ? leftTile - 2 : 0;
	      	topTile = camera.getPosition().y < 0 ? 0 : (int) Math.floor(camera.getPosition().y / tileset.getTileSize());
	      	topTile = topTile - 2 >= 0 ? topTile - 2 : 0;
	      	rightTile = leftTile + tileColumns + 4 >= tileIDs[0].length ? tileIDs[0].length : leftTile + tileColumns + 4;
	      	bottomTile = topTile + tileRows + 4 >= tileIDs.length ? tileIDs.length : topTile + tileRows + 4;
	      	
		}
	}
	
	/**
	 * Funkcja rysujaca mape kafelkowa
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 */
	public void render(Graphics g) {
		for(int x = leftTile; x < rightTile; x++)
			for(int y = topTile; y < bottomTile; y++)
				tileset.getChunksMap().get(tileIDs[y][x]).render(g, x, y, camera); 
	}
	
	/**
	 * Funkcja sprawdzajaca czy podany string jest liczba calkowita
	 * @param s - string
	 * @return Zwraca true jesli jest, false w przeciwnym przypadku
	 */
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
	
	/**
	 * Funkcja ladujaca mape z pliku
	 * @param fileName - sciezka do pliku
	 */
	private void loadLayer(String fileName) {

		List<String> tilemap = null;
		try {
			tilemap = Files.readAllLines(Paths.get(fileName), java.nio.charset.StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int numColumns = Integer.parseInt(tilemap.get(0).substring(6));
		int numRows = Integer.parseInt(tilemap.get(1).substring(7));
		int tileSize = Integer.parseInt(tilemap.get(2).substring(9));
		String tilesetFileName = tilemap.get(3).substring(8);
		
		tileset = new Tileset("tileset1", tileSize);
		tileIDs = new int [numRows][numColumns];
		
		for(int i = 0, line = 4; i < numRows; i++, line++) {
			String sLine = tilemap.get(line);
			
			for(int j = 0, k = 0; k < numColumns; j++) {
				if(!isInteger(sLine.substring(j, j + 1)))
					continue;
					
				String number = "";
				while(j < sLine.length() && isInteger(sLine.substring(j, j + 1))) {
					number += sLine.substring(j, j + 1);
					j++;
				}
				
				tileIDs[i][k] = Integer.parseInt(number);
				k++;
			}
		}
	}

	public Tileset getTileset() {
		return tileset;
	}

	public void setTileset(Tileset tileset) {
		this.tileset = tileset;
	}

	public int[][] getTileIDs() {
		return tileIDs;
	}

	public void setTileIDs(int[][] tileIDs) {
		this.tileIDs = tileIDs;
	}

	public int getLeftTile() {
		return leftTile;
	}

	public void setLeftTile(int leftTile) {
		this.leftTile = leftTile;
	}

	public int getRightTile() {
		return rightTile;
	}

	public void setRightTile(int rightTile) {
		this.rightTile = rightTile;
	}

	public int getTopTile() {
		return topTile;
	}

	public void setTopTile(int topTile) {
		this.topTile = topTile;
	}

	public int getBottomTile() {
		return bottomTile;
	}

	public void setBottomTile(int bottomTile) {
		this.bottomTile = bottomTile;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
}
