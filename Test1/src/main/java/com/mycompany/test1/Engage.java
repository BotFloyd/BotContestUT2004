package com.mycompany.test1;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.command.ImprovedShooting;
import java.util.Random;

public class Engage extends Behavior {
    
    private Repliquant bot;
    
    public Engage (Repliquant bot) {
        super(bot);
    }
    
    public void performed () {
        double distance = 0;
        IUT2004Navigation navigation = bot.getNavigation();
        ImprovedShooting shoot = bot.getShoot();
        Random random = bot.getRandom();
        AdvancedLocomotion move = bot.getMove();
        Location location = bot.getPlayers().getNearestVisibleEnemy().getLocation(), alea, strafeFirst, strafeSecond;
        if (location != null) {
            distance = location.getDistance(bot.getBot().getLocation());
            if (distance > 700) {
                navigation.navigate(location);
            }
            else {
             
                    //strafeFirst = new Location(location.x + 200 + random.nextInt(50), location.y + 200 + random.nextInt(50), location.z);
                    //strafeSecond = new Location(location.x - 200 - random.nextInt(50), location.y - 200 - random.nextInt(50), location.z);
                    //move.strafeAlong(strafeFirst, strafeSecond, location);
                    strafeFirst = new Location(bot.getBot().getLocation().x - distance, bot.getBot().getLocation().y, bot.getBot().getLocation().z);
                    move.strafeTo(strafeFirst, location);
                    /*if (bot.getRandom().nextBoolean())
                        move.strafeLeft(500, location);
                    else
                        move.strafeRight(500, location);*/
                
            }
            //bot.getLog().info("Location de base = " + location);
            alea = new Location(location.x + distance / 1000 + random.nextDouble() * 10, location.y + distance / 1000 + random.nextDouble() * 10, location.z + distance / 1000 + random.nextDouble() * 10);
            //bot.getLog().info("Location finale = " + alea);
            //shoot.shoot(location);
        }
   }
}
