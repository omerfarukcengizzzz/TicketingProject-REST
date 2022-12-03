package com.cybertek.controller;

import com.cybertek.annotation.DefaultExceptionMessage;
import com.cybertek.dto.ProjectDTO;
import com.cybertek.entity.ResponseWrapper;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{projectCode}")
    @Operation(summary = "Get project by project code")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> readByProjectCode(@PathVariable String projectCode) {
        return ResponseEntity
                .ok(new ResponseWrapper("Project retrieved successfully!", projectService.getByProjectCode(projectCode)));
    }

    @PostMapping
    @Operation(summary = "Create project")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> createProject(@RequestBody ProjectDTO dto) throws TicketingProjectException {
        return ResponseEntity
                .ok(new ResponseWrapper("Project created successfully!", projectService.save(dto)));
    }

    @PutMapping
    @Operation(summary = "Update project")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> updateProject(@RequestBody ProjectDTO dto) throws TicketingProjectException {
        return ResponseEntity
                .ok(new ResponseWrapper("Project updated successfully!", projectService.update(dto)));
    }

    @DeleteMapping("/{projectCode}")
    @Operation(summary = "Delete project")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> deleteProject(@PathVariable String projectCode) throws TicketingProjectException {
        projectService.delete(projectCode);

        return ResponseEntity
                .ok(new ResponseWrapper("Project deleted successfully!"));
    }

    @PutMapping("/complete/{projectCode}")
    @Operation(summary = "Complete project by project code")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @PreAuthorize("hasAnyAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> completeProject(@PathVariable String projectCode) throws TicketingProjectException {
        return ResponseEntity
                .ok(new ResponseWrapper("Project completed successfully!", projectService.complete(projectCode)));
    }

}
