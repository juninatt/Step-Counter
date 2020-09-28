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

	// Temporary star point factor used during development
	private static final double starPointFactor = 1;

	private final StepRepository stepRepository;
	private final MonthStepRepository monthStepRepository;
	private final WeekStepRepository weekStepRepository;

//	private static final double starPointFactor = 0.01;

	public StepService(final StepRepository stepRepository, final MonthStepRepository monthStepRepository,
						final WeekStepRepository weekStepRepository) {
		this.stepRepository = stepRepository;
		this.monthStepRepository = monthStepRepository;
		this.weekStepRepository = weekStepRepository;
	}

// Persist a single Step (for 1 or more step count)
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
		

	//	Get latest steps per day entity by user
	public Optional<Step> getLatestStep(String userId) {
		return stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId);
	}


	public List<StepDTO> registerMultipleSteps2(String userId, List<StepDTO> stepDtoList){
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


    /*//	Persist multiple Step //StepDTO-objects that has the same date will be merged to one where stepCount is summed and startDate is set to earliest in list
    public List<Step> registerMultipleSteps(String userId, List<StepDTO> stepDtoList) {
        List<Step> stepList = new ArrayList<>();
        //if new user, add all to db after grouping by date
        if (!stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId).isPresent()) {
            Map<Integer, List<StepDTO>> groupedByDayOfYearMap =  groupObjectsInListsByEndDate(stepDtoList);
            stepDtoList = mergeStepDtoObjectsWithSameDate(groupedByDayOfYearMap);
        	for (StepDTO s : stepDtoList) {
                stepList.add(addStepToDB(userId, s));
                addStepsToMonthTable(userId, s.getStepCount(), s.getEndTime().getMonthValue(), s.getEndTime().getYear());
                addStepsToWeekTable(s.getEndTime().getYear(), getWeekNumber(s.getEndTime()), s.getStepCount(), userId);

            }
        } else {
            Step latest = stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId).get();
            // TODO: 2020-09-08 communicate conflict with response?
			//objects that conflicts with latest entry in BD will be discarded
            stepDtoList = stepDtoList.stream().filter(stepDTO -> stepDTO.getEndTime().isAfter(latest.getEnd())).collect(Collectors.toList());
            Map<Integer, List<StepDTO>> groupedByDayOfYearMap =  groupObjectsInListsByEndDate(stepDtoList);

            if (!stepDtoList.isEmpty()) {
                stepDtoList = mergeStepDtoObjectsWithSameDate(groupedByDayOfYearMap);
                stepList = addOrUpdateStepDtoObjectsToDB(userId, stepDtoList, latest);
                for(StepDTO s : stepDtoList) {
                    addStepsToMonthTable(userId, s.getStepCount(), s.getEndTime().getMonthValue(), s.getEndTime().getYear());
                    addStepsToWeekTable(s.getEndTime().getYear(), getWeekNumber(s.getEndTime()), s.getStepCount(), userId);
                }
            }
        }

        return stepList;
    }*/

    private void addStepsToMonthTable(String userId, int steps, int month, int year) {
        monthStepRepository.findByUserIdAndYearAndMonth(userId, year, month).ifPresentOrElse(
                m->{int stepSum = steps + m.getSteps();
                    m.setSteps(stepSum);
                    monthStepRepository.save(m);
                },
                ()-> monthStepRepository.save(new MonthStep(userId, month, year, steps)));
	}

    /*//Helper method. Insert StepDTO-list to stepsPerDay-table
    private List<Step> addOrUpdateStepDtoObjectsToDB(String userId, List<StepDTO> stepDtoList, Step latest) {
        List<Step> stepList = new ArrayList<>();
        //Put earliest date first in list
        stepDtoList = sortListByEndTime(stepDtoList, false);

        if (latest.getEnd().getDayOfYear() == stepDtoList.get(0).getEndTime().getDayOfYear()) {
            stepList.add(updateEntryinDB(stepDtoList.get(0), latest));
            for (int i = 1; i < stepDtoList.size(); i++) {
                stepList.add(addStepToDB(userId, stepDtoList.get(i)));
            }
        }
        return stepList;
    }
*/
/*
    //Helper method. Group objects to map by same endDate
    private Map<Integer, List<StepDTO>> groupObjectsInListsByEndDate(List<StepDTO> list) {
        return list.stream()
                .collect(Collectors.groupingBy(sDto -> sDto.getEndTime().getDayOfYear()));
    }
*/

  /*  //Helper method. Add new entry of steps per day to DB
    private Step addStepToDB(String id, StepDTO s) {
        return stepRepository.save(new Step(id, s.getStepCount(), s.getStartTime(), s.getEndTime(), s.getUploadedTime()));
    }*/

 /*   //Helper method. Update existing entry in DB
    private Step updateEntryinDB(StepDTO stepDTO, Step step) {
        step.setStepCount(step.getStepCount() + stepDTO.getStepCount());
        step.setEnd(stepDTO.getEndTime());
        step.setUploadedTime(stepDTO.getUploadedTime());
        return stepRepository.save(step);
    }

    //Helper method to merge objects with same date to one object and return list of StepDTO objects
    private List<StepDTO> mergeStepDtoObjectsWithSameDate(Map<Integer, List<StepDTO>> map) {
        List<StepDTO> list = new ArrayList<>();

        map.forEach((key, value) -> {
            value = sortListByEndTime(value, true); //sort list to object with last endDate at index 0
            value.get(0).setStepCount(value.stream().mapToInt(StepDTO::getStepCount).sum()); //sum stepCount from all objects in list
            value.get(0).setStartTime(value.get(value.size() - 1).getStartTime()); //set startDate to earliest startDate in list, (keep end- and uploadedTime)
            list.add(value.get(0)); //add modified object to new list
        });

        return list;
    }*/

    //Helper method to sort list by EndTime
    private List<StepDTO> sortListByEndTime(List<StepDTO> stepDtoList, boolean reverseOrder) {
        if (reverseOrder) {
            return stepDtoList.stream().sorted(Comparator.comparing(StepDTO::getEndTime).reversed()).collect(Collectors.toList());
        } else {
            return stepDtoList.stream().sorted(Comparator.comparing(StepDTO::getEndTime)).collect(Collectors.toList());
        }

    }

    //Helper method to get number of week from date
    private int getWeekNumber(LocalDateTime inputDate){

	    GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setFirstDayOfWeek(GregorianCalendar.MONDAY);
	    calendar.setMinimalDaysInFirstWeek(4);
	    calendar.setTime(Date.from(inputDate.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

	    return calendar.get(GregorianCalendar.WEEK_OF_YEAR);

    }


   /* //	Get sum of step count by userId, start date and end Date.
    //vilken data ska hämta denna metod, alla steg, steg per dag,vecka eller månad???????
    public int getStepSumByUser(String userId, String startDate, String endDate) {
        LocalDateTime end;
        if (endDate == null || endDate.equals("")) {
            end = LocalDateTime.now();
        } else {
            end = LocalDate.parse(endDate).atTime(23, 59, 59);
        }
        List<Step> allSteps = stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(userId,
                LocalDate.parse(startDate).atStartOfDay(), end);
        return 0;
    }
*/
/*    // Get step count per day by user ID and between two dates
    public List<StepDateDTO> getStepsByUser(String userId, String startDate, String endDate) {
        List<StepDateDTO> list = new ArrayList<>();
        java.sql.Date firstDate = java.sql.Date.valueOf(startDate);
        java.sql.Date lastDate;
        if (endDate == null || endDate.equals("")) {
            lastDate = java.sql.Date.valueOf(LocalDate.now());
        } else {
            lastDate = java.sql.Date.valueOf(endDate);
        }
        stepRepository.findAllByUserIdAndEndTimeBetween(userId, firstDate, lastDate).forEach(step -> list.add(new StepDateDTO(java.sql.Date.valueOf(step.getEnd().toString()), step.getStepCount())));
        return list;
    }*/

    // Get step count per day per multiple users
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

    // Translate steps to star points for a list of users
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

    public void addStepsToWeekTable(int year, int week, int steps, String userId){
	    weekStepRepository.findByUserIdAndYearAndWeek(userId, year, week).ifPresentOrElse(
	            w->{int stepSum = steps + w.getSteps();
                    w.setSteps(stepSum);
                    weekStepRepository.save(w);},
                ()->weekStepRepository.save(new WeekStep(userId, week, year, steps))
        );
    }

    //return stepcount per month
    public Optional<Integer> getStepCountMonth(String userId, int year, int month){
	    return monthStepRepository.getStepCountMonth(userId, year, month);
    }

    //return step count per week
    public Optional<Integer> getStepCountWeek(String userId, int year, int week){
        return weekStepRepository.getStepCountWeek(userId, year, week);
    }

    //Return list of steps per day per current week
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
       
    public void deleteStepTabel() {
    	stepRepository.deleteAllFromStep();
    }

}
