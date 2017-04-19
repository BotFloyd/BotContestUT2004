package com.mycompany.test1;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;

public class MedKit extends Behavior{
    
    public MedKit(Repliquant bot){
        super(bot);
    }
    @Override
    public void performed(){
         
            Item unItem = getBot().getItems().getNearestVisibleItem(ItemType.Category.HEALTH);
            if(unItem != null)
                getBot().getNavigation().navigate(unItem);
            else
                getBot().getNavigation().navigate(getBot().getItems().getPathNearestItem(ItemType.Category.HEALTH));
    }
}
