package com.mycompany.test1.state.concrete;

import com.mycompany.test1.main.Repliquant;
import com.mycompany.test1.state.Behavior;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Players;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;

public class Defense extends Behavior {

    AdvancedLocomotion move;
    Players players;

    public Defense(Repliquant bot) {
        super(bot);
    }

    @Override
    public void performs() {
        initVars();
        if (!(players.canSeeEnemies())) {
            move.turnHorizontal(180);
        }
    }

    private void initVars() {
        move = getBot().getMove();
        players = getBot().getPlayers();
    }

}
