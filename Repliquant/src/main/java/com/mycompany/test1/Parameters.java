package com.mycompany.test1;

import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;

public class Parameters extends UT2004BotParameters {
    
    private String botSkin;
    private String name;
    private int skillLevel;
    
    public String getBotSkin() {
        return botSkin;
    }
    
    public Parameters setBotSkin(String botSkin) {
        this.botSkin = botSkin;
        return this;
    }
    
    public String getName() {
        return name;
    }
    
    public Parameters setName(String name) {
        this.name = name;
        return this;
    }

    public int getSkillLevel() {
        return skillLevel;
    }
    
    public Parameters setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
        return this;
    }
    
    public Initialize initialize () {
        return new Initialize().setName(getName()).setSkin(getBotSkin()).setDesiredSkill(getSkillLevel()); 
    }
    
}
