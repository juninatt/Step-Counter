package se.pbt.stepcounter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import se.pbt.stepcounter.dto.stepdto.StepDTO;
import se.pbt.stepcounter.dto.stepdto.StepDateDTO;
import se.pbt.stepcounter.model.MonthStep;
import se.pbt.stepcounter.model.Step;
import se.pbt.stepcounter.model.WeekStep;

/**
 * Interface for mapping between StepDTO, Step, WeekStep and MonthStep objects and StepDateDTO objects.
 *
 * @see StepDTO
 */
@Mapper
public interface StepMapper {

    /**
     * A static instance of the mapper.
     */
    StepMapper mapper = Mappers.getMapper(StepMapper.class);

    /**
     * Maps a StepDTO object to a Step object.
     *
     * @param stepDto A {@link StepDTO} object
     * @return A {@link Step} object
     */
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "stepCount", source = "stepCount")
    @Mapping(target = "startTime", source = "startTime")
    @Mapping(target = "endTime", source = "endTime")
    @Mapping(target = "uploadTime", source = "uploadTime")
    Step stepDtoToStep(StepDTO stepDto);


    /**
     * @param stepDto A {@link StepDTO} object
     * @return A {@link WeekStep} object
     */
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "stepCount", source = "stepCount")
    @Mapping(target = "week", expression = "java(DateHelper.getWeek(stepDto.getStartTime()))")
    @Mapping(target = "year", expression = "java(stepDto.getStartTime().getYear())")
    WeekStep stepDtoToWeekStep(StepDTO stepDto);

    /**
     * @param stepDto A {@link StepDTO} object
     * @return A {@link MonthStep} object
     */
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "stepCount", source = "stepCount")
    @Mapping(target = "month", expression = "java(stepDto.getStartTime().getMonthValue())")
    @Mapping(target = "year", expression = "java(stepDto.getStartTime().getYear())") MonthStep stepDtoToMonthStep(StepDTO stepDto);

    /**
     * @param  step A {@link Step} object
     * @return A {@link StepDateDTO} object
     */
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "steps", source = "stepCount")
    @Mapping(target = "date", source = "endTime")
    @Mapping(target = "dayOfWeek", expression = "java(DateHelper.getWeek(step.getEndTime()))")
    StepDateDTO stepToStepDateDto(Step step);

    /**
     * Maps a Step object to a StepDTO object.
     *
     * @param step A {@link Step} object
     * @return A {@link StepDTO} object
     */
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "stepCount", source = "stepCount")
    @Mapping(target = "startTime", source = "startTime")
    @Mapping(target = "endTime", source = "endTime")
    @Mapping(target = "uploadTime", source = "uploadTime")
    StepDTO stepToStepDTO(Step step);
}
