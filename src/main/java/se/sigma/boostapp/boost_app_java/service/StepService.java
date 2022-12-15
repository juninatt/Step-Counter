package se.sigma.boostapp.boost_app_java.service;

import org.springframework.stereotype.Service;
import se.sigma.boostapp.boost_app_java.dto.BulkUsersStepsDTO;
import se.sigma.boostapp.boost_app_java.dto.StepDTO;
import se.sigma.boostapp.boost_app_java.dto.StepDateDTO;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.WeekStep;
import se.sigma.boostapp.boost_app_java.repository.MonthStepRepository;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;
import se.sigma.boostapp.boost_app_java.repository.WeekStepRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class StepService {

    private final StepRepository stepRepository;
    private final MonthStepRepository monthStepRepository;
    private final WeekStepRepository weekStepRepository;

    /**
     * List meant to temporarily hold Step objects
     */
    private final List<Step> stepHolder = new ArrayList<>();

    /**
     * List meant to temporarily hold values for weeks, months and years
     */
    private final List<Integer> intHolder = new ArrayList<>();

    /**
     * Constructor
     *
     * @param stepRepository    {@link se.sigma.boostapp.boost_app_java.repository.StepRepository}
     * @param monthStepRepository   {@link se.sigma.boostapp.boost_app_java.repository.MonthStepRepository}
     * @param weekStepRepository    {@link se.sigma.boostapp.boost_app_java.repository.WeekStepRepository}
     */
    public StepService(final StepRepository stepRepository,
                       final MonthStepRepository monthStepRepository,
                       final WeekStepRepository weekStepRepository) {
        this.stepRepository = stepRepository;
        this.monthStepRepository = monthStepRepository;
        this.weekStepRepository = weekStepRepository;
    }

    /**
     * Check if any steps are present in database from requested user
     * If present update step-data for existing user otherwise create and save new step
     * Start time must be before end time, which in turn must be before uploaded time
     *
     * @param userId  ID of the user
     * @param stepDto Data for the steps {@link se.sigma.boostapp.boost_app_java.dto.StepDTO}
     */
    public Optional<Step> registerSteps(String userId, StepDTO stepDto) {
        // "stepDTO": "Start time must before end time, which in turn must be before uploaded time"
        stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId)
                .ifPresentOrElse(step -> addStepToHolder(registerStepExistingUser(userId, stepDto, step).get()),
                        () ->  addStepToHolder(registerNewStep(userId, stepDto).get()));
        return Optional.of(getStepFromHolder());
    }

    private Optional<Step> registerStepExistingUser(String userId, StepDTO stepDto, Step existingStep) {
        // existing last step day is the same as new step day && existing step endTime is before new step endTime
        if (endTimeIsSameYear(stepDto, existingStep) && existingStep.getEndTime().isBefore(stepDto.getEndTime())) {

            updateExistingStep(stepDto, existingStep);
            addStepToWeekAndMonthTable(userId, stepDto);
            addStepToHolder(stepRepository.save(existingStep));
        } else if (existingStep.getEndTime().isBefore(stepDto.getEndTime())) {
            addStepToHolder(registerNewStep(userId, stepDto).get());
        } else {
            addStepToHolder(null);
        }
        return Optional.of(getStepFromHolder());
    }

    private void addStepToWeekAndMonthTable(String userId, StepDTO stepDto) {
        addStepsToMonthTable(userId, stepDto.getStepCount(), stepDto.getEndTime().getMonthValue(), stepDto.getEndTime().getYear());
        addStepsToWeekTable(stepDto.getEndTime().getYear(), getWeekNumberFromDate(stepDto.getEndTime()), stepDto.getStepCount(), userId);
    }

    private void updateExistingStep(StepDTO stepDto, Step existingStep) {
        existingStep.setStepCount(existingStep.getStepCount() + stepDto.getStepCount());
        existingStep.setEndTime(stepDto.getEndTime());
        existingStep.setUploadedTime(stepDto.getUploadedTime());
    }

    private Optional<Step> registerNewStep(String userId, StepDTO stepDto) {
        //add steps to monthStep table
        addStepToWeekAndMonthTable(userId, stepDto);
        return Optional.of(stepRepository.save(new Step(userId, stepDto.getStepCount(), stepDto.getStartTime(),
                stepDto.getEndTime(), stepDto.getUploadedTime())));
    }

    /**
     * Check if any steps are present in database from requested user
     * If present returns the latest step from user, otherwise returns new step
     *
     * @param userId ID of the user
     * @return A Step {@link se.sigma.boostapp.boost_app_java.model.Step}
     */
    public Optional<Step> getLatestStepFromUser(String userId) {
        stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId)
                .ifPresentOrElse(this::addStepToHolder,
                        () -> addStepToHolder(new Step(userId, 0)));
        return Optional.of(getStepFromHolder());
    }

    /**
     * List of user to step, monthstep and weekstep table
     *
     * @param userId      A user ID
     * @param stepDtoList Data for the list of steps
     */
    public List<StepDTO> registerMultipleSteps(String userId, List<StepDTO> stepDtoList) {
        //if new user, add all to db
        if (!userHasSteps(userId)) {
            //get the earliest object in list
            var s = sortStepDTOListByEndTime(stepDtoList).get(0);
            stepRepository.save(new Step(userId, s.getStepCount(), s.getStartTime(), s.getEndTime(), s.getUploadedTime()));
            weekStepRepository.save(new WeekStep(userId, getWeekNumberFromDate(s.getEndTime()), s.getEndTime().getYear(), s.getStepCount()));
            monthStepRepository.save(new MonthStep(userId, s.getEndTime().getMonthValue(), s.getEndTime().getYear(), s.getStepCount()));
        }
        var existingStep = stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId).get();
        //check if any old dates are in list, throw away
        stepDtoList = stepDtoList.stream()
                .filter(stepDTO -> stepDTO.getEndTime().isAfter(existingStep.getEndTime()))
                .collect(Collectors.toList());
        stepDtoList.forEach(stepDTO -> {
                    updateLastStepInStepTable(stepDTO, userId, existingStep);
                    addStepToWeekAndMonthTable(userId, stepDTO);
                });
        return stepDtoList;
    }

    /**
     * Update last step in Step table
     *
     * @param userId     A user ID
     * @param latestStep The latest step
     */
    public void updateLastStepInStepTable(StepDTO stepDTO, String userId, Step latestStep) {
        if (endTimeIsSameDay(stepDTO, latestStep)) {
            updateExistingStep(stepDTO, latestStep);
            stepRepository.save(latestStep);
        } else {
            stepRepository.save(new Step(userId, stepDTO.getStepCount(), stepDTO.getStartTime(), stepDTO.getEndTime(), stepDTO.getUploadedTime()));
        }
    }

    /**
     * Add steps to month table
     *
     * @param userId A user ID
     * @param steps  Number of steps
     * @param month  Actual month
     * @param year   Actual year
     */
    private void addStepsToMonthTable(String userId, int steps, int month, int year) {
        monthStepRepository.findByUserIdAndYearAndMonth(userId, year, month)
                .ifPresentOrElse(monthStep -> {
                    monthStep.setSteps(steps + monthStep.getSteps());
                    monthStepRepository.save(monthStep);
                },
                        () -> monthStepRepository.save(new MonthStep(userId, month, year, steps)));
    }

    /**
     * Sort StepDTO-list by EndTime
     *
     * @param stepDtoList Data for the list of steps
     */
    public List<StepDTO> sortStepDTOListByEndTime(List<StepDTO> stepDtoList) {
        return stepDtoList
                .stream()
                .sorted(Comparator.comparing(StepDTO::getEndTime))
                .collect(Collectors.toList());
    }

    /**
     * Get week number of a certain date
     *
     * @param localDateTime Date as LocalDateTime @see <a href="https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html">...</a>
     * @return The week number of the requested date
     */
    int getWeekNumberFromDate(LocalDateTime localDateTime) {
        LocalDate date = LocalDate.ofYearDay(localDateTime.getYear(), localDateTime.getDayOfYear());
        return date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
    }

    /**
     * Check if the users given to the method matches users in the database, then create
     * new BulkUsersStepDTO for each matching user and return them in form of a list
     *
     * @param users     List of the requested users
     * @param startDate Date as String. Steps done before this date will not be collected (yyyy-[m]m-[d]d)
     * @param endDate   Date as String. Steps done after this date will not be collected (yyyy-[m]m-[d]d)
     * @return List of {@link se.sigma.boostapp.boost_app_java.dto.BulkUsersStepsDTO}
     */
    public Optional<List<BulkUsersStepsDTO>> getStepsByMultipleUsers(List<String> users, String startDate, String endDate) {
        List<String> matchingUsers = stepRepository.getAllUsers().stream()
                .filter(requestedUser -> users.stream()
                        .anyMatch(existingUser -> existingUser.equals(requestedUser)))
                .collect(Collectors.toList());

        return matchingUsers.isEmpty() ? Optional.empty() :  Optional.of(createListOfBulkUserStepDTO(matchingUsers, toSqlDate(startDate), toSqlDate(endDate)));
    }

    /**
     * Check if the date is in the correct format to be cast to java.sql.Date
     * If format is correct returns date, otherwise returns current moment.
     *
     * @param date Date as String (yyyy-[m]m-[d]d)
     * @return Date as java.sql.Date @see <a href="https://docs.oracle.com/javase/7/docs/api/java/sql/Date.html">...</a>
     */
    private java.sql.Date toSqlDate(String date) {
        java.sql.Date sqlDate;
        try {
            sqlDate = java.sql.Date.valueOf(date);
        }
        catch (IllegalArgumentException exception) {
            sqlDate =  java.sql.Date.valueOf(LocalDate.now());
        }
        return sqlDate;
    }

    /**
     * Take a list of users and create a new BulkUserStepDTO for each user
     * Then return them in a new list
     *
     * @param users List of the requested users
     * @param firstDate Date as java.sql.Date. Steps done before this date will not be collected
     * @param lastDate Date as java.sql.Date. Steps done after this date will not be collected
     * @return List of {@link se.sigma.boostapp.boost_app_java.dto.BulkUsersStepsDTO}
     */
    private List<BulkUsersStepsDTO> createListOfBulkUserStepDTO(List<String> users, java.sql.Date firstDate, java.sql.Date lastDate) {
        List<BulkUsersStepsDTO> bulkUsersStepsDTOList = new ArrayList<>();
        users.forEach(user -> bulkUsersStepsDTOList.add(new BulkUsersStepsDTO(user, stepRepository.getStepCount(user, firstDate, lastDate))));
        return bulkUsersStepsDTOList;
    }

    /**
     * Add steps to week table
     *
     * @param year   Actual year
     * @param week   Actual week
     * @param steps  Number of step
     * @param userId A user ID
     */
    public void addStepsToWeekTable(int year, int week, int steps, String userId) {
        weekStepRepository.findByUserIdAndYearAndWeek(userId, year, week)
                .ifPresentOrElse(weekStep -> {
                    weekStep.setSteps(steps + weekStep.getSteps());
                    weekStepRepository.save(weekStep);
                },
                    () -> weekStepRepository.save(new WeekStep(userId, week, year, steps))
        );
    }

    /**
     * Step count per month
     *
     * @param userId A user ID
     * @param year   Actual year
     * @param month  Actual month
     */
    public Optional<Integer> getStepCountMonth(String userId, int year, int month) {
        monthStepRepository.getStepCountMonth(userId, year, month)
                .ifPresentOrElse(this::addIntToHolder,
                        () -> addIntToHolder(0));
        return Optional.of(getIntFromHolder());
    }

    /**
     * Step count per week
     *
     * @param userId A user ID
     * @param year   Actual year
     * @param week   Actual week
     */
    public Optional<Integer> getUserStepCountForWeek(String userId, int year, int week) {
        weekStepRepository.getStepCountWeek(userId, year, week)
                .ifPresentOrElse(this::addIntToHolder,
                    () -> addIntToHolder(0));
        return Optional.of(getIntFromHolder());
    }

    /**
     * List of steps per day per current week
     *
     * @param userId A user ID
     */
    public Optional<List<StepDateDTO>> getListOfStepsForCurrentWeekFromUser(String userId) {
        stepRepository.findByUserId(userId)
                .ifPresentOrElse(this::addStepsToHolder,
                        () -> {
                    addStepToHolder(new Step(userId, 0));
                    getStepFromHolder().setEndTime(LocalDateTime.now());
                        });
        return Optional.of(createStepDateDTOList(userId, getStepsFromHolder()));
    }

    private List<StepDateDTO> createStepDateDTOList(String userId, List<Step> steps) {
        List<StepDateDTO> stepDateDTOList = new ArrayList<>();
            steps.forEach(step -> {
                            Date endTime = Date.from(step.getEndTime().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                            Calendar c = Calendar.getInstance();
                            c.setTime(endTime);
                            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                            stepDateDTOList.add(new StepDateDTO(userId, endTime, dayOfWeek, step.getStepCount()));
        });
        return stepDateDTOList;
    }

    /**
     * Delete data in step table
     */
    public void deleteStepTable() {
        stepRepository.deleteAllFromStep();
    }

    /**
     * Check if is user has any steps stored
     * @param userId ID of a user
     * @return True if user has steps stored, otherwise false
     */
    private boolean userHasSteps(String userId) {
        return stepRepository.findByUserId(userId).isPresent();
    }

    /**
     * Check if endTime of objects are on the same year
     * @param stepDTO A StepDTO {@link se.sigma.boostapp.boost_app_java.dto.StepDTO}
     * @param step A Step {@link se.sigma.boostapp.boost_app_java.model.Step}
     * @return True if the objects endTime are on the same year, otherwise false
     */
    private boolean endTimeIsSameYear(StepDTO stepDTO, Step step) {
        return step.getEndTime().getYear() == stepDTO.getEndTime().getYear();
    }

    /**
     * Check if endTime of objects are on the same day of the same year
     * @param stepDTO A StepDTO {@link se.sigma.boostapp.boost_app_java.dto.StepDTO}
     * @param step A Step {@link se.sigma.boostapp.boost_app_java.model.Step}
     * @return True if the objects endTime are on the same day of the same year, otherwise false
     */
    private boolean endTimeIsSameDay(StepDTO stepDTO, Step step) {
        return endTimeIsSameYear(stepDTO, step)
                && step.getEndTime().getDayOfYear() == stepDTO.getEndTime().getDayOfYear();
    }

    /**
     * Add a Step to stepHolder-list after clearing the list of any previous values
     * @param step A Step {@link se.sigma.boostapp.boost_app_java.model.Step}
     */
    private void addStepToHolder(Step step) {
        stepHolder.clear();
        stepHolder.add(step);
    }
    /**
     * Add a Step to stepHolder-list after clearing the list of any previous values
     * @param stepList A list of Steps {@link se.sigma.boostapp.boost_app_java.model.Step}
     */
    private void addStepsToHolder(List<Step> stepList) {
        stepHolder.clear();
        stepHolder.addAll(stepList);
    }

    /**
     * Get the value currently stored in stepHolder-list at index 0
     * @return A Step {@link se.sigma.boostapp.boost_app_java.model.Step}
     */
    private Step getStepFromHolder() {
        return stepHolder.get(0);
    }

    /**
     * Get a list with steps currently stored in stepHolder-list
     * @return List of Steps {@link se.sigma.boostapp.boost_app_java.model.Step}
     */
    private List<Step> getStepsFromHolder() {
        return stepHolder;
    }

    /**
     * Add an Integer to intHolder-list after clearing the list of any previous values
     * @param integer The number being added to the inHolder-list
     */
    private void addIntToHolder(Integer integer) {
        intHolder.clear();
        intHolder.add(integer);
    }

    /**
     * Get the value currently held in intHolder-list
     * @return The number held in the list
     */
    private Integer getIntFromHolder() {
        return intHolder.get(0);
    }
}
