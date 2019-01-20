import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Vector;

/**
 * 
 * 
 * Klasa stolicy.
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class Capital extends Town {
	
	/**
	 * Lista superbohaterow przesiadujacych w miescie
	 */
	private Vector<Superhero> superHeroes;

	/**
	 * Konstruktor klasy
	 * 
	 * @param pos - pozycja w swiecie (w pikselach)
	 * @param size - rozmiar stolicy (w pikselach)
	 * @param world - referencja na swiat symulacji
	 * @param name - nazwa miasta
	 * @param gpIDs - ID punktow grafu jakie znajduja sie w miescie
	 */
	public Capital(MyPoint pos, Dimension size, World world, String name, Vector<Integer> gpIDs) {
		super(pos, size, world, name, gpIDs);
		type = TownType.CAPITAL;
		superHeroes = new Vector<Superhero>();
		activeSprite = "capital";
	}
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param town - referencja na istniejace miasto
	 */
	public Capital(Town town) {
		super(town);
		type = TownType.CAPITAL;
		superHeroes = new Vector<Superhero>();
		activeSprite = "capital";
	}
	
	public Vector<Superhero> getSuperHeroes() {
		return superHeroes;
	}

	public void setSuperHeroes(Vector<Superhero> superHeroes) {
		this.superHeroes = superHeroes;
	}
}
