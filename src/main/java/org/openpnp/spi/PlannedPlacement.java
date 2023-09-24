package org.openpnp.spi;

import org.openpnp.spi.PnpJobProcessor.JobPlacement;

public class PlannedPlacement extends PlannedAction {
    public final Nozzle nozzle;
    public final NozzleTip nozzleTip;
    public Feeder feeder;

    public PlannedPlacement(Nozzle nozzle, NozzleTip nozzleTip, JobPlacement jobPlacement) {
        super(jobPlacement);
        this.nozzle = nozzle;
        this.nozzleTip = nozzleTip;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) -> %s", nozzle.getName(), nozzleTip.getName(), jobPlacement);
    }
}