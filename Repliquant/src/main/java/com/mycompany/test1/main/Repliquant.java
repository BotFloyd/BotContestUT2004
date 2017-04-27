package com.mycompany.test1.main;

import com.mycompany.test1.settings.Parameters;
import com.mycompany.test1.settings.WeaponPreferences;
import com.mycompany.test1.state.concrete.Pursue;
import com.mycompany.test1.state.concrete.Engage;
import com.mycompany.test1.state.concrete.Defense;
import com.mycompany.test1.state.concrete.Collect;
import com.mycompany.test1.state.Behavior;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.AddInventory;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.RemoveRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerKilled;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3d;

public class Repliquant extends UT2004BotModuleController {

    Behavior now;
    Pursue pursue = new Pursue(this);
    Collect collect = new Collect(this);
    Engage engage = new Engage(this);
    Defense defense = new Defense(this);
    Player target;
    List<WeaponPreferences> wPrefs = new ArrayList<WeaponPreferences>();
    WeaponPreferences currentWeapon;
    private UT2004PathAutoFixer autoFixer;
    int mort = 0, kill = 0, risque;

    IWorldObjectEventListener<Player, WorldObjectAppearedEvent<Player>> playerAppeared = new IWorldObjectEventListener<Player, WorldObjectAppearedEvent<Player>>() {
        @Override
        public void notify(WorldObjectAppearedEvent<Player> event) {
            chooseWeapon();
        }
    };

    @Override
    public void botInitialized(GameInfo info, ConfigChange currentConfig, InitedMessage init) {
        getBot().getAct().act(new RemoveRay("All"));
        raycasting.createRay("LEFT90", new Vector3d(0, -1, 0), 175, true, false, false);
        raycasting.createRay("RIGHT90", new Vector3d(0, 1, 0), 175, true, false, false);
        raycasting.createRay("BOTTOMLEFT45", new Vector3d(0, -1, -0.4), 250, true, false, false);
        raycasting.createRay("BOTTOMRIGHT45", new Vector3d(0, 1, -0.4), 250, true, false, false);
        raycasting.createRay("BOTTOMLEFT", new Vector3d(0, -1, -0.1), 500, true, false, false);
        raycasting.createRay("BOTTOMRIGHT", new Vector3d(0, 1, -0.1), 500, true, false, false);
        raycasting.createRay("BOTTOMBACK", new Vector3d(-1, 0, -0.3), 250, false, false, false);
        raycasting.createRay("BACK", new Vector3d(-1, 0, 0), 300, true, false, false);
        raycasting.getAllRaysInitialized().addListener(new FlagListener<Boolean>() {
            @Override
            public void flagChanged(Boolean changedValue) {
                engage.setRayLeft(raycasting.getRay("LEFT90"));
                engage.setRayRight(raycasting.getRay("RIGHT90"));
                engage.setRayBottomLeft(raycasting.getRay("BOTTOMLEFT45"));
                engage.setRayBottomRight(raycasting.getRay("BOTTOMRIGHT45"));
                engage.setRayBottomLeft2(raycasting.getRay("BOTTOMLEFT"));
                engage.setRayBottomRight2(raycasting.getRay("BOTTOMRIGHT"));
                engage.setRayBottomBack(raycasting.getRay("BOTTOMBACK"));
                engage.setRayBack(raycasting.getRay("BACK"));
            }
        });
        raycasting.endRayInitSequence();
        getAct().act(new Configuration().setDrawTraceLines(true).setAutoTrace(true));
    }

    @Override
    public Initialize getInitializeCommand() {
        return (new Parameters().setParams(bot.getParams()).initialize());
    }

