package settings;

import main.Repliquant;
import state.concrete.Engage;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Raycasting;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMeshModule;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.RemoveRay;
import cz.cuni.amis.utils.flag.FlagListener;
import java.util.List;
import javax.vecmath.Vector3d;

public class Initialization {
    
    private boolean navMeshDrawn = false, offMeshLinksDrawn = false;
    
    public void raycastingInit(Repliquant unBot) {
        final Raycasting raycasting = unBot.getRaycasting();
        final Engage engage = unBot.getEngage();
        unBot.getAct().act(new RemoveRay("All"));
        raycasting.createRay("LEFT90", new Vector3d(0, -1, 0), 175, true, false, false);
        raycasting.createRay("RIGHT90", new Vector3d(0, 1, 0), 175, true, false, false);
        raycasting.createRay("BOTTOMLEFT45", new Vector3d(0, -1, -0.4), 250, true, false, false);
        raycasting.createRay("BOTTOMRIGHT45", new Vector3d(0, 1, -0.4), 250, true, false, false);
        raycasting.createRay("BOTTOMLEFT", new Vector3d(0, -1, -0.1), 500, true, false, false);
        raycasting.createRay("BOTTOMRIGHT", new Vector3d(0, 1, -0.1), 500, true, false, false);
        raycasting.createRay("BOTTOMBACK", new Vector3d(-1, 0, -0.3), 250, false, false, false);
        raycasting.createRay("BACK", new Vector3d(-1, 0, 0), 300, true, false, false);
        raycasting.getAllRaysInitialized().addListener(new FlagListener<Boolean>() {
            @Override
            public void flagChanged(Boolean changedValue) {
                engage.setRayLeft(raycasting.getRay("LEFT90"));
                engage.setRayRight(raycasting.getRay("RIGHT90"));
                engage.setRayBottomLeft(raycasting.getRay("BOTTOMLEFT45"));
                engage.setRayBottomRight(raycasting.getRay("BOTTOMRIGHT45"));
                engage.setRayBottomLeft2(raycasting.getRay("BOTTOMLEFT"));
                engage.setRayBottomRight2(raycasting.getRay("BOTTOMRIGHT"));
                engage.setRayBottomBack(raycasting.getRay("BOTTOMBACK"));
                engage.setRayBack(raycasting.getRay("BACK"));
            }
        });
        raycasting.endRayInitSequence();
        unBot.getAct().act(new Configuration().setDrawTraceLines(true).setAutoTrace(true));
    }
    
    public void navigationInit(Repliquant unBot) {
        unBot.getNavigation().getPathExecutor().getState().addStrongListener(new FlagListener<IPathExecutorState>() {

            @Override
            public void flagChanged(IPathExecutorState changedValue) {
                pathExecutorStateChange(changedValue);
            }
        });
        unBot.getNMNav().getPathExecutor().getState().addStrongListener(new FlagListener<IPathExecutorState>() {

            @Override
            public void flagChanged(IPathExecutorState changedValue) {
                pathExecutorStateChange(changedValue);
            }
        });
    }
    
    private void pathExecutorStateChange(IPathExecutorState event) {
        switch (event.getState()) {
            case PATH_COMPUTATION_FAILED:
                // if path computation fails to whatever reason, just try another navpoint
                // taboo bad navpoint for 3 minutes
                break;

            case TARGET_REACHED:
                // taboo reached navpoint for 3 minutes
                break;

            case STUCK:
                // the bot has stuck! ... target nav point is unavailable currently
                break;

            case STOPPED:
                // path execution has stopped
                break;
        }
    }
    
    public boolean drawNavMesh(Repliquant unBot) {
        double waitingForMesh = 0;
        int waitForMesh = 0;
        NavMeshModule navMeshModule = unBot.getNavMeshModule();
        AgentInfo info = unBot.getInfo();
        if (!navMeshDrawn) {
            navMeshDrawn = true;
            navMeshModule.getNavMeshDraw().clearAll();
            navMeshModule.getNavMeshDraw().draw(true, false);
            waitForMesh = navMeshModule.getNavMesh().getPolys().size() / 35;
            waitingForMesh = -info.getTimeDelta();
        }
        if (waitForMesh > 0) {
            waitForMesh -= info.getTimeDelta();
            waitingForMesh += info.getTimeDelta();
            if (waitingForMesh > 2)
                    waitingForMesh = 0;
            if (waitForMesh > 0)
                    return false;		
        }
        return true;
    }

    public boolean drawOffMeshLinks(Repliquant unBot) {
        double waitingForOffMeshLinks = 0;
        int waitForOffMeshLinks = 0;
        NavMeshModule navMeshModule = unBot.getNavMeshModule();
        AgentInfo info = unBot.getInfo();
        if (!offMeshLinksDrawn) {
            offMeshLinksDrawn = true;
            if (navMeshModule.getNavMesh().getOffMeshPoints().isEmpty())
                return true;
            navMeshModule.getNavMeshDraw().draw(false, true);
            waitForOffMeshLinks = navMeshModule.getNavMesh().getOffMeshPoints().size() / 10;
            waitingForOffMeshLinks = -info.getTimeDelta();
        }
        if (waitForOffMeshLinks > 0) {
            waitForOffMeshLinks -= info.getTimeDelta();
            waitingForOffMeshLinks += info.getTimeDelta();
            if (waitingForOffMeshLinks > 2)
                    waitingForOffMeshLinks = 0;
            if (waitForOffMeshLinks > 0)
                    return false;		
        }
        return true;
    }

    public void wPrefsInit (List <WeaponPreferences> wPrefs) {
        wPrefs.add(new WeaponPreferences(UT2004ItemType.REDEEMER, 6, 0.9, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.ROCKET_LAUNCHER, 7, 0.6, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.ROCKET_LAUNCHER, 7, 0.55, false));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.SNIPER_RIFLE, 8, 0.7, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.FLAK_CANNON, 7, 0.7, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.FLAK_CANNON, 1, 0.2, false));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.MINIGUN, 6, 0.6, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.MINIGUN, 6, 0.55, false));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.LIGHTNING_GUN, 5, 0.5, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.SHOCK_RIFLE, 6, 0.55, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.SHOCK_RIFLE, 6, 0.55, false));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.LINK_GUN, 3, 0.75, false));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.LINK_GUN, 3, 0.75, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.BIO_RIFLE, 2, 0.3, true));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.BIO_RIFLE, 2, 0.3, false));
        wPrefs.add(new WeaponPreferences(UT2004ItemType.ASSAULT_RIFLE, 1, 0.2, false));
    }
}
