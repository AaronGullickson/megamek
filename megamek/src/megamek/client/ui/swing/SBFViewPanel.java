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
package megamek.client.ui.swing;

import megamek.client.ui.swing.util.SpringUtilities;
import megamek.client.ui.swing.util.UIUtil;
import megamek.common.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class SBFViewPanel extends JPanel {

    public static final int DEFAULT_WIDTH = 360;
    public static final int DEFAULT_HEIGHT = 600;
    public static final int COLS = 12;
    
    private int elements = 0;

    public SBFViewPanel(Collection<SBFFormation> formations) {
        setLayout(new SpringLayout());

        for (SBFFormation formation : formations) {
            addFormationHeaders();
            addGridElement(formation.getName(), UIUtil.uiDarkBlue(), FlowLayout.LEFT);
            addGridElement(formation.getType().toString(), UIUtil.uiDarkBlue());
            addGridElement(formation.getSize() + "", UIUtil.uiDarkBlue());
            addGridElement(formation.getMovement() + "", UIUtil.uiDarkBlue());
            addGridElement(formation.getJumpMove() + "", UIUtil.uiDarkBlue());
            addGridElement(formation.getTrspMovement() + "", UIUtil.uiDarkBlue());
            addGridElement(formation.getTmm() + "", UIUtil.uiDarkBlue());
            addGridElement(formation.getTactics() + "", UIUtil.uiDarkBlue());
            addGridElement(formation.getMorale() + "", UIUtil.uiDarkBlue());
            addGridElement(formation.getSkill() + "", UIUtil.uiDarkBlue());
            addGridElement(formation.getPointValue() + "", UIUtil.uiDarkBlue());
            addGridElement(formation.getSpecialsString() + "", UIUtil.uiDarkBlue(), FlowLayout.LEFT);

            addUnitHeaders();
            int row = 1;
            for (SBFUnit unit : formation.getUnits()) {
                boolean oddRow = (row++ % 2) == 1;
                Color bgColor = oddRow ? UIUtil.alternateTableBGColor() : null;
                addGridElement("  " + unit.getName(), bgColor, FlowLayout.LEFT);
                addGridElement(unit.getType().toString(), bgColor);
                addGridElement(unit.getSize() + "", bgColor);
                addGridElement(unit.getMovement() + "", bgColor);
                addGridElement(unit.getJumpMove() + "", bgColor);
                addGridElement(unit.getTrspMovement() + "", bgColor);
                addGridElement(unit.getTmm() + "", bgColor);
                addGridElement(unit.getArmor() + "", bgColor);
                addGridElement(unit.getDamage() + "", bgColor);
                addGridElement(unit.getSkill() + "", bgColor);
                addGridElement(unit.getPointValue() + "", bgColor);
                addGridElement(unit.getSpecialsString(), bgColor, FlowLayout.LEFT);
            }
            addSpacer();
        }

        SpringUtilities.makeCompactGrid(this, elements / COLS, COLS, 5, 5, 1, 5);
    }

    private void addGridElement(String text) {
        addGridElement(text,null, FlowLayout.CENTER);
    }

    private void addGridElement(String text, Color bgColor) {
        addGridElement(text, bgColor, FlowLayout.CENTER);
    }

    private void addGridElement(String text, int alignment) {
        addGridElement(text,null, alignment);
    }

    private void addGridElement(String text, Color bgColor, int alignment) {
        var panel = new UIUtil.FixedYPanel(new FlowLayout(alignment));
        panel.setBackground(bgColor);
        panel.add(new JLabel(text));
        add(panel);
        elements++;
    }

    private void addHeader(String text, float alignment) {
        var panel = new UIUtil.FixedYPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        var textLabel = new JLabel(text);
        textLabel.setAlignmentX(alignment);
        textLabel.setFont(getFont().deriveFont(Font.BOLD));
        textLabel.setForeground(UIUtil.uiLightBlue());
        panel.add(Box.createVerticalStrut(8));
        panel.add(textLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(new JSeparator());
        add(panel);
        elements++;
    }

    private void addHeader(String text) {
        addHeader(text, JComponent.CENTER_ALIGNMENT);
    }

    private void addFormationHeaders() {
        addHeader(" Formation", JComponent.LEFT_ALIGNMENT);
        addHeader("Type");
        addHeader("Size");
        addHeader("Move");
        addHeader("JUMP");
        addHeader("T. Move");
        addHeader("TMM");
        addHeader("Tactics");
        addHeader("Morale");
        addHeader("Skill");
        addHeader("PV");
        addHeader("Specials");
    }

    private void addUnitHeaders() {
        addHeader("   Unit", JComponent.LEFT_ALIGNMENT);
        addHeader(" ");
        addHeader(" ");
        addHeader(" ");
        addHeader(" ");
        addHeader(" ");
        addHeader(" ");
        addHeader("Arm");
        addHeader("Dmg S/M/L/E");
        addHeader(" ");
        addHeader(" ");
        addHeader(" ");
    }

    private void addSpacer() {
        for (int i = 0; i < COLS; i++) {
            add(Box.createVerticalStrut(12));
            elements++;
        }
    }

}