    @Override
    public void prepareBot(UT2004Bot bot) {

        autoFixer = new UT2004PathAutoFixer(bot, navigation.getPathExecutor(), fwMap, aStar, navBuilder);
        wPrefs.add(new WeaponPreferences(UT2004ItemType.REDEEMER, 6, 0.9, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.ROCKET_LAUNCHER, 7, 0.6, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.ROCKET_LAUNCHER, 7, 0.55, false));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.SNIPER_RIFLE, 8, 0.7, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.FLAK_CANNON, 7, 0.7, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.FLAK_CANNON, 1, 0.2, false));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.MINIGUN, 6, 0.6, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.MINIGUN, 6, 0.55, false));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.LIGHTNING_GUN, 5, 0.5, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.SHOCK_RIFLE, 6, 0.55, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.SHOCK_RIFLE, 6, 0.55, false));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.LINK_GUN, 3, 0.75, false));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.LINK_GUN, 3, 0.75, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.BIO_RIFLE, 2, 0.3, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.BIO_RIFLE, 2, 0.3, false));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.ASSAULT_RIFLE, 1, 0.2, false));
        currentWeapon = new WeaponPreferences(UT2004ItemType.ASSAULT_RIFLE, 1, 0.2, true);
        wPrefs.add(currentWeapon);
        getWorldView().addObjectListener(Player.class, WorldObjectAppearedEvent.class, playerAppeared);
        // FIRST we DEFINE GENERAL WEAPON PREFERENCES
        /*weaponPrefs.addGeneralPref(UT2004ItemType.ROCKET_LAUNCHER, false);
         weaponPrefs.addGeneralPref(UT2004ItemType.ROCKET_LAUNCHER, true);
         weaponPrefs.addGeneralPref(UT2004ItemType.SNIPER_RIFLE, true);
         weaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, true);
         weaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, false);
         weaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, true);
         weaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, false);
         weaponPrefs.addGeneralPref(UT2004ItemType.LIGHTNING_GUN, true);
         weaponPrefs.addGeneralPref(UT2004ItemType.SHOCK_RIFLE, true);
         weaponPrefs.addGeneralPref(UT2004ItemType.SHOCK_RIFLE, false);
         weaponPrefs.addGeneralPref(UT2004ItemType.LINK_GUN, false);
         weaponPrefs.addGeneralPref(UT2004ItemType.LINK_GUN, true);
         weaponPrefs.addGeneralPref(UT2004ItemType.BIO_RIFLE, true);
         weaponPrefs.addGeneralPref(UT2004ItemType.ASSAULT_RIFLE, true);
         weaponPrefs.addGeneralPref(UT2004ItemType.ASSAULT_RIFLE, false);

         // AND THEN RANGED
         weaponPrefs.newPrefsRange(600)
         .add(UT2004ItemType.LINK_GUN, false)
         .add(UT2004ItemType.BIO_RIFLE, false)
         .add(UT2004ItemType.FLAK_CANNON, true);

         weaponPrefs.newPrefsRange(1000)
         .add(UT2004ItemType.FLAK_CANNON, true)
         .add(UT2004ItemType.MINIGUN, false)
         .add(UT2004ItemType.LINK_GUN, true)
         .add(UT2004ItemType.ROCKET_LAUNCHER, true)
         .add(UT2004ItemType.ASSAULT_RIFLE, true);

         weaponPrefs.newPrefsRange(1500)
         .add(UT2004ItemType.ROCKET_LAUNCHER, true)
         .add(UT2004ItemType.MINIGUN, false)
         .add(UT2004ItemType.SHOCK_RIFLE, false);

         weaponPrefs.newPrefsRange(4000)
         .add(UT2004ItemType.SHOCK_RIFLE, true)
         .add(UT2004ItemType.ROCKET_LAUNCHER, false);

         weaponPrefs.newPrefsRange(100000)
         .add(UT2004ItemType.SNIPER_RIFLE, true)
         .add(UT2004ItemType.LIGHTNING_GUN, true);
         shoot.setChangeWeaponCooldown(2000);*/
    }

    @Override
    public void logic() throws PogamutException {
        //cheatArme();
        if (players.canSeeEnemies()) {
            if (target == null || !target.isVisible()) {
                target = players.getNearestVisibleEnemy();
            }
            bot.getBotName().setInfo("ENGAGE");
            now = engage;
        } else if (target == null && (senses.seeIncomingProjectile() || senses.isBeingDamaged())) {
            bot.getBotName().setInfo("DEFENSE");
            now = defense;
        } else if (target != null && weaponry.hasLoadedRangedWeapon()) {
            bot.getBotName().setInfo("PURSUE");
            shoot.stopShooting();
            now = pursue;
        } else {
            bot.getBotName().setInfo("COLLECT");
            shoot.stopShooting();
            now = collect;
        }
        now.performs();
    }

    private void cheatArme() {
        UT2004ItemType arme = UT2004ItemType.SHOCK_RIFLE;
        UT2004ItemType munition = UT2004ItemType.SHOCK_RIFLE_AMMO;

        if (!weaponry.hasWeapon(arme)) {
            log.info("Getting WEAPON");
            getAct().act(new AddInventory().setType(arme.getName()));
        }

        if (!weaponry.hasLoadedWeapon(arme)) {
            log.info("Getting AMMO");
            getAct().act(new AddInventory().setType(munition.getName()));
        }

        if (weaponry.getCurrentWeapon().getType() != arme) {
            weaponry.changeWeapon(arme);
        }
    }

    @EventListener(eventClass = PlayerKilled.class)
    public void playerKilled(PlayerKilled event) {
        body.getCommunication().sendGlobalTextMessage("mort : " + mort + " kill : " + kill + " courage : " + risque);
        if (event.getKiller().equals(info.getId())) {
            kill++;
            risque = mort - kill;
            shoot.stopShooting();
            currentWeapon.upNbKills();
            currentWeapon.updateProbability();
            target = null;
        }
    }

    @Override
    public void botKilled(BotKilled event) {
        mort++;
        risque = mort - kill;
        currentWeapon.upNbDeaths();
        currentWeapon.updateProbability();
        for (int i = 0; i < wPrefs.size(); i++) {
            WeaponPreferences prefWeap = wPrefs.get(i);
            if (prefWeap.getWeapon().equals(UT2004ItemType.ASSAULT_RIFLE) && prefWeap.isUsingPrimary() == true) {
                currentWeapon = prefWeap;
            }
        }
        body.getCommunication().sendGlobalTextMessage("mort : " + mort + " kill : " + kill + " courage : " + risque);
    }

    public void chooseWeapon() {
        double epsilon = 1.0;
        double x = random.nextDouble();
        double highestProbability = 0.0;
        WeaponPreferences temp = null;
        List<WeaponPreferences> tempList = new ArrayList<WeaponPreferences>();
        for (int i = 0; i < wPrefs.size(); i++) {
            if (wPrefs.get(i).isUsingPrimary()
                    && weaponry.hasPrimaryLoadedWeapon(wPrefs.get(i).getWeapon())) {
                log.info("Add : " + wPrefs.get(i).getWeapon().toString());
                tempList.add(wPrefs.get(i));
            } else if (!(wPrefs.get(i).isUsingPrimary())
                    && weaponry.hasSecondaryLoadedWeapon(wPrefs.get(i).getWeapon())) {
                log.info("Add : " + wPrefs.get(i).getWeapon().toString());
                tempList.add(wPrefs.get(i));
            }
        }
        if (tempList.isEmpty()) {
            currentWeapon = null;
            return;
        }
        if (x < epsilon) {
            log.info("Arme au hasard");
            currentWeapon = tempList.get(random.nextInt(tempList.size()));
        } else {
            log.info("Arme avec la plus grande probabilité");
            for (int i = 0; i < tempList.size(); i++) {
                if (highestProbability < tempList.get(i).getProbability()) {
                    temp = tempList.get(i);
                    highestProbability = tempList.get(i).getProbability();
                }
            }
            currentWeapon = temp;
        }
    }

    @EventListener(eventClass = BotDamaged.class)
    public void botDamaged(BotDamaged event) {
    }

    /*@ObjectClassEventListener(eventClass = WorldObjectAppearedEvent.class, objectClass = Player.class)
     protected void playerAppeared(WorldObjectAppearedEvent<Player> event) {
     chooseWeapon();
     }*/
    public Player getTarget() {
        return target;
    }

    public WeaponPreferences getCurrentWeapon() {
        return this.currentWeapon;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public static void main(String args[]) throws PogamutException {
        new UT2004BotRunner<UT2004Bot, Parameters>(Repliquant.class).setMain(true).startAgents(
                new Parameters().setName("Bot1").setBotSkin("HumanMaleA.MercMaleC").setSkillLevel(7),
                new Parameters().setName("Bot2").setBotSkin("HumanFemaleA.MercFemaleB").setSkillLevel(7));
    }

}
