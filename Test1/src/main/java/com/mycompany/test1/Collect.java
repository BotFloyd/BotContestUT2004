/*
 * Copyright (C) 2017 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mycompany.test1;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Items;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavPoints;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import java.util.Map;

/**
 *
 * @author flori
 */
public class Collect extends Behavior{

    Items items;
    AgentInfo info;
    IUT2004Navigation navigation;
    Weaponry weaponry;
    NavPoints nav;
    Map<UT2004ItemType, Double> groupPriority; 
    
    public Collect(EmptyBot unBot) {
        super(unBot);
    }

    private void initVars(){
        items = getBot().getItems();
        info = getBot().getInfo();
        navigation = getBot().getNavigation();
        weaponry = getBot().getWeaponry();
        nav = getBot().getNavPoints();
    }
    
    @Override
    public void performed() {
        initVars();
        if(! navigation.isNavigating()){ 
            Item selectedItem = null;
            updateGroupPriority();
            if(! info.isHealthy() && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(ItemType.Category.HEALTH);
            }
            if (! weaponry.hasWeapon(UT2004ItemType.ROCKET_LAUNCHER) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.ROCKET_LAUNCHER);
            }
            if (! weaponry.isLoaded(UT2004ItemType.ROCKET_LAUNCHER) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.ROCKET_LAUNCHER_AMMO);
            }
            if (! weaponry.hasWeapon(UT2004ItemType.SNIPER_RIFLE) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.SNIPER_RIFLE);
            }
            if (! weaponry.isLoaded(UT2004ItemType.SNIPER_RIFLE) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.SNIPER_RIFLE_AMMO);
            }
            if (! weaponry.hasWeapon(UT2004ItemType.FLAK_CANNON) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.FLAK_CANNON);
            }
            if (! weaponry.isLoaded(UT2004ItemType.FLAK_CANNON) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.FLAK_CANNON_AMMO);
            }
            if (! weaponry.hasWeapon(UT2004ItemType.MINIGUN) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.MINIGUN);
            }
            if (! weaponry.isLoaded(UT2004ItemType.MINIGUN) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.MINIGUN_AMMO);
            }
            if (! weaponry.hasWeapon(UT2004ItemType.LIGHTNING_GUN) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.LIGHTNING_GUN);
            }
            if (! weaponry.isLoaded(UT2004ItemType.LIGHTNING_GUN) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.LIGHTNING_GUN_AMMO);
            }
            if (! weaponry.hasWeapon(UT2004ItemType.SHOCK_RIFLE) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.SHOCK_RIFLE);
            }
            if (! weaponry.isLoaded(UT2004ItemType.SHOCK_RIFLE) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.SHOCK_RIFLE_AMMO);
            }
            if (! weaponry.hasWeapon(UT2004ItemType.LINK_GUN) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.LINK_GUN);
            }
            if (! weaponry.isLoaded(UT2004ItemType.LINK_GUN) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.LINK_GUN_AMMO);
            }
            if (! weaponry.hasWeapon(UT2004ItemType.BIO_RIFLE) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.BIO_RIFLE);
            }
            if (! weaponry.isLoaded(UT2004ItemType.BIO_RIFLE) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.BIO_RIFLE_AMMO);
            }
            if (! weaponry.isLoaded(UT2004ItemType.ASSAULT_RIFLE) && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.ASSAULT_RIFLE_AMMO);
            }
            if (! info.hasArmor() && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.UT2004Group.SUPER_ARMOR);
            }
            if (! info.hasLowArmor() && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.UT2004Group.SMALL_ARMOR);
            }
            if (! info.isSuperHealthy() && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(UT2004ItemType.MINI_HEALTH_PACK);
            }
            if (! info.isAdrenalineFull() && selectedItem == null){
                selectedItem = items.getPathNearestSpawnedItem(ItemType.Category.ADRENALINE);
            }
            if(selectedItem == null){
                navigation.navigate(nav.getRandomNavPoint());
            } else {
                navigation.navigate(selectedItem);
            }
        }
    }
    
    private void updateGroupPriority(){
        
        groupPriority.put(UT2004ItemType.MINI_HEALTH_PACK, 3.0*(199-info.getHealth())/199);
        groupPriority.put(UT2004ItemType.ADRENALINE_PACK, 1.0*(99-info.getAdrenaline())/99);
        groupPriority.put(UT2004ItemType.SHIELD_PACK, 4.0*(50-info.getLowArmor())/50);
        groupPriority.put(UT2004ItemType.SUPER_SHIELD_PACK, 6.0*(100-info.getHighArmor())/100);
        
        if(weaponry.hasWeapon(UT2004ItemType.ROCKET_LAUNCHER)){
            groupPriority.put(UT2004ItemType.ROCKET_LAUNCHER, 0.0);
            groupPriority.put(UT2004ItemType.ROCKET_LAUNCHER_AMMO, 10.0*(weaponry.getMaxAmmo(UT2004ItemType.ROCKET_LAUNCHER_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.ROCKET_LAUNCHER_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.ROCKET_LAUNCHER_AMMO));
        } else {
            groupPriority.put(UT2004ItemType.ROCKET_LAUNCHER, 10.0);
            groupPriority.put(UT2004ItemType.ROCKET_LAUNCHER_AMMO, 5.0*(weaponry.getMaxAmmo(UT2004ItemType.ROCKET_LAUNCHER_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.ROCKET_LAUNCHER_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.ROCKET_LAUNCHER_AMMO));
        }
        
        if(weaponry.hasWeapon(UT2004ItemType.SNIPER_RIFLE)){
            groupPriority.put(UT2004ItemType.SNIPER_RIFLE, 0.0);
            groupPriority.put(UT2004ItemType.SNIPER_RIFLE_AMMO, 9.0*(weaponry.getMaxAmmo(UT2004ItemType.SNIPER_RIFLE_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.SNIPER_RIFLE_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.SNIPER_RIFLE_AMMO));
        } else {
            groupPriority.put(UT2004ItemType.SNIPER_RIFLE, 9.0);
            groupPriority.put(UT2004ItemType.SNIPER_RIFLE_AMMO, 4.5*(weaponry.getMaxAmmo(UT2004ItemType.SNIPER_RIFLE_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.SNIPER_RIFLE_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.SNIPER_RIFLE_AMMO));
        }
        
        if(weaponry.hasWeapon(UT2004ItemType.FLAK_CANNON)){
            groupPriority.put(UT2004ItemType.FLAK_CANNON, 0.0);
            groupPriority.put(UT2004ItemType.FLAK_CANNON_AMMO, 8.0*(weaponry.getMaxAmmo(UT2004ItemType.FLAK_CANNON_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.FLAK_CANNON_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.FLAK_CANNON_AMMO));
        } else {
            groupPriority.put(UT2004ItemType.FLAK_CANNON, 8.0);
            groupPriority.put(UT2004ItemType.FLAK_CANNON_AMMO, 4.0*(weaponry.getMaxAmmo(UT2004ItemType.FLAK_CANNON_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.FLAK_CANNON_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.FLAK_CANNON_AMMO));
        }
        
        if(weaponry.hasWeapon(UT2004ItemType.MINIGUN)){
            groupPriority.put(UT2004ItemType.MINIGUN, 0.0);
            groupPriority.put(UT2004ItemType.MINIGUN_AMMO, 7.0*(weaponry.getMaxAmmo(UT2004ItemType.MINIGUN_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.MINIGUN_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.MINIGUN_AMMO));
        } else {
            groupPriority.put(UT2004ItemType.MINIGUN, 7.0);
            groupPriority.put(UT2004ItemType.MINIGUN_AMMO, 3.5*(weaponry.getMaxAmmo(UT2004ItemType.MINIGUN_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.MINIGUN_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.MINIGUN_AMMO));
        }
       
        if(weaponry.hasWeapon(UT2004ItemType.LIGHTNING_GUN)){
            groupPriority.put(UT2004ItemType.LIGHTNING_GUN, 0.0);
            groupPriority.put(UT2004ItemType.LIGHTNING_GUN_AMMO, 6.0*(weaponry.getMaxAmmo(UT2004ItemType.LIGHTNING_GUN_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.LIGHTNING_GUN_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.LIGHTNING_GUN_AMMO));
        } else {
            groupPriority.put(UT2004ItemType.LIGHTNING_GUN, 6.0);
            groupPriority.put(UT2004ItemType.LIGHTNING_GUN_AMMO, 3.0*(weaponry.getMaxAmmo(UT2004ItemType.LIGHTNING_GUN_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.LIGHTNING_GUN_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.LIGHTNING_GUN_AMMO));
        }
        
        if(weaponry.hasWeapon(UT2004ItemType.SHOCK_RIFLE)){
            groupPriority.put(UT2004ItemType.SHOCK_RIFLE, 0.0);
            groupPriority.put(UT2004ItemType.SHOCK_RIFLE_AMMO, 5.0*(weaponry.getMaxAmmo(UT2004ItemType.SHOCK_RIFLE_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.SHOCK_RIFLE_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.SHOCK_RIFLE_AMMO));
        } else {
            groupPriority.put(UT2004ItemType.SHOCK_RIFLE, 5.0);
            groupPriority.put(UT2004ItemType.SHOCK_RIFLE_AMMO, 2.5*(weaponry.getMaxAmmo(UT2004ItemType.SHOCK_RIFLE_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.SHOCK_RIFLE_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.SHOCK_RIFLE_AMMO));
        }
        
        if(weaponry.hasWeapon(UT2004ItemType.LINK_GUN)){
            groupPriority.put(UT2004ItemType.LINK_GUN, 0.0);
            groupPriority.put(UT2004ItemType.LINK_GUN_AMMO, 4.0*(weaponry.getMaxAmmo(UT2004ItemType.LINK_GUN_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.LINK_GUN_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.LINK_GUN_AMMO));
        } else {
            groupPriority.put(UT2004ItemType.LINK_GUN, 4.0);
            groupPriority.put(UT2004ItemType.LINK_GUN_AMMO, 2.0*(weaponry.getMaxAmmo(UT2004ItemType.LINK_GUN_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.LINK_GUN_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.LINK_GUN_AMMO));
        }
        
        if(weaponry.hasWeapon(UT2004ItemType.BIO_RIFLE)){
            groupPriority.put(UT2004ItemType.BIO_RIFLE, 0.0);
            groupPriority.put(UT2004ItemType.BIO_RIFLE_AMMO, 3.0*(weaponry.getMaxAmmo(UT2004ItemType.BIO_RIFLE_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.BIO_RIFLE_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.BIO_RIFLE_AMMO));
        } else {
            groupPriority.put(UT2004ItemType.BIO_RIFLE, 3.0);
            groupPriority.put(UT2004ItemType.BIO_RIFLE_AMMO, 1.5*(weaponry.getMaxAmmo(UT2004ItemType.BIO_RIFLE_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.BIO_RIFLE_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.BIO_RIFLE_AMMO));
        }
        
        groupPriority.put(UT2004ItemType.ASSAULT_RIFLE_AMMO, 1.0*(weaponry.getMaxAmmo(UT2004ItemType.ASSAULT_RIFLE_AMMO)-weaponry.getWeaponAmmo(UT2004ItemType.ASSAULT_RIFLE_AMMO))/weaponry.getMaxAmmo(UT2004ItemType.ASSAULT_RIFLE_AMMO));
        
    }
}