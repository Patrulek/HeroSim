import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 
 * 
 * Klasa do dzialan na plikach
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class IO {
	/**
	 * Funkcja wczytujaca plik
	 * 
	 * @param fileName - sciezka do pliku
	 * 
	 * @return Zwraca zczytane linie pliku lub null gdy wystapi blad
	 */
	public static List<String> readAllLines(String fileName) {
		try {
			return Files.readAllLines(Paths.get(fileName), java.nio.charset.StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
