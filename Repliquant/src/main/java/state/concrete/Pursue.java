package state.concrete;

import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import state.Behavior;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import main.Repliquant;

public class Pursue extends Behavior{
    
    IUT2004Navigation navigation;
    
    @Override
    public void performs(Repliquant unBot) {
        if (unBot.getNMNav().isAvailable())
            navigation = unBot.getNMNav();
        else
            navigation = unBot.getNavigation();
        Player pursued = unBot.getTarget();
        navigation.navigate(pursued);
        if(!navigation.isNavigating()){
            unBot.setTarget(null);
        }
    }
}
