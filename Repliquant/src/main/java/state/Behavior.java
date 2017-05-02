package state;

import main.Repliquant;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;

/**
 * Classe mère des états, ne peut être instanciée. Behavior est composée d'un
 * objet Repliquant, accessible via getBot() ainsi que d'une fonction abstraite
 * performs()
 * @author flori
 */
public abstract class Behavior {
    
        /*if (unBot.getNMNav().isAvailable())
            navigation = unBot.getNMNav();
        else
            navigation = unBot.getNavigation();*/
     /**
     * Fonction contenant les actions qu'effectuera le bot tant qu'il sera dans
     * cet état
     * @param unBot
     */
    public abstract void performs(Repliquant unBot);
}
