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
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
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
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerScore;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Repliquant extends UT2004BotModuleController {

    private Behavior now;
    private final Pursue pursue = new Pursue();
    private final Collect collect = new Collect();
    private final Engage engage = new Engage();
    private final Defense defense = new Defense();
    private final Travel travel = new Travel();
    private final Dodge dodge = new Dodge();
    private final Initialization initialization = new Initialization();
    private IUT2004Navigation navToUse;
    private Player target;
    private final List<WeaponPreferences> wPrefs = new ArrayList<WeaponPreferences>();
    private WeaponPreferences currentWeapon;
    private double risque = 0;
    private TabooSet<Item> tabooItems;
    private boolean canPursue = false;

    @Override
    public void botInitialized(GameInfo info, ConfigChange currentConfig, InitedMessage init) {
        tabooItems = new TabooSet(bot);
        initialization.raycastingInit(this);
        initialization.navigationInit(this);
        initialization.wPrefsInit(wPrefs);
        navToUse = navigation;
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
            if (!initialization.drawNavMesh(this)) return;
            if (!initialization.drawOffMeshLinks(this)) return;
            navToUse = nmNav;
        }
        Item nearbyObj = items.getNearestVisibleItem();
        if (nearbyObj != null && tabooItems.isTaboo(nearbyObj)) {
            nearbyObj = null;
        }
        if (players.canSeeEnemies()) {
            if (target == null || !target.isVisible()) {
                target = players.getNearestVisibleEnemy();
            }
            bot.getBotName().setInfo("ENGAGE");
            now = engage;
            canPursue = true;
        } else if (senses.isBeingDamaged()) {
            bot.getBotName().setInfo("DEFENSE");
            now = defense;
        } else if (target == null && senses.seeIncomingProjectile()) {
            bot.getBotName().setInfo("DODGE");
            now = dodge;
        } else if (target != null && weaponry.hasLoadedRangedWeapon() && (risque >= 0 || canPursue)) {
            bot.getBotName().setInfo("PURSUE");
            shoot.stopShooting();
            canPursue = false;
            now = pursue;
        } else if (nearbyObj != null && nearbyObj.getLocation().getDistance(bot.getLocation()) < 300) {
            bot.getBotName().setInfo("TRAVEL");
            shoot.stopShooting();
            now = travel;
            tabooItems.add(nearbyObj, (random.nextDouble() * 0.5 + 1) * items.getItemRespawnTime(nearbyObj));
        } else {
            bot.getBotName().setInfo("COLLECT");
            shoot.stopShooting();
            now = collect;
        }
        now.performs(this);
    }

    private void cheatArme() {
        UT2004ItemType arme = UT2004ItemType.SHOCK_RIFLE;
        UT2004ItemType munition = UT2004ItemType.SHOCK_RIFLE_AMMO;
        if (!weaponry.hasWeapon(arme)) {
            getAct().act(new AddInventory().setType(arme.getName()));
        }
        if (!weaponry.hasLoadedWeapon(arme)) {
            getAct().act(new AddInventory().setType(munition.getName()));
        }
        if (weaponry.getCurrentWeapon().getType() != arme) {
            weaponry.changeWeapon(arme);
        }
    }

    IWorldObjectEventListener<Player, WorldObjectAppearedEvent<Player>> playerAppeared = new IWorldObjectEventListener<Player, WorldObjectAppearedEvent<Player>>() {
        @Override
        public void notify(WorldObjectAppearedEvent<Player> event) {
            chooseWeapon();
        }
    };

    @EventListener(eventClass = PlayerKilled.class)
    public void playerKilled(PlayerKilled event) {
        if (event.getKiller().equals(info.getId())) {
            updateRisque();
            shoot.stopShooting();
            if (currentWeapon != null) {
                currentWeapon.upNbKills();
                currentWeapon.updateProbability();
            }
            target = null;
            bot.getBotName().setInfo("COLLECT");
            now = collect;
            canPursue = false;
        }
    }

    @EventListener(eventClass = BotDamaged.class)
    public void botDamaged(BotDamaged event) {

    }

    @Override
    public void botKilled(BotKilled event) {
        updateRisque();
        if (currentWeapon != null && !event.isCausedByWorld()) {
            currentWeapon.upNbDeaths();
            currentWeapon.updateProbability();
        }
        for (int i = 0; i < wPrefs.size(); i++) {
            WeaponPreferences prefWeap = wPrefs.get(i);
            if (prefWeap.getWeapon().equals(UT2004ItemType.ASSAULT_RIFLE) && prefWeap.isUsingPrimary() == true) {
                currentWeapon = prefWeap;
            }
        }
        canPursue = false;
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

    private void updateRisque() {
        int maxKills = 0;
        int kills;
        for (Map.Entry<UnrealId, PlayerScore> playerScore : game.getPlayerScores().entrySet()) {
            if (!playerScore.getKey().equals(info.getId())) {
                kills = playerScore.getValue().getScore();
                if (maxKills < kills) {
                    maxKills = kills;
                }
            }
        }
        risque = (maxKills - info.getKills()) / (game.getFragLimit() * 1.0);
    }

    public Player getTarget() {
        return target;
    }

    public Engage getEngage() {
        return engage;
    }

    public TabooSet<Item> getTabooItems() {
        return tabooItems;
    }

    public WeaponPreferences getCurrentWeapon() {
        return currentWeapon;
    }

    public double getRisque() {
        return risque;
    }
    
    public IUT2004Navigation getNavToUse() {
        return navToUse;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public static void main(String args[]) throws PogamutException {
        new UT2004BotRunner<UT2004Bot, Parameters>(Repliquant.class).setMain(true).startAgents(
                //new Parameters().setName("Bot1").setBotSkin("HumanMaleA.MercMaleC").setSkillLevel(7),
                new Parameters().setName("Bot2").setBotSkin("HumanFemaleA.MercFemaleB").setSkillLevel(7));
    }
}
