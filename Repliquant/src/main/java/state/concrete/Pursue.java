package state.concrete;

import main.Repliquant;
import state.Behavior;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

public class Pursue extends Behavior{
    
    public Pursue(Repliquant unBot) {
        super(unBot);
    }
    
    @Override
    public void performs() {
        Player pursued = getBot().getTarget();
        navigation.navigate(pursued);
        if(!navigation.isNavigating()){
            getBot().setTarget(null);
        }
    }
}
