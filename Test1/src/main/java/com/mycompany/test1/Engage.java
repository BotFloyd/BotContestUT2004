package com.mycompany.test1;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Raycasting;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.command.ImprovedShooting;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import java.util.Random;

public class Engage extends Behavior {
    
    IUT2004Navigation navigation;
    ImprovedShooting shoot;
    Random random;
    AdvancedLocomotion move;
    Location location, alea;
    Raycasting raycasting;
    AutoTraceRay right, left, bottomLeft, bottomRight;

    public Engage (Repliquant bot) {
        super(bot);
    }
    
    public void setRayRight(AutoTraceRay ray) {
        this.right = ray;
    }
    
    public void setRayLeft(AutoTraceRay ray) {
        this.left = ray;
    }
     
    public void setRayBottomLeft(AutoTraceRay ray) {
        this.bottomLeft = ray;
    }
    
    public void setRayBottomRight(AutoTraceRay ray) {
        this.bottomRight = ray;
    }
     
    public void initVars () {
        navigation = getBot().getNavigation();
        shoot = getBot().getShoot();
        random = getBot().getRandom();
        move = getBot().getMove();
        location = getBot().getPlayers().getNearestVisibleEnemy().getLocation();
        raycasting = getBot().getRaycasting();
        
    }
    
    @Override
    public void performed () {
        initVars();
        double distance = 0;
        boolean choix = false;
        if (location != null) {
            distance = location.getDistance(getBot().getBot().getLocation());
            if (distance > 700) {
                navigation.navigate(location);
            } else {
                do {
                    choix = choixAction();
                } while (!choix);
            }
            //alea = new Location(location.x + distance / 1000 + random.nextDouble() * 10, location.y + distance / 1000 + random.nextDouble() * 10, location.z + distance / 1000 + random.nextDouble() * 10);
            //shoot.shoot(location);
        }
    }
        
        private boolean choixAction () {
            int action = random.nextInt(99);
            boolean result = false;
            getBot().getConfig().setSpeedMultiplier(0.8f);
            navigation.stopNavigation();
            if (action < 2 || action > 98)
                move.jump();
            else if ((action >= 2 && action <= 5) || (action >= 76 && action <= 79))
                move.doubleJump();
            if (!left.isResult() && bottomLeft.isResult() && action > 40) {
                move.strafeLeft(500, location);
                result = true;
            }
            else if (!right.isResult() && bottomRight.isResult() && action < 60) {
                move.strafeRight(500, location);
                result = true;
            }
            else if (action >= 40 && action <= 60)
                result = true;
            getBot().getConfig().setSpeedMultiplier(1.0f);
            return (result);
        }
 }
