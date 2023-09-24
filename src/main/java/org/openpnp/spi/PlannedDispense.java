package org.openpnp.spi;

import org.openpnp.spi.PnpJobProcessor.JobPlacement;

public class PlannedDispense extends PlannedAction {
    public final Dispenser dispenser;
    public Feeder feeder;

    public PlannedDispense(Dispenser dispenser, JobPlacement jobPlacement) {
        super(jobPlacement);
        this.dispenser = dispenser;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) -> %s", dispenser.getName(), this.jobPlacement);
    }
}