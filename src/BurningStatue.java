

/**
 * 
 * Klasa bedaca konkretnym zrodlem mocy 
 * @see PowerSource
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 *
 */
public class BurningStatue extends PowerSource {
	
	/**
	 * Konstruktor klasy
	 * 
	 * @param town - referencja na miasto w ktorym znajdowac sie ma zrodlo
	 */
	public BurningStatue(Town town) {
		super(town);
		name = "Burning Statue";
		potential = 50.0;
		abilities = new Abilities(0, 0, 0, 0, 0, MyRandom.getDouble(5, 15));
	}
}
