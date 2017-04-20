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

package com.mycompany.test1;

/**
 * Classe mère des états, ne peut être instanciée. Behavior est composée d'un
 * objet Repliquant, accessible via getBot() ainsi que d'une fonction abstraite
 * performed()
 * @author flori
 */
public abstract class Behavior {
    
    private final Repliquant unBot;
    
    /**
     * Constructeur initialisant l'objet Repliquant de cette classe
     * @param unBot  
     */
    public Behavior(Repliquant unBot){
        this.unBot = unBot;
    }
    
    /**
     * Getter de la classe
     * @return l'objet Repliquant de la classe 
     */
    public Repliquant getBot(){
        return this.unBot;
    }
    
    /**
     * Fonction contenant les actions qu'effectuera le bot tant qu'il sera dans
     * cet état
     */
    public abstract void performed();
}
