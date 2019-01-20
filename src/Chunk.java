import java.awt.Graphics;

/**
 * 
 * Klasa przedstawiajaca pojedynczy kafelek mapy
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class Chunk {
	/**
	 * Identyfikator kafelka
	 */
	private int id;
	/**
	 * Referencja na tileset
	 */
	private Tileset tileset;
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param id - identyfikator klocka
	 * @param tileset - referencja na tileset
	 */
	public Chunk(int id, Tileset tileset) {
		this.id = id;
		this.tileset = tileset;
	}
	
	/**
	 * Funkcja rysujaca kafelek
	 * 
	 * @param g - obiekt {@link Graphics} ze standardowej biblioteki Javy, po ktorym rysowany bedzie obiekt
	 * @param x - pozycja klocka w swiecie (w ktorej kolumnie znajduje sie klocek)
	 * @param y - pozycja klocka w swiecie (w ktorym wierszu znajduje sie klocek)
	 * @param cam - kamera swiata
	 */
	public void render(Graphics g, int x, int y, Camera cam) {
		int rowInTileset = (id - 1) / tileset.getNumCols();   // trzeba zmniejszyæ wartoœæ o 1, aby prawid³owo wskazywa³o na miejsce w obrazie
		int colInTileset = (id - 1) % tileset.getNumCols();
		
		int tileSize = tileset.getTileSize();
		
		int screenX = x * tileSize - (int)cam.getPosition().x;
	    int screenY = y * tileSize - (int)cam.getPosition().y;
	    
	    g.drawImage(TextureManager.getInstance().getTexture(tileset.getTextureKey()),
	    	       screenX, screenY, screenX + tileSize, screenY + tileSize,
	    	       colInTileset * tileSize, rowInTileset * tileSize, colInTileset * tileSize + tileSize, rowInTileset * tileSize + tileSize,
	    	       null);
	    
	}
}
