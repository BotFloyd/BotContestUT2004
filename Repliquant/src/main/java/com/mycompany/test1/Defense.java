package com.mycompany.test1;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Items;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Players;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.util.Random;



public class Defense extends Behavior{
    
    IUT2004Navigation navigation;
    Players players;
    AdvancedLocomotion move;
    AgentInfo info;
    Random random;
    Items items;
    
    public Defense(Repliquant bot){
        super(bot);
    }
    
    @Override
    public void performs(){      
        initVars();
        if(! players.canSeeEnemies()){
            move.turnVertical(180);
        } else {
            Player opponent = info.getNearestVisiblePlayer();
            move.turnTo(opponent);
            String weapOpponent = opponent.getWeapon();
            if("XWeapons.RocketLauncher".equals(weapOpponent)){
                dodgeRocketLauncher(opponent);
            }
        }
    }
    
    private void dodgeRocketLauncher(Player opponent) {
        
    }
    
    private void initVars(){
        navigation = getBot().getNavigation();
        players = getBot().getPlayers();
        move = getBot().getMove();
        info = getBot().getInfo();
        random = getBot().getRandom();
        items = getBot().getItems();
    }
    
    /*
    private void dodge() {
        int uneValeurRandom = getBot().getRandom().nextInt(5);
        if(uneValeurRandom < 5){
            Player player1 = getBot().getInfo().getNearestPlayer();
            NavPoint recul = getBot().getNavigation().getNearestNavPoint(getBot().getBot());
            Location uneLocation = player1.getLocation().sub(recul.getLocation());

            getBot().getMove().doubleJump();
            getBot().getLog().info("Gauche");
        }
        if(uneValeurRandom >= 5){
            getBot().getMove().doubleJump();
            getBot().getLog().info("Jump");
        }
                
    }
*/
}
