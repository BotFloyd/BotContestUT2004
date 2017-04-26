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
    
    private final UT2004ItemType weapon;
    private final double weight;
    private double probability;
    private final double initialProbability;
    private final boolean usePrimary;
    private int nbKills;
    private int nbDeaths;

    public WeaponPreferences(UT2004ItemType weapon, double weight, double probability, boolean usePrimary) {
        this.weapon = weapon;
        this.weight = weight;
        this.probability = probability;
        this.initialProbability = probability;
        this.usePrimary = usePrimary;
        this.nbDeaths = 0;
        this.nbKills = 0;
    }
    
    public void updateProbability(){
        probability = (initialProbability * weight + nbKills)/(weight + nbKills + nbDeaths);
    }
    
    public double getProbability(){
        return this.probability;
    }
    
    public UT2004ItemType getWeapon(){
        return this.weapon;
    }
    
    public boolean isUsingPrimary(){
        return this.usePrimary;
    }
    
    public void upNbKills() {
        this.nbKills++;
    }

    public void upNbDeaths() {
        this.nbDeaths++;
    }
}
