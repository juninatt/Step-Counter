package com.nexergroup.boostapp.java.step.model;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import java.time.ZonedDateTime;


@Entity
@Table(name = "step")
@Schema(description = "All details about the step entity. ")
public class Step {

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
    private ZonedDateTime startTime;

    @Schema(description = "End time")
    @Column(name = "end_time", nullable = false)
    private ZonedDateTime endTime;

    @Schema(description = "Uploaded")
    @Column(name = "uploaded", nullable = false)
    private ZonedDateTime uploadTime;

    public Step() {
    }

    public Step(String userId, int stepCount, ZonedDateTime start, ZonedDateTime end, ZonedDateTime uploadTime) {
        this.userId = userId;
        this.stepCount = stepCount;
        this.startTime = start;
        this.endTime = end;
        this.uploadTime = uploadTime;
    }

    public Step(String userId, int stepCount, ZonedDateTime uploadTime) {
        this.userId = userId;
        this.stepCount = stepCount;
        this.uploadTime = uploadTime;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public ZonedDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(ZonedDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getUserId() {
        return userId;
    }


    public void setStepCount(int stepCount) { this.stepCount = stepCount;  }

    public int getStepCount() {
        return stepCount;
    }

    public String toString() {
        return "Step{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", stepCount=" + stepCount +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", uploadedTime=" + uploadTime +
                '}';
    }
}
