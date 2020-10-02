package se.sigma.boostapp.boost_app_java.service;

import org.springframework.stereotype.Service;
import se.sigma.boostapp.boost_app_java.dto.*;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.WeekStep;
import se.sigma.boostapp.boost_app_java.repository.MonthStepRepository;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;
import se.sigma.boostapp.boost_app_java.repository.WeekStepRepository;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.GregorianCalendar;


@Service
public class StepService {

	/** Temporary star point factor used during development */
	private static final double starPointFactor = 1;

	private final StepRepository stepRepository;
	private final MonthStepRepository monthStepRepository;
	private final WeekStepRepository weekStepRepository;
	
/**
 * Constructor 
 * @param stepRepository 
 * @param monthStepRepository 
 * @param weekStepRepository 
 */
	public StepService(final StepRepository stepRepository, final MonthStepRepository monthStepRepository,
						final WeekStepRepository weekStepRepository) {
		this.stepRepository = stepRepository;
		this.monthStepRepository = monthStepRepository;
		this.weekStepRepository = weekStepRepository;
	}


	/**
	 * Single step for existing and new user to step, monthstep and weekstep table <br>
	 * Start time must before end time, which in turn must be before uploaded time
	 * @param userId A user ID
	 * @param stepDto Data for the steps
	 * 
	 */
	public Optional<Step> registerSteps(String userId, StepDTO stepDto) {
		// "stepDTO": "Start time must before end time, which in turn must be before uploaded time"
		
		/**
		 * User already exist
		 */
		if (stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId).isPresent()) {
			Step existingStep = stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId).get();

				if (existingStep.getEnd().getDayOfYear() == stepDto.getEndTime().getDayOfYear()
					&& existingStep.getEnd().isBefore(stepDto.getEndTime())) {
					
					existingStep.setStepCount(existingStep.getStepCount() + stepDto.getStepCount());
					existingStep.setEnd(stepDto.getEndTime());
					existingStep.setUploadedTime(stepDto.getUploadedTime());
					
					/** add steps to monthstep table */
					addStepsToMonthTable(userId, stepDto.getStepCount(), stepDto.getEndTime().getMonthValue(),
							stepDto.getEndTime().getYear());
					/** add steps to weekstep table */
					addStepsToWeekTable(stepDto.getEndTime().getYear(), getWeekNumber(stepDto.getEndTime()), stepDto.getStepCount(), userId);
					
					return Optional.of(stepRepository.save(existingStep));
				}
				else if (existingStep.getEnd().isBefore(stepDto.getEndTime())){
					/** add steps to monthstep table */
					addStepsToMonthTable(userId, stepDto.getStepCount(), stepDto.getEndTime().getMonthValue(),stepDto.getEndTime().getYear());
					/** add steps to weekstep table */
					addStepsToWeekTable(stepDto.getEndTime().getYear(), getWeekNumber(stepDto.getEndTime()), stepDto.getStepCount(), userId);
					return Optional.of(stepRepository.save(new Step(userId, stepDto.getStepCount(), stepDto.getStartTime(), stepDto.getEndTime(), stepDto.getUploadedTime())));
				} 
				else {// tänka att skriva kod för meddelande till användare
					return Optional.empty();
					}
					
		}  
		/**
		 * new user
		 */
		else{
			/** add steps to monthstep table */
			addStepsToMonthTable(userId, stepDto.getStepCount(), stepDto.getEndTime().getMonthValue(),stepDto.getEndTime().getYear());
			/** add steps to weekstep table */
			addStepsToWeekTable(stepDto.getEndTime().getYear(), getWeekNumber(stepDto.getEndTime()), stepDto.getStepCount(), userId);
					
			return Optional.of(stepRepository.save(new Step(userId, stepDto.getStepCount(), stepDto.getStartTime(),
					stepDto.getEndTime(), stepDto.getUploadedTime())));}

	}
		

	
	/**
	 * Latest steps per dag entity by user
	 * @param userId A user ID
	 * 
	 */
	public Optional<Step> getLatestStep(String userId) {
		return stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId);
	}

