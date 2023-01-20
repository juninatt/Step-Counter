package se.sigma.boostapp.boost_app_java.mapper;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.WeekStep;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StepMapperTest {

    private final StepMapper stepMapper = StepMapper.mapper;
    private final LocalDateTime now = LocalDateTime.now();
    StepDTO testStepDto = new StepDTO(69, now, now, now);

    @Test
    @DisplayName("Test StepDTO to Step mapping")
    public void testStepDtoToStep() {
        var step = stepMapper.stepDtoToStep(testStepDto);

        assertThat(testStepDto.getStepCount()).isEqualTo(step.getStepCount());
        assertThat(testStepDto.getStartTime()).isEqualTo(step.getStartTime());
        assertThat(testStepDto.getEndTime()).isEqualTo(step.getEndTime());
        assertThat(testStepDto.getUploadTime()).isEqualTo(step.getUploadedTime());
    }

    @Test
    @DisplayName("Test StepDTO to WeekStep mapping")
    public void testStepDtoToWeekStep() {
        WeekStep weekStep = stepMapper.stepDtoToWeekStep(testStepDto);

        assertThat(testStepDto.getYear()).isEqualTo(weekStep.getYear());
        assertThat(DateHelper.getWeek(testStepDto.getEndTime())).isEqualTo(weekStep.getWeek());
        assertThat(testStepDto.getStepCount()).isEqualTo(weekStep.getStepCount());
    }

    @Test
    @DisplayName("Test StepDTO to MonthStep mapping")
    public void testStepDtoToMonthStep() {
        MonthStep monthStep = stepMapper.stepDtoToMonthStep(testStepDto);

        assertThat(testStepDto.getYear()).isEqualTo(monthStep.getYear());
        assertThat(testStepDto.getMonth()).isEqualTo(monthStep.getMonth());
        assertThat(testStepDto.getStepCount()).isEqualTo(monthStep.getStepCount());
    }

    @Test
    @DisplayName("Test Step to StepDateDTO mapping")
    public void testStepToStepDateDto() {
        var step = new Step("userId", 13, now, now, now);

        var stepDateDto = stepMapper.stepToStepDateDto(step);

        Instant stepDate = step.getEndTime().toInstant(ZoneOffset.UTC);

        assertThat(Date.from(stepDate)).isEqualTo(stepDateDto.getDate());
        assertThat(step.getStepCount()).isEqualTo(stepDateDto.getSteps());
        assertThat(step.getUserId()).isEqualTo(stepDateDto.getUserId());
    }
}
