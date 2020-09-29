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

/**
 * 
 * @author SigmaIT
 *
 */
@Service
public class StepService {

	// Temporary star point factor used during development
	private static final double starPointFactor = 1;

	private final StepRepository stepRepository;
	private final MonthStepRepository monthStepRepository;
	private final WeekStepRepository weekStepRepository;

//	private static final double starPointFactor = 0.01;
	
/**
 * @author SigmaIT
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

// Persist a single Step (for 1 or more step count)
	/**
	 * @author SigmaIT
	 * @param userId
	 * @param stepDto
	 * @return Single step for existing and new user to step, monthstep and weekstep table
	 */
	public Optional<Step> registerSteps(String userId, StepDTO stepDto) {
		// "stepDTO": "Start time must before end time, which in turn must be before uploaded time"
		
		//user that already exist
		if (stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId).isPresent()) {
			Step existingStep = stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId).get();

				if (existingStep.getEnd().getDayOfYear() == stepDto.getEndTime().getDayOfYear()
					&& existingStep.getEnd().isBefore(stepDto.getEndTime())) {
					
					existingStep.setStepCount(existingStep.getStepCount() + stepDto.getStepCount());
					existingStep.setEnd(stepDto.getEndTime());
					existingStep.setUploadedTime(stepDto.getUploadedTime());
					
					// monthStep
					addStepsToMonthTable(userId, stepDto.getStepCount(), stepDto.getEndTime().getMonthValue(),
							stepDto.getEndTime().getYear());
					addStepsToWeekTable(stepDto.getEndTime().getYear(), getWeekNumber(stepDto.getEndTime()), stepDto.getStepCount(), userId);
					
					return Optional.of(stepRepository.save(existingStep));
				}
				else if (existingStep.getEnd().isBefore(stepDto.getEndTime())){
					// monthStep
					addStepsToMonthTable(userId, stepDto.getStepCount(), stepDto.getEndTime().getMonthValue(),stepDto.getEndTime().getYear());
                    addStepsToWeekTable(stepDto.getEndTime().getYear(), getWeekNumber(stepDto.getEndTime()), stepDto.getStepCount(), userId);
					return Optional.of(stepRepository.save(new Step(userId, stepDto.getStepCount(), stepDto.getStartTime(), stepDto.getEndTime(), stepDto.getUploadedTime())));
				} 
				else {// tänka att skriva kod för meddelande till användare
					return Optional.empty();
					}
					
		}  
		//new user
		else{
			//monthStep
			addStepsToMonthTable(userId, stepDto.getStepCount(), stepDto.getEndTime().getMonthValue(),stepDto.getEndTime().getYear());
			addStepsToWeekTable(stepDto.getEndTime().getYear(), getWeekNumber(stepDto.getEndTime()), stepDto.getStepCount(), userId);
					
			return Optional.of(stepRepository.save(new Step(userId, stepDto.getStepCount(), stepDto.getStartTime(),
					stepDto.getEndTime(), stepDto.getUploadedTime())));}

	}
		

	
	/**
	 * @author SigmaIT
	 * @param userId
	 * @return Latest steps per dag entity by user
	 */
	public Optional<Step> getLatestStep(String userId) {
		return stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId);
	}

/**
 * @author SigmaIT
 * @param userId
 * @param stepDtoList
 * @return list of user to step, monthstep and weekstep table 
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
	 * @author SigmaIT
	 * @param s
	 * @param userId 
	 * @param latestStep
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
 * @author SigmaIT
 * @param userId
 * @param steps
 * @param month
 * @param year
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
     * @author SigmaIT
     * @param stepDtoList
     * @param reverseOrder
     * @return Sort list by EndTime
     */
    private List<StepDTO> sortListByEndTime(List<StepDTO> stepDtoList, boolean reverseOrder) {
        if (reverseOrder) {
            return stepDtoList.stream().sorted(Comparator.comparing(StepDTO::getEndTime).reversed()).collect(Collectors.toList());
        } else {
            return stepDtoList.stream().sorted(Comparator.comparing(StepDTO::getEndTime)).collect(Collectors.toList());
        }

    }

    //Helper method to get number of week from date
    /**
     * @author SigmaIT
     * @param inputDate
     * @return Number of current week
     */
    private int getWeekNumber(LocalDateTime inputDate){

	    GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setFirstDayOfWeek(GregorianCalendar.MONDAY);
	    calendar.setMinimalDaysInFirstWeek(4);
	    calendar.setTime(Date.from(inputDate.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

	    return calendar.get(GregorianCalendar.WEEK_OF_YEAR);
    }


   /**
     * @author SigmaIT
     * @param users
     * @param startDate
     * @param endDate
     * @return  Step count per day per multiple users
     */
    public Optional<List<BulkUsersStepsDTO>> getStepsByMultipleUsers(List<String> users, String startDate, String endDate) {

        // String startDate → java.sql.Date firstDate
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
     * @author SigmaIT
     * @param requestStarPointsDTO
     * @return Translate steps to star points for a list of users
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
     * @author SigmaIT
     * @param year
     * @param week
     * @param steps
     * @param userId
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
     * @author SigmaIT
     * @param userId
     * @param year
     * @param month
     * @return Step count per month
     */
    public Optional<Integer> getStepCountMonth(String userId, int year, int month){
	    return monthStepRepository.getStepCountMonth(userId, year, month);
    }

 
    /**
     * @author SigmaIT
     * @param userId
     * @param year
     * @param week
     * @return Step count per week
     */
    public Optional<Integer> getStepCountWeek(String userId, int year, int week){
        return weekStepRepository.getStepCountWeek(userId, year, week);
    }

 
    /**
     * @author SigmaIT
     * @param userId
     * @return List of steps per day per current week
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
     * @author SigmaIT
     */
    public void deleteStepTabel() {
    	stepRepository.deleteAllFromStep();
    }

}
