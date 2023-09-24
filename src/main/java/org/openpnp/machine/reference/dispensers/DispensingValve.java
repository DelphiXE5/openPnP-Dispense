package org.openpnp.machine.reference.dispensers;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import org.openpnp.Translations;
import org.openpnp.gui.MainFrame;
import org.openpnp.gui.support.Icons;
import org.openpnp.gui.support.MessageBoxes;
import org.openpnp.gui.support.PropertySheetWizardAdapter;
import org.openpnp.gui.support.Wizard;
import org.openpnp.machine.reference.ReferenceHeadMountable;
import org.openpnp.machine.reference.ReferenceNozzle;
import org.openpnp.machine.reference.camera.ReferenceCamera;
import org.openpnp.machine.reference.dispensers.wizards.DispensingValveConfigurationWizard;
import org.openpnp.model.Configuration;
import org.openpnp.model.Length;
import org.openpnp.model.LengthUnit;
import org.openpnp.model.Location;
import org.openpnp.model.Part;
import org.openpnp.spi.Actuator;
import org.openpnp.spi.Camera;
import org.openpnp.spi.Camera.Looking;
import org.openpnp.spi.HeadMountable;
import org.openpnp.spi.PropertySheetHolder;
import org.openpnp.spi.base.AbstractDispenser;
import org.openpnp.util.MovableUtils;
import org.pmw.tinylog.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.core.Persist;

public class DispensingValve extends AbstractDispenser implements ReferenceHeadMountable {

    @Element
    private Location headOffsets = new Location(LengthUnit.Millimeters);

    @Attribute(required = false)
    private double dotSize;

    @Attribute(required = false)
    private int defaultDispenseTime;

    @Element(required = false)
    private PadCoveringMethod padCoveringMethod;

    @Deprecated
    @Element(required = false)
    protected Length safeZ = null;

    @Attribute(required = false)
    private boolean enableDynamicSafeZ = false;

    @Element(required = false)
    private String compressedAirActuatorName;

    @Attribute(required = false)
    private int version; // the OpenPnP target version/migration status (version x 100)

    private Actuator compressedAirActuator;

    public DispensingValve() {
        super();
    }
    public DispensingValve(String id) {
        this();
        this.id = id;
    }

    @Override
    public void applyConfiguration(Configuration configuration) {
        super.applyConfiguration(configuration);
        // When brand new nozzles are created rather than loaded from configuration, configurationLoaded() is also 
        // triggered. Therefore we need to check for the presence of the head. 
        if (getHead() !=  null) {
            // Resolve the actuators.
            compressedAirActuator = getHead().getActuatorByName(compressedAirActuatorName); 
        }
    }

    @Persist
    protected void persist() {
        // Make sure the latest actuator names are persisted.
        compressedAirActuatorName = (compressedAirActuator == null ? null : compressedAirActuator.getName());
    }

    public boolean isEnableDynamicSafeZ() {
        return enableDynamicSafeZ;
    }

    public void setEnableDynamicSafeZ(boolean enableDynamicSafeZ) {
        this.enableDynamicSafeZ = enableDynamicSafeZ;
    }

    public double getDotSize() {
        return dotSize;
    }

    public void setDotSize(double dotSize) {
        this.dotSize = dotSize;
    }

    public int getDefaultDispenseTime() {
        return defaultDispenseTime;
    }

    public void setDefaultDispenseTime(int defaultDispenceTime) {
        this.defaultDispenseTime = defaultDispenceTime;
    }

    public PadCoveringMethod getPadCoveringMethod(){
        return padCoveringMethod;
    }

    public void setPadCoveringMethod(PadCoveringMethod padCoveringMethod){
        this.padCoveringMethod = padCoveringMethod;
    }

    @Override
    public Location getHeadOffsets() {
        return headOffsets;
    }

    @Override
    public void setHeadOffsets(Location headOffsets) {
        Location oldValue = this.headOffsets;
        this.headOffsets = headOffsets;
        firePropertyChange("headOffsets", oldValue, headOffsets);
        adjustHeadOffsetsDependencies(oldValue, headOffsets);
    }

