package state.concrete;

import main.Repliquant;
import settings.WeaponPreferences;
import state.Behavior;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Items;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPref;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
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
    IUT2004Navigation navigation;

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

    private void initVars(Repliquant unBot) {
        shoot = unBot.getShoot();
        random = unBot.getRandom();
        move = unBot.getMove();
        location = unBot.getTarget().getLocation();
        weaponPref = unBot.getCurrentWeapon();
        senses = unBot.getSenses();
        weaponry = unBot.getWeaponry();
        items = unBot.getItems();
        info = unBot.getInfo();
        if (unBot.getNMNav().isAvailable()) {
            navigation = unBot.getNMNav();
        } else {
            navigation = unBot.getNavigation();
        }
    }

    @Override
    public void performs(Repliquant unBot) {
        initVars(unBot);
        double distance;
        boolean choix;
        if (location != null) {
            distance = location.getDistance(unBot.getBot().getLocation());
            if (distance > 1500 && (weaponry.hasAmmoForWeapon(UT2004ItemType.LIGHTNING_GUN) || weaponry.hasAmmoForWeapon(UT2004ItemType.REDEEMER) || weaponry.hasAmmoForWeapon(UT2004ItemType.ION_PAINTER))) {
                shootFarAway(unBot);
            } else if (distance > 700) {
                if (!navigation.isNavigating()) {
                    navigation.navigate(location);
                }
                unBot.getLog().severe("location = " + location);
            } else if (distance < 300 && !back.isResult() && !bottomBack.isResult()) {
                moveBackwards();
            } else {
                do {
                    choix = choixAction(unBot);
                } while (!choix);
            }
            alea = new Location(location.x + distance / 5000 + random.nextDouble(), location.y + distance / 5000 + random.nextDouble(), location.z + distance / 5000 + random.nextDouble());
            if (unBot.getCurrentWeapon() != null) {
                if ((unBot.getCurrentWeapon().isUsingPrimary()
                        && !(weaponry.hasPrimaryWeaponAmmo(unBot.getCurrentWeapon().getWeapon())))
                        || (!(unBot.getCurrentWeapon().isUsingPrimary())
                        && !(weaponry.hasSecondaryWeaponAmmo(unBot.getCurrentWeapon().getWeapon())))) {
                    unBot.chooseWeapon();
                    weaponPref = unBot.getCurrentWeapon();
                }
            } else {
                weaponPref = null;
            }
            if (weaponPref == null) {
                noAmmo(unBot);
            } else {
                UT2004ItemType weapon = weaponPref.getWeapon();
                if (weapon.equals(UT2004ItemType.SHOCK_RIFLE) && random.nextBoolean()) {
                    shockRifle();
                } else if (weapon.equals(UT2004ItemType.ROCKET_LAUNCHER) || weapon.equals(UT2004ItemType.BIO_RIFLE)) {
                    alea = new Location(alea.x, alea.y, location.z - 80);
                    shoot.shoot(new WeaponPref(weapon, weaponPref.isUsingPrimary()), alea);
                } else {
                    shoot.shoot(new WeaponPref(weapon, weaponPref.isUsingPrimary()), alea);
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

    private boolean choixAction(Repliquant unBot) {
        int action = random.nextInt(100);
        boolean result = false;
        unBot.getConfig().setSpeedMultiplier(0.7f);
        navigation.stopNavigation();
        if (action < 2 || action > 98) {
            move.jump();
        }
        if (!left.isResult() && bottomLeft.isResult() && action < 40) {
            move.strafeLeft(200 + random.nextInt(100), location);
            result = true;
            if (action < 2 && bottomLeft2.isResult()) {
                move.jump();
            }
        } else if (!right.isResult() && bottomRight.isResult() && action > 60) {
            move.strafeRight(200 + random.nextInt(100), location);
            result = true;
            if (action > 98 && bottomRight2.isResult()) {
                move.jump();
            }
        } else if (!back.isResult() && !bottomBack.isResult() && action >= 40 && action < 50) {
            result = true;
            moveBackwards();
        } else if (action >= 50 && action <= 60) {
            result = true;
        }
        unBot.getConfig().setSpeedMultiplier(1.0f);
        return (result);
    }

    private void noAmmo(Repliquant unBot) {
        Item weapon = items.getPathNearestSpawnedItem(Category.WEAPON);
        Item ammo = items.getPathNearestSpawnedItem(Category.AMMO);
        Item selectedItem = null;
        if (ammo != null && weapon != null) {
            if (weaponry.hasWeapon(weaponry.getWeaponForAmmo(ammo.getType())) && (unBot.getFwMap().getDistance(info.getNearestNavPoint(), ammo.getNavPoint()) < unBot.getFwMap().getDistance(info.getNearestNavPoint(), weapon.getNavPoint()))) {
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
            unBot.getBody().getCommunication().sendGlobalTextMessage("see projectile");
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

    private void shootFarAway(Repliquant unBot) {
        if (weaponry.hasAmmoForWeapon(UT2004ItemType.REDEEMER)) {
            shoot.changeWeaponNow(UT2004ItemType.REDEEMER);
            navigation.stopNavigation();
            shoot.shoot(new Location(location.x, location.y, location.z - 80));
        } else if (weaponry.hasAmmoForWeapon(UT2004ItemType.ION_PAINTER)) {
            shoot.changeWeaponNow(UT2004ItemType.ION_PAINTER);
            navigation.stopNavigation();
            shoot.shoot(new Location(location.x, location.y, location.z - 80));
        } else if (weaponry.hasAmmoForWeapon(UT2004ItemType.LIGHTNING_GUN)) {
            shoot.changeWeaponNow(UT2004ItemType.LIGHTNING_GUN);
            navigation.stopNavigation();
            unBot.getAct().act(new SetCrouch(true));
            shoot.shoot(location);
        }
    }
}
