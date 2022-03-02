/*
 * Copyright (c) 2022 - The MegaMek Team. All Rights Reserved.
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
package megamek.common.alphaStrike;

import megamek.client.ui.swing.calculationReport.CalculationReport;
import megamek.common.Aero;
import megamek.common.Entity;

import java.util.Locale;
import java.util.function.Function;

import static megamek.common.alphaStrike.ASUnitType.*;
import static megamek.common.alphaStrike.BattleForceSPA.*;

public class ASPointValueConverter {

    static int getPointValue(ASConverter.ConversionData conversionData) {
        AlphaStrikeElement element = conversionData.element;
        Entity entity = conversionData.entity;
        CalculationReport report = conversionData.conversionReport;
        report.addSubHeader("Point Value:");

        if (element.isGround()) {
            double dmgS = getPointValueSDamage(element);
            double dmgM = getPointValueMDamage(element);
            double dmgL = getPointValueLDamage(element);
            double offensiveValue = dmgS + dmgM * 2 + dmgL;
            report.addLine("Damage", dmgS + " + 2 x " + dmgM + " + " + dmgL, "= ", offensiveValue);

            if (element.isAnyTypeOf(BM, PM)) {
                offensiveValue += 0.5 * element.getSize();
                report.addLine("Size", "+ " + element.getSize() + " / 2", "= ", offensiveValue);
            }

            if (element.getOverheat() >= 1) {
                offensiveValue += 1 + 0.5 * (element.getOverheat() - 1);
                report.addLine("Overheat", "+ " + (1 + 0.5 * (element.getOverheat() - 1)), "= ", offensiveValue);
            }

            offensiveValue += getGroundOffensiveSPAMod(conversionData);
            report.addLine("", "= ", offensiveValue);
            double blanketMod = getGroundOffensiveBlanketMod(conversionData);
            offensiveValue *= blanketMod;
            report.addLine("Offensive Value",
                    String.format(Locale.US, "%1$,.1f x %2$,.1f", offensiveValue, blanketMod),
                    "= ", offensiveValue);

            report.addEmptyLine();
            double defensiveValue = 0.125 * getHighestMove(element);
            report.addLine("Movement", getHighestMove(element) + " / 8", "= " + defensiveValue);

            if (element.movement.containsKey("j")) {
                defensiveValue += 0.5;
                report.addLine("Jump-capable", "+ 0.5", "= ", defensiveValue);
            }
            defensiveValue += getGroundDefensiveSPAMod(conversionData);
            report.addLine("", "= ", defensiveValue);

            defensiveValue += getDefensiveDIR(conversionData);
            report.addLine("", "= ", defensiveValue);

            double subTotal = offensiveValue + defensiveValue;
            report.addLine("Subtotal", "= ", subTotal);

            double bonus = agileBonus(element);
            if (c3Bonus(conversionData)) {

            }
            bonus += c3Bonus(conversionData) ? 0.05 * subTotal : 0;

            bonus -= subTotal * brawlerMalus(element);
            subTotal += bonus;
            subTotal += forceBonus(element);
            return Math.max(1, (int)Math.round(subTotal));

        } else if (element.isAnyTypeOf(AF, CF)
                || (element.isType(SV) && (entity instanceof Aero))) {
            int dmgS = element.getDmgS();
            int dmgM = element.getDmgM();
            int dmgL = element.getDmgL();
            double offensiveValue = dmgS + dmgM + dmgM + dmgL;

            if (element.getOverheat() >= 1) {
                double overheatFactor = 1 + 0.5 * (element.getOverheat() - 1);
                overheatFactor /= (dmgM + dmgL == 0) ? 2 : 1;
                offensiveValue += overheatFactor;
            }

            offensiveValue += getAeroOffensiveSPAMod(conversionData);
            offensiveValue *= getAeroOffensiveBlanketMod(element);
            offensiveValue = ASConverter.roundUpToHalf(offensiveValue);

            double defensiveValue = 0.25 * getHighestMove(element);
            defensiveValue += getHighestMove(element) >= 10 ? 1 : 0;
            defensiveValue += getAeroDefensiveSPAMod(element);
            defensiveValue += getAeroDefensiveFactors(element);

            double subTotal = offensiveValue + defensiveValue;
            subTotal += forceBonus(element);

            return Math.max(1, (int)Math.round(subTotal));
        }
        return 0;
    }

    static void adjustPVforSkill(AlphaStrikeElement element) {
        int multiplier = 1;
        if (element.getSkill() > 4) {
            if (element.getFinalPoints() > 14) {
                multiplier += (element.getFinalPoints() - 5) / 10;
            }
            element.points -= (element.getSkill() - 4) * multiplier;
        } else if (element.getSkill() < 4) {
            if (element.getFinalPoints() > 7) {
                multiplier += (element.getFinalPoints() - 3) / 5;
            }
            element.points += (4 - element.getSkill()) * multiplier;
        }
    }

    /** Returns the Ground Offensive SPA modifier, ASC p.139. */
    private static double getGroundOffensiveSPAMod(ASConverter.ConversionData conversionData) {
        AlphaStrikeElement element = conversionData.element;
        CalculationReport report = conversionData.conversionReport;

        double result = 0;
        result += processSPAMod("Offensive SPA", conversionData, TAG, e -> 0.5);
        result += processSPAMod("Offensive SPA", conversionData, LTAG, e -> 0.25);
        result += processSPAMod("Offensive SPA", conversionData, SNARC, e -> (double) element.getSPA(SNARC));
        result += processSPAMod("Offensive SPA", conversionData, INARC, e -> (double) element.getSPA(INARC));
        result += processSPAMod("Offensive SPA", conversionData, CNARC, e -> 0.5 * (double) element.getSPA(CNARC));
        result += processSPAMod("Offensive SPA", conversionData, TSM, e -> 1.0);
        result += processSPAMod("Offensive SPA", conversionData, ECS, e -> 0.25);
        result += processSPAMod("Offensive SPA", conversionData, MEL, e -> 0.5);
        result += processSPAMod("Offensive SPA", conversionData, MDS, e -> (double) element.getSPA(MDS));
        result += processSPAMod("Offensive SPA", conversionData, MTAS, e -> (double) element.getSPA(MTAS));
        result += processSPAMod("Offensive SPA", conversionData, BTAS, e -> 0.25 * (double) element.getSPA(BTAS));
        result += processSPAMod("Offensive SPA", conversionData, TSEMP, e -> 5 * (double) element.getSPA(TSEMP));
        result += processSPAMod("Offensive SPA", conversionData, TSEMPO, e -> Math.min(5.0, (int) element.getSPA(TSEMPO)));
        result += processSPAMod("Offensive SPA", conversionData, BT, e -> 0.5 * getHighestMove(element) * element.getSize());
        result += processSPAMod("Offensive SPA", conversionData, IATM, e -> (double) ((ASDamageVector) element.getSPA(IATM)).L.damage);
        result += processSPAMod("Offensive SPA", conversionData, OVL, e -> 0.25 * element.getOverheat());
        result += processSPAMod("Offensive SPA", conversionData, HT, e -> {
            ASDamageVector ht = (ASDamageVector) element.getSPA(HT);
            return Math.max(ht.S.damage, Math.max(ht.M.damage, ht.L.damage)) + ((ht.M.damage > 0) ? 0.5 : 0);
        });
        result += processSPAMod("Offensive SPA", conversionData, IF,
                e -> element.isMinimalIF() ? 0.5 : ((ASDamageVector)element.getSPA(IF)).S.damage);
        if (element.hasSPA(RHS)) {
            if (element.hasSPA(OVL)) {
                result += 1;
                report.addLine("Offensive SPA", "RHS and OVL", "+ 1");
            } else if (element.getOverheat() > 0) {
                result += 0.5;
                report.addLine("Offensive SPA", "RHS and OV > 0", "+ 0.5");
            } else {
                result += 0.25;
                report.addLine("Offensive SPA", "RHS", "+ 0.25");
            }
        }
        result += getArtyOffensiveSPAMod(conversionData);
        return result;
    }

    /** Returns the Artillery part of the Ground Offensive SPA modifier, ASC p.139. */
    private static double getArtyOffensiveSPAMod(ASConverter.ConversionData conversionData) {
        double result = 0;
        result += processSPAMod("Offensive SPA", conversionData, ARTAIS, e -> 12.0 * (int) e.getSPA(ARTAIS));
        result += processSPAMod("Offensive SPA", conversionData, ARTAC, e -> 12.0 * (int) e.getSPA(ARTAC));
        result += processSPAMod("Offensive SPA", conversionData, ARTT, e -> 6.0 * (int) e.getSPA(ARTT));
        result += processSPAMod("Offensive SPA", conversionData, ARTS, e -> 12.0 * (int) e.getSPA(ARTS));
        result += processSPAMod("Offensive SPA", conversionData, ARTBA, e -> 6.0 * (int) e.getSPA(ARTBA));
        result += processSPAMod("Offensive SPA", conversionData, ARTLTC, e -> 12.0 * (int) e.getSPA(ARTLTC));
        result += processSPAMod("Offensive SPA", conversionData, ARTSC, e -> 6.0 * (int) e.getSPA(ARTSC));
        result += processSPAMod("Offensive SPA", conversionData, ARTCM5, e -> 30.0 * (int) e.getSPA(ARTCM5));
        result += processSPAMod("Offensive SPA", conversionData, ARTCM7, e -> 54.0 * (int) e.getSPA(ARTCM7));
        result += processSPAMod("Offensive SPA", conversionData, ARTCM9, e -> 72.0 * (int) e.getSPA(ARTCM9));
        result += processSPAMod("Offensive SPA", conversionData, ARTCM12, e -> 93.0 * (int) e.getSPA(ARTCM12));
        result += processSPAMod("Offensive SPA", conversionData, ARTLT, e -> 27.0 * (int) e.getSPA(ARTLT));
        result += processSPAMod("Offensive SPA", conversionData, ARTTC, e -> 3.0 * (int) e.getSPA(ARTTC));
        return result;
    }

    /**
     * A helper method for SPA modifiers of various types. Returns the modifier (or 0)
     * and adds a line to the report.
     *
     * @param type A String that will be printed as the first report line element
     * @param conversionData The ConversionData object holding entity, element and report
     * @param spa The SPA to be checked
     * @param spaMod A Function object that returns the modifier for the element and SPA. This is
     *               called only when the given SPA is present on the element
     * @return The result of the given spaMod function if the SPA is present on the element, 0 otherwise.
     */
    private static double processSPAMod(String type, ASConverter.ConversionData conversionData, BattleForceSPA spa,
                                        Function<AlphaStrikeElement, Double> spaMod) {
        AlphaStrikeElement element = conversionData.element;
        if (element.hasSPA(spa)) {
            double modifier = spaMod.apply(element);
            conversionData.conversionReport.addLine(type,
                    AlphaStrikeElement.formatSPAString(spa, element.getSPA(spa)),
                    "+ ", modifier);
            return modifier;
        } else {
            return 0;
        }
    }

    private static double getGroundOffensiveBlanketMod(ASConverter.ConversionData conversionData) {
        AlphaStrikeElement element = conversionData.element;
        CalculationReport report = conversionData.conversionReport;

        double result = 1;
        report.addLine("Blanket Modifier", "1");
        result += processSPAMod("Blanket Modifier", conversionData, VRT, e -> 0.1);
        result += processSPAMod("Blanket Modifier", conversionData, SHLD, e -> -0.1);
        if (element.isAnyTypeOf(SV, IM)) {
            if (!element.hasAnySPAOf(AFC, BFC)) {
                result += -0.2;
                report.addLine("", "No AFC/BFC", "- 0.2");
            }
        } else {
            result += processSPAMod("Blanket Modifier", conversionData, BFC, e -> -0.1);
        }
        report.addLine("Blanket Modifier", "= ", result);
        return result;
    }

    private static double getAeroOffensiveSPAMod(ASConverter.ConversionData conversionData) {
        AlphaStrikeElement element = conversionData.element;
        Entity entity = conversionData.entity;
        CalculationReport report = conversionData.conversionReport;

        double result = element.hasSPA(SNARC) ? 1 : 0;
        result += element.hasSPA(INARC) ? 1 : 0;
        result += element.hasSPA(CNARC) ? 0.5 : 0;
        result += element.hasSPA(BT) ? 0.5 * getHighestMove(element) * element.getSize() : 0;
        result += element.hasSPA(OVL) ? 0.25 * element.getOverheat() : 0;
        if (element.hasSPA(HT)) {
            ASDamageVector ht = (ASDamageVector) element.getSPA(HT);
            result += Math.max(ht.S.damage, Math.max(ht.M.damage, ht.L.damage));
            result += ht.M.damage > 0 ? 0.5 : 0;
        }
        result += getArtyOffensiveSPAMod(conversionData);
        return result;
    }

    private static double getAeroOffensiveBlanketMod(AlphaStrikeElement element) {
        double result = 1;
        result += element.hasSPA(ATAC) ? 0.1 : 0;
        result += element.hasSPA(VRT) ? 0.1 : 0;
        result -= element.hasSPA(BFC) ? 0.1 : 0;
        result -= element.hasSPA(SHLD) ? 0.1 : 0; // So says the AS Companion
        result -= element.hasSPA(DRO) ? 0.1 : 0;
        result -= (element.isType(SV) && !element.hasAnySPAOf(AFC, BFC)) ? 0.2 : 0;
        return result;
    }

    private static double getGroundDefensiveSPAMod(ASConverter.ConversionData conversionData) {
        AlphaStrikeElement element = conversionData.element;
        CalculationReport report = conversionData.conversionReport;

        double armorThird = Math.floor((double)element.getFinalArmor() / 3);
        double barFactor = element.hasSPA(BAR) ? 0.5 : 1;

        double result = 0;
        result += processSPAMod("Defensive SPA", conversionData, ABA, e -> 0.5);
        result += processSPAMod("Defensive SPA", conversionData, AMS, e -> 1.0);
        result += processSPAMod("Defensive SPA", conversionData, FR, e -> 0.5);
        result += processSPAMod("Defensive SPA", conversionData, RAMS, e -> 1.25);
        result += processSPAMod("Defensive SPA", conversionData, BHJ2, e -> barFactor * armorThird);
        result += processSPAMod("Defensive SPA", conversionData, RCA, e -> barFactor * armorThird);
        result += processSPAMod("Defensive SPA", conversionData, SHLD, e -> barFactor * armorThird);
        result += processSPAMod("Defensive SPA", conversionData, BHJ3, e -> barFactor * 1.5 * armorThird);
        result += processSPAMod("Defensive SPA", conversionData, BRA, e -> barFactor * 0.75 * armorThird);
        result += processSPAMod("Defensive SPA", conversionData, IRA, e -> barFactor * 0.5 * armorThird);
        if (element.hasSPA(CR) && (element.getStructure() >= 3)) {
            result += 0.25;
            report.addLine("Defensive SPA", "CR", "+ 0.25");
        }
        if (element.hasSPA(ARM) && (element.structure > 1)) {
            result += 0.5;
            report.addLine("Defensive SPA", "ARM", "+ 0.5");
        }
        return result;
    }

    private static double getAeroDefensiveSPAMod(AlphaStrikeElement element) {
        double result = element.hasSPA(PNT) ? (int)element.getSPA(PNT) : 0;
        result += element.hasSPA(STL) ? 2 : 0;
        if (element.hasSPA(RCA)) {
            double armorThird = Math.floor((double)element.getFinalArmor() / 3);
            double barFactor = element.hasSPA(BAR) ? 0.5 : 1;
            result += armorThird * barFactor;
        }
        return result;
    }

    private static double getDefensiveDIR(ASConverter.ConversionData conversionData) {
        AlphaStrikeElement element = conversionData.element;
        CalculationReport report = conversionData.conversionReport;

        double armorMultiplier = getArmorFactorMult(conversionData);
        double result = element.getFinalArmor() * armorMultiplier;
        report.addLine("Defensive DIR Armor",  element.getFinalArmor() + " x " + armorMultiplier, "= ", result);

        double strucMultiplier = getStructureMult(conversionData);
        result += element.getStructure() * strucMultiplier;
        report.addLine("Defensive DIR Structure", element.getStructure() + " x " + strucMultiplier, "= ", result);

        result *= getDefenseFactor(conversionData);
        report.addLine("Defensive DIR Def Factor", "= ", result);

        result = 0.5 * Math.round(result * 2);
        report.addLine("Defensive DIR", "round to nearest half", "= ", result);
        return result;
    }

    private static double getArmorFactorMult(ASConverter.ConversionData conversionData) {
        AlphaStrikeElement element = conversionData.element;
        CalculationReport report = conversionData.conversionReport;

        double result = 2;
        if (element.asUnitType == CV) {
            if (element.getMovementModes().contains("t") || element.getMovementModes().contains("n")) {
                result = 1.8;
                report.addLine("Armor Multiplier", "Tracked/Naval", "1.8");
            } else if (element.getMovementModes().contains("h") || element.getMovementModes().contains("w")) {
                result = 1.7;
                report.addLine("Armor Multiplier", "Hover/Wheeled", "1.7");
            } else if (element.getMovementModes().contains("v") || element.getMovementModes().contains("g")) {
                result = 1.5;
                report.addLine("Armor Multiplier", "VTOL/WiGE", "1.5");
            }
            result += element.hasSPA(ARS) ? 0.1 : 0;
            report.addLine("", "Armored Motive System", "+ 0.1");
        }
        if (result == 2) {
            report.addLine("Armor Multiplier", "", "2");
        }
        if (element.hasSPA(BAR)) {
            result /= 2;
            report.addLine("BAR", "/ 2", "");
        }
        return result;
    }

    private static double getStructureMult(ASConverter.ConversionData conversionData) {
        AlphaStrikeElement element = conversionData.element;
        CalculationReport report = conversionData.conversionReport;

        if (element.asUnitType == BA || element.asUnitType == CI) {
            report.addLine("Structure Multiplier", "Infantry", "2");
            return 2;
        } else  if (element.asUnitType == IM || element.hasSPA(BAR)) {
            report.addLine("Structure Multiplier", "IM or BAR", "0.5");
            return 0.5;
        } else  {
            report.addLine("Structure Multiplier", "", "1");
            return 1;
        }
    }

    private static double getAeroDefensiveFactors(AlphaStrikeElement element) {
        double barFactor = element.hasSPA(BAR) ? 0.5 : 1;
        double thresholdMultiplier = Math.min(1.3 + 0.1 * element.getThreshold(), 1.9);
        double result = thresholdMultiplier * barFactor * element.getFinalArmor();
        result += element.getStructure();
        return result;
    }

    private static double getDefenseFactor(ASConverter.ConversionData conversionData) {
        CalculationReport report = conversionData.conversionReport;
        AlphaStrikeElement element = conversionData.element;

        double result = 0;
        double movemod = getMovementMod(conversionData);
        if (element.hasSPA(MAS) && (3 > movemod)) {
            result += 3;
            report.addLine("MAS and MoveMod > 3", "+ 3", "");
        } else if (element.hasSPA(LMAS) && (2 > movemod)) {
            result += 2;
            report.addLine("LMAS and MoveMod > 2", "+ 2", "");
        } else {
            result += movemod;
        }
        if (element.isAnyTypeOf(BA, PM)) {
            result += 1;
            report.addLine("BA or PM", "+ 1", "");
        }
        if ((element.isType(CV)) && (element.getMovementModes().contains("g")
                || element.getMovementModes().contains("v"))) {
            result++;
            report.addLine("VTOL or WiGE CV", "+ 1", "");
        }
        if (element.hasSPA(STL)) {
            result += 1;
            report.addLine("STL", "+ 1", "");
        }
        if (element.hasAnySPAOf(LG, SLG, VLG)) {
            result += -1;
            report.addLine("LG, SLG or VLG", "- 1", "");
        }
        double defFactor = 1 + (result <= 2 ? 0.1 : 0.25) * Math.max(result, 0);
        report.addLine("Defense Factor",
                "1 + " + ((result <= 2) ? 0.1 : 0.25) + " x " + Math.max(result, 0),
                "= " + defFactor);
        return defFactor;
    }

    /**
     * Returns the movement modifier (for the Point Value DIR calculation only),
     * AlphaStrike Companion Errata v1.4, p.17
     */
    private static double getMovementMod(ASConverter.ConversionData conversionData) {
        CalculationReport report = conversionData.conversionReport;
        AlphaStrikeElement element = conversionData.element;

        int highestNonJumpMod = -1;
        int highestJumpMod = -1;
        for (String mode : element.getMovementModes()) {
            int mod = ASConverter.tmmForMovement(element.getMovement(mode), conversionData);
            if (mode.equals("j")) {
                highestJumpMod = mod;
            } else {
                highestNonJumpMod = Math.max(highestNonJumpMod, mod);
            }
        }
        double result = (highestNonJumpMod == -1) ? highestJumpMod : highestNonJumpMod;
        report.addLine("Correct Movement Modifier TMM", "", result);
        result += element.isInfantry() && element.isJumpCapable() ? 1 : 0;
        if (element.isInfantry() && element.isJumpCapable()) {
            result += 1;
            report.addLine("Jump Capable Infantry", "+ 1", "");
        }
        report.addLine("DIR Movement modifier", "", result);
        return result;
    }

    /**
     * Determines the Brawler Malus, AlphaStrike Companion Errata v1.4, p.17
     * This is 0 if not applicable.
     */
    private static double brawlerMalus(AlphaStrikeElement element) {
        int move = getHighestMove(element);
        if (move >= 2 && !element.hasAnySPAOf(BT, ARTS, C3BSM, C3BSS, C3EM,
                C3I, C3M, C3S, AC3, NC3, NOVA, C3RS, ECM, AECM, ARTAC, ARTAIS, ARTBA, ARTCM12, ARTCM5, ARTCM7,
                ARTCM9, ARTLT, ARTLTC, ARTSC, ARTT, ARTTC)) {
            double dmgS = getPointValueSDamage(element);
            double dmgM = getPointValueMDamage(element);
            double dmgL = getPointValueLDamage(element);

            boolean onlyShortRange = (dmgM + dmgL) == 0 && (dmgS > 0);
            boolean onlyShortMediumRange = (dmgL == 0) && (dmgS + dmgM > 0);
            if ((move >= 6) && (move <= 10) && onlyShortRange) {
                return 0.25;
            } else if ((move < 6) && onlyShortRange) {
                return 0.5;
            } else if ((move < 6) && onlyShortMediumRange) {
                return 0.25;
            }
        }
        return 0;
    }

    /**
     * Determines the Agile Bonus, AlphaStrike Companion Errata v1.4, p.17
     * This is 0 if not applicable.
     */
    private static double agileBonus(AlphaStrikeElement element) {
        double result = 0;
        if (element.getTMM() >= 2) {
            double dmgS = element.isMinimalDmgS() ? 0.5 : element.getDmgS();
            double dmgM = element.isMinimalDmgM() ? 1 : element.getDmgM();
            if (dmgM > 0) {
                result = (element.getTMM() - 1) * dmgM;
            } else if (element.getTMM() >= 3) {
                result = (element.getTMM() - 2) * dmgS;
            }
        }
        return roundToHalf(result);
    }

    /** C3 Bonus, AlphaStrike Companion Errata v1.4, p.17 */
    private static boolean c3Bonus(ASConverter.ConversionData conversionData) {
        AlphaStrikeElement element = conversionData.element;
        if (element.hasAnySPAOf(C3BSM, C3BSS, C3EM, C3I, C3M, C3S, AC3, NC3, NOVA)) {
            return true;
        }
        return false;
    }

    private static double forceBonus(AlphaStrikeElement element) {
        double result = element.hasSPA(AECM) ? 3 : 0;
        result += element.hasSPA(BH) ? 2 : 0;
        result += element.hasSPA(C3RS) ? 2 : 0;
        result += element.hasSPA(ECM) ? 2 : 0;
        result += element.hasSPA(LECM) ? 0.5 : 0;
        result += element.hasSPA(MHQ) ? (int)element.getSPA(MHQ) : 0;
        result += element.hasSPA(PRB) ? 1 : 0;
        result += element.hasSPA(LPRB) ? 1 : 0;
        result += element.hasSPA(RCN) ? 2 : 0;
        result += element.hasSPA(TRN) ? 2 : 0;
        return result;
    }

    private static double getPointValueSDamage(AlphaStrikeElement element) {
        return element.isMinimalDmgS() ? 0.5 : element.getDmgS();
    }

    private static double getPointValueMDamage(AlphaStrikeElement element) {
        return element.isMinimalDmgM() ? 1 : element.getDmgM();
    }

    private static double getPointValueLDamage(AlphaStrikeElement element) {
        return element.isMinimalDmgL() ? 0.5 : element.getDmgL();
    }

    private static int getHighestMove(AlphaStrikeElement element) {
        return element.movement.values().stream().mapToInt(m -> m).max().orElse(0);
    }

    static double roundToHalf(double number) {
        return 0.5 * Math.round(number * 2);
    }

    // Make non-instantiable
    private ASPointValueConverter() { }
}
