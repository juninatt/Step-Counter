package se.sigma.boostapp.boost_app_java.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;
import java.util.List;

@ApiModel(description = "Used to request star points for multiple users with start time and end time")
public class RequestStarPointsDTO {

    @ApiModelProperty(notes = "List of users")
    private List<String> users;

    @ApiModelProperty(notes = "Start date")
    private LocalDate startDate;

    @ApiModelProperty(notes = "End date")
    private LocalDate endDate;

    public RequestStarPointsDTO() {}

    public RequestStarPointsDTO(List<String> users, String start, String end) {
        this.users = users;
        this.startDate = LocalDate.parse(start);
        this.endDate = LocalDate.parse(end);
    }

    public List<String> getUsers() {
        return users;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
