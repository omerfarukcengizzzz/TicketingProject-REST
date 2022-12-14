package com.cybertek.entity;

import com.cybertek.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Setter
@Getter
@NoArgsConstructor
@Where(clause = "is_deleted = false")
@JsonIgnoreProperties(value = {"HibernateLazyInitializer"}, ignoreUnknown = true)
public class Task extends BaseEntity{
    private String taskSubject;
    private String taskDetails;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate assignedDate;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User assignedEmployee;
}
