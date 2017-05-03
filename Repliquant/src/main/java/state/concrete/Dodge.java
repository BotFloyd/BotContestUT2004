package state.concrete;

import main.Repliquant;
import state.Behavior;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectile;
import static java.lang.Math.sqrt;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Dodge extends Behavior{
    
    Senses senses;
    AgentInfo info;
    AdvancedLocomotion move;
    
    private void initVars(Repliquant unBot) {
        senses = unBot.getSenses();
        info = unBot.getInfo();
        move = unBot.getMove();
    }
    
    @Override
    public void performs(Repliquant unBot) {
        initVars(unBot);
        long reaction = System.currentTimeMillis() + 300;
        while(System.currentTimeMillis() < reaction){
            unBot.getLog().info("Time Reaction");
        }
        IncomingProjectile projectile = senses.getLastIncomingProjectile();
        if(projectile != null){
            if(projectile.isVisible() && isDangerous(projectile)){
                if(unBot.getRandom().nextDouble() <= 0.5)
                    move.dodgeLeft(projectile.getLocation(),false);
                else
                    move.dodgeRight(projectile.getLocation(),false);
            }
        }
    }
    
    private boolean isDangerous(IncomingProjectile projectile){
        Vector3d projectileDirection = projectile.getDirection();
        double dmgRad = projectile.getDamageRadius();
        Point3d projectileLocation = projectile.getLocation().getPoint3d();
        Point3d currentLocation = info.getLocation().getPoint3d();
        double normal = sqrt(projectileDirection.getX() * 
                projectileDirection.getX() + projectileDirection.getY() * 
                projectileDirection.getY() + projectileDirection.getZ() *
                projectileDirection.getZ());
        projectileDirection.setX(projectileDirection.getX() / normal);
        projectileDirection.setY(projectileDirection.getY() / normal);
        projectileDirection.setZ(projectileDirection.getZ() / normal);
        Point3d cLpL = new Point3d(
                (currentLocation.getX() - projectileLocation.getX()),
                (currentLocation.getY() - projectileLocation.getY()),
                (currentLocation.getZ() - projectileLocation.getZ()));
        double d = cLpL.getX() * projectileDirection.getX() + cLpL.getY() *
                projectileDirection.getY() + cLpL.getZ() * projectileDirection.getZ();
        double d2 = sqrt(cLpL.getX() * cLpL.getX() + cLpL.getY() * cLpL.getY() +
                cLpL.getZ() * cLpL.getZ() - d * d);
        return (d >= 0 && d2 <= dmgRad);
    }
}
