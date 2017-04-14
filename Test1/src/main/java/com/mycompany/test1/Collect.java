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

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Items;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

/**
 *
 * @author flori
 */
public class Collect extends Behavior{

    public Collect(EmptyBot unBot) {
        super(unBot);
    }

    
    @Override
    public void performed() {
        Items items = getBot().getItems();
        
        Item selectedItem;
        Item nearestItem = items.getPathNearestSpawnedItem();
        Item visibleItem = items.getNearestVisibleItem();
        
        IUT2004Navigation navigation = getBot().getNavigation();
        
        AgentInfo info = getBot().getInfo();
            
        if(visibleItem != null){
          selectedItem = visibleItem;
        } else {
          selectedItem = nearestItem;
        }
        navigation.navigate(selectedItem);
        if(! navigation.isNavigatingToItem()){
            NavPoint RNavPoint = getBot().getNavPoints().getRandomNavPoint();
            navigation.navigate(RNavPoint);
        }
    }
}