    /**
     * Adjust any dependent head offsets, e.g. after calibration.
     * 
     * @param headOffsetsOld
     * @param headOffsetsNew
     * @param offsetsDiff
     */
    public void adjustHeadOffsetsDependencies(Location headOffsetsOld, Location headOffsetsNew) {
        Location offsetsDiff = headOffsetsNew.subtract(headOffsetsOld).convertToUnits(LengthUnit.Millimeters);

        if (offsetsDiff.isInitialized() && headOffsetsNew.isInitialized() && head != null) {
            
            if (headOffsetsOld.isInitialized()) {
                // The old offsets were not zero, adjust some dependent head offsets.

                // Where another HeadMountable, such as an Actuator, is fastened to the nozzle, it may have the same X, Y head offsets, i.e. these were very 
                // likely copied over, like customary for the ReferencePushPullFeeder actuator. Adjust them likewise. 
                for (HeadMountable hm : head.getHeadMountables()) {
                    if (this != hm 
                            && (hm instanceof ReferenceHeadMountable)
                            && !(hm instanceof ReferenceNozzle)) {
                        ReferenceHeadMountable otherHeadMountable = (ReferenceHeadMountable) hm;
                        Location otherHeadOffsets = otherHeadMountable.getHeadOffsets();
                        if (otherHeadOffsets.isInitialized() 
                                && headOffsetsOld.convertToUnits(LengthUnit.Millimeters).getLinearDistanceTo(otherHeadOffsets) <= 0.01) {
                            // Take X, Y (but not Z).
                            Location hmOffsets = otherHeadOffsets.derive(headOffsetsNew, true, true, false, false);
                            Logger.info("Set "+otherHeadMountable.getClass().getSimpleName()+" " + otherHeadMountable.getName() + " head offsets to " + hmOffsets
                                    + " (previously " + otherHeadOffsets + ")");
                            otherHeadMountable.setHeadOffsets(hmOffsets);
                        }
                    }
                }

                // Also adjust up-looking camera offsets, as these were very likely calibrated using the default nozzle.
                try {
                    if (this == head.getDefaultNozzle()) {
                        for (Camera camera : getMachine().getCameras()) {
                            if (camera instanceof ReferenceCamera 
                                    && camera.getLooking() == Looking.Up) {
                                ReferenceHeadMountable upLookingCamera = (ReferenceHeadMountable) camera;
                                Location cameraOffsets = upLookingCamera.getHeadOffsets();
                                if (cameraOffsets.isInitialized()) {
                                    cameraOffsets = cameraOffsets.add(offsetsDiff);
                                    Logger.info("Set camera " + upLookingCamera.getName() + " head offsets to " + cameraOffsets
                                            + " (previously " + upLookingCamera.getHeadOffsets() + ")");
                                    upLookingCamera.setHeadOffsets(cameraOffsets);
                                }
                            }
                        }
                    }
                }
                catch (Exception e) {
                    Logger.warn(e);
                }
            }
        }
    }

    @Override
    public void moveToPlacementLocation(Location placementLocation, Part part) throws Exception {
        // The default ReferenceNozzle implementation just moves to the placementLocation + partHeight at safe Z.
        if (part != null) {
            placementLocation = placementLocation
                    .add(new Location(part.getHeight().getUnits(), 0, 0, part.getHeight().getValue(), 0));
        }
        MovableUtils.moveToLocationAtSafeZ(this, placementLocation);
    }

    @Override
    public void place() throws Exception {
        this.place(defaultDispenseTime);
    }

    @Override
    public void place(int dispenseTime) throws Exception {
        Logger.debug("{}.place()", getName());

        Map<String, Object> globals = new HashMap<>();
        globals.put("dispenser", this);
        Configuration.get().getScripting().on("Dispenser.BeforeDispense", globals);

        actuateCompressedAirActuator(true);

        // wait for the Dwell Time and/or make sure the vacuum level decays to the desired range (with timeout)
        Thread.sleep(dispenseTime);

        actuateCompressedAirActuator(false);

        getMachine().fireMachineHeadActivity(head);

        Configuration.get().getScripting().on("Dispenser.AfterDispense", globals);
    }
    
    @Override
    public boolean isCalibrated() {
        // No calibration needed.
        return true;
    }

    public void calibrate() {}

