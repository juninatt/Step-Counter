package se.pbt.stepcounter.mapper;

import se.pbt.stepcounter.dto.stepdto.StepDTO;
import se.pbt.stepcounter.model.MonthStep;
import se.pbt.stepcounter.model.WeekStep;
import se.pbt.stepcounter.testobjects.TestObjectBuilder;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StepMapperTest {

    private final StepMapper stepMapper = StepMapper.mapper;

    TestObjectBuilder testObjectBuilder = new TestObjectBuilder(2023);

    StepDTO testStepDTO;

    @Test
    @DisplayName("Maps StepDTO to Step")
    public void testStepDtoToStep() {
        testStepDTO = testObjectBuilder.getTestStepDTO();
        var step = stepMapper.stepDtoToStep(testStepDTO);

        assertThat(testStepDTO.getStepCount()).isEqualTo(step.getStepCount());
        assertThat(testStepDTO.getStartTime()).isEqualTo(step.getStartTime());
        assertThat(testStepDTO.getEndTime()).isEqualTo(step.getEndTime());
        assertThat(testStepDTO.getUploadTime()).isEqualTo(step.getUploadTime());
    }

    @Test
    @DisplayName("Maps StepDTO to WeekStep")
    public void testStepDtoToWeekStep() {
        testStepDTO = testObjectBuilder.getTestStepDTO();
        WeekStep weekStep = stepMapper.stepDtoToWeekStep(testStepDTO);

        assertThat(testStepDTO.getStartTime().getYear()).isEqualTo(weekStep.getYear());
        assertThat(DateHelper.getWeek(testStepDTO.getEndTime())).isEqualTo(weekStep.getWeek());
        assertThat(testStepDTO.getStepCount()).isEqualTo(weekStep.getStepCount());
    }

    @Test
    @DisplayName("Maps StepDTO to MonthStep")
    public void testStepDtoToMonthStep() {
        testStepDTO = testObjectBuilder.getTestStepDTO();
        MonthStep monthStep = stepMapper.stepDtoToMonthStep(testStepDTO);

        assertThat(testStepDTO.getStartTime().getYear()).isEqualTo(monthStep.getYear());
        assertThat(testStepDTO.getStartTime().getMonthValue()).isEqualTo(monthStep.getMonth());
        assertThat(testStepDTO.getStepCount()).isEqualTo(monthStep.getStepCount());
    }

    @Test
    @DisplayName("Maps Step to StepDateDTO")
    public void testStepToStepDateDto() {
        var testStepBuilder = new TestObjectBuilder(2023);
        var step = testStepBuilder.getTestStep();

        var stepDateDto = stepMapper.stepToStepDateDto(step);

        Instant stepDate = step.getEndTime().toInstant();

        assertThat(Date.from(stepDate)).isEqualTo(stepDateDto.getDate());
        assertThat(step.getStepCount()).isEqualTo(stepDateDto.getSteps());
        assertThat(step.getUserId()).isEqualTo(stepDateDto.getUserId());
    }
}
