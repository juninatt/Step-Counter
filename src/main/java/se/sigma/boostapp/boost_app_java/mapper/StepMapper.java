package se.sigma.boostapp.boost_app_java.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDateDTO;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.WeekStep;

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
    @Mapping(target = "uploadedTime", source = "uploadTime")
    Step stepDtoToStep(StepDTO stepDto);


    /**
     * @param stepDto A {@link StepDTO} object
     * @return A {@link WeekStep} object
     */
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "stepCount", source = "stepCount")
    @Mapping(target = "week", expression = "java(DateHelper.getWeek(stepDto.getEndTime()))")
    @Mapping(target = "year", expression = "java(stepDto.getYear())")
    WeekStep stepDtoToWeekStep(StepDTO stepDto);

    /**
     * @param stepDto A {@link StepDTO} object
     * @return A {@link MonthStep} object
     */
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "stepCount", source = "stepCount")
    @Mapping(target = "month", expression = "java(stepDto.getMonth())")
    @Mapping(target = "year", expression = "java(stepDto.getYear())") MonthStep stepDtoToMonthStep(StepDTO stepDto);

    /**
     * @param  step A {@link Step} object
     * @return A {@link StepDateDTO} object
     */
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "steps", source = "stepCount")
    @Mapping(target = "date", source = "endTime")
    @Mapping(target = "dayOfWeek", expression = "java(DateHelper.getWeek(step.getEndTime()))")
    StepDateDTO stepToStepDateDto(Step step);
}