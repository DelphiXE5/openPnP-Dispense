/*
 * Copyright (C) 2011 Jason von Nieda <jason@vonnieda.org>
 * 
 * This file is part of OpenPnP.
 * 
 * OpenPnP is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * OpenPnP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with OpenPnP. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * For more information about OpenPnP visit http://openpnp.org
 */

package org.openpnp.machine.reference.dispensers.wizards;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.openpnp.Translations;
import org.openpnp.gui.components.ComponentDecorators;
import org.openpnp.gui.support.AbstractConfigurationWizard;
import org.openpnp.gui.support.ActuatorsComboBoxModel;
import org.openpnp.gui.support.AxesComboBoxModel;
import org.openpnp.gui.support.DoubleConverter;
import org.openpnp.gui.support.IntegerConverter;
import org.openpnp.gui.support.LengthConverter;
import org.openpnp.gui.support.MutableLocationProxy;
import org.openpnp.gui.support.NamedConverter;
import org.openpnp.machine.reference.ReferenceNozzle;
import org.openpnp.machine.reference.dispensers.DispensingValve;
import org.openpnp.model.Configuration;
import org.openpnp.spi.Actuator;
import org.openpnp.spi.Axis;
import org.openpnp.spi.Dispenser.PadCoveringMethod;
import org.openpnp.spi.Nozzle.RotationMode;
import org.openpnp.spi.base.AbstractAxis;
import org.openpnp.spi.base.AbstractMachine;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class DispensingValveConfigurationWizard extends AbstractConfigurationWizard {
    private final DispensingValve dispenser;

    private JTextField locationX;
    private JTextField locationY;
    private JTextField locationZ;
    private JPanel panelOffsets;
    private JPanel panelChanger;
    private JTextField textFieldSafeZ;
    private JPanel panelProperties;
    private JLabel lblName;
    private JTextField nameTf;
    private JLabel lblDwellTime;
    private JLabel lblDotSize;
    private JLabel lblDefaultDispenseTime;
    private JTextField dotSize;
    private JTextField defaultDispenseTime;
    private JLabel lblDynamicSafeZ;
    private JCheckBox chckbxDynamicsafez;
    private JComboBox axisX;
    private JComboBox axisY;
    private JComboBox axisZ;
    private JLabel lblPadCoveringMethod;
    private JComboBox padCoveringMethod;
    private JLabel lblRotation;
    private JTextField locationRotation;
    private JLabel lblAxis;
    private JLabel lblOffset;
    private JLabel lblCompressedAirActuator;
    private JComboBox compressedAirActuator;

    public DispensingValveConfigurationWizard(AbstractMachine machine, DispensingValve dispenser) {
        this.dispenser = dispenser;

        panelProperties = new JPanel();
        panelProperties.setBorder(new TitledBorder(null, Translations.getString(
                "ReferenceNozzleConfigurationWizard.PropertiesPanel.Border.title"), //$NON-NLS-1$
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        contentPanel.add(panelProperties);
        panelProperties.setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("max(70dlu;default)"),
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC, },
                new RowSpec[] {
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC, }));

        lblName = new JLabel(Translations.getString(
                "ReferenceNozzleConfigurationWizard.PropertiesPanel.NameLabel.text")); //$NON-NLS-1$
        panelProperties.add(lblName, "2, 2, right, default");

        nameTf = new JTextField();
        panelProperties.add(nameTf, "4, 2");
        nameTf.setColumns(20);

        panelOffsets = new JPanel();
        panelOffsets.setBorder(new TitledBorder(null, Translations.getString(
                "ReferenceNozzleConfigurationWizard.OffsetsPanel.Border.title"), //$NON-NLS-1$
                TitledBorder.LEADING, TitledBorder.TOP, null));
        panelOffsets.setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("max(70dlu;default)"),
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC, },
                new RowSpec[] {
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC, }));

        JLabel lblX = new JLabel("X");
        panelOffsets.add(lblX, "4, 2");

        JLabel lblY = new JLabel("Y");
        panelOffsets.add(lblY, "6, 2");

        JLabel lblZ = new JLabel("Z");
        panelOffsets.add(lblZ, "8, 2");

        lblRotation = new JLabel(Translations.getString(
                "ReferenceNozzleConfigurationWizard.OffsetsPanel.RotationLabel.text")); //$NON-NLS-1$
        panelOffsets.add(lblRotation, "10, 2");

        lblAxis = new JLabel(Translations.getString(
                "ReferenceNozzleConfigurationWizard.OffsetsPanel.AxisLabel.text")); //$NON-NLS-1$
        panelOffsets.add(lblAxis, "2, 4, right, default");

        axisX = new JComboBox(new AxesComboBoxModel(machine, AbstractAxis.class, Axis.Type.X, true));
        panelOffsets.add(axisX, "4, 4, fill, default");

        axisY = new JComboBox(new AxesComboBoxModel(machine, AbstractAxis.class, Axis.Type.Y, true));
        panelOffsets.add(axisY, "6, 4, fill, default");

        axisZ = new JComboBox(new AxesComboBoxModel(machine, AbstractAxis.class, Axis.Type.Z, true));
        panelOffsets.add(axisZ, "8, 4, fill, default");

        lblOffset = new JLabel(Translations.getString(
                "ReferenceNozzleConfigurationWizard.OffsetsPanel.OffsetLabel.text")); //$NON-NLS-1$
        panelOffsets.add(lblOffset, "2, 6, right, default");

        locationX = new JTextField();
        panelOffsets.add(locationX, "4, 6");
        locationX.setColumns(10);

        locationY = new JTextField();
        panelOffsets.add(locationY, "6, 6");
        locationY.setColumns(10);

        locationZ = new JTextField();
        panelOffsets.add(locationZ, "8, 6");
        locationZ.setColumns(10);

        contentPanel.add(panelOffsets);

        JPanel panelSafeZ = new JPanel();
        panelSafeZ.setBorder(new TitledBorder(null, Translations.getString(
                "ReferenceNozzleConfigurationWizard.SafeZPanel.Border.title"), //$NON-NLS-1$
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        contentPanel.add(panelSafeZ);
        panelSafeZ.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("max(70dlu;default)"),
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("114px"),
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC, },
                new RowSpec[] {
                        RowSpec.decode("24px"),
                        RowSpec.decode("19px"), }));

        JLabel lblSafeZ = new JLabel(Translations.getString(
                "ReferenceNozzleConfigurationWizard.SafeZPanel.SafeZLabel.text")); //$NON-NLS-1$
        panelSafeZ.add(lblSafeZ, "1, 1, right, center");

        textFieldSafeZ = new JTextField();
        textFieldSafeZ.setEditable(false);
        panelSafeZ.add(textFieldSafeZ, "3, 1, fill, top");
        textFieldSafeZ.setColumns(10);

        lblDynamicSafeZ = new JLabel(Translations.getString(
                "ReferenceNozzleConfigurationWizard.SafeZPanel.DynamicSafeZLabel.text")); //$NON-NLS-1$
        lblDynamicSafeZ.setToolTipText(
                "<html>\r\nWhen moving to Safe Z, account for the part height on the nozzle i.e. lift the nozzle higher with a taller part.<br/>\r\nThis allows you to use a lower Safe Z which might improve the machine speed. \r\n</html>");
        lblDynamicSafeZ.setHorizontalAlignment(SwingConstants.TRAILING);
        panelSafeZ.add(lblDynamicSafeZ, "1, 2");

        chckbxDynamicsafez = new JCheckBox("");
        chckbxDynamicsafez.setToolTipText(Translations.getString(
                "ReferenceNozzleConfigurationWizard.SafeZPanel.DynamicSafeZChkbox.toolTipText")); //$NON-NLS-1$
        panelSafeZ.add(chckbxDynamicsafez, "3, 2");

        panelChanger = new JPanel();
        panelChanger.setBorder(new TitledBorder(null, Translations.getString(
                "ReferenceNozzleConfigurationWizard.ChangerPanel.Border.title"), //$NON-NLS-1$
                TitledBorder.LEADING, TitledBorder.TOP, null));
        contentPanel.add(panelChanger);
        panelChanger
                .setLayout(
                        new FormLayout(new ColumnSpec[] {
                                FormSpecs.RELATED_GAP_COLSPEC,
                                ColumnSpec.decode("max(70dlu;default)"),
                                FormSpecs.RELATED_GAP_COLSPEC,
                                FormSpecs.DEFAULT_COLSPEC,
                                FormSpecs.RELATED_GAP_COLSPEC,
                                ColumnSpec.decode("default:grow"),
                                FormSpecs.RELATED_GAP_COLSPEC,
                                ColumnSpec.decode("default:grow"),
                                FormSpecs.RELATED_GAP_COLSPEC,
                                ColumnSpec.decode("default:grow"), },
                                new RowSpec[] {
                                        FormSpecs.RELATED_GAP_ROWSPEC,
                                        FormSpecs.DEFAULT_ROWSPEC,
                                        FormSpecs.RELATED_GAP_ROWSPEC,
                                        FormSpecs.DEFAULT_ROWSPEC,
                                        FormSpecs.RELATED_GAP_ROWSPEC,
                                        FormSpecs.DEFAULT_ROWSPEC,
                                        FormSpecs.RELATED_GAP_ROWSPEC,
                                        FormSpecs.DEFAULT_ROWSPEC, }));

        lblDotSize = new JLabel(
                "Default Dot Size (Diameter in mm)"); //$NON-NLS-1$
        panelChanger.add(lblDotSize, "2, 2, right, default");

        dotSize = new JTextField();
        panelChanger.add(dotSize, "4, 2, fill, default");
        dotSize.setColumns(10);

        lblDefaultDispenseTime = new JLabel(
                "Default Dispense Time (ms)"); //$NON-NLS-1$
        panelChanger.add(lblDefaultDispenseTime, "2, 4, right, default");

        defaultDispenseTime = new JTextField();
        panelChanger.add(defaultDispenseTime, "4, 4, fill, default");
        defaultDispenseTime.setColumns(10);

        // CellConstraints cc = new CellConstraints();
        // lblDwellTime = new JLabel(Translations.getString(
        // "ReferenceNozzleConfigurationWizard.ChangerPanel.DwellTimeLabel.text"));
        // //$NON-NLS-1$
        // panelChanger.add(lblDwellTime, "2, 6, 9, 1, fill, default");

        lblPadCoveringMethod = new JLabel(
                "Pad Covering Method"); //$NON-NLS-1$
        panelChanger.add(lblPadCoveringMethod, "2, 6, right, default");

        padCoveringMethod = new JComboBox(PadCoveringMethod.values());
        panelChanger.add(padCoveringMethod, "4, 6, fill, default");

        lblCompressedAirActuator = new JLabel("Compressed Air Actuator"); //$NON-NLS-1$
        panelChanger.add(lblCompressedAirActuator, "2, 8, right, center");

        compressedAirActuator = new JComboBox();
        compressedAirActuator.setMaximumRowCount(15);
        compressedAirActuator.setModel(new ActuatorsComboBoxModel(dispenser.getHead()));
        panelChanger.add(compressedAirActuator, "4, 8");
    }

    @Override
    public void createBindings() {
        AbstractMachine machine = (AbstractMachine) Configuration.get().getMachine();
        LengthConverter lengthConverter = new LengthConverter();
        IntegerConverter intConverter = new IntegerConverter();
        DoubleConverter doubleConverter = new DoubleConverter(Configuration.get().getLengthDisplayFormat());
        NamedConverter<Axis> axisConverter = new NamedConverter<>(machine.getAxes());

        dispenser.getHead();
        NamedConverter<Actuator> actuatorConverter = (new NamedConverter<>(dispenser.getHead().getActuators()));

        addWrappedBinding(dispenser, "name", nameTf, "text");

        addWrappedBinding(dispenser, "axisX", axisX, "selectedItem", axisConverter);
        addWrappedBinding(dispenser, "axisY", axisY, "selectedItem", axisConverter);
        addWrappedBinding(dispenser, "axisZ", axisZ, "selectedItem", axisConverter);
        // addWrappedBinding(dispenser, "axisRotation", axisRotation, "selectedItem",
        // axisConverter);
        // addWrappedBinding(dispenser, "rotationMode", rotationMode, "selectedItem");
        // addWrappedBinding(dispenser, "aligningRotationMode", aligningRotationMode,
        // "selected");

        MutableLocationProxy headOffsets = new MutableLocationProxy();
        bind(UpdateStrategy.READ_WRITE, dispenser, "headOffsets", headOffsets, "location");
        addWrappedBinding(headOffsets, "lengthX", locationX, "text", lengthConverter);
        addWrappedBinding(headOffsets, "lengthY", locationY, "text", lengthConverter);
        addWrappedBinding(headOffsets, "lengthZ", locationZ, "text", lengthConverter);
        // addWrappedBinding(headOffsets, "rotation", locationRotation, "text",
        // doubleConverter);

        // addWrappedBinding(dispenser, "enableDynamicSafeZ", chckbxDynamicsafez,
        // "selected");
        addWrappedBinding(dispenser, "safeZ", textFieldSafeZ, "text", lengthConverter);
        addWrappedBinding(dispenser, "dotSize", dotSize, "text",
                doubleConverter);
        addWrappedBinding(dispenser, "defaultDispenseTime", defaultDispenseTime, "text",
                intConverter);
        addWrappedBinding(dispenser, "padCoveringMethod", padCoveringMethod, "selectedItem");
        addWrappedBinding(dispenser, "compressedAirActuator", compressedAirActuator, "selectedItem", actuatorConverter);

        ComponentDecorators.decorateWithAutoSelect(nameTf);
        ComponentDecorators.decorateWithAutoSelect(dotSize);
        ComponentDecorators.decorateWithAutoSelect(defaultDispenseTime);
        ComponentDecorators.decorateWithAutoSelectAndLengthConversion(locationX);
        ComponentDecorators.decorateWithAutoSelectAndLengthConversion(locationY);
        ComponentDecorators.decorateWithAutoSelectAndLengthConversion(locationZ);
        ComponentDecorators.decorateWithAutoSelectAndLengthConversion(textFieldSafeZ);
    }
}
