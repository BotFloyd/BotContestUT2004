package com.mycompany.test1.state.concrete;

import com.mycompany.test1.main.Repliquant;
import com.mycompany.test1.state.Behavior;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;

public class Travel extends Behavior {
    
    public Travel (Repliquant bot) {
        super(bot);
    }
    
    public void initVars() {
        Repliquant bot = getBot();
        navigation = bot.getNavigation();
        nmNav = bot.getNMNav();
        if (nmNav.isAvailable())
            navigation = nmNav;
    }

    @Override
    public void performs() {
        initVars();
        Item obj = getBot().getItems().getNearestVisibleItem();
        if (navigation.isNavigating()) {
            if (obj != null) {
                ILocated last = navigation.getLastTarget();
                if (last != null) {
                    navigation.navigate(obj.getLocation());
                    navigation.setContinueTo(last);
                }
            }
        } else if (obj != null) {
            navigation.navigate(obj.getLocation());
        }
    }
}
