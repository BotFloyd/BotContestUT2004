package state.concrete;

import main.Repliquant;
import state.Behavior;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Players;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;

public class Defense extends Behavior {

    AdvancedLocomotion move;
    Players players;


    private void initVars(Repliquant unBot) {
        move = unBot.getMove();
        players = unBot.getPlayers();
    }
    
    @Override
    public void performs(Repliquant unBot) {
        initVars(unBot);
        if (!(players.canSeeEnemies())) {
            move.turnHorizontal(180);
        }
    }
}
