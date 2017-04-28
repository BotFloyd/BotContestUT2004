/*
 * Copyright (C) 2017 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mycompany.test1.state.concrete;

import com.mycompany.test1.main.Repliquant;
import com.mycompany.test1.state.Behavior;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectile;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 *
 * @author flori
 */
public class Dodge extends Behavior{
    
    Senses senses;
    AgentInfo info;
    AdvancedLocomotion move;

    public Dodge(Repliquant unBot) {
        super(unBot);
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
    
    private void initVars() {
        senses = getBot().getSenses();
        info = getBot().getInfo();
        move = getBot().getMove();
    }
    
}
