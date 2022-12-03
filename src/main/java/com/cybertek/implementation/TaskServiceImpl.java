package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.Task;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.mapper.ProjectMapper;
import com.cybertek.mapper.TaskMapper;
import com.cybertek.mapper.UserMapper;
import com.cybertek.repository.TaskRepository;
import com.cybertek.repository.UserRepository;
import com.cybertek.service.TaskService;
import com.cybertek.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MapperUtil mapperUtil;

    @Override
    public TaskDTO findById(Long id) throws TicketingProjectException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TicketingProjectException("Task does not exists!"));

        return mapperUtil.convert(task, new TaskDTO());
    }

    @Override
    public List<TaskDTO> listAllTasks() {
        List<Task> taskList = taskRepository.findAll();

        return taskList.stream()
                .map(taskMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO save(TaskDTO taskDTO) {
        Task task = taskMapper.convertToEntity(taskDTO);

        task.setAssignedDate(LocalDate.now());
        task.setStatus(Status.OPEN);
        task.setId(taskDTO.getId());

        Task savedTask = taskRepository.save(task);

        return mapperUtil.convert(savedTask, new TaskDTO());
    }

    @Override
    public TaskDTO update(TaskDTO taskDTO) {
        Optional<Task> task = taskRepository.findById(taskDTO.getId());
        Task convertedTask = taskMapper.convertToEntity(taskDTO);

        if (task.isPresent()) {
            convertedTask.setId(task.get().getId());
            convertedTask.setStatus(task.get().getStatus());
            convertedTask.setAssignedDate(task.get().getAssignedDate());
            taskRepository.save(convertedTask);
        }

        return mapperUtil.convert(convertedTask, new TaskDTO());
    }

    @Override
    public void delete(Long id) throws TicketingProjectException {
        Task foundTask = taskRepository.findById(id).orElseThrow(() -> new TicketingProjectException("Task does not exists!"));

        foundTask.setIsDeleted(true);

        taskRepository.save(foundTask);
    }

    @Override
    public Integer totalCompletedTasks(String projectCode) {
        return taskRepository.totalCompletedTasks(projectCode);
    }

    @Override
    public Integer totalNonCompletedTasks(String projectCode) {
        return taskRepository.totalNonCompletedTasks(projectCode);
    }

    @Override
    public void deleteByProject(ProjectDTO projectDTO) {
        List<TaskDTO> taskDTOList = listAllTasksByProject(projectDTO);

        taskDTOList.stream()
                .forEach(taskDTO -> {
                    try {
                        delete(taskDTO.getId());
                    } catch (TicketingProjectException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public List<TaskDTO> listAllTasksByProject(ProjectDTO projectDTO) {
        List<Task> taskList = taskRepository.findAllByProject(projectMapper.convertToEntity(projectDTO));

        return taskList.stream()
                .map(taskMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIsNot(Status status) {
        String employeeUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User employee = userRepository.findByUserName(employeeUsername);

        List<Task> taskList = taskRepository.findAllByStatusIsNotAndAssignedEmployee(status, employee);

        return taskList.stream()
                .map(taskMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByProjectManager() throws TicketingProjectException {
        // below code will check which user is logged in based on id
        String managerId = SecurityContextHolder.getContext().getAuthentication().getName();

        Long id = Long.parseLong(managerId);

        User manager = userRepository.findById(id).orElseThrow(() -> new TicketingProjectException("This user does not exists!"));

        List<Task> taskList = taskRepository.findAllByProjectAssignedManager(manager);

        return taskList.stream()
                .map(taskMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(TaskDTO taskDTO) {
        Optional<Task> task = taskRepository.findById(taskDTO.getId());

        if (task.isPresent()) {
            task.get().setStatus(taskDTO.getStatus());
            taskRepository.save(task.get());
        }

    }

    @Override
    public List<Task> listAllByAssignedEmployee(UserDTO employee) {
        User user = userMapper.convertToEntity(employee);

        List<Task> taskList = taskRepository.findAllByAssignedEmployee(user);

        return taskList;
    }

    @Override
    public List<TaskDTO> listAllByStatusIsCompleted() {
        String employeeUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User employee = userRepository.findByUserName(employeeUsername);

        List<Task> taskList = taskRepository.findAllByStatusIsAndAssignedEmployee(Status.COMPLETED, employee);

        return taskList.stream()
                .map(taskMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> readAllByAssignedEmployee(User employee) {
        List<Task> taskList = taskRepository.findAllByAssignedEmployee(employee);

        return taskList.stream()
                .map(taskMapper::convertToDTO)
                .collect(Collectors.toList());
    }

}
