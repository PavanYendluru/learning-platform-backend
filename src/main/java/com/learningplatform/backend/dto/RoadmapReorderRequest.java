package com.learningplatform.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class RoadmapReorderRequest {

    @NotEmpty(message = "Step IDs list cannot be empty")
    private List<Long> stepIds;

    public List<Long> getStepIds() {
        return stepIds;
    }

    public void setStepIds(List<Long> stepIds) {
        this.stepIds = stepIds;
    }
}
