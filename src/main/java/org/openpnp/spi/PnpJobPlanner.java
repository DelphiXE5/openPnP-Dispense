package org.openpnp.spi;

import java.util.List;

import org.openpnp.spi.PnpJobProcessor.JobPlacement;

public interface PnpJobPlanner {    
    public List<PlannedPlacement> plan(Head head, List<JobPlacement> placements);
}
