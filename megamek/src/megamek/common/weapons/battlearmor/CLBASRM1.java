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

import megamek.common.TechConstants;
import megamek.common.weapons.SRMWeapon;


/**
 * @author Sebastian Brocks
 */
public class CLBASRM1 extends SRMWeapon {

    /**
     *
     */
    private static final long serialVersionUID = 7660446177480426870L;

    /**
     *
     */
    public CLBASRM1() {
        super();
        name = "SRM 1";
        setInternalName("CLBASRM1");
        addLookupName("Clan BA SRM-1");
        addLookupName("Clan BA SRM 1");
        rackSize = 1;
        shortRange = 3;
        mediumRange = 6;
        longRange = 9;
        extremeRange = 12;
        bv = 15;
        flags = flags.or(F_NO_FIRES).or(F_BA_WEAPON).andNot(F_MECH_WEAPON).andNot(F_TANK_WEAPON).andNot(F_AERO_WEAPON).andNot(F_PROTO_WEAPON);
        tonnage = .035f;
        criticals = 1;
        cost = 5000;
        introDate = 2860;
		techLevel.put(2860, TechConstants.T_CLAN_EXPERIMENTAL);
		techLevel.put(2868, TechConstants.T_CLAN_ADVANCED);	
		techLevel.put(2870, TechConstants.T_CLAN_TW);	
		availRating = new int[] { RATING_X ,RATING_D ,RATING_C ,RATING_B};	
		techRating = RATING_F;
		rulesRefs = "261, TM";
    }
}
