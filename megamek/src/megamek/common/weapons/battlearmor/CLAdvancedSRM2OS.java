/**
 * MegaMek - Copyright (C) 2005 Ben Mazur (bmazur@sev.org)
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */
package megamek.common.weapons.battlearmor;

import megamek.common.TechAdvancement;
import megamek.common.weapons.AdvancedSRMWeapon;
/**
 * @author Sebastian Brocks
 */
public class CLAdvancedSRM2OS extends AdvancedSRMWeapon {

    /**
     *
     */
    private static final long serialVersionUID = 5607708375304559535L;

    /**
     *
     */
    public CLAdvancedSRM2OS() {
        super();
         name = "Advanced SRM 2 (OS)";
        setInternalName("CLAdvancedSRM2OS");
        rackSize = 2;
        shortRange = 4;
        mediumRange = 8;
        longRange = 12;
        extremeRange = 16;
        bv = 6;
        flags = flags.or(F_NO_FIRES).or(F_BA_WEAPON).or(F_ONESHOT).andNot(F_MECH_WEAPON).andNot(F_TANK_WEAPON).andNot(F_AERO_WEAPON).andNot(F_PROTO_WEAPON);
        tonnage = .07;
        criticals = 3;
        cost = 15000;

        techAdvancement.setTechBase(TechAdvancement.TECH_BASE_CLAN);
        techAdvancement.setClanAdvancement(3047, 3056, 3062);
        techAdvancement.setTechRating(RATING_F);
        techAdvancement.setAvailability( new int[] { RATING_X, RATING_X, RATING_F, RATING_D });
    }
}
