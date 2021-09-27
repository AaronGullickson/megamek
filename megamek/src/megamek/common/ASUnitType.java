/*
 * Copyright (c) 2021 - The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MegaMek.
 *
 * MegaMek is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MegaMek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MegaMek. If not, see <http://www.gnu.org/licenses/>.
 */
package megamek.common;

import java.util.Arrays;

/** Represents the AlphaStrike Element Types (ASC, page 91) */
public enum ASUnitType {

    BM, IM, PM, CV, SV, MS, BA, CI, AF, CF, SC, DS, DA, JS, WS, SS;

    /** Returns the AlphaStrike element type for the given entity or null if it has no AS equivalent. */
    //TODO: Add a NONE type to avoid null, maybe for buildings?
    public static ASUnitType getUnitType(Entity en) {
        if (en instanceof Mech) {
            return ((Mech)en).isIndustrial() ? IM : BM;
        } else if (en instanceof Protomech) {
            return PM;
        } else if (en instanceof Tank) {
            return en.isSupportVehicle() ? SV : CV;
        } else if (en instanceof BattleArmor) {
            return BA;
        } else if (en instanceof Infantry) {
            return CI;
        } else if (en instanceof SpaceStation) {
            return SS;
        } else if (en instanceof Warship) {
            return WS;
        } else if (en instanceof Jumpship) {
            return JS;
        } else if (en instanceof Dropship) {
            return ((Dropship)en).isSpheroid() ? DS : DA;
        } else if (en instanceof SmallCraft) {
            return SC;
        } else if (en instanceof FixedWingSupport) {
            return SV;
        } else if (en instanceof ConvFighter) {
            return CF;
        } else if (en instanceof Aero) {
            return AF;
        }
        return null;
    }

    /** Returns true if this AS Element Type is equal to any of the given Types. */
    public boolean isAnyOf(ASUnitType type, ASUnitType... furtherTypes) {
        return (this == type) || Arrays.stream(furtherTypes).anyMatch(t -> this == t);
    }

}
