package state.concrete;

import main.Repliquant;
import state.Behavior;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;

public class Travel extends Behavior {
    
    public Travel (Repliquant bot) {
        super(bot);
    }

    @Override
    public void performs() {
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
