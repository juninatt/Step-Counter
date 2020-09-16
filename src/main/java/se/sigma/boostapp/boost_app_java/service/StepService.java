package se.sigma.boostapp.boost_app_java.service;

import org.springframework.stereotype.Service;
import se.sigma.boostapp.boost_app_java.dto.*;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;

import se.sigma.boostapp.boost_app_java.repository.MonthStepRepository;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;
import se.sigma.boostapp.boost_app_java.repository.WeekStepRepository;


import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StepService {

    // Temporary star point factor used during development
    private static final double starPointFactor = 1;


	private final StepRepository stepRepository;

    private final MonthStepRepository monthStepRepository;
    private final WeekStepRepository weekStepRepository;



//	private static final double starPointFactor = 0.01;


    public StepService(final StepRepository stepRepository, final MonthStepRepository monthStepRepository, final WeekStepRepository weekStepRepository) {
        this.stepRepository = stepRepository;
        this.monthStepRepository = monthStepRepository;
        this.weekStepRepository = weekStepRepository;
    }


// Persist a single Step (for 1 or more step count)

	/*
	 * deras metod som funkar och jag kommer att ändra 
	 * public Optional<Step> registerSteps(String userId, StepDTO stepDto) { return
	 * Optional.of(stepRepository.save(new Step(userId, stepDto.getStepCount(),
	 * stepDto.getStartTime(), stepDto.getEndTime(), stepDto.getUploadedTime()))); }
	 */


// Persist a single Step (for 1 or more step count)
	public Optional<Step> registerSteps(String userId, StepDTO stepDto) {

		// code -write first all rows( for different days) for one user and then all
		// rows for second user
		// "stepDTO": "Start time must before end time, which in turn must be before
		// uploaded time"
		if (stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId).isPresent()) {
			Step existingStep = stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId).get();

			if (existingStep.getEnd().getDayOfYear() == stepDto.getEndTime().getDayOfYear()
					&& existingStep.getEnd().isBefore(stepDto.getEndTime())) {
				existingStep.setStepCount(existingStep.getStepCount() + stepDto.getStepCount());
				existingStep.setEnd(stepDto.getEndTime());
				existingStep.setUploadedTime(stepDto.getUploadedTime());
				return Optional.of(stepRepository.save(existingStep));
			}
			// take care only of time
			else if (existingStep.getEnd().getDayOfYear() == stepDto.getEndTime().getDayOfYear()
					&& (existingStep.getEnd().equals(stepDto.getEndTime())
							|| existingStep.getEnd().isAfter(stepDto.getEndTime()))) {
				// tänka att skriva kod för meddelande till användare
				return Optional.empty();
			} else if (existingStep.getEnd().getDayOfYear() != stepDto.getEndTime().getDayOfYear())
				existingStep.setStepCount(existingStep.getStepCount() /* + stepDto.getStepCount() */);
			return Optional.of(stepRepository.save(new Step(userId, stepDto.getStepCount(), stepDto.getStartTime(),
					stepDto.getEndTime(), stepDto.getUploadedTime())));

		} else
			return Optional.of(stepRepository.save(new Step(userId, stepDto.getStepCount(), stepDto.getStartTime(),
					stepDto.getEndTime(), stepDto.getUploadedTime())));

	}


