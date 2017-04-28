package com.mycompany.test1.state.concrete;

import com.mycompany.test1.main.Repliquant;
import com.mycompany.test1.state.Behavior;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

/**
 *
 * @author Kedrisse
 */
public class Pursue extends Behavior{
    
    Player pursued;

    public Pursue(Repliquant unBot) {
        super(unBot);
    }
    
    public void initVars() {
        Repliquant bot = getBot();
        navigation = bot.getNavigation();
        pursued = bot.getTarget();
        nmNav = bot.getNMNav();
        if (nmNav.isAvailable())
            navigation = nmNav;
    }
    
    @Override
    public void performs() {
        initVars();
        navigation.navigate(pursued);
        if(!navigation.isNavigating()){
            getBot().setTarget(null);
        }
    }
}
