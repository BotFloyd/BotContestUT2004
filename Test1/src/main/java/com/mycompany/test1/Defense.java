package com.mycompany.test1;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Players;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;



public class Defense extends Behavior{
    
    IUT2004Navigation navigation;
    Players players;
    AdvancedLocomotion move;
    
    public Defense(Repliquant bot){
        super(bot);
    }
    
    @Override
    public void performed(){      
        initVars();
           /*
        Location ennemiLocation = getBot().
            getBot().getLog().info("Gauche" + ennemiLocation);
        Location recul = getBot().getNavigation().getNearestNavPoint(getBot().getBot()).getLocation();
            getBot().getLog().info("Gauche" + recul);
        Location uneLocation = ennemiLocation.sub(recul);
            getBot().getLog().info("Gauche" + uneLocation);
        */
        navigation.stopNavigation();
        move.turnVertical(180);
        
        move.doubleJump();
        //Location uneLocation = getBot().getInfo().getLocation();
        //getBot().getLog().log(Level.INFO, "Gauche{0} ", uneLocation);
        if(! players.canSeeEnemies())
            move.turnVertical(180);
            
        //UnrealId locationEnnemi = getBot().getSenses().getNoiseSource();
            //getBot().getLog().info("Gauche" + locationEnnemi);
              
    }
    
        private void initVars(){
            navigation = getBot().getNavigation();
            players = getBot().getPlayers();
            move = getBot().getMove();
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
