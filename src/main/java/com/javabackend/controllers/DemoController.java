package com.javabackend.controllers;

import com.javabackend.model.Task;
import com.javabackend.model.TaskExecution;
// import com.javabackend.model.TaskExecution;
import com.javabackend.service.TaskService;
import com.javabackend.exception.TaskNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api")
@Validated
public class DemoController {

    @Autowired
    private TaskService taskService;
    private static final int MAX_PAGE_SIZE = 100;

    // POST - Create a new task
    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    // GET - Get all tasks with pagination
    @GetMapping("/tasks")
    public ResponseEntity<Page<Task>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        if (page < 0 || size < 0) {
            throw new IllegalArgumentException("Page and size must be non-negative");
        }
        if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }
        Page<Task> tasks = taskService.getAllTasks(page, size);
        return ResponseEntity.ok(tasks);
    }

    // GET - Get task by ID
    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTask(
            @PathVariable @NotBlank(message = "ID cannot be empty") String id) {
        Task task = taskService.getTaskById(id.trim());
        if (task == null) {
            throw new TaskNotFoundException("Task with ID: " + id + " not found");
        }
        return ResponseEntity.ok(task);
    }

    // GET - Search tasks by name
    @GetMapping("/tasks/search/{name}")
    public ResponseEntity<Page<Task>> getTaskByName(
            @PathVariable @NotBlank(message = "Name cannot be empty") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        if (page < 0 || size < 0) {
            throw new IllegalArgumentException("Page and size must be non-negative");
        }
        if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }
        Page<Task> tasks = taskService.getTaskByName(name.trim(), page, size);
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("No tasks found containing name: " + name);
        }
        return ResponseEntity.ok(tasks);
    }

    // DELETE - Delete task
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable @NotBlank(message = "ID cannot be empty") String id) {
        boolean deleted = taskService.deleteTask(id.trim());
        if (!deleted) {
            throw new TaskNotFoundException("Task with ID: " + id + " not found");
        }
        return ResponseEntity.noContent().build();
    }

    // PUT - Execute task command and add task execution
    // This is the main endpoint for executing commands
    @PutMapping("/tasks/{id}/executions")
    public ResponseEntity<Task> executeTask(
            @PathVariable @NotBlank(message = "ID cannot be empty") String id) {
        Task updatedTask = taskService.executeTaskCommand(id.trim());
        return ResponseEntity.ok(updatedTask);
    }

    // PATCH - Add manual task execution (for testing/manual entry)
    // Use this if you want to manually add execution records without executing command
    @PatchMapping("/tasks/executions/{id}")
public ResponseEntity<Task> addTaskExecutionManual(
        @PathVariable @NotBlank(message = "ID cannot be empty") String id,
        @Valid @RequestBody TaskExecution taskExecution) {
    Task updatedTask = taskService.addTaskExecution(id.trim(), taskExecution);
    if (updatedTask == null) {
        throw new TaskNotFoundException("Task with ID: " + id + " not found");
    }
    return ResponseEntity.ok(updatedTask);
}
}