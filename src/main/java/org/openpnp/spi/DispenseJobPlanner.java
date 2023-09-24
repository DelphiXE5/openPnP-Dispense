package org.openpnp.spi;

import java.util.List;

import org.openpnp.spi.PnpJobProcessor.JobPlacement;

public interface DispenseJobPlanner {
    public List<PlannedDispense> plan(Head head, List<JobPlacement> placements);
}
