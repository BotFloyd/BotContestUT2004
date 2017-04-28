package com.mycompany.test1.state.concrete;

import com.mycompany.test1.main.Repliquant;
import com.mycompany.test1.settings.WeaponPreferences;
import com.mycompany.test1.state.Behavior;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Items;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPref;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.command.ImprovedShooting;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SetCrouch;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectile;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import java.util.Random;

public class Engage extends Behavior {

    ImprovedShooting shoot;
    Random random;
    AdvancedLocomotion move;
    Location location, alea;
    Senses senses;
    AutoTraceRay right, left, bottomLeft, bottomRight, bottomLeft2, bottomRight2, back, bottomBack;
    WeaponPreferences weaponPref;
    Weaponry weaponry;
    Items items;
    AgentInfo info;

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

    public void setRayBack(AutoTraceRay ray) {
        this.back = ray;
    }

    public void setRayBottomBack(AutoTraceRay ray) {
        this.bottomBack = ray;
    }

    private void initVars() {
        Repliquant bot = getBot();
        shoot = bot.getShoot();
        random = bot.getRandom();
        move = bot.getMove();
        location = bot.getTarget().getLocation();
        weaponPref = bot.getCurrentWeapon();
        senses = bot.getSenses();
        weaponry = bot.getWeaponry();
        items = bot.getItems();
        info = bot.getInfo();
    }

    @Override
    public void performs() {
        initVars();
        double distance;
        boolean choix;
        Repliquant bot = getBot();
        if (location != null) {
            distance = location.getDistance(bot.getBot().getLocation());
            if (distance > 4000 && weaponry.hasAmmoForWeapon((UT2004ItemType.LIGHTNING_GUN))) {
                shoot.changeWeapon(UT2004ItemType.LIGHTNING_GUN);
                navigation.stopNavigation();
                shoot.shoot(location);
                bot.getAct().act(new SetCrouch(true));
            } else if (distance > 700) {
                navigation.navigate(location);
            } else if (distance < 300 && !back.isResult() && !bottomBack.isResult()) {
                moveBackwards();
            } else {
                do {
                    choix = choixAction();
                } while (!choix);
            }
            alea = new Location(location.x + distance / 5000 + random.nextDouble(), location.y + distance / 5000 + random.nextDouble(), location.z + distance / 5000 + random.nextDouble());
            if (bot.getInfo().getCurrentWeaponName().equals("ShockRifle") && (random.nextInt(10) % 3 == 0)) {
                shockRifle();
            } else {
                if ((bot.getCurrentWeapon().isUsingPrimary()
                        && !(weaponry.hasPrimaryWeaponAmmo(bot.getCurrentWeapon().getWeapon())))
                        || (!(bot.getCurrentWeapon().isUsingPrimary())
                        && !(weaponry.hasSecondaryWeaponAmmo(bot.getCurrentWeapon().getWeapon())))) {
                    bot.chooseWeapon();
                    weaponPref = bot.getCurrentWeapon();
                }
                if (weaponPref == null) {
                    noAmmo();
                } else {
                    shoot.shoot(new WeaponPref(weaponPref.getWeapon(), weaponPref.isUsingPrimary()), alea);
                }
            }
        } else {
            shoot.stopShooting();
        }
    }

    private void moveBackwards() {
        Location locationBack = bottomBack.getHitLocation();
        if ((location != null) && !back.isResult() && !bottomBack.isResult()) {
            move.strafeTo(locationBack, location);
        }
    }

    private void shockRifle() {
        IncomingProjectile proj;
        shoot.shootSecondary(alea);
        if (senses.seeIncomingProjectile()) {
            proj = senses.getLastIncomingProjectile();
            if (proj.getType().equals("XWeapons.ShockProjectile")) {
                shoot.shootPrimary(proj.getLocation());
            }
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
        if (!left.isResult() && bottomLeft.isResult() && action < 45) {
            move.strafeLeft(200, location);
            result = true;
            if ((action < 2 || action > 98) && bottomLeft2.isResult()) {
                move.jump();
            }
        } else if (!right.isResult() && bottomRight.isResult() && action > 55) {
            move.strafeRight(200, location);
            result = true;
            if ((action < 2 || action > 98) && bottomRight2.isResult()) {
                move.jump();
            }
        } else if (action >= 45 && action <= 55) {
            result = true;
        }
        bot.getConfig().setSpeedMultiplier(1.0f);
        return (result);
    }

    private void noAmmo() {
        Item weapon = items.getPathNearestSpawnedItem(Category.WEAPON);
        Item ammo = items.getPathNearestSpawnedItem(Category.AMMO);
        Item selectedItem = null;
        if (ammo != null && weapon != null) {
            if (weaponry.hasWeapon(weaponry.getWeaponForAmmo(ammo.getType())) && (getBot().getFwMap().getDistance(info.getNearestNavPoint(), ammo.getNavPoint()) < getBot().getFwMap().getDistance(info.getNearestNavPoint(), weapon.getNavPoint()))) {
                selectedItem = ammo;
            } else {
                selectedItem = weapon;
            }
        } else if (ammo == null && weapon != null) {
            selectedItem = weapon;
        } else if (ammo != null && weapon == null) {
            selectedItem = ammo;
        }
        if (senses.seeIncomingProjectile() || senses.isShot()) {
            getBot().getBody().getCommunication().sendGlobalTextMessage("see projectile");
            shoot.changeWeaponNow(UT2004ItemType.SHIELD_GUN);
            if (senses.getLastIncomingProjectile() != null) {
                navigation.setFocus(senses.getLastIncomingProjectile().getLocation());
            }
            shoot.shootSecondary();
        } else {
            shoot.stopShooting();
            navigation.setFocus(location);
        }
        if (selectedItem != null) {
            navigation.navigate(selectedItem);
        } else {
            shoot.changeWeaponNow(UT2004ItemType.SHIELD_GUN);
            shoot.shoot();
            navigation.navigate(location);
        }
    }
}
