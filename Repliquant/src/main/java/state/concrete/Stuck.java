package state.concrete;

import main.Repliquant;
import state.Behavior;

/**
 *
 * @author flori
 */
public class Stuck extends Behavior{

    
    
    @Override
    public void performs(Repliquant unBot) {
        unBot.getLog().info("Entering Stuck Mode");
    }
    
}
