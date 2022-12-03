package com.cybertek.controller;

import com.cybertek.annotation.DefaultExceptionMessage;
import com.cybertek.entity.ResponseWrapper;
import com.cybertek.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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




}
