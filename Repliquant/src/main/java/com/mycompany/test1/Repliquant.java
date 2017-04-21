package com.mycompany.test1;

import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
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
import javax.vecmath.Vector3d;

public class Repliquant extends UT2004BotModuleController {

    Behavior now, before = null;
    Pursue pursue = new Pursue(this);
    MedKit medkit = new MedKit(this);
    Collect collect = new Collect(this);
    Engage engage = new Engage(this);
    Defense defense = new Defense(this);
    Player target;

    @Override
    public void botInitialized(GameInfo info, ConfigChange currentConfig, InitedMessage init) {
        getBot().getAct().act(new RemoveRay("All"));
        raycasting.createRay("LEFT90",  new Vector3d(0, -1, 0), 175, true, false, false);
        raycasting.createRay("RIGHT90", new Vector3d(0, 1, 0), 175, true, false, false);
        raycasting.createRay("BOTTOMLEFT45",  new Vector3d(0, -1, -0.4), 250, true, false, false);
        raycasting.createRay("BOTTOMRIGHT45", new Vector3d(0, 1, -0.4), 250, true, false, false);
        raycasting.createRay("BOTTOMLEFT",  new Vector3d(0, -1, -0.1), 500, true, false, false);
        raycasting.createRay("BOTTOMRIGHT", new Vector3d(0, 1, -0.1), 500, true, false, false);
        raycasting.getAllRaysInitialized().addListener(new FlagListener<Boolean>() {
            public void flagChanged(Boolean changedValue) {
                engage.setRayLeft(raycasting.getRay("LEFT90"));
                engage.setRayRight(raycasting.getRay("RIGHT90"));
                engage.setRayBottomLeft(raycasting.getRay("BOTTOMLEFT45"));
                engage.setRayBottomRight(raycasting.getRay("BOTTOMRIGHT45"));
                engage.setRayBottomLeft2(raycasting.getRay("BOTTOMLEFT"));
                engage.setRayBottomRight2(raycasting.getRay("BOTTOMRIGHT"));
            }
        });
        raycasting.endRayInitSequence();
        getAct().act(new Configuration().setDrawTraceLines(true).setAutoTrace(true));
    }
 
    @Override
    public Initialize getInitializeCommand() {
    	// uncomment to have the bot less skill (make him miss occasionally)
    	Initialize uneVar = super.getInitializeCommand();
        uneVar.setDesiredSkill(2);
        uneVar.setName("Tamere");
        uneVar.setSkin("HumanMaleA.MercMaleA");
        return uneVar;
    	//return super.getInitializeCommand();
        // 
    }
    
    @Override
    public void prepareBot(UT2004Bot bot) {
        
        // FIRST we DEFINE GENERAL WEAPON PREFERENCES
        weaponPrefs.addGeneralPref(UT2004ItemType.ROCKET_LAUNCHER, false);
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
		
        // AND THEN RANGED
        weaponPrefs.newPrefsRange(80)
                .add(UT2004ItemType.SHIELD_GUN, true);
        
        weaponPrefs.newPrefsRange(600)
                .add(UT2004ItemType.LINK_GUN, false)
                .add(UT2004ItemType.BIO_RIFLE, false)
                .add(UT2004ItemType.ASSAULT_RIFLE, false);

        weaponPrefs.newPrefsRange(1000)
                .add(UT2004ItemType.ROCKET_LAUNCHER, true)
                .add(UT2004ItemType.FLAK_CANNON, false)
                .add(UT2004ItemType.MINIGUN, false)
                .add(UT2004ItemType.LINK_GUN, true)
                .add(UT2004ItemType.ASSAULT_RIFLE, true); 
        
        weaponPrefs.newPrefsRange(1500)
                .add(UT2004ItemType.MINIGUN, false)
                .add(UT2004ItemType.SHOCK_RIFLE, false);

        weaponPrefs.newPrefsRange(4000)
                .add(UT2004ItemType.SHOCK_RIFLE, true)
                .add(UT2004ItemType.ROCKET_LAUNCHER, false);

        weaponPrefs.newPrefsRange(100000)
                .add(UT2004ItemType.SNIPER_RIFLE, true)
                .add(UT2004ItemType.LIGHTNING_GUN, true);
    }

    @Override
    public void logic() throws PogamutException {
        cheatArme();
        if(players.canSeeEnemies()){
            if(target == null || !target.isVisible()){
                target = players.getNearestVisibleEnemy();
            }
            now = engage;
        } else if (target == null && senses.isBeingDamaged()){
            body.getCommunication().sendGlobalTextMessage("I'm being damaged");
            now = defense;
        } else if(target != null && weaponry.hasLoadedRangedWeapon()){
            body.getCommunication().sendGlobalTextMessage("I follow you");
            now = pursue;
        } else if(info.getHealth()<70 && !navigation.isNavigating()){
            body.getCommunication().sendGlobalTextMessage("I need health");
            now = medkit;
        } else {
            body.getCommunication().sendGlobalTextMessage("I am roaming");
            now = collect;
        }
        now.performs();
    }
    
    private void cheatArme(){
        UT2004ItemType arme = UT2004ItemType.LIGHTNING_GUN;
        UT2004ItemType munition = UT2004ItemType.LIGHTNING_GUN_AMMO;
        
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
        if (event.getKiller().equals(info.getId())) {
            shoot.stopShooting();
        }
    }

    @Override
    public void botKilled(BotKilled event) {
    }

    @EventListener(eventClass = BotDamaged.class)
    public void botDamaged(BotDamaged event) {
        if (event.isDirectDamage()) {
        }
    }

    public Player getTarget(){
        return target;
    }

    public void setTarget(Player target){
        this.target = target;
    }

    
    public static void main(String args[]) throws PogamutException {
        UT2004BotRunner unBot = new UT2004BotRunner(Repliquant.class);
        UT2004BotParameters unParamDeBot1 = new UT2004BotParameters();
        UT2004BotParameters unParamDeBot2 = new UT2004BotParameters();
        unBot.setMain(true).startAgents(unParamDeBot1);
    }

}