/**
 * List of user to step, monthstep and weekstep table
 * @param userId A user ID
 * @param stepDtoList Data for the list of steps
 * 
 */
	public List<StepDTO> registerMultipleSteps(String userId, List<StepDTO> stepDtoList){
	    //if new user, add all to db
        if(!stepRepository.findByUserId(userId).isPresent()){
           //get earliest object in list
            var s = sortListByEndTime(stepDtoList, false).get(0);
           stepRepository.save(new Step(userId,  s.getStepCount(), s.getStartTime(), s.getEndTime(), s.getUploadedTime()));
           weekStepRepository.save(new WeekStep(userId, getWeekNumber(s.getEndTime()), s.getEndTime().getYear(), s.getStepCount()));
           monthStepRepository.save(new MonthStep(userId, s.getEndTime().getMonthValue(), s.getEndTime().getYear(), s.getStepCount()));
        }
        var existingStep = stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId).get();
        //check if any old dates are in list, throw away
        stepDtoList = stepDtoList.stream().filter(stepDTO -> stepDTO.getEndTime().isAfter(existingStep.getEnd())).collect(Collectors.toList());

        for (int i = 0; i < stepDtoList.size(); i++) {
            updateLastStepInStepTable(stepDtoList.get(i), userId, existingStep);
            addStepsToWeekTable(stepDtoList.get(i).getEndTime().getYear(), getWeekNumber(stepDtoList.get(i).getEndTime()), stepDtoList.get(i).getStepCount(), userId);
            addStepsToMonthTable(userId, stepDtoList.get(i).getStepCount(), stepDtoList.get(i).getEndTime().getMonthValue(), stepDtoList.get(i).getEndTime().getYear());
        }
	    return stepDtoList;
    }

	/**
	 * Update Last step in Step Table
	 * 
	 * @param userId A user ID
	 * @param latestStep The latest step
	 */
    public void updateLastStepInStepTable(StepDTO s, String userId, Step latestStep){
	    if(latestStep.getEnd().getYear() == s.getEndTime().getYear() && latestStep.getEnd().getDayOfYear() == s.getEndTime().getDayOfYear()){
	        latestStep.setStepCount(latestStep.getStepCount()+s.getStepCount());
	        latestStep.setEnd(s.getEndTime());
	        latestStep.setUploadedTime(s.getUploadedTime());
	        stepRepository.save(latestStep);
        }
	    else{
            stepRepository.save(new Step(userId,  s.getStepCount(), s.getStartTime(), s.getEndTime(), s.getUploadedTime()));
        }
    }

