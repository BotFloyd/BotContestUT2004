package com.mycompany.test1;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Raycasting;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPrefs;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.command.ImprovedShooting;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectile;
import java.util.Random;

public class Engage extends Behavior {

    IUT2004Navigation navigation;
    ImprovedShooting shoot;
    Random random;
    AdvancedLocomotion move;
    Location location, alea;
    Raycasting raycasting;
    Senses senses;
    AutoTraceRay right, left, bottomLeft, bottomRight, bottomLeft2, bottomRight2;
    WeaponPrefs weaponPrefs;

    public Engage(Repliquant bot) {
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

    public void initVars() {
        Repliquant bot = getBot();
        navigation = bot.getNavigation();
        shoot = bot.getShoot();
        random = bot.getRandom();
        move = bot.getMove();
        location = bot.getPlayers().getNearestVisibleEnemy().getLocation();
        raycasting = bot.getRaycasting();
        weaponPrefs = bot.getWeaponPrefs();
        senses = bot.getSenses();
    }

    @Override
    public void performs() {
        initVars();
        double distance;
        boolean choix;
        Repliquant bot = getBot();
        IncomingProjectile proj;
        if (location != null) {
            distance = location.getDistance(bot.getBot().getLocation());
            if (distance > 700) {
                navigation.navigate(location);
            } else {
                do {
                    choix = choixAction();
                } while (!choix);
            }
            alea = new Location(location.x + distance / 5000 + random.nextDouble(), location.y + distance / 5000 + random.nextDouble(), location.z + distance / 5000 + random.nextDouble());
            if (bot.getInfo().getCurrentWeaponName().equals("ShockRifle") && (random.nextInt(10) % 3 == 0)) {
                shoot.shootSecondary(alea);
                if (senses.seeIncomingProjectile()) {
                    proj = senses.getLastIncomingProjectile();
                    if (proj.getType().equals("XWeapons.ShockProjectile")) {
                        shoot.shootPrimary(proj.getLocation());
                    }
                }
            } else {
                shoot.shoot(weaponPrefs, alea);
            }
        } else {
            shoot.stopShooting();
        }
    }

    private boolean choixAction() {
        Repliquant bot = getBot();
        int action = random.nextInt(100);
        boolean result = false;
        bot.getConfig().setSpeedMultiplier(0.7f);
        navigation.stopNavigation();
        if (action < 2 || action > 98) {
            move.jump();
        }
        if (!left.isResult() && bottomLeft.isResult() && action > 40) {
            move.strafeLeft(200, location);
            result = true;
            if ((action < 2 || action > 98) && bottomLeft2.isResult()) {
                move.jump();
            }
        } else if (!right.isResult() && bottomRight.isResult() && action < 60) {
            move.strafeRight(200, location);
            result = true;
            if ((action < 2 || action > 98) && bottomRight2.isResult()) {
                move.jump();
            }
        } else if (action >= 40 && action <= 60) {
            result = true;
        }
        bot.getConfig().setSpeedMultiplier(1.0f);
        return (result);
    }
}
