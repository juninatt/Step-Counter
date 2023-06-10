package se.pbt.stepcounter.testobjects;

import se.pbt.stepcounter.dto.stepdto.StepDTO;
import se.pbt.stepcounter.model.Step;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Builder class for creating test {@link se.pbt.stepcounter.model.Step} objects.
 */
public class TestObjectBuilder {
    private final ZonedDateTime startTime;
    private final ZonedDateTime endTime;
    private final ZonedDateTime uploadTime;

    /**
     * Constructs a TestStepBuilder with the specified year.
     *
     * @param year the year to use for constructing the time fields
     */
    public TestObjectBuilder(int year) {
        startTime = LocalDateTime.of(year, 1, 1, 1, 1).atZone(ZoneId.systemDefault());
        endTime = startTime.plusMinutes(10);
        uploadTime = endTime.plusMinutes(10);
    }

    /**
     * Creates a test {@link Step} object starting the first minute of the year
     * and with a 10-minute delay to each time field. User ID 'testUser'. Step count 13.
     *
     * @return the created test {@link Step} object
     */
    public Step getTestStep() {
        return new Step("testUser", 13, startTime, endTime, uploadTime);
    }

    /**
     * Creates a test {@link StepDTO} object starting the first minute of the year
     * and with a 10-minute delay to each time field. User ID 'testUser'. Step count 13.
     *
     * @return the created test {@link StepDTO} object
     */
    public StepDTO getTestStepDTO() {
        return new StepDTO("testUser", 13, startTime, endTime, uploadTime);
    }

    /**
     * Creates a copy of the given {@link Step} object with the time fields postponed by the specified number of minutes.
     *
     * @param step    the original {@link Step} object
     * @param minutes the number of minutes to postpone the time fields by
     * @return the new {@link Step} object with postponed time fields
     */
    public Step copyAndPostponeMinutes(Step step, int minutes) {
        return new Step(
                step.getUserId(),
                step.getStepCount(),
                step.getStartTime().plusMinutes(minutes),
                step.getEndTime().plusMinutes(minutes),
                step.getUploadTime().plusMinutes(minutes)
        );
    }

    /**
     * Creates a copy of the given {@link StepDTO} object with the time fields postponed by the specified number of minutes.
     *
     * @param stepDTO    the original {@link StepDTO} object
     * @param minutes the number of minutes to postpone the time fields by
     * @return the new {@link StepDTO} object with postponed time fields
     */
    public StepDTO copyAndPostponeMinutes(StepDTO stepDTO, int minutes) {
        return new StepDTO(
                stepDTO.getUserId(),
                stepDTO.getStepCount(),
                stepDTO.getStartTime().plusMinutes(minutes),
                stepDTO.getEndTime().plusMinutes(minutes),
                stepDTO.getUploadTime().plusMinutes(minutes)
        );
    }
}
