package com.javabackend.model;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.javabackend.validation.ValidDateRange;
import jakarta.validation.constraints.NotNull;

@ValidDateRange
public class TaskExecution {

    @NotNull(message = "Start time cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "End Time cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @NotNull(message = "Output cannot be empty")
    private String output;

    public TaskExecution() {
    }

    public TaskExecution(LocalDateTime startTime, LocalDateTime endTime, String output){
        this.startTime = startTime;
        this.endTime = endTime;
        this.output = output;
    }

    public LocalDateTime getStartTime(){return startTime;}
    public void setStartTime(LocalDateTime startTime){this.startTime = startTime;}

    public LocalDateTime getEndTime(){return endTime;}
    public void setEndTime(LocalDateTime endTime){this.endTime = endTime;}

    public String getOutput(){return output;}
    public void setOutput(String output){this.output = output;}
}
