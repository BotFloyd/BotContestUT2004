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

package com.mycompany.test1.settings;

import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;

/**
 *
 * @author flori
 */
public class WeaponPreferences {
    
    private UT2004ItemType weapon;
    private boolean usePrimary;
    private double probability;
    private double weight;
    private int nbKills;
    private int nbDeaths;
    
    public WeaponPreferences(UT2004ItemType weapon, boolean usePrimary, 
            double probability, double weight){
        this.weapon = weapon;
        this.usePrimary = usePrimary;
        this.weight = weight;
        this.probability = probability;
        this.nbDeaths = 0;
        this.nbKills = 0;
    }
    
    
    
    public void setProbability(){
        
    }
}
