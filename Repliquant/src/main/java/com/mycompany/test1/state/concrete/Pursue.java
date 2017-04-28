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

package com.mycompany.test1.state.concrete;

import com.mycompany.test1.main.Repliquant;
import com.mycompany.test1.state.Behavior;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathfollowing.NavMeshNavigation;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

/**
 *
 * @author Kedrisse
 */
public class Pursue extends Behavior{
    
    NavMeshNavigation nmNav;
    IUT2004Navigation navigation;
    Player pursued;


    public Pursue(Repliquant unBot) {
        super(unBot);
    }
    
    public void initVars() {
        Repliquant bot = getBot();
        navigation = bot.getNavigation();
        pursued = bot.getTarget();
        nmNav = bot.getNMNav();
        if (nmNav.isAvailable())
            navigation = nmNav;
    }
    @Override
    public void performs() {
        initVars();
        navigation.navigate(pursued);
        if(!navigation.isNavigating()){
            getBot().setTarget(null);
        }
    }
}
