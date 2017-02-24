/**
 * MegaMek - Copyright (C) 2004,2005 Ben Mazur (bmazur@sev.org)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */
/*
 * Created on Sep 25, 2004
 */
package megamek.common.weapons;

import megamek.common.AmmoType;
import megamek.common.EquipmentType;
import megamek.common.TechConstants;
import megamek.common.TechAdvancement;

/**
 * @author Andrew Hunter
 */
public class CLImprovedAC5 extends ACWeapon {
    /**
     * 
     */
    private static final long serialVersionUID = 8756042527483383101L;

    /**
     * 
     */
    public CLImprovedAC5() {
        super();

        name = "Improved Autocannon/5";
        setInternalName("Improved Autocannon/5");
        addLookupName("CLIMPAC5");
        heat = 1;
        damage = 5;
        rackSize = 5;
        minimumRange = 3;
        shortRange = 6;
        mediumRange = 12;
        longRange = 18;
        extremeRange = 24;
        tonnage = 7.0f;
        criticals = 2;
        bv = 70;
        cost = 125000;
        shortAV = 5;
        medAV = 5;
        maxRange = RANGE_MED;
        explosionDamage = damage;
        ammoType = AmmoType.T_AC_IMP;
        introDate = 2810;
        extinctDate = 2833;
        reintroDate = 3080;
        techLevel.put(2810, TechConstants.T_CLAN_ADVANCED);   ///ADV
        techLevel.put(2818, TechConstants.T_CLAN_TW);   ///COMMON
        availRating = new int[] { RATING_X, RATING_C, RATING_X, RATING_X };
        techRating = RATING_E;
        rulesRefs = "96, IO";
        techAdvancement.setTechBase(TechAdvancement.TECH_BASE_CLAN);
        techAdvancement.setClanAdvancement(DATE_NONE, 2810, 2818, 2833, 3080);
        techAdvancement.setTechRating(RATING_E);
        techAdvancement.setAvailability( new int[] { RATING_X, RATING_C, RATING_X, RATING_X });
    }
}
