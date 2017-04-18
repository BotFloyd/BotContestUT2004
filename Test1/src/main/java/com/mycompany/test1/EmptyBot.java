package com.mycompany.test1;

import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.AddInventory;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerKilled;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;

public class EmptyBot extends UT2004BotModuleController {

    Behavior test;
    Pursue pursue = new Pursue(this);
    MedKit medkit = new MedKit(this);
    Collect collect = new Collect(this);
    Player target;
    
    @Override
    public Initialize getInitializeCommand() {
    	// uncomment to have the bot less skill (make him miss occasionally)
    	Initialize uneVar = super.getInitializeCommand().setLocation(new Location(2784,-2399,-78));
        uneVar.setDesiredSkill(2);
        uneVar.setName("Tamere");
        uneVar.setSkin("HumanMaleA.MercMaleA");
        return uneVar;
    	//return super.getInitializeCommand();
        // 
    }
    
    @Override
    public void logic() throws PogamutException {
        cheatArme();
        if(players.canSeeEnemies()){
            if(navigation.isNavigating()){
                navigation.stopNavigation();
            }
            if(target == null || !target.isVisible()){
                target = players.getNearestVisibleEnemy();
            }
            test = null;
            body.getCommunication().sendGlobalTextMessage("I see you");
        } else if(target != null && weaponry.hasLoadedRangedWeapon()){
            body.getCommunication().sendGlobalTextMessage("I follow you");
            test = pursue;
        } else if(info.getHealth()<70 && !navigation.isNavigating()){
            body.getCommunication().sendGlobalTextMessage("I need health");
            test = medkit;
        } else {
            body.getCommunication().sendGlobalTextMessage("I am roaming");
            test = collect;
        }
        if(test != null){
            test.performed();
        }
        /*
        if (players.canSeePlayers()) {
            Player player1 = players.getNearestVisiblePlayer();
            NavPoint recul = navigation.getNearestNavPoint(bot);
            Location uneLocation = player1.getLocation().sub(recul.getLocation());
            tirer();
            
            boolean uneValeur = random.nextBoolean();
            move.dodgeLeft(info, uneValeur);
        }
        else{
            shoot.stopShooting();
            if(!navigation.isNavigating())
                navigation.navigate(navPoints.getRandomNavPoint());
        }*/
     
        /*
        if (players.canSeePlayers()){
            return;
        }
        else {
            shoot.stopShooting();
            return;
        }
                */
    }

    private void cheatArme(){
               
    	if (!weaponry.hasWeapon(UT2004ItemType.SHOCK_RIFLE)) {
    		log.info("Getting WEAPON");
    		getAct().act(new AddInventory().setType(UT2004ItemType.SHOCK_RIFLE.getName()));
    	}
    	
    	if (!weaponry.hasLoadedWeapon(UT2004ItemType.SHOCK_RIFLE)) {
    		log.info("Getting AMMO");
    		getAct().act(new AddInventory().setType(UT2004ItemType.SHOCK_RIFLE_AMMO.getName()));
    	}
    	
    	if (weaponry.getCurrentWeapon().getType() != UT2004ItemType.SHOCK_RIFLE) {
    		weaponry.changeWeapon(UT2004ItemType.SHOCK_RIFLE);
    	}
    }

    private void tirer() {
        log.info("JE TIIIIIIRE BOOOOM");
        shoot.shoot(players.getNearestVisiblePlayer());
    }
    
    @EventListener(eventClass = PlayerKilled.class)
    public void playerKilled(PlayerKilled event) {
        if (event.getKiller().equals(info.getId())) {
            body.getCommunication().sendGlobalTextMessage("BOOM BITCH");
        }
    }
    
    @Override
    public void botKilled(BotKilled event) {
            body.getCommunication().sendGlobalTextMessage("TEST");
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
        //new UT2004BotRunner(EmptyBot.class, "ImABot").setMain(true).startAgent();
        UT2004BotRunner unBot = new UT2004BotRunner(EmptyBot.class);
        UT2004BotParameters unParamDeBot1 = new UT2004BotParameters();
        UT2004BotParameters unParamDeBot2 = new UT2004BotParameters();
        unBot.setMain(true).startAgents(unParamDeBot1);
    }
    
}