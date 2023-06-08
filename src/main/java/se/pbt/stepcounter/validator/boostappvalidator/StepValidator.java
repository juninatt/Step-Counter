package se.pbt.stepcounter.validator.boostappvalidator;

import se.pbt.stepcounter.dto.stepdto.StepDTO;
import se.pbt.stepcounter.exception.DateTimeValueException;
import se.pbt.stepcounter.exception.InvalidUserIdException;
import se.pbt.stepcounter.exception.ValidationFailedException;
import se.pbt.stepcounter.model.Step;
import se.pbt.stepcounter.repository.StepRepository;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * A utility class that validates the data in {@link StepDTO} objects.
 * Uses Optional wrappers to avoid nullPointer exceptions when data is not present.
 * Uses {@link StepRepository} to find Step objects in the database belonging to the same userId as the {@link StepDTO}.
 *
 * @see Step
 */
public class StepValidator {
    private final StepRepository repository;

    /**
     * Constructs a new StepValidator with the given repository.
     *
     * @param repository the {@link StepRepository} used for interacting with the database
     */
    public StepValidator(StepRepository repository) {
        this.repository = repository;
    }

    /**
     * Checks if a single {@link StepDTO} object is valid.
     * The {@link StepDTO} object is seen as valid if is passes the {@link StepValidator#stepDtoIsValid(StepDTO)} method.
     *
     * @param stepData the {@link StepDTO} object to validate
     * @return true if the object is valid, false otherwise
     */
    public boolean stepDataIsValid(StepDTO stepData) {
        return stepDtoIsValid(stepData);
    }

    /**
     * Checks if the list passed as input is null or not
     * and if the {@link StepDTO} objects in the list passes the {@link StepValidator#stepDtoIsValid(StepDTO)} method.
     * *
     * @param stepDataList the list of {@link StepDTO} objects to validate
     * @return true if all objects in the list are seen as valid, false otherwise
     */
    public boolean stepDataIsValid(List<StepDTO> stepDataList) {
        return stepDataList != null &&
                stepDataList.stream()
                        .allMatch(this::stepDtoIsValid);
    }

    /**
     * Checks if the {@link Step} object should be updated or not
     *
     * @param stepData the {@link StepDTO} object holding the new data
     * @return true if the Step object should be updated, false otherwise
     */
    public boolean shouldUpdateStep(@NonNull StepDTO stepData) {
        // Fetch the most recently stored Step object for the specified user
        var existingStep = repository.findFirstByUserIdOrderByStartTimeDesc(stepData.getUserId());
        // Returns true if a Step is found for the userId with an endTime that is after the startTime of the new data
        return existingStep.map(step -> step.getStartTime().getDayOfYear() == stepData.getStartTime().getDayOfYear())
                .orElse(false);
    }

    /**
     * Checks if the fields of the {@link StepDTO} are null or not and if the time-fields are ok.
     * The time-fields are seen as ok if startTime is before endTime, and endTime is before uploadTime.
     *
     * @param stepDto the {@link StepDTO} object holding the data to validate
     * @return true if no fields are null and the time-fields are ok, false and throws exception otherwise
     */
    private boolean stepDtoIsValid(StepDTO stepDto) {
        return  noFieldsAreNull(stepDto) && timeFieldsAreCorrect(stepDto);

    }

    /**
     * Checks if any of the fields in the given {@link StepDTO} object are null.
     *
     * @param stepDto the {@link StepDTO} object holding the data to check
     * @return true if fields are null, false and throws exception otherwise
     */
    private boolean noFieldsAreNull(@NotNull StepDTO stepDto) {
        if (stepDto == null)
            throw new ValidationFailedException("Object holding new data cant be null");

        // Check each field and throw exception if null value is found
        if (stepDto.getUserId() == null)
            throw new InvalidUserIdException("User ID cant be null");
        else if (stepDto.getStepCount() < 1)
            throw new IllegalArgumentException("Step count must be greater than 0");
        else if (stepDto.getStartTime() == null)
            throw new DateTimeValueException("Start time cant be null");
        else if (stepDto.getEndTime() == null)
            throw new DateTimeValueException("End time cant be null");
        else if (stepDto.getUploadTime() == null)
            throw new DateTimeValueException("Upload time cant be null");
        else
            return true;
    }

    private boolean timeFieldsAreCorrect(StepDTO stepDto) {
        if (stepDto.getEndTime().isBefore(stepDto.getStartTime())){
            throw new DateTimeValueException("Start time must be before end time\n" + notValidDTOString(stepDto));
        } else {
            return true;
        }
    }


    /**
     * To print out DTO object to give further information if validation throws exception.
     * @param stepDTO the {@link StepDTO} object holding the time-fields
     * @return String of object whether if fields are null or not
     */
    public String notValidDTOString(StepDTO stepDTO) {

        String defaultMessage = "No Data Available";

        return String.format("stepCount= %s\nstartTime= %s\nendTime= %s\nuploadTime= %s",
                stepDTO.getStepCount() <= 1 ? defaultMessage : stepDTO.getStepCount(),
                stepDTO.getStartTime() == null ? defaultMessage : stepDTO.getStartTime(),
                stepDTO.getEndTime() == null ? defaultMessage : stepDTO.getEndTime(),
                stepDTO.getUploadTime() == null ? defaultMessage : stepDTO.getUploadTime());
    }
}