    @Override
    public Location getCameraToolCalibratedOffset(Camera camera) {
        return new Location(camera.getUnitsPerPixel().getUnits());
    }

    @Override 
    public Length getEffectiveSafeZ() throws Exception {
        Length safeZ = super.getEffectiveSafeZ();
        if (safeZ == null) {
            throw new Exception("Nozzle "+getName()+" has no Z axis with Safe Zone mapped.");
        }
        return safeZ;
    }

    @Override
    public void home() throws Exception {}

    protected void ensureZCalibrated(boolean assumeNozzleTipLoaded) throws Exception {}

    @Override
    public Wizard getConfigurationWizard() {
        return new DispensingValveConfigurationWizard(getMachine(), this);
    }

    @Override
    public String getPropertySheetHolderTitle() {
        return getClass().getSimpleName() + " " + getName();
    }

    @Override
    public PropertySheetHolder[] getChildPropertySheetHolders() {
        return null;
    }

    @Override
    public PropertySheet[] getPropertySheets() {
        return new PropertySheet[] {
                new PropertySheetWizardAdapter(getConfigurationWizard()),
                // new PropertySheetWizardAdapter(new ReferenceNozzleCompatibleNozzleTipsWizard(this),
                //         Translations.getString("ReferenceNozzle.PropertySheetHolder.NozzleTips.title")), //$NON-NLS-1$
                // new PropertySheetWizardAdapter(new ReferenceNozzleVacuumWizard(this),
                //         Translations.getString("ReferenceNozzle.PropertySheetHolder.Vacuum.title")), //$NON-NLS-1$
                // new PropertySheetWizardAdapter(new ReferenceNozzleToolChangerWizard(this),
                //         Translations.getString("ReferenceNozzle.PropertySheetHolder.ToolChanger.title")), //$NON-NLS-1$
                // new PropertySheetWizardAdapter(new ReferenceNozzleCameraOffsetWizard(this),
                //         Translations.getString("ReferenceNozzle.PropertySheetHolder.OffsetWizard.title")), //$NON-NLS-1$
        };
    }

    @Override
    public Action[] getPropertySheetHolderActions() {
        return new Action[] {deleteAction};
    }

    public Action deleteAction = new AbstractAction("Delete Nozzle") {
        {
            putValue(SMALL_ICON, Icons.nozzleRemove);
            putValue(NAME, Translations.getString("ReferenceNozzle.Action.Delete")); //$NON-NLS-1$
            putValue(SHORT_DESCRIPTION, Translations.getString("ReferenceNozzle.Action.Delete.Description")); //$NON-NLS-1$
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (getHead().getNozzles().size() == 1) {
                MessageBoxes.errorBox(null, "Error: Nozzle Not Deleted", "Can't delete last nozzle. There must be at least one nozzle.");
                return;
            }
            int ret = JOptionPane.showConfirmDialog(MainFrame.get(),
                    Translations.getString("DialogMessages.ConfirmDelete.text") + " " + getName() + "?", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    Translations.getString("DialogMessages.ConfirmDelete.title") + " " + getName() + "?", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    JOptionPane.YES_NO_OPTION);
            if (ret == JOptionPane.YES_OPTION) {
                getHead().removeDispencer(DispensingValve.this);
            }
        }
    };

    @Override
    public String toString() {
        return getName() + " " + getId();
    }

    /**
     * @return The actuator used to switch the vacuum valve on the Nozzle. 
     */
    public Actuator getCompressedAirActuator() {
        return compressedAirActuator;
    }

    /**
     * @return The actuator used to switch the vacuum valve on the Nozzle. 
     * @throws Exception when the actuator is not configured.
     */
    public Actuator getExpectedCompressedAirActuator() throws Exception {
        Actuator actuator = getCompressedAirActuator();
        if (actuator == null) {
            throw new Exception("Dispenser "+getName()+" has no compressed air actuator assigned.");
        }
        return actuator;
    }
    

    /**
     * Set the actuator used to switch the vacuum valve on the Nozzle. 
     * @param actuator
     */
    public void setCompressedAirActuator(Actuator actuator) {
        compressedAirActuator = actuator;
    }

    protected void actuateCompressedAirActuator(boolean value) throws Exception {
        getExpectedCompressedAirActuator().actuate(value);

    }
}
