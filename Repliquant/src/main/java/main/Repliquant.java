package main;

import settings.Initialization;
import settings.Parameters;
import settings.WeaponPreferences;
import state.concrete.Pursue;
import state.concrete.Engage;
import state.concrete.Defense;
import state.concrete.Collect;
import state.Behavior;
import state.concrete.Dodge;
import state.concrete.Travel;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.AddInventory;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerKilled;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import java.util.ArrayList;
import java.util.List;

public class Repliquant extends UT2004BotModuleController {

    private Behavior now;
    private Pursue pursue;
    private Collect collect;
    private Engage engage;
    private Defense defense;
    private Travel travel;
    private Dodge dodge;
    private Initialization initialization;
    private Player target;
    private final List<WeaponPreferences> wPrefs = new ArrayList<WeaponPreferences>();
    private WeaponPreferences currentWeapon;
    private UT2004PathAutoFixer autoFixer;
    private int mort = 0, kill = 0, risque;
    private Item nearbyObj;
    private TabooSet<Item> tabooItems;

    @Override
    public void botInitialized(GameInfo info, ConfigChange currentConfig, InitedMessage init) {
        pursue = new Pursue(this);
        collect = new Collect(this);
        engage = new Engage(this);
        defense = new Defense(this);
        travel = new Travel(this);
        dodge = new Dodge(this);
        initialization = new Initialization(this);
        tabooItems = new TabooSet(bot);
        initialization.raycastingInit();
        initialization.navigationInit();
        initialization.wPrefsInit(wPrefs);
        autoFixer = new UT2004PathAutoFixer(bot, navigation.getPathExecutor(), fwMap, aStar, navBuilder);
    }

    @Override
    public Initialize getInitializeCommand() {
        return (new Parameters().setParams(bot.getParams()).initialize());
    }
    
    @Override
    public void mapInfoObtained() {
    	navMeshModule.setReloadNavMesh(true);	
    }

    @Override
    public void prepareBot(UT2004Bot bot) {
        currentWeapon = new WeaponPreferences(UT2004ItemType.ASSAULT_RIFLE, 1, 0.2, true);
        wPrefs.add(currentWeapon);
        getWorldView().addObjectListener(Player.class, WorldObjectAppearedEvent.class, playerAppeared);
    }
    
    @Override
    public void logic() throws PogamutException {
        if (nmNav.isAvailable()) {
            if (!initialization.drawNavMesh()) return;
            if (!initialization.drawOffMeshLinks()) return;
        }
        nearbyObj = items.getNearestVisibleItem();
        if (nearbyObj != null && tabooItems.isTaboo(nearbyObj))
            nearbyObj = null;
        if (players.canSeeEnemies()) {
            if (target == null || !target.isVisible()) {
                target = players.getNearestVisibleEnemy();
            }
            bot.getBotName().setInfo("ENGAGE");
            now = engage;
        } else if (senses.isBeingDamaged()) {
            bot.getBotName().setInfo("DEFENSE");
            now = defense;
        } else if (target == null && senses.seeIncomingProjectile()){
            bot.getBotName().setInfo("DODGE");
            now = dodge;
        } else if (nearbyObj != null && nearbyObj.getLocation().getDistance(bot.getLocation()) < 300) {
            bot.getBotName().setInfo("TRAVEL");
            now = travel;
            tabooItems.add(nearbyObj, items.getItemRespawnTime(nearbyObj));
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
        if (!weaponry.hasWeapon(arme))
            getAct().act(new AddInventory().setType(arme.getName()));
        if (!weaponry.hasLoadedWeapon(arme))
            getAct().act(new AddInventory().setType(munition.getName()));
        if (weaponry.getCurrentWeapon().getType() != arme)
            weaponry.changeWeapon(arme);
    }
    
     IWorldObjectEventListener<Player, WorldObjectAppearedEvent<Player>> playerAppeared = new IWorldObjectEventListener<Player, WorldObjectAppearedEvent<Player>>() {
        @Override
        public void notify(WorldObjectAppearedEvent<Player> event) {
            chooseWeapon();
        }
    };

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
            bot.getBotName().setInfo("COLLECT");
            now = collect;
        }
    }
    
    @EventListener(eventClass = BotDamaged.class)
    public void botDamaged(BotDamaged event) {
        
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
                tempList.add(wPrefs.get(i));
            } else if (!(wPrefs.get(i).isUsingPrimary())
                    && weaponry.hasSecondaryLoadedWeapon(wPrefs.get(i).getWeapon())) {
                tempList.add(wPrefs.get(i));
            }
        }
        if (tempList.isEmpty()) {
            currentWeapon = null;
            return;
        }
        if (x < epsilon) {
            currentWeapon = tempList.get(random.nextInt(tempList.size()));
        } else {
            for (int i = 0; i < tempList.size(); i++) {
                if (highestProbability < tempList.get(i).getProbability()) {
                    temp = tempList.get(i);
                    highestProbability = tempList.get(i).getProbability();
                }
            }
            currentWeapon = temp;
        }
    }

    public Player getTarget() {
        return target;
    }
    
    public Engage getEngage() {
        return engage;
    }
    
    public TabooSet<Item> getTabooItems () {
        return tabooItems;
    }

    public WeaponPreferences getCurrentWeapon() {
        return currentWeapon;
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