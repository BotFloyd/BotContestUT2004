package state.concrete;

import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import main.Repliquant;
import state.Behavior;

/**
 *
 * @author flori
 */
public class Stuck extends Behavior{

    IUT2004Navigation navigation;
    
    @Override
    public void performs(Repliquant unBot) {
        initVars(unBot);
        unBot.getLog().info("Entering Stuck Mode");
        navigation.navigate(unBot.getItems().getRandomItem());
    }
    
    private void initVars(Repliquant unBot){
        navigation = unBot.getNavToUse();
    }
    
}
