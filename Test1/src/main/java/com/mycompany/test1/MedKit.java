package com.mycompany.test1;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Items;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import java.util.Collection;
import java.util.Map;

public class MedKit extends Behavior {
    
    private Repliquant bot;
    private TabooSet <Item> tabooItems;
    
    public MedKit (Repliquant bot) {
        super(bot);
        this.tabooItems = new TabooSet(bot.getBot());
    }
    
    public void performed () {
        double moreTime = bot.getRandom().nextDouble() + 0.75;
        IUT2004Navigation navigation = bot.getNavigation();
        LogCategory log = bot.getLog();
        Items items = bot.getItems();
        Item item = items.getNearestVisibleItem(ItemType.Category.HEALTH);
        if (item != null) {
            log.info("Found item, let's go !");
            navigation.navigate(item);
            tabooItems.add(item, items.getItemRespawnTime(item) * moreTime);
        } else {
            Map <UnrealId, Item> listPickups = items.getKnownPickups(ItemType.Category.HEALTH);
            Collection <Item> setItems = tabooItems.filter(listPickups.values());
            if (!setItems.isEmpty()) {
                item = setItems.iterator().next();
                log.info("Going for a known pickup.");
                navigation.navigate(item);
                tabooItems.add(item, items.getItemRespawnTime(item) * moreTime);
            }
        }
    }

}
