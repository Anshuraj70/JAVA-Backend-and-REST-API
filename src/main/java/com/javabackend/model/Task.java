package com.javabackend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.javabackend.validation.ValidCommand;
import java.util.*;
import jakarta.validation.Valid;


@Document(collection = "tasks")
public class Task {

    @Id
    private String id;
    
    @NotBlank(message = "Task name is required")
    @Size(min = 1, max = 100, message = "Task name must be between 1 and 100 characters.")
    private String name;
    
    @Size(min = 1, max = 100, message = "Owner must be between 1 and 100 characters")
    @NotBlank(message = "Task owner is required")
    private String owner;
    
    @NotBlank(message = "Command is required")
    @ValidCommand
    private String command;

    @Valid
    private List<TaskExecution> taskExecutions;

    // No-arg constructor for frameworks (Jackson) that need it during deserialization
    public Task() {
        this.taskExecutions = new ArrayList<>();
    }

    public Task( String name, String owner, String command) {
        this.name = name;
        this.owner = owner;
        this.command = command;
        this.taskExecutions = new ArrayList<>();

    }
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    
    public List<TaskExecution> getTaskExecutions() { 
        return taskExecutions != null ? taskExecutions : new ArrayList<>(); 
    }
    public void setTaskExecutions(List<TaskExecution> taskExecutions) { 
        this.taskExecutions = taskExecutions; 
    }
    
    // Helper method to add a task execution
    public void addTaskExecution(TaskExecution taskExecution) {
        if (this.taskExecutions == null) {
            this.taskExecutions = new ArrayList<>();
        }
        this.taskExecutions.add(taskExecution);
    }
    
}
