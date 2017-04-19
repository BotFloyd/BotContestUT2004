package com.mycompany.test1;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Rotate;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.util.logging.Level;



public class Defense extends Behavior{
    
    public Defense(Repliquant bot){
        super(bot);
    }
    
    @Override
    public void performed(){      
        
           /*
        Location ennemiLocation = getBot().
            getBot().getLog().info("Gauche" + ennemiLocation);
        Location recul = getBot().getNavigation().getNearestNavPoint(getBot().getBot()).getLocation();
            getBot().getLog().info("Gauche" + recul);
        Location uneLocation = ennemiLocation.sub(recul);
            getBot().getLog().info("Gauche" + uneLocation);
        */
        getBot().getNavigation().stopNavigation();
        getBot().getMove().turnVertical(180);
        
        getBot().getMove().doubleJump();
        //Location uneLocation = getBot().getInfo().getLocation();
        //getBot().getLog().log(Level.INFO, "Gauche{0} ", uneLocation);
        if(!getBot().getPlayers().canSeeEnemies())
            getBot().getMove().turnVertical(180);
            
        //UnrealId locationEnnemi = getBot().getSenses().getNoiseSource();
            //getBot().getLog().info("Gauche" + locationEnnemi);
              
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
