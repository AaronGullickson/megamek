/*
 * MegaMek - Copyright (C) 2004, 2005 Ben Mazur (bmazur@sev.org)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */
package megamek.common.weapons;

import java.util.Vector;

import megamek.common.*;
import megamek.common.actions.WeaponAttackAction;
import megamek.common.planetaryconditions.PlanetaryConditions;
import megamek.server.totalwarfare.TWGameManager;

/**
 * @author Sebastian Brocks
 * Created on Oct 15, 2004
 */
public class MPodHandler extends LBXHandler {
    private static final long serialVersionUID = -1591751929178217495L;

    /**
     * @param t
     * @param w
     * @param g
     * @param m
     */
    public MPodHandler(ToHitData t, WeaponAttackAction w, Game g, TWGameManager m) {
        super(t, w, g, m);
        sSalvoType = " pellet(s) ";
    }

    /*
     * (non-Javadoc)
     * 
     * @see megamek.common.weapons.WeaponHandler#calcHits(Vector<Report>
     * vPhaseReport)
     */
    @Override
    protected int calcHits(Vector<Report> vPhaseReport) {
        // conventional infantry gets hit in one lump
        // BAs do one lump of damage per BA suit
        if (target.isConventionalInfantry()) {
            return 1;
        }
        int shots = 15;
        if (nRange == 2) {
            shots = 10;
        } else if (nRange == 3) {
            shots = 5;
        } else if (nRange == 4) {
            shots = 2;
        }

        int hitMod = 0;
        if (bGlancing) {
            hitMod -= 4;
        }
        
        if (bLowProfileGlancing) {
            hitMod -= 4;
        }

        PlanetaryConditions conditions = game.getPlanetaryConditions();
        if (conditions.getEMI().isEMI()) {
            hitMod -= 2;
        }

        int shotsHit = allShotsHit() ? shots : Compute.missilesHit(shots,
                hitMod);

        Report r = new Report(3325);
        r.subject = subjectId;
        r.add(shotsHit);
        r.add(sSalvoType);
        r.add(toHit.getTableDesc());
        r.newlines = 0;
        vPhaseReport.addElement(r);
        r = new Report(3345);
        r.subject = subjectId;
        vPhaseReport.addElement(r);
        bSalvo = true;
        return shotsHit;
    }
}
