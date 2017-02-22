/**
 * MegaMek - Copyright (C) 2004 Ben Mazur (bmazur@sev.org)
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
import megamek.common.TechProgression;
import megamek.common.weapons.LaserWeapon;

/**
 * @author Andrew Hunter
 */
public class CLBAHeavyMediumLaser extends LaserWeapon {
    /**
     * 
     */
    private static final long serialVersionUID = -3836305728245548205L;

    /**
     * 
     */
    public CLBAHeavyMediumLaser() {
        super();
        this.name = "Heavy Medium Laser";
        this.setInternalName("CLBAHeavyMediumLaser");
        this.addLookupName("Clan BA Medium Heavy Laser");
        this.heat = 7;
        this.damage = 10;
        this.toHitModifier = 1;
        this.shortRange = 3;
        this.mediumRange = 6;
        this.longRange = 9;
        this.extremeRange = 12;
        this.waterShortRange = 2;
        this.waterMediumRange = 4;
        this.waterLongRange = 6;
        this.waterExtremeRange = 8;
        this.tonnage = 1.0f;
        this.criticals = 4;
        this.bv = 76;
        this.cost = 100000;
        this.shortAV = 10;
        this.maxRange = RANGE_SHORT;
        flags = flags.or(F_NO_FIRES).or(F_BA_WEAPON).andNot(F_MECH_WEAPON).andNot(F_TANK_WEAPON).andNot(F_AERO_WEAPON).andNot(F_PROTO_WEAPON);
        introDate = 3052;
        techLevel.put(3052, TechConstants.T_CLAN_EXPERIMENTAL);
        techLevel.put(3059, TechConstants.T_CLAN_ADVANCED);
        techLevel.put(3062, TechConstants.T_CLAN_TW);
        availRating = new int[] { RATING_X ,RATING_X ,RATING_D ,RATING_D};
        techRating = RATING_F;
        rulesRefs = "258, TM";
        techProgression.setTechBase(TechProgression.TECH_BASE_CLAN);
        techProgression.setClanProgression(3052, 3059, 3062);
        techProgression.setTechRating(RATING_F);
        techProgression.setAvailability( new int[] { RATING_X, RATING_X, RATING_D, RATING_D });
    }
}
