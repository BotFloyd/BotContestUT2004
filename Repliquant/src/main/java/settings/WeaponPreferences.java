package settings;

import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;

/**
 *
 * @author flori
 */
public class WeaponPreferences {
    
    private final UT2004ItemType weapon;
    private final double weight;
    private double probability;
    private final double initialProbability;
    private final boolean usePrimary;
    private int nbKills;
    private int nbDeaths;

    public WeaponPreferences(UT2004ItemType weapon, double weight, double probability, boolean usePrimary) {
        this.weapon = weapon;
        this.weight = weight;
        this.probability = probability;
        this.initialProbability = probability;
        this.usePrimary = usePrimary;
        this.nbDeaths = 0;
        this.nbKills = 0;
    }
    
    public void updateProbability(){
        probability = (initialProbability * weight + nbKills)/(weight + nbKills + nbDeaths);
    }
    
    public double getProbability(){
        return this.probability;
    }
    
    public UT2004ItemType getWeapon(){
        return this.weapon;
    }
    
    public boolean isUsingPrimary(){
        return this.usePrimary;
    }
    
    public void upNbKills() {
        this.nbKills++;
    }

    public void upNbDeaths() {
        this.nbDeaths++;
    }
}
