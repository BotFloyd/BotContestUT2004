package listener;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

/**
 *
 * @author flori
 */
public class TimeStuck {

    private Location location;
    private long timelimit;
    
    public boolean stuck(Location currentLocation){
        if(!(location.equals(currentLocation))){
            location = currentLocation;
            timelimit = System.currentTimeMillis() + 1200;
        }
        return (timelimit >= System.currentTimeMillis()); 
    }
}
