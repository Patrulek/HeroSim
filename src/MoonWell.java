/**
 * 
 * Klasa bedaca konkretnym zrodlem mocy 
 * @see PowerSource
 *
 * @author Patryk Lewandowski
 * @version 1.0
 * 
 */
public class MoonWell extends PowerSource {
	/**
	 * Konstruktor klasy
	 * 
	 * @param town - referencja na miasto w ktorym znajdowac sie ma zrodlo
	 */
	public MoonWell(Town town) {
		super(town);
		name = "Moon Well";
		potential = 50.0;
		abilities = new Abilities(MyRandom.getDouble(5, 15), 0, 0, 0, 0, 0);
	}
}