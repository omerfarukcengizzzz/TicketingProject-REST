package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.Project;
import com.cybertek.entity.Task;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.mapper.ProjectMapper;
import com.cybertek.mapper.UserMapper;
import com.cybertek.repository.ProjectRepository;
import com.cybertek.repository.TaskRepository;
import com.cybertek.repository.UserRepository;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import com.cybertek.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepository taskRepository;
    @Lazy
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MapperUtil mapperUtil;

    @Override
    public ProjectDTO getByProjectCode(String code) {
        Project project = projectRepository.findByProjectCode(code);
        return projectMapper.convertToDTO(project);
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        List<Project> projectList = projectRepository.findAll(Sort.by("projectCode"));
        return projectList.stream()
                .map(projectMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDTO save(ProjectDTO dto) throws TicketingProjectException {
        Project foundProject = projectRepository.findByProjectCode(dto.getProjectCode());

        if (foundProject != null) {
            throw new TicketingProjectException("Project with this code already exists!");
        }

        Project obj = mapperUtil.convert(dto, new Project());
        obj.setStatus(Status.OPEN);

        Project createdProject = projectRepository.save(obj);

        return mapperUtil.convert(createdProject, new ProjectDTO());
    }

    @Override
    public ProjectDTO update(ProjectDTO dto) throws TicketingProjectException {
        // find the current dto on entity
        Project project = projectRepository.findByProjectCode(dto.getProjectCode());

        if (project == null) {
            throw new TicketingProjectException("Project does not exists!");
        }

        // convert the dto to entity
        Project convertedProject = projectMapper.convertToEntity(dto);
        // set id and status
        convertedProject.setId(project.getId());
        convertedProject.setStatus(project.getStatus());

        // save on db
        projectRepository.save(convertedProject);

        return mapperUtil.convert(convertedProject, new ProjectDTO());
    }

    @Override
    public void delete(String code) throws TicketingProjectException {
        // soft delete
        Project project = projectRepository.findByProjectCode(code);

        if (project == null) {
            throw new TicketingProjectException("Project does not exists!");
        }

        project.setIsDeleted(true);
        project.setProjectCode(project.getProjectCode() + "-" + project.getId());
        projectRepository.save(project);
        taskService.deleteByProject(projectMapper.convertToDTO(project));
    }

    @Override
    public ProjectDTO complete(String projectCode) throws TicketingProjectException {
        Project project = projectRepository.findByProjectCode(projectCode);

        if (project == null) {
            throw new TicketingProjectException("Project does not exists");
        }

        project.setStatus(Status.COMPLETED);
        projectRepository.save(project);

        return mapperUtil.convert(project, new ProjectDTO());
    }

    @Override
    public List<ProjectDTO> listAllProjectsByManager(UserDTO manager) {
        User user = userMapper.convertToEntity(manager);

        List<Project> projectList = projectRepository.findByAssignedManager(user);

        return projectList.stream()
                .map(project -> {

                    List<Task> taskList = taskRepository.findAllByProject(project);

                    outer : for (Task task : taskList) {
                        if (task.getStatus() != Status.COMPLETED) {
                            project.setStatus(Status.OPEN);
                            try {
                                save(projectMapper.convertToDTO(project));
                            } catch (TicketingProjectException e) {
                                throw new RuntimeException(e);
                            }
                            break outer;
                        }
                    }

                    ProjectDTO projectDTO = projectMapper.convertToDTO(project);

                    projectDTO.setCompletedTasks(taskService.totalCompletedTasks(project.getProjectCode()));
                    projectDTO.setUnfinishedTasks(taskService.totalNonCompletedTasks(project.getProjectCode()));

                    return projectDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() throws TicketingProjectException {
        String managerId = SecurityContextHolder.getContext().getAuthentication().getName();

        Long currentId = Long.parseLong(managerId);

        User user = userRepository.findById(currentId).orElseThrow(() -> new TicketingProjectException("This manager does not exists!"));



//        UserDTO manager = userService.findByUserName(managerId);

//        User user = userMapper.convertToEntity(manager);

        List<Project> projectList = projectRepository.findByAssignedManager(user);

        return projectList.stream()
                .map(project -> {

                    List<Task> taskList = taskRepository.findAllByProject(project);

                    outer : for (Task task : taskList) {
                        if (task.getStatus() != Status.COMPLETED) {
                            project.setStatus(Status.OPEN);
                            try {
                                save(projectMapper.convertToDTO(project));
                            } catch (TicketingProjectException e) {
                                throw new RuntimeException(e);
                            }
                            break outer;
                        }
                    }

                    ProjectDTO projectDTO = projectMapper.convertToDTO(project);

                    projectDTO.setCompletedTasks(taskService.totalCompletedTasks(project.getProjectCode()));
                    projectDTO.setUnfinishedTasks(taskService.totalNonCompletedTasks(project.getProjectCode()));

                    return projectDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNotCompletedProjects() {
        List<Project> projectList = projectRepository.findAllByStatusIsNot(Status.COMPLETED);

        return projectList.stream()
                .map(projectMapper::convertToDTO)
                .collect(Collectors.toList());
    }
}
