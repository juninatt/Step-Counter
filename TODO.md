# TODO List
Currently on project: Petter Bergström, David Urman, Oscar Nordgren.

- Always keep team members and documentation updated on potential changes
- Make sure TODO list is always updated

### Replace hard code with queries
- Assigned to: Petter Bergström
- [ ] Research JPA queries
- [ ] Decide on what logic to replace
- [ ] Implement changes
- [ ] Update README

### Create custom exceptions
- Assigned to:
- [ ] Research custom exception creation 
- [ ] Decide on which custom exceptions to create
- [ ] Implement custom exceptions
- [ ] Update README


### Make sure Step data  stored correctly
- Assigned to:
- [ ] Make sure a Step object represents one day
- [ ] Make sure a WeekStep object represents one week
- [ ] Make sure a MonthStep object represents a month
- [ ] Decide which time field/fields (startTime/endTime/uploadTime) that is the deciding one when a Step is between two periods 
- [ ] Test extensively 
- [ ] Update README 

### Add validation of objects
- Assigned to:
- [ ] Improve on object validation annotation
- [ ] Apply more responses depending on issue
- [ ] Add more descriptive responses with potential solutions
- [ ] Update README

### Things that pop into your mind:
- Petter Bergström: The 'filterUsersAndCreateListOfBulkStepDateDtoWithRange' method in Abstracts StepService does a lot!
Can we make sure that the list with users is correct at an earlier stage? With the hypothetical Validation-mechanism?
Is it even needed? It creates a list of 'BulksStepDateDTO:s' that simply holds a lists of 'StepDateDTO:s'... Should be a better way to do that.
- Comments:

### Keep testing up to date by:
-  Make sure every class is covered
-  Test multiple scenarios of each method
-  Write tests parallel with coding