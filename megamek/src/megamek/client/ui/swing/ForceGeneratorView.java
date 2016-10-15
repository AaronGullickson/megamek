/**
 * 
 */
package megamek.client.ui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import megamek.client.ratgenerator.AbstractUnitRecord;
import megamek.client.ratgenerator.FactionRecord;
import megamek.client.ratgenerator.ForceDescriptor;
import megamek.client.ratgenerator.ForceNode;
import megamek.client.ratgenerator.MissionRole;
import megamek.client.ratgenerator.RATGenerator;
import megamek.client.ratgenerator.Ruleset;
import megamek.client.ratgenerator.TOCNode;
import megamek.client.ratgenerator.ValueNode;
import megamek.common.EntityWeightClass;
import megamek.common.IGame;
import megamek.common.UnitType;

/**
 * Controls to set options for force generator.
 * 
 * @author Neoancient
 *
 */


public class ForceGeneratorView extends JPanel implements FocusListener, ActionListener {
	
	private static final long serialVersionUID = 5269823128861856001L;
	
	private IGame game;
	private int currentYear;
	private Consumer<ForceDescriptor> onGenerate = null;
	private boolean ignoreActions;

	private ForceDescriptor forceDesc = new ForceDescriptor();
	
	private JTextField txtYear;
	private JComboBox<FactionRecord> cbFaction;
	private JComboBox<FactionRecord> cbSubfaction;
	private JComboBox<Integer> cbUnitType;	
	private JComboBox<String> cbFormation;
	private JComboBox<String> cbRating;
	private JComboBox<String> cbFlags;

	private JComboBox<String> cbExperience;
	private JComboBox<Integer> cbWeightClass;
	
	private DefaultListCellRenderer factionRenderer = new CBRenderer<FactionRecord>("General",
			fRec -> fRec.getName(currentYear));
	
	private HashMap<String,String> formationDisplayNames = new HashMap<>();
	private HashMap<String,String> flagDisplayNames = new HashMap<>();
	
	private JPanel panGroundRole;
	private JPanel panInfRole;
	private JPanel panAirRole;
	
	private JCheckBox chkRoleRecon;
	private JCheckBox chkRoleFireSupport;
	private JCheckBox chkRoleUrban;
	private JCheckBox chkRoleInfantrySupport;
	private JCheckBox chkRoleCavalry;
	private JCheckBox chkRoleRaider;
	private JCheckBox chkRoleIncindiary;
	private JCheckBox chkRoleAntiAircraft;
	private JCheckBox chkRoleAntiInfantry;	
	private JCheckBox chkRoleArtillery;
	private JCheckBox chkRoleMissileArtillery;
	private JCheckBox chkRoleTransport;
	private JCheckBox chkRoleEngineer;

	private JCheckBox chkRoleFieldGun;
	private JCheckBox chkRoleFieldArtillery;
	private JCheckBox chkRoleFieldMissileArtillery;
	
	private JCheckBox chkRoleAirRecon;
	private JCheckBox chkRoleGroundSupport;
	private JCheckBox chkRoleInterceptor;
	private JCheckBox chkRoleEscort;
	private JCheckBox chkRoleBomber;
	private JCheckBox chkRoleAssault;
	private JCheckBox chkRoleAirTransport;

	private JButton btnGenerate;
	
	public ForceGeneratorView(IGame game, Consumer<ForceDescriptor> onGenerate) {
		this.game = game;
		this.onGenerate = onGenerate;
		if (!Ruleset.isInitialized()) {
			Ruleset.loadData();
		}
		initUi();
	}
	
