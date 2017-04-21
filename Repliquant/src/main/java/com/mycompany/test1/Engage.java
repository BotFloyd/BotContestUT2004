package com.mycompany.test1;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Raycasting;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPrefs;
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
    AutoTraceRay right, left, bottomLeft, bottomRight, bottomLeft2, bottomRight2;
    WeaponPrefs weaponPrefs;

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
    
    public void setRayBottomLeft2(AutoTraceRay ray) {
        this.bottomLeft2 = ray;
    }
    
    public void setRayBottomRight2(AutoTraceRay ray) {
        this.bottomRight2 = ray;
    }
     
    public void initVars () {
        navigation = getBot().getNavigation();
        shoot = getBot().getShoot();
        random = getBot().getRandom();
        move = getBot().getMove();
        location = getBot().getPlayers().getNearestVisibleEnemy().getLocation();
        raycasting = getBot().getRaycasting();
        weaponPrefs = getBot().getWeaponPrefs();
        
    }
    
    @Override
    public void performs () {
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
            shoot.shoot(weaponPrefs, location);
        }
    }
        
        private boolean choixAction () {
            int action = random.nextInt(99);
            boolean result = false;
            getBot().getConfig().setSpeedMultiplier(0.8f);
            navigation.stopNavigation();      
            getBot().getLog().info("test left : " + bottomLeft.isResult());
            getBot().getLog().info("test left 2 : " + !left.isResult());
            getBot().getLog().info("test right : " + bottomRight.isResult());
            getBot().getLog().info("test right 2 : " + !right.isResult());
            if (action < 2 || action > 98)
                move.jump();
            if (!left.isResult() && bottomLeft.isResult() && action > 40) {
                move.strafeLeft(200, location);
                result = true;
                if ((action < 2 || action > 98) && bottomLeft2.isResult())
                    move.jump();
            }
            else if (!right.isResult() && bottomRight.isResult() && action < 60) {
                move.strafeRight(200, location);
                result = true;
                if ((action < 2 || action > 98) && bottomRight2.isResult())
                    move.jump();
            }
            else if (action >= 40 && action <= 60)
                result = true;

            getBot().getConfig().setSpeedMultiplier(1.0f);
            return (result);
        }
 }
