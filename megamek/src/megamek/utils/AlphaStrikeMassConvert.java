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
package megamek.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

import megamek.common.*;
import megamek.common.loaders.EntityLoadingException;

public class AlphaStrikeMassConvert {
    
    private static final ASUnitType typeFilter = ASUnitType.BM;  

    public static void main(String[] args) throws EntityLoadingException {
        System.out.println("Starting AlphaStrike conversion for the unit type " + typeFilter);
        StringBuilder table = new StringBuilder(clipboardHeaderString());
        MechSummary[] units = MechSummaryCache.getInstance().getAllMechs();
        for (MechSummary unit : units) {
            Entity entity = new MechFileParser(unit.getSourceFile(), unit.getEntryName()).getEntity();
            if (!AlphaStrikeConverter.canConvert(entity)) {
                continue;
            }
            if ((entity instanceof Aero && entity.isFighter()) || entity instanceof Mech
                    || entity instanceof Tank || entity instanceof Infantry || entity instanceof Protomech) {
                System.out.println(entity.getShortName());
                AlphaStrikeElement ase = AlphaStrikeConverter.convert(entity);
//                if (ase.getUnitType() == typeFilter) {
                    table.append(clipboardElementString(ase));
//                }
            }
        }
        StringSelection stringSelection = new StringSelection(table.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        System.out.println("Finished.");
        System.exit(0);

    }
    
    private static String clipboardHeaderString() {
        List<String> headers = new ArrayList<>();
        headers.add("Chassis");
        headers.add("Model");
        headers.add("Role");
        headers.add("Type");
        headers.add("SZ");
        headers.add("MV");
        headers.add("Arm");
        headers.add("Str");
        headers.add("Thr");
        headers.add("Dmg S/M/L");
        headers.add("OV");
        headers.add("PV");
        headers.add("Specials");
        headers.add("MUL ID");
        headers.add("\n");
        return String.join("\t", headers);
    }

    /** Returns a String representing the entities to export to the clipboard. */
    private static StringBuilder clipboardElementString(AlphaStrikeElement element) {
        List<String> stats = new ArrayList<>();
        stats.add(element.getChassis());
        stats.add(element.getModel());
        stats.add(element.getRole().toString());
        stats.add(element.getUnitType().toString());
        stats.add(element.getSize() + "");
        stats.add(element.getMovementAsString());
        stats.add(element.getFinalArmor() + "");
        stats.add(element.getStructure() + "");
        stats.add(element.usesThreshold() ? element.getFinalThreshold() + "" : " ");
        if (element.usesSML()) {
            stats.add(element.getStandardDamage() + "");
        } else if (element.usesSMLE()) {
            stats.add(element.getStandardDamage() + "");
        }
        stats.add(element.getOverheat() + "");
        stats.add(element.getFinalPoints()+"");
        stats.add(element.getSpecialsString());
        stats.add(element.getMulId() + "");
        stats.add("\n");
        return new StringBuilder(String.join("\t", stats));
    }

}
