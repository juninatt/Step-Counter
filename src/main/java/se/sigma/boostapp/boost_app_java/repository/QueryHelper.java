package se.sigma.boostapp.boost_app_java.repository;

/**
 * The QueryHelper class contains several predefined SQL queries for the Step repository.
 *
 * @see se.sigma.boostapp.boost_app_java.repository.StepRepository
 * @see se.sigma.boostapp.boost_app_java.dto.stepdto.StepDateDTO
 */
class QueryHelper {

    /**
     * SELECT_STEP_DATA_WITHIN_TIME_RANGE is a query that selects step data within a specific time range.
     * It utilizes the StepDateDTO class to map the results to a more user-friendly format.
     */
    public static final String SELECT_STEP_DATA_WITHIN_TIME_RANGE =
            "SELECT new se.sigma.boostapp.boost_app_java.dto.stepdto.StepDateDTO(cast(s.startTime as date), sum(s.stepCount)) " +
                    "FROM Step s " +
                    "WHERE s.userId = :userId AND cast(s.startTime as date) " +
                    "BETWEEN :startDate AND :endDate " +
                    "GROUP BY s.userId, cast(s.startTime as date) " +
                    "ORDER BY cast(s.startTime as date) ASC";


    /**
     * SELECT_ALL_USER_ID is a query that selects all user IDs from the Step table
     */
    public static final String SELECT_ALL_USER_ID =
            "SELECT DISTINCT s.userId FROM Step s";


    /**
     * SELECT_STEP_COUNT_WITHIN_TIME_RANGE is a query that selects the total step count within a specific time range for a given user ID
     */
    public static final String SELECT_STEP_COUNT_WITHIN_TIME_RANGE =
            "SELECT sum(s.stepCount) " +
                    "FROM Step s " +
                    "WHERE s.userId = :userId " +
                    "AND s.uploadedTime >= :startTime " +
                    "AND s.uploadedTime <= :endTime";


    /**
     * DELETE_ALL_STEP is a query that deletes all rows in the Step table
     */
    public static final String DELETE_ALL_STEP =
            "DELETE FROM Step";


    /**
     * DELETE_ALL_WEEK_STEP is a query that deletes all rows in the Step table
     */
    public static final String DELETE_ALL_WEEK_STEP =
            "DELETE FROM WeekStep";


    /**
     * SELECT_STEP_COUNT_YEAR_MONTH is a query that selects the total step count for a given user ID for a specific year and month
     */
    public static final String SELECT_STEP_COUNT_YEAR_MONTH =
            "SELECT m.steps " +
                    "FROM MonthStep m " +
                    "WHERE m.userId = :userId " +
                    "AND m.year = :year " +
                    "AND m.month = :month";


    /**
     * SELECT_STEP_COUNT_YEAR_AND_WEEK is a query that selects the total step count for a given user ID for a specific year and week
     */
    public static final String SELECT_STEP_COUNT_YEAR_AND_WEEK =
            "SELECT w.steps " +
                    "FROM WeekStep w " +
                    "WHERE w.userId = :userId " +
                    "AND w.year = :year " +
                    "AND w.week = :week";
}
