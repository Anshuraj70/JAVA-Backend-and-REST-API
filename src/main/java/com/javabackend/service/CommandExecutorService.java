package com.javabackend.service;

import com.javabackend.model.TaskExecution;
import com.javabackend.exception.InvalidCommandException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class CommandExecutorService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommandExecutorService.class);
    private static final long COMMAND_TIMEOUT_SECONDS = 30;  // Max 30 seconds execution time
    private static final int MAX_OUTPUT_LENGTH = 10000;  // Max 10KB output
    
    /**
     * Executes a shell command and returns TaskExecution with results
     * 
     * @param command The shell command to execute
     * @return TaskExecution containing startTime, endTime, and output
     * @throws InvalidCommandException if command execution fails
     */
    public TaskExecution executeCommand(String command) {
        logger.info("Starting command execution: {}", command);
        
    LocalDateTime startTime = LocalDateTime.now();
        StringBuilder output = new StringBuilder();
        
        try {
            // Create ProcessBuilder for secure command execution
            ProcessBuilder processBuilder = new ProcessBuilder();
            
            // Different OS require different command formats
            if (isWindows()) {
                processBuilder.command("cmd.exe", "/c", command);
            } else {
                // For Linux/Mac
                processBuilder.command("/bin/bash", "-c", command);
            }
            
            // Redirect error stream to output stream
            processBuilder.redirectErrorStream(true);
            
            // Start the process
            Process process = processBuilder.start();
            
            // Set timeout for process execution
            boolean finished = process.waitFor(COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            
            if (!finished) {
                process.destroy();
                logger.warn("Command execution timeout: {}", command);
                throw new InvalidCommandException(
                    "Command execution timeout after " + COMMAND_TIMEOUT_SECONDS + " seconds"
                );
            }
            
            // Read output from process
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    
                    // Prevent memory overflow from too much output
                    if (output.length() > MAX_OUTPUT_LENGTH) {
                        logger.warn("Output exceeded maximum length for command: {}", command);
                        output.append("\n[Output truncated - exceeded maximum length]");
                        break;
                    }
                }
            }
            
            // Check exit code
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                logger.warn("Command execution failed with exit code {}: {}", exitCode, command);
            }
            
            // Create TaskExecution with captured data
            LocalDateTime endTime = LocalDateTime.now();
            TaskExecution execution = new TaskExecution(
                startTime,
                endTime,
                output.toString().trim()
            );
            
            logger.info("Command execution completed successfully: {}", command);
            return execution;
            
        } catch (InvalidCommandException e) {
            logger.error("Invalid command detected: {}", command, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error executing command: {}", command, e);
            throw new InvalidCommandException(
                "Failed to execute command: " + e.getMessage()
            );
        }
    }
    
    /**
     * Detects if running on Windows OS
     */
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}