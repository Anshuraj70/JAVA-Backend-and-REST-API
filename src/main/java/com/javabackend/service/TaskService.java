package com.javabackend.service;

import com.javabackend.model.Task;
import com.javabackend.model.TaskExecution;
import com.javabackend.repository.TaskRepository;
import com.javabackend.exception.TaskNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

@Service
public class TaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private CommandExecutorService commandExecutorService;  // NEW: Inject CommandExecutorService
    
    // Create a new task
    public Task createTask(Task task) {
        logger.info("Creating new task: {}", task.getName());
        return taskRepository.save(task);
    }
    
    // Get all tasks (paginated)
    public Page<Task> getAllTasks(int page, int size) {
        logger.debug("Fetching all tasks - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return taskRepository.findAll(pageable);
    }
    
    public Task addTaskExecution(String taskId, TaskExecution taskExecution) {
    logger.info("Adding manual task execution to task ID: {}", taskId);
    
    Optional<Task> taskOptional = taskRepository.findById(taskId);
    if (!taskOptional.isPresent()) {
        logger.warn("Task not found for adding execution: {}", taskId);
        throw new TaskNotFoundException("Task with ID: " + taskId + " not found");
    }
    
    Task task = taskOptional.get();
    task.addTaskExecution(taskExecution);
    return taskRepository.save(task);
}

    // Get task by ID
    public Task getTaskById(String id) {
        logger.debug("Fetching task by ID: {}", id);
        return taskRepository.findById(id).orElse(null);
    }
    
    // Get task by name
    public Page<Task> getTaskByName(String name, int page, int size) {
        logger.debug("Searching tasks by name: {}", name);
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return taskRepository.findByNameContaining(name, pageable);
    }
    
    // NEW: Execute command and add task execution
    public Task executeTaskCommand(String taskId) {
        logger.info("Executing task command for task ID: {}", taskId);
        
        // Find the task
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (!taskOptional.isPresent()) {
            logger.warn("Task not found for execution: {}", taskId);
            throw new TaskNotFoundException("Task with ID: " + taskId + " not found");
        }
        
        Task task = taskOptional.get();
        
        // Execute the command using CommandExecutorService
        TaskExecution execution = commandExecutorService.executeCommand(task.getCommand());
        
        // Add execution to task
        task.addTaskExecution(execution);
        
        // Save task with new execution
        Task savedTask = taskRepository.save(task);
        
        logger.info("Task execution saved successfully for task ID: {}", taskId);
        return savedTask;
    }
    
    // Delete task
    public boolean deleteTask(String id) {
        logger.info("Deleting task ID: {}", id);
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }
}