package state.concrete;

import state.Behavior;
import main.Repliquant;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Items;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavPoints;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import java.util.HashMap;
import java.util.Map;

public class Collect extends Behavior {

    Items items;
    AgentInfo info;
    Weaponry weaponry;
    NavPoints nav;
    Map<UT2004ItemType, Double> groupPriority = new HashMap();
    TabooSet<Item> tabooItems;
    IUT2004Navigation navigation;
    double risque;

    private void initVars(Repliquant unBot) {
        items = unBot.getItems();
        info = unBot.getInfo();
        weaponry = unBot.getWeaponry();
        nav = unBot.getNavPoints();
        tabooItems = unBot.getTabooItems();
        risque = unBot.getRisque();
        navigation = unBot.getNavToUse();
    }

    @Override
    public void performs(Repliquant unBot) {
        initVars(unBot);
        if (!navigation.isNavigating()) {
            Item selectedItem = null;
            Double highestPriority = 0.0;
            Double priority;
            updateGroupPriority(unBot);
            unBot.getLog().severe("Groupe priority : " + groupPriority.toString());
            if (!items.getSpawnedItems().values().isEmpty() && !navigation.isNavigating()) {
                for (Item item : tabooItems.filter(items.getSpawnedItems().values())) {
                    if (groupPriority.containsKey(item.getType()) && (groupPriority.get(item.getType()) > 0)) {
                        priority = groupPriority.get(item.getType()) * 200 - unBot.getBot().getLocation().getDistance(item.getLocation());
                        if (priority == highestPriority) {
                            if (selectedItem == null || (groupPriority.get(item.getType()) > groupPriority.get((UT2004ItemType) selectedItem.getType()))) {
                                selectedItem = item;
                            }
                        } else if (priority > highestPriority) {
                            selectedItem = item;
                            highestPriority = priority;
                        }
                    }
                }
            }

            if (!navigation.isNavigating()) {
                if (selectedItem == null) {
                    navigation.navigate(nav.getRandomNavPoint());
                } else {
                    unBot.getLog().severe(" Selected Item : " + selectedItem.getType());
                    navigation.navigate(selectedItem);
                    tabooItems.add(selectedItem, (unBot.getRandom().nextDouble() * 0.5 + 1) * items.getItemRespawnTime(selectedItem));
                }
            }
        }
    }

