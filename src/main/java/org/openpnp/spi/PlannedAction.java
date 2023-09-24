package org.openpnp.spi;

import org.openpnp.spi.PnpJobProcessor.JobPlacement;

public class PlannedAction {
    public final JobPlacement jobPlacement;
    public PartAlignment.PartAlignmentOffset alignmentOffsets;

    public PlannedAction(JobPlacement jobPlacement) {
        this.jobPlacement = jobPlacement;
    }

    @Override
    public String toString() {
        return String.format("%s", jobPlacement);
    }
}
