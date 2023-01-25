package se.sigma.boostapp.boost_app_java.builder;

import se.sigma.boostapp.boost_app_java.dto.stepdto.BulkStepDateDTO;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDateDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * The BulkStepDateDTOBuilder class is a builder class that creates
 * {@link BulkStepDateDTO} objects. It allows for building the object
 *  step by step, by setting its different properties.
 */
public class BulkStepDateDTOBuilder extends BaseDTOBuilder<BulkStepDateDTO> {
    private final List<StepDateDTO> stepList = new ArrayList<>();

    /**
     * Creates an instance of the class
     *
     * @return An instance of BulkStepDateDTO
     */
    protected BulkStepDateDTO createDto() {
        return new BulkStepDateDTO();
    }

    /**
     * Sets the userId of the BulkStepDateDTO
     *
     * @param userId the userId of the BulkStepDateDTO
     * @return this builder instance
     */

    public BulkStepDateDTOBuilder withUserId(String userId) {
        dto.setUserId(userId);
        return this;
    }

    /**
     * Sets the stepList of the BulkStepDateDTO
     *
     * @param stepList the list of {@link StepDateDTO} to be added to the BulkStepDateDTO
     * @return this builder instance
     */
    public BulkStepDateDTOBuilder withStepList(List<StepDateDTO> stepList) {
        this.stepList.addAll(stepList);
        return this;
    }

    /**
     * Builds the BulkStepDateDTO with all the properties set
     *
     * @return The built BulkStepDateDTO object
     */
    public BulkStepDateDTO build(){
        dto.setStepList(stepList);
        return dto;
    }
}
