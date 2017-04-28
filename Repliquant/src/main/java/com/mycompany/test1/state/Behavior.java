package com.mycompany.test1.state;

import com.mycompany.test1.main.Repliquant;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathfollowing.NavMeshNavigation;

/**
 * Classe mère des états, ne peut être instanciée. Behavior est composée d'un
 * objet Repliquant, accessible via getBot() ainsi que d'une fonction abstraite
 * performs()
 * @author flori
 */
public abstract class Behavior {
    
    private final Repliquant unBot;
    protected NavMeshNavigation nmNav;
    protected IUT2004Navigation navigation;
    
    /**
     * Constructeur initialisant l'objet Repliquant de cette classe
     * @param unBot  
     */
    public Behavior(Repliquant unBot){
        this.unBot = unBot;
    }
    
    /**
     * Getter de la classe
     * @return l'objet Repliquant de la classe 
     */
    public Repliquant getBot(){
        return this.unBot;
    }
    
    /**
     * Fonction contenant les actions qu'effectuera le bot tant qu'il sera dans
     * cet état
     */
    public abstract void performs();
}
