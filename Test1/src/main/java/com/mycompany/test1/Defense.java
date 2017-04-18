package com.mycompany.test1;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Rotate;



public class Defense extends Behavior{
    
    public Defense(EmptyBot bot){
        super(bot);
    }
    
    @Override
    public void performed(){
            getBot().getAct().act(new Rotate().setAmount(32000));
           
    }

}