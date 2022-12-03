package com.cybertek.controller;

import com.cybertek.annotation.DefaultExceptionMessage;
import com.cybertek.entity.ResponseWrapper;
import com.cybertek.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/project")
@Tag(name = "Project Controller", description = "Project API")
public class ProjectController {

    @Autowired
    private ProjectService projectService;


    @GetMapping
    @Operation(summary = "Get all projects")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> readAll() {
        return ResponseEntity
                .ok(new ResponseWrapper("Projects retrieved successfully!", projectService.listAllProjects()));
    }

}
