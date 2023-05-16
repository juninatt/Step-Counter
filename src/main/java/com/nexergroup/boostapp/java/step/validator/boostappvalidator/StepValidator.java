package com.nexergroup.boostapp.java.step.validator.boostappvalidator;

import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.exception.DateTimeValueException;
import com.nexergroup.boostapp.java.step.exception.UserIdValueException;
import com.nexergroup.boostapp.java.step.exception.ValidationFailedException;
import com.nexergroup.boostapp.java.step.mapper.DateHelper;
import com.nexergroup.boostapp.java.step.model.MonthStep;
import com.nexergroup.boostapp.java.step.model.WeekStep;
import com.nexergroup.boostapp.java.step.repository.StepRepository;
import com.nexergroup.boostapp.java.step.model.Step;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * A utility class that validates the data in {@link StepDTO} objects.
 * Uses Optional wrappers to avoid nullPointer exceptions when data is not present.
 * Uses {@link StepRepository} to find Step objects in the database belonging to the same userId as the {@link StepDTO}.
 *
 * @see com.nexergroup.boostapp.java.step.model.Step
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
     * Checks if the given {@link  StepDTO} should update the provided {@link WeekStep} object.
     *
     * @param stepDTO  the {@link StepDTO} holding the data to be stored in the database
     * @param weekStep  the {@link WeekStep} object to compare to.
     * @return true if the stepDTO belongs to the same week as the WeekStep object, false otherwise.
     */
    public boolean shouldUpdateWeekStep(@NonNull StepDTO stepDTO, WeekStep weekStep) {
        return DateHelper.getWeek(stepDTO.getStartTime()) == weekStep.getWeek();
    }

    /**
     * Checks if the given {@link  StepDTO} should update the provided {@link  MonthStep}.
     *
     * @param stepDTO   the {@link StepDTO} holding the data to be stored in the database
     * @param monthStep  the {@link MonthStep} object to compare to
     * @return true if the stepDTO belongs to the same month as the MonthStep object, false otherwise.
     */
    public boolean shouldUpdateMonthStep(@NonNull StepDTO stepDTO, MonthStep monthStep) {
        return monthStep.getMonth() == stepDTO.getStartTime().getMonthValue();
    }

    /**
     * Checks if the fields of the {@link StepDTO} are null or not and if the time-fields are ok.
     * The time-fields are seen as ok if startTime is before endTime, and endTime is before uploadTime.
     *
     * @param stepDto the {@link StepDTO} object holding the data to validate
     * @return true if no fields are null and the time-fields are ok, false and throws exception otherwise
     */
    private boolean stepDtoIsValid(StepDTO stepDto) {
        return  noFieldsAreNull(stepDto) &&
                endTimeIsAfterStartTime(stepDto) &&
                uploadTimeIsAfterEndTime(stepDto);
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
        // Assign all the DTO fields values to variables for readability
        var userId = stepDto.getUserId();
        var stepCount = stepDto.getStepCount();
        var startTime = stepDto.getStartTime();
        var endTime = stepDto.getEndTime();
        var uploadTime = stepDto.getUploadTime();

        // Check each field and throw exception if null value is found
        if (userId == null)
            throw new UserIdValueException("User ID cant be null");
        else if (stepCount < 1)
            throw new IllegalArgumentException("Step count must be greater than 0");
        else if (startTime == null)
            throw new DateTimeValueException("Start time cant be null");
        else if (endTime == null)
            throw new DateTimeValueException("End time cant be null");
        else if (uploadTime == null)
            throw new DateTimeValueException("Upload time cant be null");
        else
            return true;

    }

    /**
     * Checks whether the endTime of a {@link StepDTO} object is after its startTime or not.
     *
     * @param stepDto the {@link StepDTO} object holding the time-fields to check
     * @return true is the endTime of the {@link StepDTO} object is after its startTime, false and throws exception otherwise
     */
    private boolean endTimeIsAfterStartTime(StepDTO stepDto) {
        if (stepDto.getEndTime().isBefore(stepDto.getStartTime())){
            throw new DateTimeValueException("Start time must be before end time\n" + notValidDTOString(stepDto));
        } else {
            return true;
        }
    }

    /**
     * Checks whether the uploadTime of a {@link StepDTO} object is after its endTime or not.
     *
     * @param stepDto the {@link StepDTO} object holding the time-fields to check
     * @return true is the uploadTime of the {@link StepDTO} object is after endTime, false and throws exception otherwise
     */
    private boolean uploadTimeIsAfterEndTime(StepDTO stepDto) {
        if (stepDto.getUploadTime().isBefore(stepDto.getEndTime())) {
            throw new DateTimeValueException("Upload time must be after end time\n" + notValidDTOString(stepDto));
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