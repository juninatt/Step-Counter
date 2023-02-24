package com.nexergroup.boostapp.java.step.validator.boostappvalidator;

import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.mapper.DateHelper;
import com.nexergroup.boostapp.java.step.model.MonthStep;
import com.nexergroup.boostapp.java.step.model.WeekStep;
import com.nexergroup.boostapp.java.step.exception.DateTimeValueException;
import com.nexergroup.boostapp.java.step.exception.ValidationFailedException;
import com.nexergroup.boostapp.java.step.repository.StepRepository;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Optional.ofNullable;

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
     * Checks if the {@link com.nexergroup.boostapp.java.step.model.Step} object should be updated or not
     *
     * @param stepData the {@link StepDTO} object holding the new data
     * @return true if the Step object should be updated, false otherwise
     */
    public boolean shouldUpdateStep(@NonNull StepDTO stepData) {
        // Fetch the most recently stored Step object for the specified user
        var existingStep = repository.findFirstByUserIdOrderByEndTimeDesc(stepData.getUserId());
        // Returns true if a Step is found for the userId with an endTime that is after the startTime of the new data
        return existingStep.map(step -> firstTimeFieldIsAfterSecondTimeField(step.getEndTime(), stepData.getStartTime()))
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
        return monthStep.getMonth() == stepDTO.getMonth();
    }

    /**
     * Checks if the fields of the {@link StepDTO} are null or not and if the time-fields are ok.
     * The time-fields are seen as ok if startTime is before endTime, and endTime is before uploadTime.
     *
     * @param stepDto the {@link StepDTO} object holding the data to validate
     * @return true if no fields are null and the time-fields are ok, false and throws exception otherwise
     */
    private boolean stepDtoIsValid(StepDTO stepDto) {
        return stepDtoIsNotNull(stepDto) &&
                startTimeIsNotNull(stepDto) &&
                endTimeIsNotNull(stepDto) &&
                uploadTimeIsNotNull(stepDto) &&
                endTimeIsAfterStartTime(stepDto) &&
                uploadTimeIsAfterEndTime(stepDto) &&
                startTimeDoesNotEqualEndTime(stepDto) &&
                startTimeDoesNotEqualUploadTime(stepDto) &&
                endTimeDoesNotEqualUploadTime(stepDto);
    }

    /**
     * Checks if a given {@link StepDTO} object is null.
     *
     * @param stepDto the {@link StepDTO} object to check
     * @return true if the object is not null, false and throws exception otherwise
     */
    private boolean stepDtoIsNotNull(StepDTO stepDto) {
        if (stepDto == null)
            throw new ValidationFailedException("Step object is empty");
         else
            return true;
    }

    /**
     * Checks if the startTime field of a given {@link StepDTO} object is null.
     *
     * @param stepDto the {@link StepDTO} object holding the data to check
     * @return true if the startTime field is not null, false and throws exception otherwise
     */
    private boolean startTimeIsNotNull(StepDTO stepDto) {
        if (!ofNullable(stepDto).map(StepDTO::getStartTime).isPresent()) {
            throw new ValidationFailedException("Step start time was null\n" + notValidDTOString(stepDto));
        } else {
            return true;
        }
    }

    /**
     * Checks if the endTime field of a given {@link StepDTO} object is null.
     *
     * @param stepDto the {@link StepDTO} object holding the data to check
     * @return true if the endTime field is not null, false and throws exception otherwise
     */
    private boolean endTimeIsNotNull(StepDTO stepDto) {
        if (!ofNullable(stepDto).map(StepDTO::getEndTime).isPresent()) {
            throw new ValidationFailedException("End time was null\n" + notValidDTOString(stepDto));
        } else {
            return true;
        }
    }

    /**
     * Checks if the uploadTime field of a given {@link StepDTO} object is null.
     *
     * @param stepDto the {@link StepDTO} object holding the data to check
     * @return true if the uploadTime field is not null, false and throws exception otherwise
     */
    private boolean uploadTimeIsNotNull(StepDTO stepDto) {
        if (!ofNullable(stepDto).map(StepDTO::getUploadTime).isPresent()) {
            throw new ValidationFailedException("Upload time is null\n" + notValidDTOString(stepDto));
        } else {
            return true;
        }
    }

    /**
     * Checks whether the endTime of a {@link StepDTO} object is after its startTime or not.
     *
     * @param stepDto the {@link StepDTO} object holding the time-fields to check
     * @return true is the endTime of the {@link StepDTO} object is after its startTime, false and throws exception otherwise
     */
    private boolean endTimeIsAfterStartTime(StepDTO stepDto) {
        if (!firstTimeFieldIsAfterSecondTimeField(stepDto.getEndTime(), stepDto.getStartTime())){
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
        if (!firstTimeFieldIsAfterSecondTimeField(stepDto.getUploadTime(), stepDto.getEndTime())) {
            throw new DateTimeValueException("Upload time must be after end time\n" + notValidDTOString(stepDto));
        } else {
            return true;
        }
    }

    /**
     * Checks whether startTime is equal to endTime of a {@link StepDTO} object
     * @param stepDto the {@link StepDTO} object holding the time-fields to check
     * @return true if the startTime of the {@link StepDTO} object does not equal endTime, false and throws exception otherwise
     */
    private boolean startTimeDoesNotEqualEndTime(StepDTO stepDto) {
        if (!firstTimeFieldDoesNotEqualSecondTimeField(stepDto.getStartTime(), stepDto.getEndTime())) {
            throw new DateTimeValueException("Start time needs to be before end time\n" + notValidDTOString(stepDto));
        } else {
            return true;
        }
    }

    /**
     * Checks whether startTime is equal to uploadTime of a {@link StepDTO} object
     * @param stepDto the {@link StepDTO} object holding the time-fields to check
     * @return true if the startTime of the {@link StepDTO} object does not equal uploadTime, false and throws exception otherwise
     */
    private boolean startTimeDoesNotEqualUploadTime(StepDTO stepDto) {
        if (!firstTimeFieldDoesNotEqualSecondTimeField(stepDto.getStartTime(), stepDto.getUploadTime())) {
            throw new DateTimeValueException("Start time needs to be before upload time\n" + notValidDTOString(stepDto));
        } else {
            return true;
        }
    }

    /**
     * Checks whether endTime is equal to uploadTime of a {@link StepDTO} object
     * @param stepDto the {@link StepDTO} object holding the time-fields to check
     * @return true if the endTime of the {@link StepDTO} object does not equal uploadTime, false and throws exception otherwise
     */
    private boolean endTimeDoesNotEqualUploadTime(StepDTO stepDto) {
        if (!firstTimeFieldDoesNotEqualSecondTimeField(stepDto.getStartTime(), stepDto.getEndTime())) {
            throw new DateTimeValueException("End time needs to be before upload time\n" + notValidDTOString(stepDto));
        } else {
            return true;
        }
    }

    /**
     * Compares two LocalDateTime objects to determine if they are equal or not.
     * @param fieldOne the first LocalDateTime object to compare
     * @param fieldTwo the second LocalDateTime object to compare
     * @return true if the first LocalDateTime object does not equal the second, false otherwise
     */
    private boolean firstTimeFieldDoesNotEqualSecondTimeField(LocalDateTime fieldOne, LocalDateTime fieldTwo) {
        return !fieldOne.isEqual(fieldTwo);
    }

    /**
     * Compares two LocalDateTime objects to determine if the first is after the second
     *
     * @param field1 the first LocalDateTime object to compare
     * @param field2 the second LocalDateTime object to compare
     * @return true if the first LocalDateTime object is after the second, false otherwise
     */
    private boolean firstTimeFieldIsAfterSecondTimeField(LocalDateTime field1, LocalDateTime field2) {
        if (field1 != null && field2 != null){
            return field1.isAfter(field2);
        }
        return false;
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