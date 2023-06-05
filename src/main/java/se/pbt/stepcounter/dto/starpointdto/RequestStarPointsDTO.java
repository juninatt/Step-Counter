package se.pbt.stepcounter.dto.starpointdto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;
import java.util.List;

@Schema(description = "Used to request star points for multiple users with start time and end time")
public class RequestStarPointsDTO {

    @Schema(description = "List of users")
    private List<String> users;

    @Schema(description = "Start time")
    private ZonedDateTime startTime;

    @Schema(description = "End time")
    private ZonedDateTime endTime;

    public RequestStarPointsDTO() {}

    public RequestStarPointsDTO(List<String> users, ZonedDateTime startTime, ZonedDateTime endTime) {
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

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }
}
