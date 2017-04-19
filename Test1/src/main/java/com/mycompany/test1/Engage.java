package com.mycompany.test1;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.command.ImprovedShooting;
import java.util.Random;

public class Engage extends Behavior {
        
    public Engage (Repliquant bot) {
        super(bot);
    }
    
    public void performed () {
        double distance = 0;
        IUT2004Navigation navigation = getBot().getNavigation();
        ImprovedShooting shoot = getBot().getShoot();
        Random random = getBot().getRandom();
        AdvancedLocomotion move = getBot().getMove();
        Location location = getBot().getPlayers().getNearestVisibleEnemy().getLocation(), alea, strafeFirst, strafeSecond;
        if (location != null) {
            distance = location.getDistance(getBot().getBot().getLocation());
            if (distance > 700) {
                navigation.navigate(location);
            }
            else {
             
                    //strafeFirst = new Location(location.x + 200 + random.nextInt(50), location.y + 200 + random.nextInt(50), location.z);
                    //strafeSecond = new Location(location.x - 200 - random.nextInt(50), location.y - 200 - random.nextInt(50), location.z);
                    //move.strafeAlong(strafeFirst, strafeSecond, location);
                    //strafeFirst = new Location(getBot().getBot().getLocation().x - distance, getBot().getBot().getLocation().y, getBot().getBot().getLocation().z);
                    //move.strafeTo(strafeFirst, location);
                    if (!move.isRunning() && getBot().getRandom().nextBoolean())
                        move.strafeLeft(500, location);
                    else
                        move.strafeRight(500, location);
                
            }
            //bot.getLog().info("Location de base = " + location);
            alea = new Location(location.x + distance / 1000 + random.nextDouble() * 10, location.y + distance / 1000 + random.nextDouble() * 10, location.z + distance / 1000 + random.nextDouble() * 10);
            //bot.getLog().info("Location finale = " + alea);
            //shoot.shoot(location);
        }
   }
}
