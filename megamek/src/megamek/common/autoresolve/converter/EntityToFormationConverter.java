/*
 * Copyright (c) 2025 - The MegaMek Team. All Rights Reserved.
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
package megamek.common.autoresolve.converter;

import megamek.client.ui.swing.calculationReport.DummyCalculationReport;
import megamek.common.Entity;
import megamek.common.ForceAssignable;
import megamek.common.alphaStrike.ASDamage;
import megamek.common.alphaStrike.ASDamageVector;
import megamek.common.alphaStrike.ASRange;
import megamek.common.alphaStrike.AlphaStrikeElement;
import megamek.common.alphaStrike.conversion.ASConverter;
import megamek.common.autoresolve.acar.SimulationContext;
import megamek.common.autoresolve.component.Formation;
import megamek.common.force.Force;
import megamek.common.force.Forces;
import megamek.common.strategicBattleSystems.BaseFormationConverter;
import megamek.common.strategicBattleSystems.SBFUnit;
import megamek.common.strategicBattleSystems.SBFUnitConverter;
import megamek.logging.MMLogger;

import java.util.ArrayList;

public class EntityToFormationConverter extends BaseFormationConverter<Formation> {
    private static final MMLogger logger = MMLogger.create(EntityToFormationConverter.class);
    private final Entity entity;

    public EntityToFormationConverter(Entity entity, SimulationContext game) {
        super(null, game, new Formation(), new DummyCalculationReport());
        this.entity = entity;
    }

    @Override
    public Formation convert() {
        var thisUnit = new ArrayList<AlphaStrikeElement>();
        var element = ASConverter.convertAndKeepRefs(entity);
        if (element != null) {
            thisUnit.add(element);
        } else {
            var msg = String.format("Could not convert entity %s to AS element", entity);
            logger.error(msg);
            return null;
        }

        SBFUnit convertedUnit = new SBFUnitConverter(thisUnit, entity.getDisplayName(), report).createSbfUnit();
        formation.addUnit(convertedUnit);
        formation.setOwnerId(entity.getOwnerId());
        formation.setName(entity.getDisplayName());
        formation.setStdDamage(setStdDamageForFormation(formation));
        for (var unit : formation.getUnits()) {
            var health = 0;
//            health += entity.getTotalOArmor();
//            health += entity.getTotalInternal();
            for (var el : unit.getElements()) {
                health += el.getFullArmor() + el.getFullStructure();
            }
            unit.setArmor(health);
            unit.setCurrentArmor(health);
        }
        return formation;
    }

    private ASDamageVector setStdDamageForFormation(Formation formation) {
        // Get the list of damage objects from the units in the formation
        var damages = formation.getUnits().stream().map(SBFUnit::getDamage).toList();
        var size = damages.size();

        // Initialize accumulators for the different damage types
        var l = 0;
        var m = 0;
        var s = 0;

        // Sum up the damage values for each type
        for (var damage : damages) {
            l += damage.getDamage(ASRange.LONG).damage;
            m += damage.getDamage(ASRange.MEDIUM).damage;
            s += damage.getDamage(ASRange.SHORT).damage;
        }
        return new ASDamageVector(
            new ASDamage(Math.ceil((double) s / size)),
            new ASDamage(Math.ceil((double) m / size)),
            new ASDamage(Math.ceil((double) l / size)),
            null,
            size,
            true);
    }

}