//	Get latest step entity by user
	// //2020-09-03 denna metoden skickar en summa av steg per dag per användare
	// summan ändrar varje 10-minuter (om man gör nya steg) pga metoden
	// registerSteps som fyller databasen varje 10 minuter
	public Optional<Step> getLatestStep(String userId) {
		return stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId);
	}


    //	Persist multiple Step //StepDTO-objects that has the same date will be merged to one where stepCount is summed and startDate is set to earliest in list
    public List<Step> registerMultipleSteps(String userId, List<StepDTO> stepDtoList) {
        List<Step> stepList = new ArrayList<>();
        //if new user, add all to db after grouping by date
        if (!stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId).isPresent()) {
            Map<Integer, List<StepDTO>> groupedByDayOfYearMap =  groupObjectsInListsByEndDate(stepDtoList);
            stepDtoList = mergeStepDtoObjectsWithSameDate(groupedByDayOfYearMap);
        	for (StepDTO s : stepDtoList) {
                stepList.add(addStepToDB(userId, s));
                //
                addStepsToMonthStep(userId, s.getStepCount(), s.getEndTime().getMonthValue(), s.getEndTime().getYear());

            }
        } else {
            Step latest;
            //get latest entry from db
            latest = stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId).get();
            //filter out any object in list that is before latest entry
            // TODO: 2020-09-08 communicate conflict with response?
			//objects that conflicts with latest entry in BD will be discarded
            stepDtoList = stepDtoList.stream().filter(stepDTO -> stepDTO.getEndTime().isAfter(latest.getEnd())).collect(Collectors.toList());

            //Group objects in lists by endDate
            Map<Integer, List<StepDTO>> groupedByDayOfYearMap =  groupObjectsInListsByEndDate(stepDtoList);

            if (!stepDtoList.isEmpty()) {
                //Return list of merged objects with the same endDate
                stepDtoList = mergeStepDtoObjectsWithSameDate(groupedByDayOfYearMap);
                //StepList with entities registered in DB
                stepList = addOrUpdateStepDtoObjectsToDB(userId, stepDtoList, latest);
                for(StepDTO s : stepDtoList)
                addStepsToMonthStep(userId, s.getStepCount(),s.getEndTime().getMonthValue(),s.getEndTime().getYear());
            }
        }

        return stepList;
    }

    private void addStepsToMonthStep(String userId, int stepCount, int monthValue, int year) {
        //uppdate stepCount to month column
        if(!monthStepRepository.findFirstByUserId(userId).isPresent()) {
            monthStepRepository.save(new MonthStep(userId, year));
            var m = monthStepRepository.findFirstByUserId(userId).get();
            m.setOneMonth(monthValue, stepCount);
            monthStepRepository.save(m);
        }
        else{
            var m = monthStepRepository.findFirstByUserId(userId).get();
            m.setOneMonth(monthValue, stepCount);
            monthStepRepository.save(m);
        }
	}

    //Helper method. Insert StepDTO-list to DB
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

    //Helper method. Group objects to map by same endDate
    private Map<Integer, List<StepDTO>> groupObjectsInListsByEndDate(List<StepDTO> list) {
        return list.stream()
                .collect(Collectors.groupingBy(sDto -> sDto.getEndTime().getDayOfYear()));
    }

    //Helper method. Add new entry of steps per day to DB
    private Step addStepToDB(String id, StepDTO s) {
        return stepRepository.save(new Step(id, s.getStepCount(), s.getStartTime(), s.getEndTime(), s.getUploadedTime()));
    }

    //Helper method. Update existing entry in DB
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
    }

    //Helper method to sort list by EndTime
    private List<StepDTO> sortListByEndTime(List<StepDTO> stepDtoList, boolean reverseOrder) {
        if (reverseOrder) {
            return stepDtoList.stream().sorted(Comparator.comparing(StepDTO::getEndTime).reversed()).collect(Collectors.toList());
        } else {
            return stepDtoList.stream().sorted(Comparator.comparing(StepDTO::getEndTime)).collect(Collectors.toList());
        }

    }
    
    //	Helper method. Get total step count from a list of Steps
    private int getStepCount(List<Step> steps) {
        int total = 0;
        for (Step step : steps) {
            total += step.getStepCount();
        }
        return total;
    }

    //	Get sum of step count by userId, start date and end Date.
    public int getStepSumByUser(String userId, String startDate, String endDate) {
        LocalDateTime end;
        if (endDate == null || endDate.equals("")) {
            end = LocalDateTime.now();
        } else {
            end = LocalDate.parse(endDate).atTime(23, 59, 59);
        }
        List<Step> allSteps = stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(userId,
                LocalDate.parse(startDate).atStartOfDay(), end);
        return getStepCount(allSteps);
    }

    // Get step count per day by user ID
    // getLatestStep nu gör denna räkning
    public List<StepDateDTO> getStepsByUser(String userId, String startDate, String endDate) {
        Date firstDate = Date.valueOf(startDate);
        Date lastDate;
        if (endDate == null || endDate.equals("")) {
            lastDate = Date.valueOf(LocalDate.now());
        } else {
            lastDate = Date.valueOf(endDate);
        }
        return stepRepository.getStepCount(userId, firstDate, lastDate);
    }

    // Get step count per day per multiple users
    public Optional<List<BulkUsersStepsDTO>> getStepsByMultipleUsers(List<String> users, String startDate, String endDate) {

        // String startDate → java.sql.Date firstDate
        Date firstDate = Date.valueOf(startDate);
        Date lastDate;
        if (endDate == null || endDate.equals("")) {
            lastDate = Date.valueOf(LocalDate.now());
        } else {
            lastDate = Date.valueOf(endDate);
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





	

}
