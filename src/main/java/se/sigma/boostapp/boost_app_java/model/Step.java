package se.sigma.boostapp.boost_app_java.model;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "step")
@Schema(description = "All details about the step entity. ")
public class Step implements BoostAppStep {

    /**
     * Entity step table
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The database generated step ID")
    private long id;

    @Column(name = "user_id")
    @Schema(description = "User Id")
    private String userId;

    @Schema(description = "Step count")
    @Column(name = "step_count")
    private int stepCount;

    @Schema(description = "Start time")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Schema(description = "End time")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Schema(description = "Uploaded")
    @Column(name = "uploaded", nullable = false)
    private LocalDateTime uploadedTime;

    public Step() {
    }

    public Step(String userId, int stepCount, LocalDateTime start, LocalDateTime end, LocalDateTime uploadedTime) {
        this.userId = userId;
        this.stepCount = stepCount;
        this.startTime = start;
        this.endTime = end;
        this.uploadedTime = uploadedTime;
    }

    public Step(String userId, int stepCount, LocalDateTime uploadedTime) {
        this.userId = userId;
        this.stepCount = stepCount;
        this.uploadedTime = uploadedTime;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime start) {
        this.startTime = start;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime end) {
        this.endTime = end;
    }

    public LocalDateTime getUploadedTime() {
        return uploadedTime;
    }

    public void setUploadedTime(LocalDateTime uploadedSteps) {
        this.uploadedTime = uploadedSteps;
    }

    @Override
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    @Override
    public int getStepCount() {
        return stepCount;
    }

    @Override
    public String toString() {
        return "Step{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", stepCount=" + stepCount +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", uploadedTime=" + uploadedTime +
                '}';
    }
}
