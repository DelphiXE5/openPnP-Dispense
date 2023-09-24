package org.openpnp.spi;

import org.openpnp.model.Location;
import org.openpnp.model.Part;

/**
 * A Dispencer is a tool capable of dispencing (solder) paste. It is attached to a Head and
 * may move entirely with the head or partially independent of it. 
 */
public interface Dispenser
        extends HeadMountable, WizardConfigurable, PropertySheetHolder {

    public double getDotSize();

    public int getDefaultDispenseTime();

    public enum PadCoveringMethod {
      Dotting,
      VolumeScaling
    }

    public PadCoveringMethod getPadCoveringMethod();

    /**
     * Move the Nozzle to the given placementLocation. This will move at safe Z and position the Nozzle
     * so it is ready for {@link #place()}. This might or might not involve offsets and actions for 
     * contact-probing. 
     * 
     * @param placementLocation
     * @param part Part to be placed, null on discard. 
     * @throws Exception
     */
    void moveToPlacementLocation(Location placementLocation, Part part) throws Exception;

    /**
     * Commands the Nozzle to perform it's place operation. Generally this just consists of
     * releasing vacuum and may include a puff of air to set the Part. When this is called during
     * job processing the processor will have already positioned the nozzle over the part to be
     * placed and lowered it to the correct height.
     * 
     * @throws Exception
     */
    public void place() throws Exception;

    public void place(int dispenseTime) throws Exception;

    public enum PartOnStep {
        AfterPick,
        Align,
        BeforePlace
    }

    public void calibrate() throws Exception;
    public boolean isCalibrated();

}
