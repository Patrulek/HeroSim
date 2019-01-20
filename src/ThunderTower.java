/**
 * 
 * Klasa bedaca konkretnym zrodlem mocy 
 * @see PowerSource
 *
 * 
 * @author Patryk Lewandowski
 * @version 1.0
 */
public class ThunderTower extends PowerSource {
	/**
	 * Konstruktor klasy
	 * 
	 * @param town - referencja na miasto w ktorym znajdowac sie ma zrodlo
	 */
	public ThunderTower(Town town) {
		super(town);
		name = "Thunder Tower";
		potential = 50.0;
		abilities = new Abilities(0, MyRandom.getDouble(5, 15), 0, 0, 0, 0);
	}
}