	private void initUi() {
		currentYear = game.getOptions().intOption("year");
		RATGenerator rg = RATGenerator.getInstance();
		rg.loadYear(currentYear);
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		int y = 0;
		
		gbc.gridx = 0;
		gbc.gridy = y;
		add(new JLabel("Year:"), gbc);
		txtYear = new JTextField();
		txtYear.setEditable(true);
		txtYear.setText(Integer.toString(currentYear));
		gbc.gridx = 1;
		gbc.gridy = y++;
		add(txtYear, gbc);
		txtYear.addFocusListener(this);
		gbc.gridx = 0;
		gbc.gridy = y;
		add(new JLabel("Faction:"), gbc);
		cbFaction = new JComboBox<>();
		cbFaction.setRenderer(factionRenderer);
		gbc.gridx = 1;
		gbc.gridy = y;
		add(cbFaction, gbc);
		cbFaction.addActionListener(this);
		
		gbc.gridx = 2;
		gbc.gridy = y;
		add(new JLabel("Subfaction:"), gbc);
		cbSubfaction = new JComboBox<>();
		cbSubfaction.setRenderer(factionRenderer);
		gbc.gridx = 3;
		gbc.gridy = y++;
		add(cbSubfaction, gbc);
		cbFaction.addActionListener(this);
		
		gbc.gridx = 0;
		gbc.gridy = y;
		add(new JLabel("Unit Type:"), gbc);
		cbUnitType = new JComboBox<Integer>();
		cbUnitType.setRenderer(new CBRenderer<Integer>("Combined",
				ut -> UnitType.getTypeName(ut)));
		gbc.gridx = 1;
		gbc.gridy = y;
		add(cbUnitType, gbc);
		cbUnitType.addActionListener(this);
		
		gbc.gridx = 2;
		gbc.gridy = y;
		add(new JLabel("Formation:"), gbc);
		cbFormation = new JComboBox<>();
		cbFormation.setRenderer(new CBRenderer<String>("Random", f -> formationDisplayNames.get(f)));
		gbc.gridx = 3;
		gbc.gridy = y++;
		add(cbFormation, gbc);
		cbFormation.addActionListener(this);
		
		gbc.gridx = 0;
		gbc.gridy = y;
		add(new JLabel("Rating:"), gbc);
		cbRating = new JComboBox<>();
		gbc.gridx = 1;
		gbc.gridy = y;
		add(cbRating, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = y;
		add(new JLabel("Weight:"), gbc);
		cbWeightClass = new JComboBox<Integer>();
		cbWeightClass.setRenderer(new CBRenderer<Integer>("Random",
				wc -> EntityWeightClass.getClassName(wc)));
		cbWeightClass.addItem(null);
		cbWeightClass.addItem(EntityWeightClass.WEIGHT_LIGHT);
		cbWeightClass.addItem(EntityWeightClass.WEIGHT_MEDIUM);
		cbWeightClass.addItem(EntityWeightClass.WEIGHT_HEAVY);
		cbWeightClass.addItem(EntityWeightClass.WEIGHT_ASSAULT);
		gbc.gridx = 3;
		gbc.gridy = y++;
		add(cbWeightClass, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = y;
		add(new JLabel("Other:"), gbc);
		cbFlags = new JComboBox<>();
		cbFlags.setRenderer(new CBRenderer<String>("---",
				f -> flagDisplayNames.get(f)));
		gbc.gridx = 1;
		gbc.gridy = y;
		add(cbFlags, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = y;
		add(new JLabel("Experience:"), gbc);
		cbExperience = new JComboBox<String>();
		cbExperience.addItem("Random");
		cbExperience.addItem("Green");
		cbExperience.addItem("Regular");
		cbExperience.addItem("Veteran");
		cbExperience.addItem("Elite");
		gbc.gridx = 3;
		gbc.gridy = y++;
		add(cbExperience, gbc);
		
		gbc.gridwidth = 4;
		panGroundRole = new JPanel(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = y++;
		add(panGroundRole, gbc);
		
		panInfRole = new JPanel(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = y++;
		add(panInfRole, gbc);
		panInfRole.setVisible(false);
		
		panAirRole = new JPanel(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = y++;
		add(panAirRole, gbc);
		panAirRole.setVisible(false);

		gbc.gridwidth = 1;
		btnGenerate = new JButton("Generate");
		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.gridwidth = 1;
		gbc.weighty = 1.0;
		add(btnGenerate, gbc);
		btnGenerate.addActionListener(ev -> generateForce());

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		
		chkRoleRecon = new JCheckBox("Reconnaisance");
		gbc.gridx = 0;
		gbc.gridy = 0;
		panGroundRole.add(chkRoleRecon, gbc);
		
		chkRoleFireSupport = new JCheckBox("Fire Support");
		gbc.gridx = 1;
		gbc.gridy = 0;
		panGroundRole.add(chkRoleFireSupport, gbc);
		
		chkRoleUrban = new JCheckBox("Urban");
		gbc.gridx = 2;
		gbc.gridy = 0;
		panGroundRole.add(chkRoleUrban, gbc);
		
		chkRoleCavalry = new JCheckBox("Cavalry");
		gbc.gridx = 3;
		gbc.gridy = 0;
		panGroundRole.add(chkRoleCavalry, gbc);
		
		chkRoleRaider = new JCheckBox("Raider");
		gbc.gridx = 0;
		gbc.gridy = 1;
		panGroundRole.add(chkRoleRaider, gbc);
		
		chkRoleIncindiary = new JCheckBox("Incindiary");
		gbc.gridx = 1;
		gbc.gridy = 1;
		panGroundRole.add(chkRoleIncindiary, gbc);
		
		chkRoleAntiAircraft = new JCheckBox("Anti-Aircraft");
		gbc.gridx = 2;
		gbc.gridy = 1;
		panGroundRole.add(chkRoleAntiAircraft, gbc);
		
		chkRoleAntiInfantry = new JCheckBox("Anti-Infantry");
		gbc.gridx = 3;
		gbc.gridy = 1;
		panGroundRole.add(chkRoleAntiInfantry, gbc);
		
		chkRoleArtillery = new JCheckBox("Artillery");
		gbc.gridx = 0;
		gbc.gridy = 2;
		panGroundRole.add(chkRoleArtillery, gbc);
		
		chkRoleMissileArtillery = new JCheckBox("Missile Artillery");
		gbc.gridx = 1;
		gbc.gridy = 2;
		panGroundRole.add(chkRoleMissileArtillery, gbc);
		
		chkRoleInfantrySupport = new JCheckBox("Infantry Support");
		gbc.gridx = 2;
		gbc.gridy = 2;
		panGroundRole.add(chkRoleInfantrySupport, gbc);
		
		chkRoleTransport = new JCheckBox("Transport");
		gbc.gridx = 0;
		gbc.gridy = 3;
		panGroundRole.add(chkRoleTransport, gbc);
		
		chkRoleEngineer = new JCheckBox("Engineer");
		gbc.gridx = 1;
		gbc.gridy = 3;
		panGroundRole.add(chkRoleEngineer, gbc);
		
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		
		chkRoleFieldGun = new JCheckBox("Field Gun");
		gbc.gridx = 0;
		gbc.gridy = 0;
		panInfRole.add(chkRoleFieldGun, gbc);
		
		chkRoleFieldArtillery = new JCheckBox("Field Artillery");
		gbc.gridx = 1;
		gbc.gridy = 0;
		panInfRole.add(chkRoleFieldArtillery, gbc);
		
		chkRoleFieldMissileArtillery = new JCheckBox("Missile Artillery");
		gbc.gridx = 2;
		gbc.gridy = 0;
		panInfRole.add(chkRoleFieldMissileArtillery, gbc);
		
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		
		chkRoleAirRecon = new JCheckBox("Recon");
		gbc.gridx = 0;
		gbc.gridy = 0;
		panAirRole.add(chkRoleAirRecon, gbc);
		
		chkRoleGroundSupport = new JCheckBox("Ground Support");
		gbc.gridx = 1;
		gbc.gridy = 0;
		panAirRole.add(chkRoleGroundSupport, gbc);
		
		chkRoleInterceptor = new JCheckBox("Interceptor");
		gbc.gridx = 2;
		gbc.gridy = 0;
		panAirRole.add(chkRoleInterceptor, gbc);
				
		chkRoleEscort = new JCheckBox("Escort");
		gbc.gridx = 0;
		gbc.gridy = 1;
		panAirRole.add(chkRoleEscort, gbc);
				
		chkRoleBomber = new JCheckBox("Bomber");
		gbc.gridx = 1;
		gbc.gridy = 1;
		panAirRole.add(chkRoleBomber, gbc);
				
		chkRoleAssault = new JCheckBox("Dropship Assault");
		gbc.gridx = 0;
		gbc.gridy = 2;
		panAirRole.add(chkRoleAssault, gbc);
		
		chkRoleAirTransport = new JCheckBox("Transport");
		gbc.gridx = 1;
		gbc.gridy = 2;
		panAirRole.add(chkRoleAirTransport, gbc);
		
		ignoreActions = true;
		refreshFactions();
		ignoreActions = false;
	}
	
	private void generateForce() {
		ForceDescriptor fd = new ForceDescriptor();
		fd.setTopLevel(true);
		fd.setYear(forceDesc.getYear());
		fd.setFaction(forceDesc.getFaction());
		fd.setUnitType(forceDesc.getUnitType());
		fd.setEschelon(forceDesc.getEschelon());
		fd.setAugmented(forceDesc.isAugmented());
		fd.setSizeMod(forceDesc.getSizeMod());
		fd.getFlags().addAll(forceDesc.getFlags());
		fd.setRating(forceDesc.getRating());
		fd.setWeightClass(forceDesc.getWeightClass());
		if (forceDesc.getUnitType() != null) {
			switch (forceDesc.getUnitType()) {
			case UnitType.MEK:case UnitType.TANK:
				if (chkRoleRecon.isSelected()) {
					fd.getRoles().add(MissionRole.RECON);
				}
				if (chkRoleFireSupport.isSelected()) {
					fd.getRoles().add(MissionRole.FIRE_SUPPORT);
				}
				if (chkRoleUrban.isSelected()) {
					fd.getRoles().add(MissionRole.URBAN);
				}
				if (chkRoleInfantrySupport.isSelected()) {
					fd.getRoles().add(MissionRole.INF_SUPPORT);
				}
				if (chkRoleCavalry.isSelected()) {
					fd.getRoles().add(MissionRole.CAVALRY);
				}
				if (chkRoleRaider.isSelected()) {
					fd.getRoles().add(MissionRole.RAIDER);
				}
				if (chkRoleIncindiary.isSelected()) {
					fd.getRoles().add(MissionRole.INCENDIARY);
				}
				if (chkRoleAntiAircraft.isSelected()) {
					fd.getRoles().add(MissionRole.ANTI_AIRCRAFT);
				}
				if (chkRoleAntiInfantry.isSelected()) {
					fd.getRoles().add(MissionRole.ANTI_INFANTRY);
				}			
				if (chkRoleArtillery.isSelected()) {
					fd.getRoles().add(MissionRole.ARTILLERY);
				}
				if (chkRoleMissileArtillery.isSelected()) {
					fd.getRoles().add(MissionRole.MISSILE_ARTILLERY);
				}
				if (chkRoleTransport.isSelected()) {
					fd.getRoles().add(MissionRole.CARGO);
				}
				if (chkRoleEngineer.isSelected()) {
					fd.getRoles().add(MissionRole.ENGINEER);
				}
				break;
			case UnitType.INFANTRY:
				if (chkRoleFieldGun.isSelected()) {
					fd.getRoles().add(MissionRole.FIELD_GUN);
				}
				if (chkRoleFieldArtillery.isSelected()) {
					fd.getRoles().add(MissionRole.ARTILLERY);
				}
				if (chkRoleFieldMissileArtillery.isSelected()) {
					fd.getRoles().add(MissionRole.MISSILE_ARTILLERY);
				}
				break;
			case UnitType.AERO:
				if (chkRoleAirRecon.isSelected()) {
					fd.getRoles().add(MissionRole.RECON);
				}
				if (chkRoleGroundSupport.isSelected()) {
					fd.getRoles().add(MissionRole.GROUND_SUPPORT);
				}
				if (chkRoleInterceptor.isSelected()) {
					fd.getRoles().add(MissionRole.INTERCEPTOR);
				}
				if (chkRoleAssault.isSelected()) {
					fd.getRoles().add(MissionRole.ASSAULT);
				}
				if (chkRoleAirTransport.isSelected()) {
					fd.getRoles().add(MissionRole.CARGO);
				}
			}
		}
		
		Ruleset.findRuleset(fd).process(fd);

		forceDesc = fd;
		if (onGenerate != null) {
			onGenerate.accept(fd);
		}
	}
	
	public ForceDescriptor getForceDescriptor() {
		return forceDesc;
	}
	
	private void refreshFactions() {
		String oldFaction = forceDesc.getFaction();
		cbFaction.removeAllItems();
		ArrayList<FactionRecord> sorted = new ArrayList<>();
		sorted.addAll(RATGenerator.getInstance().getFactionList());
		sorted.sort((fr1, fr2) -> fr1.getName(currentYear).compareTo(fr2.getName(currentYear)));
		for (FactionRecord fRec : sorted) {
			if (!fRec.getKey().contains(".") && fRec.isActiveInYear(currentYear)) {
				cbFaction.addItem(fRec);
			}
		}
		
		if (oldFaction != null) {
			cbFaction.setSelectedItem(oldFaction.split("\\.")[0]);			
		} else {
			forceDesc.setFaction(((FactionRecord)cbFaction.getSelectedItem()).getKey());
		}
		if (cbFaction.getSelectedIndex() < 0) {
			cbFaction.setSelectedIndex(0);
		}
		refreshSubfactions();
	}
	
	private void refreshSubfactions() {
		String oldFaction = forceDesc.getFaction();
		cbSubfaction.removeAllItems();
		if (forceDesc.getFactionRec() != null) {
			cbSubfaction.addItem(null);
			if (cbFaction.getSelectedItem() != null) {
				String currentFaction = ((FactionRecord)cbFaction.getSelectedItem()).getKey();
				ArrayList<FactionRecord> sorted = new ArrayList<>();
				sorted.addAll(RATGenerator.getInstance().getFactionList());
				sorted.sort((fr1, fr2) -> fr1.getName(currentYear).compareTo(fr2.getName(currentYear)));
				for (FactionRecord fRec : sorted) {
					if (fRec.getPctSalvage(currentYear) != null &&
							fRec.getKey().startsWith(currentFaction + ".")) {
						cbSubfaction.addItem(fRec);
					}
				}
			}
			if (oldFaction != null) {
				cbSubfaction.setSelectedItem(oldFaction.contains(".")?oldFaction:null);
			} else {
				cbSubfaction.setSelectedItem(null);
			}
		} else {
			System.out.println("factionrec is null");
		}
		if (cbSubfaction.getSelectedIndex() < 0) {
			cbSubfaction.setSelectedIndex(0);
		}
		refreshUnitTypes();
	}
	
	private void refreshUnitTypes() {
		TOCNode tocNode = findTOCNode();
		Integer currentType = forceDesc.getUnitType();
		boolean hasCurrent = false;
		cbUnitType.removeAllItems();
		if (tocNode != null) {
			ValueNode n = tocNode.findUnitTypes(forceDesc);
			if (n != null) {
				for (String unitType : n.getContent().split(",")) {
					if (unitType.equals("null")) {
						cbUnitType.addItem(null);
						if (currentType == null) {
							hasCurrent = true;
						}
					} else {
						cbUnitType.addItem(AbstractUnitRecord.parseUnitType(unitType));
						if (currentType != null && currentType.equals(unitType)) {
							hasCurrent = true;
						}
					}
				}
			} else {
				System.out.println("No unit type node found.");
				cbUnitType.addItem(null);
			}
		} else {
			cbUnitType.addItem(null);
		}
		
		if (hasCurrent) {
			cbUnitType.setSelectedItem(currentType);
		} else {
			Ruleset rs = Ruleset.findRuleset(forceDesc.getFaction());
			Integer unitType = rs.getDefaultUnitType(forceDesc);
			if (unitType == null && cbUnitType.getItemCount() > 0) {
				unitType = (Integer)cbUnitType.getItemAt(0);
			}
			cbUnitType.setSelectedItem(unitType);
			forceDesc.setUnitType(unitType);
		}
		refreshFormations();
	}
	
	private void refreshFormations() {
		if (cbUnitType.getSelectedItem() != null) {
			Integer unitType = (Integer)cbUnitType.getSelectedItem();
			if (unitType != null) {
				panGroundRole.setVisible(unitType == UnitType.MEK || unitType == UnitType.TANK);
				panInfRole.setVisible(unitType == UnitType.INFANTRY
						|| unitType == UnitType.BATTLE_ARMOR);
				panAirRole.setVisible(unitType == UnitType.AERO
						|| unitType == UnitType.CONV_FIGHTER);
			}
		}
		
		TOCNode tocNode = findTOCNode();
		String currentFormation = (String)cbFormation.getSelectedItem();
		boolean hasCurrent = false;
		Ruleset ruleset = Ruleset.findRuleset(forceDesc);
		cbFormation.removeAllItems();
		
		if (tocNode != null) {
			ValueNode n = tocNode.findEschelons(forceDesc);
			if (n != null) {
				formationDisplayNames.clear();
				for (String formation : n.getContent().split(",")) {
					Ruleset rs = ruleset;
					ForceNode fn = null;
					do {
						fn = rs.findForceNode(forceDesc,
								Ruleset.getConstantVal(formation.replaceAll("[^0-9A-Z]", "")),
										formation.endsWith("+"));
						if (fn == null) {
							if (rs.getParent() != null) {
								rs = Ruleset.findRuleset(rs.getParent());
							} else {
								rs = null;
							}
						}
					} while (fn == null && rs != null);
					String formName = (fn != null)?fn.getEschelonName() : formation;
					if (formation.endsWith("+")) {
						formName = "Reinforced " + formName;
					}
					if (formation.endsWith("-")) {
						formName = "Understrength " + formName;
					}
					formationDisplayNames.put(formation, formName);
					cbFormation.addItem(formation);
					if (currentFormation != null && currentFormation.equals(formation)) {
						hasCurrent = true;
					}
				}
			}
		} else {
			System.out.println("No eschelon node found.");
		}
		
		if (hasCurrent) {
			cbFormation.setSelectedItem(currentFormation);
		} else {
			Ruleset rs = Ruleset.findRuleset(forceDesc.getFaction());
			String esch = rs.getDefaultEschelon(forceDesc);
			if ((esch == null || !!formationDisplayNames.containsKey(esch)
					&& cbFormation.getItemCount() > 0)) {
				esch = (String)cbFormation.getItemAt(0);
			}
			if (esch != null) {
				cbFormation.setSelectedItem(esch);
				setFormation(esch);
			}
		}
		
		refreshRatings();
	}
	
	private void refreshRatings() {
		TOCNode tocNode = findTOCNode();
		String currentRating = forceDesc.getRating();
		boolean hasCurrent = false;
		cbRating.removeAllItems();
		cbRating.addItem(null);
		if (tocNode != null) {
			ValueNode n = tocNode.findRatings(forceDesc);
			if (n != null && n.getContent() != null) {
				for (String rating : n.getContent().split(",")) {
					if (rating.contains(":")) {
						String[] fields = rating.split(":");
						cbRating.addItem(fields[1]);
					} else {
						cbRating.addItem(rating);
					}
				}
			} else {
				System.out.println("No rating found.");
			}
		}
		
		if (hasCurrent) {
			cbRating.setSelectedItem(currentRating);
		} else {
			Ruleset rs = Ruleset.findRuleset(forceDesc.getFaction());
			String rating = rs.getDefaultRating(forceDesc);
			if (rating == null && cbRating.getItemCount() > 0) {
				rating = cbRating.getItemAt(0);
			}
			if (rating != null) {
				cbRating.setSelectedItem(rating);
				forceDesc.setRating(rating);
			}
		}
		refreshFlags();
	}
	
	private void refreshFlags() {
		TOCNode tocNode = findTOCNode();
		String currentFlag = (String)cbFlags.getSelectedItem();
		boolean hasCurrent = false;
		cbFlags.removeAllItems();
		cbFlags.addItem(null);
		if (tocNode != null) {
			ValueNode n = tocNode.findFlags(forceDesc);
			if (n != null && n.getContent() != null) {
				for (String flag : n.getContent().split(",")) {
					if (flag.contains(":")) {
						String[] fields = flag.split(":");
						flagDisplayNames.put(fields[0], fields[1]);
						cbFlags.addItem(fields[0]);
					} else {
						flagDisplayNames.put(flag, flag);
						cbFlags.addItem(flag);
					}
				}
			}
		}
		
		if (hasCurrent) {
			cbFlags.setSelectedItem(currentFlag);
		} else {
			cbFlags.setSelectedIndex(0);
		}
		forceDesc.getFlags().clear();
		if (cbFlags.getSelectedItem() != null) {
			forceDesc.getFlags().add((String)cbFlags.getSelectedItem());
		}
	}
	
	private TOCNode findTOCNode() {
		Ruleset rs = Ruleset.findRuleset(forceDesc);
		TOCNode toc = null;
		do {
			toc = rs.getTOCNode();
			if (toc == null) {
				if (rs.getParent() == null) {
					rs = null;
				} else {
					rs = Ruleset.findRuleset(rs.getParent());
				}				
			}
		} while (rs != null && toc == null);
		return toc;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (ignoreActions) {
			return;
		}
		ignoreActions = true;
		if (arg0.getSource() == cbFaction) {
			if (cbFaction.getSelectedItem() != null) {
				forceDesc.setFaction(((FactionRecord)cbFaction.getSelectedItem()).getKey());
			}
			refreshSubfactions();
		} else if (arg0.getSource() == cbSubfaction) {
			if (cbSubfaction.getSelectedItem() != null) {
				forceDesc.setFaction(((FactionRecord)cbSubfaction.getSelectedItem()).getKey());
			} else {
				forceDesc.setFaction(((FactionRecord)cbFaction.getSelectedItem()).getKey());
			}
			refreshUnitTypes();
		} else if (arg0.getSource() == cbUnitType) {
			forceDesc.setUnitType((Integer)cbUnitType.getSelectedItem());
			refreshFormations();
		} else if (arg0.getSource() == cbFormation) {
			String esch = (String)cbFormation.getSelectedItem();
			if (esch != null) {
				setFormation(esch);
			}
			refreshRatings();
		} else if (arg0.getSource() == cbRating) {
			forceDesc.setRating((String)cbRating.getSelectedItem());
			refreshFlags();
		} else if (arg0.getSource() == cbFlags) {
			forceDesc.getFlags().clear();
			if (cbFlags.getSelectedItem() != null) {
				forceDesc.getFlags().add((String)cbFlags.getSelectedItem());
			}
		} else if (arg0.getSource() == cbWeightClass) {
			if (cbWeightClass.getSelectedIndex() < 1) {
				forceDesc.setWeightClass(null);
			} else {
				forceDesc.setWeightClass(cbWeightClass.getSelectedIndex());
			}
		}
		ignoreActions = false;
	}

	private void setFormation(String esch) {
		forceDesc.setEschelon(Ruleset.getConstantVal(esch.replaceAll("[^0-9A-Z]", "")));
		forceDesc.setAugmented(esch.contains("^"));
		if (esch.endsWith("+")) {
			forceDesc.setSizeMod(1);
		} else if (esch.endsWith("-")) {
			forceDesc.setSizeMod(-1);
		} else {
			forceDesc.setSizeMod(0);
		}
	}		

	@Override
	public void focusGained(FocusEvent arg0) {
		//Do nothing
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		try {
			currentYear = Integer.parseInt(txtYear.getText());
			if (currentYear < RATGenerator.getInstance().getEraSet().first()) {
				currentYear = RATGenerator.getInstance().getEraSet().first();
			} else if (currentYear > RATGenerator.getInstance().getEraSet().last()) {
				currentYear = RATGenerator.getInstance().getEraSet().last();
			}
		} catch (NumberFormatException ex) {
			//ignore and restore to previous value
		}
		txtYear.setText(String.valueOf(currentYear));
		RATGenerator.getInstance().loadYear(currentYear);
		refreshFactions();
	}

	static class CBRenderer<T> extends DefaultListCellRenderer {
		
		private static final long serialVersionUID = 4895258839502183158L;

		private String nullVal = "Default";
		private Function<T,String> toString;
		
		public CBRenderer(String nullVal) {
			this(nullVal, null);
		}
		
		public CBRenderer(String nullVal, Function<T,String> strConverter) {
			this.nullVal = nullVal;
			if (strConverter == null) {
				toString = obj -> obj.toString();
			} else {
				toString = strConverter;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public Component getListCellRendererComponent(JList<? extends Object> list, Object entry,
				int position, boolean arg3, boolean arg4) {
			if (entry == null) {
				setText(nullVal);
			} else {
				setText(toString.apply((T)entry));
			}
			return this;
		}
	};
	
    static class ForceTreeModel implements TreeModel {
    	
    	private ForceDescriptor root;
    	private ArrayList<TreeModelListener> listeners;
    	
    	public ForceTreeModel(ForceDescriptor root) {
    		this.root = root;
    		listeners = new ArrayList<TreeModelListener>();		
    	}

    	@Override
    	public void addTreeModelListener(TreeModelListener listener) {
    		if (null != listener && !listeners.contains(listener)) {
    			listeners.add(listener);
    		}
    	}

    	@Override
    	public Object getChild(Object parent, int index) {
    		if (parent instanceof ForceDescriptor) {
    			return ((ForceDescriptor)parent).getAllChildren().get(index);
    		}
    		return null;
    	}

    	@Override
    	public int getChildCount(Object parent) {
    		if (parent instanceof ForceDescriptor) {
    			return ((ForceDescriptor)parent).getAllChildren().size();
    		}
    		return 0;
    	}

    	@Override
    	public int getIndexOfChild(Object parent, Object child) {
    		if (parent instanceof ForceDescriptor) {
    			return ((ForceDescriptor)parent).getAllChildren().indexOf(child);
    		}
    		return 0;
    	}

    	@Override
    	public Object getRoot() {
    		return root;
    	}

    	@Override
    	public boolean isLeaf(Object node) {
    		return ((ForceDescriptor)node).getEschelon() == 0
    				|| (node instanceof ForceDescriptor && getChildCount(node) == 0);
    	}

    	@Override
    	public void removeTreeModelListener(TreeModelListener listener) {
    		if (null != listener) {
    			listeners.remove(listener);
    		}
    	}

    	@Override
    	public void valueForPathChanged(TreePath arg0, Object arg1) {
    		// TODO Auto-generated method stub

    	}

    }
    
    static class UnitRenderer extends DefaultTreeCellRenderer {
    	/**
    	 * 
    	 */
    	private static final long serialVersionUID = -5915350078441133119L;
    	
    	private MechTileset mt;
    	
    	public UnitRenderer() {
            mt = new MechTileset(new File("data/images/units"));
            try {
                mt.loadFromFile("mechset.txt");
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
    	}

        @Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(
                    tree, value, sel,
                    expanded, leaf, row,
                    hasFocus);
            setOpaque(true);
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
            if(sel) {
                setBackground(Color.DARK_GRAY);
                setForeground(Color.WHITE);
            }

            ForceDescriptor fd = (ForceDescriptor)value;
            if(fd.isElement()) {
                StringBuilder name = new StringBuilder();
                String uname = "";
                if(fd.getCo() == null) {
                    name.append("<font color='red'>No Crew</font>");
                } else {
                    name.append(fd.getCo().getName());
                    name.append(" (").append(fd.getCo().getGunnery()).append("/").append(fd.getCo().getPiloting()).append(")");
                }
                uname = "<i>" + fd.getModelName() + "</i>";
                if (fd.getFluffName() != null) {
                	uname += "<br /><i>" + fd.getFluffName() + "</i>";
                }
                setText("<html>" + name.toString() + ", " + uname + "</html>");
            } else {
            	StringBuilder desc = new StringBuilder("<html>");
            	desc.append(fd.parseName()).append("<br />").append(fd.getDescription());
            	if (fd.getCo() != null) {
            		desc.append("<br />").append(fd.getCo().getTitle() == null?"CO":fd.getCo().getTitle());
            		desc.append(fd.getCo().getName());
            	}
            	if (fd.getXo() != null) {
            		desc.append("<br />").append(fd.getXo().getTitle() == null?"XO":fd.getXo().getTitle());
            		desc.append(fd.getXo().getName());
            	}
           		setText(desc.append("</html>").toString());
            }
            return this;
        }
    }    
}
