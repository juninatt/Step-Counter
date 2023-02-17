# TODO List
Currently on project: Petter Bergström, David Urman, Oscar Nordgren.

- Always keep team members and documentation updated on potential changes
- Make sure TODO list is always updated

### Replace hard code with queries
Assigned to: Petter Bergström
- [x] Research JPA queries
- [ ] Decide on what logic to replace
- [ ] Implement changes
- [ ] Add and adjust tests
- [ ] Update README

### Create custom exceptions
Assigned to:
- [ ] Research custom exception creation 
- [ ] Decide on which custom exceptions to create
- [ ] Implement custom exceptions
- [ ] Add and adjust tests
- [ ] Update README

### Reduce the use of Optional
Assigned to:
- [ ] Reduce the use of Optional wrappers
- [ ] Adjust or create tests
- [ ] Update README.md

### Make sure Step data  stored correctly
Assigned to: Petter Bergström
- [x] Make sure a Step object represents one day
- Comment: It shouldn't
- [ ] Make sure a WeekStep object represents one week
- [ ] Make sure a MonthStep object represents a month
- [ ] Decide which time field/fields (startTime/endTime/uploadTime) that is the deciding one when a Step is between two periods 
- [ ] Add and adjust tests
- [ ] Update README 

### Add validation of objects
Assigned to: Petter Bergström
- [x] Improve on object validation annotation
- Comment: Created separate class for now.
- [x] Implement logic
- [ ] Add tests
- [ ] Update README

### Add more checks to StepValidator
Assigned to:
- [ ] Add more checks to prevent unexpected errors
- [ ] Add tests
- [ ] Update README

### Remove userId from method parameters
Assigned to: 
- [ ] Remove userId from methods where StepDTO already includes it
- [ ] Add and adjust tests
- [ ] Update README
 
### Move methods from AbstractStepService to new class(es)
Assigned to:
- [ ] Move logic of 'filterUsersAndCreateListOfBulkStepDateDtoWithRange' method 
- [ ] Move logic of 'createBulkStepDateDtoForUserForCurrentWeek' method 
- [ ] Move logic of 'createBulkStepDateDtoForUser' method 
- [ ] Add and adjust tests

### Things that pop into your mind:
Petter Bergström: Should move logic from AbstractStepService to StepService. It is kind of backwards at the moment.

### Keep testing up to date by:
-  Make sure every class is covered
-  Test multiple scenarios of each method
-  Write tests parallel with coding