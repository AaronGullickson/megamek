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
 * Created on Sep 7, 2005
 *
 */
package megamek.common.weapons;

import megamek.common.BombType;
import megamek.common.TechConstants;
import megamek.common.TechProgression;

/**
 * @author Sebastian Brocks
 */
public class CLBombTAG extends TAGWeapon {

    /**
     * 
     */
    private static final long serialVersionUID = -7692653575300083613L;

    public CLBombTAG() {
        super();

        this.name = "TAG Pod";
        this.setInternalName("Clan " + BombType.getBombWeaponName(BombType.B_TAG));
        this.tonnage = 0;
        this.criticals = 0;
        this.hittable = false;
        this.spreadable = false;
        this.heat = 0;
        this.damage = 0;
        this.shortRange = 5;
        this.mediumRange = 10;
        this.longRange = 15;
        this.extremeRange = 20;
        this.bv = 0;
        this.cost = 50000;
        introDate = 2807;
        techLevel.put(2807, TechConstants.T_CLAN_TW);
        availRating = new int[] { RATING_X,RATING_F ,RATING_D ,RATING_D};
        techRating = RATING_E;
        rulesRefs = "238, TM";

        techProgression.setTechBase(TechProgression.TECH_BASE_CLAN);
        techProgression.setClanProgression(DATE_NONE, DATE_NONE, 2807);
        techProgression.setTechRating(RATING_E);
        techProgression.setAvailability( new int[] { RATING_X, RATING_F, RATING_D, RATING_D });
    }
}
