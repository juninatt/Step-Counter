package se.pbt.stepcounter.dto.stepdto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;

@Schema(description = "DTO class that holds weekly step data")
public class WeeklyStepDTO {
    @Schema(description = "ID of the user", required = true)
    private final String userId;

    @Schema(description = "Weekly step counts", required = true)
    @NotNull(message = "The 'weeklySteps' array must not be null")
    @Size(min = 52, max = 52, message = "The 'weeklySteps' array must have exactly 52 slots")
    private final ArrayList<Integer> weeklySteps;

    public WeeklyStepDTO(
            @Schema(description = "User ID", required = true) String userId,
            @Schema(description = "Weekly step counts", required = true) ArrayList<Integer> weeklySteps) {
        this.userId = userId;
        this.weeklySteps = weeklySteps;
    }

    public String getUserId() {
        return userId;
    }

    public ArrayList<Integer> getWeeklySteps() {
        return weeklySteps;
    }
}

