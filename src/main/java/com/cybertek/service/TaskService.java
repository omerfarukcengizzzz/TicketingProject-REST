package com.cybertek.service;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.Task;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import com.cybertek.exception.TicketingProjectException;

import java.util.List;

public interface TaskService {

    TaskDTO findById(Long id) throws TicketingProjectException;

    List<TaskDTO> listAllTasks();

    TaskDTO save(TaskDTO taskDTO);

    TaskDTO update(TaskDTO taskDTO);

    void delete(Long id) throws TicketingProjectException;

    Integer totalCompletedTasks(String projectCode);

    Integer totalNonCompletedTasks(String projectCode);

    void deleteByProject(ProjectDTO projectDTO);

    List<TaskDTO> listAllTasksByProject(ProjectDTO projectDTO);

    List<TaskDTO> listAllTasksByStatusIsNot(Status status) throws TicketingProjectException;

    List<TaskDTO> listAllTasksByProjectManager() throws TicketingProjectException;

    void updateStatus(TaskDTO taskDTO);

    List<Task> listAllByAssignedEmployee(UserDTO employee);

    List<TaskDTO> listAllByStatusIsCompleted();

    List<TaskDTO> readAllByAssignedEmployee(User employee);

}
