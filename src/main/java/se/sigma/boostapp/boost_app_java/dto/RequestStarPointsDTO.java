package se.sigma.boostapp.boost_app_java.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Used to request star points for multiple users with start time and end time")
public class RequestStarPointsDTO {

    @Schema(description = "List of users")
    private List<String> users;

    @Schema(description = "Start time")
    private LocalDateTime startTime;

    @Schema(description = "End time")
    private LocalDateTime endTime;

    public RequestStarPointsDTO() {}

    public RequestStarPointsDTO(List<String> users, LocalDateTime startTime, LocalDateTime endTime) {
        this.users = users;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
