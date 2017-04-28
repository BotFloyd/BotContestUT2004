package state.concrete;

import main.Repliquant;
import state.Behavior;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectile;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Dodge extends Behavior{
    
    Senses senses;
    AgentInfo info;
    AdvancedLocomotion move;

    public Dodge(Repliquant unBot) {
        super(unBot);
    }
    
    private void initVars() {
        Repliquant bot = getBot();
        senses = bot.getSenses();
        info = bot.getInfo();
        move = bot.getMove();
    }
    
    @Override
    public void performs() {
        initVars();
        IncomingProjectile projectile = senses.getLastIncomingProjectile();
        if(projectile != null){
            if(projectile.isVisible()){
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
    }
}
