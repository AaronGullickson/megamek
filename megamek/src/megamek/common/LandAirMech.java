/*
 * MegaMek - Copyright (C) 2000-2003 Ben Mazur (bmazur@sev.org)
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
package megamek.common;

import java.util.Map;

public class LandAirMech extends BipedMech {

    /**
     *
     */
    private static final long serialVersionUID = -8118673802295814548L;

    public static final int LAM_AVIONICS = 15;

    public static final int LAM_LANDING_GEAR = 16;

    public static final String systemNames[] =
        { "Life Support", "Sensors", "Cockpit", "Engine", "Gyro", null, null, "Shoulder", "Upper Arm", "Lower Arm", "Hand", "Hip", "Upper Leg", "Lower Leg", "Foot", "Avionics", "Landing Gear" };
    
    public static final int LAM_UNKNOWN  = -1;
    public static final int LAM_STANDARD = 0;
    public static final int LAM_BIMODAL  = 1;
    
    public static final String[] LAM_STRING = { "Standard", "Bimodal" };

    private int fuel;
    private int lamType;
    
    public LandAirMech(int inGyroType, int inCockpitType, int inLAMType) {
        super(inGyroType, inCockpitType);
        lamType = inLAMType;
        setTechLevel(TechConstants.T_IS_ADVANCED);
        setCritical(Mech.LOC_HEAD, 3, new CriticalSlot(CriticalSlot.TYPE_SYSTEM, LAM_AVIONICS));
        setCritical(Mech.LOC_LT, 1, new CriticalSlot(CriticalSlot.TYPE_SYSTEM, LAM_AVIONICS));
        setCritical(Mech.LOC_RT, 1, new CriticalSlot(CriticalSlot.TYPE_SYSTEM, LAM_AVIONICS));
        setCritical(Mech.LOC_LT, 0, new CriticalSlot(CriticalSlot.TYPE_SYSTEM, LAM_LANDING_GEAR));
        setCritical(Mech.LOC_RT, 0, new CriticalSlot(CriticalSlot.TYPE_SYSTEM, LAM_LANDING_GEAR));
        setCritical(Mech.LOC_CT, 10, new CriticalSlot(CriticalSlot.TYPE_SYSTEM, LAM_LANDING_GEAR));
        setFuel(80);
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public int getFuel() {
        return fuel;
    }

    @Override
    public String getSystemName(int index) {
        if (index == SYSTEM_GYRO) {
            return Mech.getGyroDisplayString(gyroType);
        }
        if (index == SYSTEM_COCKPIT) {
            return Mech.getCockpitDisplayString(cockpitType);
        }
        return systemNames[index];
    }

    @Override
    public String getRawSystemName(int index) {
        return systemNames[index];
    }

    public int getAirMechWalkMP(boolean gravity, boolean ignoremodulararmor) {
        return getJumpMP(gravity, ignoremodulararmor) * 3;
    }

    public int getAirMechRunMP(boolean gravity, boolean ignoremodulararmor) {
        return (int)Math.ceil(getAirMechWalkMP(gravity, ignoremodulararmor) * 1.5);
    }

    public int getFighterModeWalkMP(boolean gravity, boolean ignoremodulararmor) {
        return getJumpMP(gravity, ignoremodulararmor);
    }

    public int getFighterModeRunMP(boolean gravity, boolean ignoremodulararmor) {
        return (int)Math.ceil(getFighterModeWalkMP(gravity, ignoremodulararmor) * 1.5);
    }

    public int getAirMechWalkMP() {
        return getAirMechWalkMP(true,false);
    }

    public int getAirMechRunMP() {
        return getAirMechRunMP(true, false);
    }

    public int getFighterModeWalkMP() {
        return getFighterModeWalkMP(true, false);
    }

    public int getFighterModeRunMP() {
        return getFighterModeRunMP(true, false);
    }
    
    public int getLAMType() {
        return lamType;
    }
    
    public void setLAMType(int lamType) {
        this.lamType = lamType;
    }
    
    private final static TechAdvancement[] TA_LAM = {
            new TechAdvancement(TECH_BASE_ALL).setISAdvancement(2683, 2688, DATE_NONE, 3085)
                .setClanAdvancement(DATE_NONE, 2688, DATE_NONE, 2825)
                .setPrototypeFactions(F_TH).setProductionFactions(F_TH)
                .setTechRating(RATING_D).setAvailability(RATING_D, RATING_E, RATING_F, RATING_F), //standard
            new TechAdvancement(TECH_BASE_ALL).setISAdvancement(2680, 2684, DATE_NONE, 2781)
                .setClanAdvancement(DATE_NONE, 2684, DATE_NONE, 2801)
                .setPrototypeFactions(F_TH).setProductionFactions(F_TH)
                .setTechRating(RATING_E).setAvailability(RATING_E, RATING_F, RATING_X, RATING_X) //bimodal
    };
    
    @Override
    protected TechAdvancement getConstructionTechAdvancement() {
        return TA_LAM[lamType];
    }
    
    @Override
    public void setBattleForceMovement(Map<String,Integer> movement) {
    	super.setBattleForceMovement(movement);
    	if (lamType != LAM_BIMODAL) {
    	    movement.put("g", getAirMechWalkMP(true, false));
    	}
    	movement.put("a", getFighterModeWalkMP(true, false));
    }
    
    @Override
    public void setAlphaStrikeMovement(Map<String,Integer> movement) {
    	super.setBattleForceMovement(movement);
    	if (lamType != LAM_BIMODAL) {
    	    movement.put("g", getAirMechWalkMP(true, false) * 2);
    	}
    	movement.put("a", getFighterModeWalkMP(true, false));
    }
    
    @Override
    public void addBattleForceSpecialAbilities(Map<BattleForceSPA,Integer> specialAbilities) {
        super.addBattleForceSpecialAbilities(specialAbilities);
        int bombs = (int)getEquipment().stream().filter(m -> m.getType().hasFlag(MiscType.F_BOMB_BAY))
                .count();
        if (bombs > 0) {
            specialAbilities.put(BattleForceSPA.BOMB, bombs / 5);
        }
        if (lamType == LAM_BIMODAL) {
            specialAbilities.put(BattleForceSPA.BIM, null);
        } else {
            specialAbilities.put(BattleForceSPA.LAM, null);
        }
    }
    
    public String getLAMTypeString(int lamType) {
        if (lamType < 0 || lamType >= LAM_STRING.length) {
            return LAM_STRING[LAM_UNKNOWN];
        }
        return LAM_STRING[lamType];
    }
    
    public String getLAMTypeString() {
        return getLAMTypeString(getLAMType());
    }

    public static int getLAMTypeForString(String inType) {
        if ((inType == null) || (inType.length() < 1)) {
            return LAM_UNKNOWN;
        }
        for (int x = 0; x < LAM_STRING.length; x++) {
            if (inType.equals(LAM_STRING[x])) {
                return x;
            }
        }
        return LAM_UNKNOWN;
    }

    public long getEntityType(){
        return Entity.ETYPE_MECH | Entity.ETYPE_LAND_AIR_MECH;
    }
}
