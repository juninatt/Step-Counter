package com.nexergroup.boostapp.java.step.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDateDTO;
import com.nexergroup.boostapp.java.step.model.MonthStep;
import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.model.WeekStep;

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
