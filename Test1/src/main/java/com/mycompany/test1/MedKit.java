package com.mycompany.test1;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;

public class MedKit {
    
    private EmptyBot unBot;
    
    public MedKit(EmptyBot bot){
        this.unBot = bot;
    }
    public void performed(){
         
            Item unItem = unBot.getItems().getNearestVisibleItem(ItemType.Category.HEALTH);
            if(unItem != null)
                unBot.getNavigation().navigate(unItem);
            else
                unBot.getNavigation().navigate(unBot.getItems().getPathNearestItem(ItemType.Category.HEALTH));
    }
}
