/**
 * MegaMek - Copyright (C) 2004,2005 Ben Mazur (bmazur@sev.org)
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
/*
 * Created on Sep 12, 2004
 *
 */
package megamek.common.weapons.battlearmor;

import megamek.common.EquipmentType;
import megamek.common.TechConstants;
import megamek.common.TechAdvancement;
import megamek.common.weapons.LaserWeapon;

/**
 * @author Andrew Hunter
 */
public class CLBAERMediumLaser extends LaserWeapon {
    /**
     *
     */
    private static final long serialVersionUID = -2063816167191977118L;

    /**
     *
     */
    public CLBAERMediumLaser() {
        super();
        name = "ER Medium Laser";
        setInternalName("CLBAERMediumLaser");
        addLookupName("Clan BA ER Medium Laser");
        heat = 5;
        damage = 7;
        shortRange = 5;
        mediumRange = 10;
        longRange = 15;
        extremeRange = 20;
        waterShortRange = 3;
        waterMediumRange = 7;
        waterLongRange = 10;
        waterExtremeRange = 14;
        tonnage = .8f;
        criticals = 3;
        bv = 108;
        cost = 80000;
        shortAV = 7;
        medAV = 7;
        maxRange = RANGE_MED;
        flags = flags.or(F_NO_FIRES).or(F_BA_WEAPON).andNot(F_MECH_WEAPON).andNot(F_TANK_WEAPON).andNot(F_AERO_WEAPON).andNot(F_PROTO_WEAPON);
        introDate = 2867;
        techLevel.put(2867, TechConstants.T_CLAN_EXPERIMENTAL);
        techLevel.put(2870, TechConstants.T_CLAN_ADVANCED);
        techLevel.put(2880, TechConstants.T_CLAN_TW);
        availRating = new int[] { RATING_X ,RATING_E ,RATING_D ,RATING_C};
        techRating = RATING_F;
        rulesRefs = "258, TM";

        techAdvancement.setTechBase(TechAdvancement.TECH_BASE_CLAN);
        techAdvancement.setClanAdvancement(2867, 2870, 2880);
        techAdvancement.setTechRating(RATING_F);
        techAdvancement.setAvailability( new int[] { RATING_X, RATING_E, RATING_D, RATING_C });
    }
}