/**
 * Add steps to month table
 * @param userId A user ID
 * @param steps Number of steps
 * @param month Actual month
 * @param year Actual year
 */
    private void addStepsToMonthTable(String userId, int steps, int month, int year) {
        monthStepRepository.findByUserIdAndYearAndMonth(userId, year, month).ifPresentOrElse(
                m->{int stepSum = steps + m.getSteps();
                    m.setSteps(stepSum);
                    monthStepRepository.save(m);
                },
                ()-> monthStepRepository.save(new MonthStep(userId, month, year, steps)));
	}

  
    /**
     * Sort list by EndTime
     * @param stepDtoList Data for the list of steps
     * 
     */
    private List<StepDTO> sortListByEndTime(List<StepDTO> stepDtoList, boolean reverseOrder) {
        if (reverseOrder) {
            return stepDtoList.stream().sorted(Comparator.comparing(StepDTO::getEndTime).reversed()).collect(Collectors.toList());
        } else {
            return stepDtoList.stream().sorted(Comparator.comparing(StepDTO::getEndTime)).collect(Collectors.toList());
        }

    }

  
    /**
     *Get number of current week from date
     */
    private int getWeekNumber(LocalDateTime inputDate){

	    GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setFirstDayOfWeek(GregorianCalendar.MONDAY);
	    calendar.setMinimalDaysInFirstWeek(4);
	    calendar.setTime(Date.from(inputDate.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

	    return calendar.get(GregorianCalendar.WEEK_OF_YEAR);
    }


   /**
     * Step count per day per multiple users
     * @param users List of users
     * @param startDate Start date as String
     * @param endDate End date as String
     * 
     */
    public Optional<List<BulkUsersStepsDTO>> getStepsByMultipleUsers(List<String> users, String startDate, String endDate) {

        java.sql.Date firstDate = java.sql.Date.valueOf(startDate);
        java.sql.Date lastDate;
        if (endDate == null || endDate.equals("")) {
            lastDate = java.sql.Date.valueOf(LocalDate.now());
        } else {
            lastDate = java.sql.Date.valueOf(endDate);
        }
        List<String> allUsers = stepRepository.getAllUsers();

        BulkUsersStepsDTO bulkUsersStepsDTO;
        List<BulkUsersStepsDTO> bulkUsersStepsDTOList = new ArrayList<>();

        for (String s : users) {
            if (!allUsers.contains(s)) continue;
            bulkUsersStepsDTO = new BulkUsersStepsDTO(s, stepRepository.getStepCount(s, firstDate, lastDate));
            bulkUsersStepsDTOList.add(bulkUsersStepsDTO);
        }
        return bulkUsersStepsDTOList.isEmpty() ? Optional.empty() : Optional.of(bulkUsersStepsDTOList);
    }

    
    /**
     * Translate steps to star points for a list of users
     * @param requestStarPointsDTO Data for star points for multiple users with start time and end time
     *  
     */
    public List<BulkUserStarPointsDTO> getStarPointsByMultipleUsers(RequestStarPointsDTO requestStarPointsDTO) {

        if (requestStarPointsDTO.getUsers() == null) requestStarPointsDTO.setUsers(stepRepository.getAllUsers());
        return requestStarPointsDTO.getUsers().stream()
                .filter(user -> (stepRepository.getStepCountSum(user, requestStarPointsDTO.getStartTime(), requestStarPointsDTO.getEndTime())).isPresent())
                .map(user -> new BulkUserStarPointsDTO(user, new StarPointDateDTO(
                        "Steps",
                        "Walking",
                        requestStarPointsDTO.getStartTime().toString(),
                        requestStarPointsDTO.getEndTime().toString(),
                        (int) Math.ceil(
                                (stepRepository.getStepCountSum(user, requestStarPointsDTO.getStartTime(), requestStarPointsDTO.getEndTime())).get()
                                        * starPointFactor)
                ))).collect(Collectors.toList());
    }

    /**
     * Add steps to week table
     * @param year Actual year
     * @param week Actual week
     * @param steps Number of step
     * @param userId A user ID
     */
    public void addStepsToWeekTable(int year, int week, int steps, String userId){
	    weekStepRepository.findByUserIdAndYearAndWeek(userId, year, week).ifPresentOrElse(
	            w->{int stepSum = steps + w.getSteps();
                    w.setSteps(stepSum);
                    weekStepRepository.save(w);},
                ()->weekStepRepository.save(new WeekStep(userId, week, year, steps))
        );
    }

  
    /**
     *Step count per month
     * @param userId A user ID
     * @param year Actual year
     * @param month Actual month
     * 
     */
    public Optional<Integer> getStepCountMonth(String userId, int year, int month){
	    return monthStepRepository.getStepCountMonth(userId, year, month);
    }

 
    /**
     * Step count per week
     * @param userId A user ID
     * @param year Actual year
     * @param week Actual week
     * 
     */
    public Optional<Integer> getStepCountWeek(String userId, int year, int week){
        return weekStepRepository.getStepCountWeek(userId, year, week);
    }

 
    /**
     * List of steps per day per current week
     * @param userId A user ID
     * 
     */
    public Optional<List<StepDateDTO>> getStepCountPerDay(String userId){
	    List<StepDateDTO> list = new ArrayList<>();
        if(stepRepository.findByUserId(userId).isPresent()){
            var steps = stepRepository.findByUserId(userId).get();

            steps.forEach(step->
            {
                Date end = Date.from(step.getEnd().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                list.add(
                    new StepDateDTO(end, step.getStepCount()));});
            return Optional.of(list);
        }
        else{
            return Optional.empty();
        }
    }
       
    /**
     * Delete data i step table
     */
    public void deleteStepTabel() {
    	stepRepository.deleteAllFromStep();
    }

}
