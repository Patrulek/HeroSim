import java.util.HashMap;
import java.util.Map;

/**
 * Klasa opisujaca tileset
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class Tileset {
	
	/**
	 * Klucz na teksture tilesetu
	 */
	private String textureKey;
	/**
	 * Liczba wierszy w tilesecie
	 */
	private int numRows;
	/**
	 * Liczba kolumn w tilesecie
	 */
	private int numCols;
	/**
	 * Rozmiar kafelka (w pikselach)
	 */
	private int tileSize;
	/**
	 * Mapa kafelkow
	 */
	private Map<Integer, Chunk> chunksMap;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param textureKey - klucz na teksture
	 * @param tileSize - rozmiar kafelka
	 */
	public Tileset(String textureKey, int tileSize) {
		
		this.tileSize = tileSize;
		this.textureKey = textureKey;
		
		numRows = TextureManager.getInstance().getTexture(textureKey).getHeight() / tileSize;
		numCols = TextureManager.getInstance().getTexture(textureKey).getWidth() / tileSize;
		
		chunksMap = new HashMap<Integer, Chunk>();
		
		for(int i = 0; i < numRows; i++)
			for(int j = 0; j < numCols; j++) {
				Integer id = i * numRows + j + 1; // indeksy tilesów w pliku zaczynaj¹ siê od 1 | 0 = brak tile'a
				chunksMap.put(id, new Chunk(id, this));
			}
	}

	public int getNumRows() {
		return numRows;
	}
	
	public String getTextureKey() {
		return textureKey;
	}

	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}

	public int getNumCols() {
		return numCols;
	}

	public void setNumCols(int numCols) {
		this.numCols = numCols;
	}

	public int getTileSize() {
		return tileSize;
	}

	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	public Map<Integer, Chunk> getChunksMap() {
		return chunksMap;
	}

	public void setChunksMap(Map<Integer, Chunk> chunksMap) {
		this.chunksMap = chunksMap;
	}
}