    private void updateGroupPriority(Repliquant unBot) {
        if (risque >= 0) {
            groupPriority.put(UT2004ItemType.MINI_HEALTH_PACK, 2.0 * (199 - info.getHealth()) / 199.0);
            groupPriority.put(UT2004ItemType.HEALTH_PACK, 8.0 * (100 - info.getHealth()) / 100.0);
            groupPriority.put(UT2004ItemType.SUPER_HEALTH_PACK, 8.0 * (199 - info.getHealth()) / 199.0);
            groupPriority.put(UT2004ItemType.SHIELD_PACK, 5.0 * (50 - info.getLowArmor()) / 50.0);
            groupPriority.put(UT2004ItemType.SUPER_SHIELD_PACK, 8.0 * (100 - info.getHighArmor()) / 100.0);
        } else {
            groupPriority.put(UT2004ItemType.MINI_HEALTH_PACK, 4.0 * (199 - info.getHealth()) / 199.0);
            groupPriority.put(UT2004ItemType.HEALTH_PACK, 16.0 * (100 - info.getHealth()) / 100.0);
            groupPriority.put(UT2004ItemType.SUPER_HEALTH_PACK, 16.0 * (199 - info.getHealth()) / 199.0);
            groupPriority.put(UT2004ItemType.SHIELD_PACK, 10.0 * (50 - info.getLowArmor()) / 50.0);
            groupPriority.put(UT2004ItemType.SUPER_SHIELD_PACK, 16.0 * (100 - info.getHighArmor()) / 100.0);
        }

        if (weaponry.hasLoadedWeapon()) {
            if (info.getRemainingUDamageTime() < 0) {
                groupPriority.put(UT2004ItemType.U_DAMAGE_PACK, 5.0);
            } else {
                groupPriority.put(UT2004ItemType.U_DAMAGE_PACK, 3.0 * (27 - info.getRemainingUDamageTime()) / 27.0);
            }
        } else {
            groupPriority.put(UT2004ItemType.U_DAMAGE_PACK, 0.0);
        }

        groupPriority.put(UT2004ItemType.REDEEMER, 999.0);
        groupPriority.put(UT2004ItemType.ION_PAINTER, 999.0);

        if (weaponry.hasWeapon(UT2004ItemType.ROCKET_LAUNCHER)) {
            groupPriority.put(UT2004ItemType.ROCKET_LAUNCHER, 0.0);
            groupPriority.put(UT2004ItemType.ROCKET_LAUNCHER_AMMO, 10.0 * (weaponry.getMaxAmmo(UT2004ItemType.ROCKET_LAUNCHER_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.ROCKET_LAUNCHER)) / weaponry.getMaxAmmo(UT2004ItemType.ROCKET_LAUNCHER_AMMO));
        } else {
            groupPriority.put(UT2004ItemType.ROCKET_LAUNCHER, 10.0);
            groupPriority.put(UT2004ItemType.ROCKET_LAUNCHER_AMMO, 5.0 * (weaponry.getMaxAmmo(UT2004ItemType.ROCKET_LAUNCHER_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.ROCKET_LAUNCHER)) / weaponry.getMaxAmmo(UT2004ItemType.ROCKET_LAUNCHER_AMMO));
        }

        if (weaponry.hasWeapon(UT2004ItemType.SNIPER_RIFLE)) {
            groupPriority.put(UT2004ItemType.SNIPER_RIFLE, 0.0);
            groupPriority.put(UT2004ItemType.SNIPER_RIFLE_AMMO, 9.0 * (weaponry.getMaxAmmo(UT2004ItemType.SNIPER_RIFLE_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.SNIPER_RIFLE)) / weaponry.getMaxAmmo(UT2004ItemType.SNIPER_RIFLE_AMMO));
        } else {
            groupPriority.put(UT2004ItemType.SNIPER_RIFLE, 9.0);
            groupPriority.put(UT2004ItemType.SNIPER_RIFLE_AMMO, 4.5 * (weaponry.getMaxAmmo(UT2004ItemType.SNIPER_RIFLE_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.SNIPER_RIFLE)) / weaponry.getMaxAmmo(UT2004ItemType.SNIPER_RIFLE_AMMO));
        }

        if (weaponry.hasWeapon(UT2004ItemType.FLAK_CANNON)) {
            groupPriority.put(UT2004ItemType.FLAK_CANNON, 0.0);
            groupPriority.put(UT2004ItemType.FLAK_CANNON_AMMO, 8.0 * (weaponry.getMaxAmmo(UT2004ItemType.FLAK_CANNON_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.FLAK_CANNON)) / weaponry.getMaxAmmo(UT2004ItemType.FLAK_CANNON_AMMO));
        } else {
            groupPriority.put(UT2004ItemType.FLAK_CANNON, 8.0);
            groupPriority.put(UT2004ItemType.FLAK_CANNON_AMMO, 4.0 * (weaponry.getMaxAmmo(UT2004ItemType.FLAK_CANNON_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.FLAK_CANNON)) / weaponry.getMaxAmmo(UT2004ItemType.FLAK_CANNON_AMMO));
        }

        if (weaponry.hasWeapon(UT2004ItemType.MINIGUN)) {
            groupPriority.put(UT2004ItemType.MINIGUN, 0.0);
            groupPriority.put(UT2004ItemType.MINIGUN_AMMO, 7.0 * (weaponry.getMaxAmmo(UT2004ItemType.MINIGUN_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.MINIGUN)) / weaponry.getMaxAmmo(UT2004ItemType.MINIGUN_AMMO));
        } else {
            groupPriority.put(UT2004ItemType.MINIGUN, 7.0);
            groupPriority.put(UT2004ItemType.MINIGUN_AMMO, 3.5 * (weaponry.getMaxAmmo(UT2004ItemType.MINIGUN_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.MINIGUN)) / weaponry.getMaxAmmo(UT2004ItemType.MINIGUN_AMMO));
        }

        if (weaponry.hasWeapon(UT2004ItemType.LIGHTNING_GUN)) {
            groupPriority.put(UT2004ItemType.LIGHTNING_GUN, 0.0);
            groupPriority.put(UT2004ItemType.LIGHTNING_GUN_AMMO, 6.0 * (weaponry.getMaxAmmo(UT2004ItemType.LIGHTNING_GUN_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.LIGHTNING_GUN)) / weaponry.getMaxAmmo(UT2004ItemType.LIGHTNING_GUN_AMMO));
        } else {
            groupPriority.put(UT2004ItemType.LIGHTNING_GUN, 6.0);
            groupPriority.put(UT2004ItemType.LIGHTNING_GUN_AMMO, 3.0 * (weaponry.getMaxAmmo(UT2004ItemType.LIGHTNING_GUN_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.LIGHTNING_GUN)) / weaponry.getMaxAmmo(UT2004ItemType.LIGHTNING_GUN_AMMO));
        }

        if (weaponry.hasWeapon(UT2004ItemType.SHOCK_RIFLE)) {
            groupPriority.put(UT2004ItemType.SHOCK_RIFLE, 0.0);
            groupPriority.put(UT2004ItemType.SHOCK_RIFLE_AMMO, 5.0 * (weaponry.getMaxAmmo(UT2004ItemType.SHOCK_RIFLE_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.SHOCK_RIFLE)) / weaponry.getMaxAmmo(UT2004ItemType.SHOCK_RIFLE_AMMO));
        } else {
            groupPriority.put(UT2004ItemType.SHOCK_RIFLE, 5.0);
            groupPriority.put(UT2004ItemType.SHOCK_RIFLE_AMMO, 2.5 * (weaponry.getMaxAmmo(UT2004ItemType.SHOCK_RIFLE_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.SHOCK_RIFLE)) / weaponry.getMaxAmmo(UT2004ItemType.SHOCK_RIFLE_AMMO));
        }

        if (weaponry.hasWeapon(UT2004ItemType.LINK_GUN)) {
            groupPriority.put(UT2004ItemType.LINK_GUN, 0.0);
            groupPriority.put(UT2004ItemType.LINK_GUN_AMMO, 4.0 * (weaponry.getMaxAmmo(UT2004ItemType.LINK_GUN_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.LINK_GUN)) / weaponry.getMaxAmmo(UT2004ItemType.LINK_GUN_AMMO));
        } else {
            groupPriority.put(UT2004ItemType.LINK_GUN, 4.0);
            groupPriority.put(UT2004ItemType.LINK_GUN_AMMO, 2.0 * (weaponry.getMaxAmmo(UT2004ItemType.LINK_GUN_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.LINK_GUN)) / weaponry.getMaxAmmo(UT2004ItemType.LINK_GUN_AMMO));
        }

        if (weaponry.hasWeapon(UT2004ItemType.BIO_RIFLE)) {
            groupPriority.put(UT2004ItemType.BIO_RIFLE, 0.0);
            groupPriority.put(UT2004ItemType.BIO_RIFLE_AMMO, 3.0 * (weaponry.getMaxAmmo(UT2004ItemType.BIO_RIFLE_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.BIO_RIFLE)) / weaponry.getMaxAmmo(UT2004ItemType.BIO_RIFLE_AMMO));
        } else {
            groupPriority.put(UT2004ItemType.BIO_RIFLE, 3.0);
            groupPriority.put(UT2004ItemType.BIO_RIFLE_AMMO, 1.5 * (weaponry.getMaxAmmo(UT2004ItemType.BIO_RIFLE_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.BIO_RIFLE)) / weaponry.getMaxAmmo(UT2004ItemType.BIO_RIFLE_AMMO));
        }

        groupPriority.put(UT2004ItemType.ASSAULT_RIFLE_AMMO, 1.0 * (weaponry.getMaxAmmo(UT2004ItemType.ASSAULT_RIFLE_AMMO) - weaponry.getWeaponAmmo(UT2004ItemType.ASSAULT_RIFLE)) / weaponry.getMaxAmmo(UT2004ItemType.ASSAULT_RIFLE_AMMO));
    }
}
