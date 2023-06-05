package se.pbt.stepcounter.mapper;

import se.pbt.stepcounter.dto.stepdto.StepDTO;
import se.pbt.stepcounter.model.MonthStep;
import se.pbt.stepcounter.model.WeekStep;
import se.pbt.stepcounter.testobjects.dto.stepdto.TestStepDtoBuilder;
import se.pbt.stepcounter.testobjects.model.TestStepBuilder;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StepMapperTest {

    private final StepMapper stepMapper = StepMapper.mapper;

    TestStepDtoBuilder testDTOBuilder = new TestStepDtoBuilder();

    StepDTO testStepDTO;

    @Test
    @DisplayName("Test StepDTO to Step mapping")
    public void testStepDtoToStep() {
        testStepDTO = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();
        var step = stepMapper.stepDtoToStep(testStepDTO);

        assertThat(testStepDTO.getStepCount()).isEqualTo(step.getStepCount());
        assertThat(testStepDTO.getStartTime()).isEqualTo(step.getStartTime());
        assertThat(testStepDTO.getEndTime()).isEqualTo(step.getEndTime());
        assertThat(testStepDTO.getUploadTime()).isEqualTo(step.getUploadTime());
    }

    @Test
    @DisplayName("Test StepDTO to WeekStep mapping")
    public void testStepDtoToWeekStep() {
        testStepDTO = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();
        WeekStep weekStep = stepMapper.stepDtoToWeekStep(testStepDTO);

        assertThat(testStepDTO.getStartTime().getYear()).isEqualTo(weekStep.getYear());
        assertThat(DateHelper.getWeek(testStepDTO.getEndTime())).isEqualTo(weekStep.getWeek());
        assertThat(testStepDTO.getStepCount()).isEqualTo(weekStep.getStepCount());
    }

    @Test
    @DisplayName("Test StepDTO to MonthStep mapping")
    public void testStepDtoToMonthStep() {
        testStepDTO = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();
        MonthStep monthStep = stepMapper.stepDtoToMonthStep(testStepDTO);

        assertThat(testStepDTO.getStartTime().getYear()).isEqualTo(monthStep.getYear());
        assertThat(testStepDTO.getStartTime().getMonthValue()).isEqualTo(monthStep.getMonth());
        assertThat(testStepDTO.getStepCount()).isEqualTo(monthStep.getStepCount());
    }

    @Test
    @DisplayName("Test Step to StepDateDTO mapping")
    public void testStepToStepDateDto() {
        var testStepBuilder = new TestStepBuilder();
        var step = testStepBuilder.createStepOfFirstMinuteOfYear();

        var stepDateDto = stepMapper.stepToStepDateDto(step);

        Instant stepDate = step.getEndTime().toInstant();

        assertThat(Date.from(stepDate)).isEqualTo(stepDateDto.getDate());
        assertThat(step.getStepCount()).isEqualTo(stepDateDto.getSteps());
        assertThat(step.getUserId()).isEqualTo(stepDateDto.getUserId());
    }
}
