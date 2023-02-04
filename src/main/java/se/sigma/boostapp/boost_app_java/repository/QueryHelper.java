package se.sigma.boostapp.boost_app_java.repository;

/**
 * QueryHelper is a static utility class for storing predefined SQL queries for the Step repositories.
 * The class contains several queries for selecting and deleting data from the BoostAppStep tables,
 * as well as for calculating the total step count for a given user, year, and month/week.
 * Repositories:
 * @see StepRepository
 * @see WeekStepRepository
 * @see MonthStepRepository
 *
 * Data Transfer Objects:
 * @see se.sigma.boostapp.boost_app_java.dto.stepdto.StepDateDTO
 *
 * Database Objects:
 * @see se.sigma.boostapp.boost_app_java.model.Step
 * @see se.sigma.boostapp.boost_app_java.model.WeekStep
 * @see se.sigma.boostapp.boost_app_java.model.MonthStep
 *
 */
class QueryHelper {

    /**
     * SELECT_ALL_USER_ID is a query that selects all user IDs from the {@link se.sigma.boostapp.boost_app_java.model.Step} table
     * and returns them in form of a List.
     */
    public static final String SELECT_ALL_USER_ID =
            "SELECT DISTINCT s.userId FROM Step s";


    /**
     * SELECT_STEP_COUNT_WITHIN_TIME_RANGE is a query that selects the total stepCount in the {@link se.sigma.boostapp.boost_app_java.model.Step} table a specific time range for a given user ID,
     * and returns it in form af an Integer.
     */
    public static final String SELECT_STEP_COUNT_WITHIN_TIME_RANGE =
            "SELECT sum(s.stepCount) " +
                    "FROM Step s " +
                    "WHERE s.userId = :userId " +
                    "AND s.uploadedTime >= :startTime " +
                    "AND s.uploadedTime <= :endTime";


    /**
     * SELECT_STEP_COUNT_YEAR_AND_WEEK is a query that selects the total stepCount for a given user ID for a specific year and week,
     * and returns it in form of an Integer
     */
    public static final String SELECT_STEP_COUNT_YEAR_AND_WEEK =
            "SELECT w.steps " +
                    "FROM WeekStep w " +
                    "WHERE w.userId = :userId " +
                    "AND w.year = :year " +
                    "AND w.week = :week";


    /**
     * SELECT_STEP_COUNT_YEAR_MONTH is a query that selects the total stepCount for a given user ID for a specific year and month,
     * and returns it in form of an integer
     */
    public static final String SELECT_STEP_COUNT_YEAR_MONTH =
            "SELECT m.steps " +
                    "FROM MonthStep m " +
                    "WHERE m.userId = :userId " +
                    "AND m.year = :year " +
                    "AND m.month = :month";


    /**
     * SELECT_STEP_DATA_WITHIN_TIME_RANGE is a query that selects step data for a given user ID, within a specific time range.
     * The results are mapped to a more user-friendly format using the {@link se.sigma.boostapp.boost_app_java.dto.stepdto.StepDateDTO} class.
     * It returns a List of {@link se.sigma.boostapp.boost_app_java.dto.stepdto.StepDateDTO} objects
     * representing the data within the specified time range.
     */
    public static final String SELECT_STEP_DATA_WITHIN_TIME_RANGE =
            "SELECT new se.sigma.boostapp.boost_app_java.dto.stepdto.StepDateDTO(cast(s.startTime as date), sum(s.stepCount)) " +
                    "FROM Step s " +
                    "WHERE s.userId = :userId AND cast(s.startTime as date) " +
                    "BETWEEN :startDate AND :endDate " +
                    "GROUP BY s.userId, cast(s.startTime as date) " +
                    "ORDER BY cast(s.startTime as date) ASC";


    /**
     * DELETE_ALL_STEP is a query that deletes all rows in the {@link se.sigma.boostapp.boost_app_java.model.Step} table.
     * Returns void
     */
    public static final String DELETE_ALL_STEP =
            "DELETE FROM Step";


    /**
     * DELETE_ALL_WEEK_STEP is a query that deletes all rows in the {@link se.sigma.boostapp.boost_app_java.model.WeekStep} table.
     * Returns void
     */
    public static final String DELETE_ALL_WEEK_STEP =
            "DELETE FROM WeekStep";
}
