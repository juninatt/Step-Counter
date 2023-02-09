package com.nexergroup.boostapp.java.step.builder;

import com.nexergroup.boostapp.java.step.dto.stepdto.BulkStepDateDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDateDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * The BulkStepDateDTOBuilder class is a builder class that creates
 * {@link BulkStepDateDTO} objects. It allows for building the object
 *  step by step, by setting its different properties.
 */
public class BulkStepDateDTOBuilder extends BaseDTOBuilder<BulkStepDateDTO> {

    /**
     * A list containing the {@link StepDateDTO} objects to be added to
     * the {@link BulkStepDateDTO} object being built.
     */
    private final List<StepDateDTO> stepList = new ArrayList<>();

    /**
     * Sets the userId of the {@link BulkStepDateDTO}.
     *
     * @param userId the userId of the {@link BulkStepDateDTO}.
     * @return an instance of this builder class.
     */
    public BulkStepDateDTOBuilder withUserId(String userId) {
        dto.setUserId(userId);
        return this;
    }

    /**
     * Sets the of {@link StepDateDTO} object to the {@link BulkStepDateDTO} object.
     *
     * @param stepList the list of {@link StepDateDTO} to be added to the {@link BulkStepDateDTO} object.
     * @return an instance of this builder class.
     */
    public BulkStepDateDTOBuilder withStepList(List<StepDateDTO> stepList) {
        this.stepList.addAll(stepList);
        return this;
    }

    /**
     * Creates an instance of the class
     *
     * @return an instance of {@link BulkStepDateDTO}
     */
    @Override
    protected BulkStepDateDTO createDto() {
        return new BulkStepDateDTO();
    }

    /**
     * Builds the {@link BulkStepDateDTO} object with all the properties set
     *
     * @return The built {@link BulkStepDateDTO} object
     */
    @Override
    public BulkStepDateDTO build(){
        dto.setStepList(stepList);
        return dto;
    }
}
