package se.sigma.boostapp.boost_app_java.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel(description = "Used to request star points for multiple users with start time and end time")
public class RequestStarPointsDTO {

    @ApiModelProperty(notes = "List of users")
    private List<String> users;

    @ApiModelProperty(notes = "Start time")
    private LocalDateTime startTime;

    @ApiModelProperty(notes = "End time")
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
