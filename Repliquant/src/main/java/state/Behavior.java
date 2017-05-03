package state;

import main.Repliquant;

/**
 * Classe mère des états, ne peut être instanciée. Behavior est composée d'un
 * objet Repliquant, accessible via getBot() ainsi que d'une fonction abstraite
 * performs() qui prend en parametre le bot
 * @author flori
 */
public abstract class Behavior {

     /**
     * Fonction contenant les actions qu'effectuera le bot tant qu'il sera dans
     * cet état
     * @param unBot
     */
    public abstract void performs(Repliquant unBot);
}
