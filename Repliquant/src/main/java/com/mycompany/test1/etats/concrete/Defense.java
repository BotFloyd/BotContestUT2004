package com.mycompany.test1.etats.concrete;

import com.mycompany.test1.main.Repliquant;
import com.mycompany.test1.etats.Behavior;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Players;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectile;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;



public class Defense extends Behavior{
    
    AdvancedLocomotion move;
    AgentInfo info;
    Senses senses;
    Players players;
    
    public Defense(Repliquant bot){
        super(bot);
    }
    
    @Override
    public void performs(){      
        initVars();
        IncomingProjectile projectile = senses.getLastIncomingProjectile();
        if(!(players.canSeeEnemies()) && !(projectile.isVisible())){
            move.turnHorizontal(180);
        } else if(projectile.isVisible()){
            Vector3d projectileDirection = projectile.getDirection();
            double dmgRad = projectile.getDamageRadius();
            Point3d projectilelocation = projectile.getLocation().getPoint3d();
            Point3d currentLocation = info.getLocation().getPoint3d();
            //TO DO : Trouver un moyen de déterminer si le bot est, oui ou non, dans le radius de l'explosion du projectile
            boolean inRadiusX = (currentLocation.getX() - projectilelocation.getX() + dmgRad/2)% projectileDirection.getX() == 0;
            boolean inRadiusY = (currentLocation.getY() - projectilelocation.getY() + dmgRad/2)% projectileDirection.getY() == 0;
            boolean inRadiusZ = (currentLocation.getZ() - projectilelocation.getZ() + dmgRad/2)% projectileDirection.getZ() == 0;
            if(inRadiusX && inRadiusY && inRadiusZ){
                move.dodge(projectile.getLocation(), true);
            }
        }
    }
    
    private void initVars(){
        info = getBot().getInfo();
        move = getBot().getMove();
        senses = getBot().getSenses();
        players = getBot().getPlayers();
    }
    
}
