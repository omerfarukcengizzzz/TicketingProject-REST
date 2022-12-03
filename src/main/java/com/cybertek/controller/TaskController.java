package com.cybertek.controller;

import com.cybertek.annotation.DefaultExceptionMessage;
import com.cybertek.dto.TaskDTO;
import com.cybertek.entity.ResponseWrapper;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/task")
@Tag(name = "Task Controller", description = "Task API")
public class TaskController {

    @Autowired
    private TaskService taskService;


    @GetMapping
    @Operation(summary = "Get all tasks")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @PreAuthorize("hasAnyAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAll() {
        return ResponseEntity
                .ok(new ResponseWrapper("Tasks are retrieved successfully!", taskService.listAllTasks()));
    }

    @GetMapping("/project-manager")
    @Operation(summary = "Get all tasks by project manager")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @PreAuthorize("hasAnyAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAllByProjectManager() throws TicketingProjectException {
        return ResponseEntity
                .ok(new ResponseWrapper("Tasks are retrieved successfully!", taskService.listAllTasksByProjectManager()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by id")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @PreAuthorize("hasAnyAuthority('Manager', 'Employee')")
    public ResponseEntity<ResponseWrapper> readById(@PathVariable Long id) throws TicketingProjectException {
        return ResponseEntity
                .ok(new ResponseWrapper("Tasks are retrieved successfully!", taskService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @PreAuthorize("hasAnyAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> createTask(@RequestBody TaskDTO taskDTO) {
        return ResponseEntity
                .ok(new ResponseWrapper("Tasks are retrieved successfully!", taskService.save(taskDTO)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task by id")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @PreAuthorize("hasAnyAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> deleteTask(@PathVariable Long id) throws TicketingProjectException {
        taskService.delete(id);

        return ResponseEntity
                .ok(new ResponseWrapper("Tasks are retrieved successfully!"));
    }

}
