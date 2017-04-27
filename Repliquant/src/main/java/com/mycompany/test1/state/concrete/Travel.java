package com.mycompany.test1.state.concrete;

import com.mycompany.test1.main.Repliquant;
import com.mycompany.test1.state.Behavior;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathfollowing.NavMeshNavigation;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;

public class Travel extends Behavior {
        
    public Travel (Repliquant bot) {
        super(bot);
    }

    @Override
    public void performs() {
        Repliquant bot = getBot();
        NavMeshNavigation nmNav = bot.getNMNav();
        Item obj = bot.getItems().getNearestVisibleItem();
        if (nmNav.isNavigating()) {
            if (obj != null) {
                ILocated last = nmNav.getLastTarget();
                if (last != null) {
                    nmNav.navigate(obj.getLocation());
                    nmNav.setContinueTo(last);
                }
            }
        } else if (obj != null) {
            nmNav.navigate(obj.getLocation());
        }
    }
}
