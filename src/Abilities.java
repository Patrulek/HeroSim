
/**
 *
 *	Klasa przechowujaca dane o umiejetnosciach wojownikow. Kazda zmienna okresla dana umiejetnosc:
 *  -intelligence / energy / strength / fightAbility - odpowiada za zadawane obrazenia
 *  -initiative - odpowiada za to, ktora postac pierwsza zaatakuje
 *  -endurance - odpowiada za zmniejszenie obrazen otrzymanych od przeciwnika
 *  
 *  
 * @author Patryk Lewandowski 
 * @version 1.0
 * 
 */
public class Abilities {
	
	public double intelligence;
	public double initiative;
	public double endurance;
	public double energy;
	public double strength;
	public double fightAbility;
	
	/**
	 * 
	 * @param intel - intelligence 
	 * @param init - initiative
	 * @param endur - endurance
	 * @param ener - energy
	 * @param str - strength
	 * @param fA - fightAbility
	 * 
	 * Konstruktor klasy
	 */
	public Abilities(double intel, double init, double endur, double ener, double str, double fA) {
		intelligence = intel;
		initiative = init;
		endurance = endur;
		energy = ener;
		strength = str;
		fightAbility = fA;
	}
	
	/**
	 *  Konstruktor klasy (wszystkie pola ustawia na 0)
	 */
	public Abilities() {
		intelligence = 0;
		initiative = 0;
		endurance = 0;
		energy = 0;
		strength = 0;
		fightAbility = 0;
	}
	
	/**
	 *
	 * @param value - wartosc o jaka zostana zwiekszone statystyki
	 * 
	 * Funkcja wykorzystywana przez {@link Superhero}, gdy wygra walke z przeciwnikiem. Zwieksza
	 * kazda statystyke o podana wartosc.
	 */
	public void increase(double value) {
		intelligence += value;
		initiative += value;
		endurance += value;
		energy += value;
		strength += value;
		fightAbility += value;
	}
}
