import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;


/**
 * Klasa (Singleton) wczytujaca i przechowujaca tekstury
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class TextureManager {
	/**
	 * Mapa przechowywanych tekstur
	 */
	private Map<String, BufferedImage> textures;
	/**
	 * Referencja na menedzera
	 */
	private static TextureManager s_instance;
	
	/**
	 * Funkcja zwracajaca menedzera
	 * @return Zwraca menedzera
	 */
	public static TextureManager getInstance() {
		if(s_instance == null)
			s_instance = new TextureManager();
		return s_instance;
	}
	
	/**
	 * Funkcja wczytujaca teksture z pliku
	 * @param key - klucz tekstury
	 * @param path - sciezka do tekstury
	 */
	public void loadTexture(String key, String path) {
		BufferedImage txt = null;
		try {
			txt = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		textures.put(key, txt);
	}
	
	/**
	 * Funkcja zwracajaca teksture
	 * @param key - klucz tekstury
	 * @return Zwraca teksture o podanym kluczu
	 */
	public BufferedImage getTexture(String key) {
		return textures.get(key);
	}
	
	/**
	 * Funkcja wyrzucajaca teksture
	 * @param key - klucz tekstury
	 */
	public void unloadTexture(String key) {
		textures.put(key, null);
		textures.remove(key);
	}
	
	/**
	 * Konstruktor klasy
	 */
	private TextureManager() {
		textures = new HashMap<String, BufferedImage>();
	}
}
