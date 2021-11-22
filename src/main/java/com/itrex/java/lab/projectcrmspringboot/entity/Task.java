package com.itrex.java.lab.projectcrmspringboot.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * The Task class is responsible for the task's for users.
 * task and user are linked in a cross table crm.user_task
 */

@Entity
@Table(name = "task", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"users"})
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "deadline")
    private LocalDate deadline;

    private String info;

    @ManyToMany(mappedBy = "tasks",fetch = FetchType.EAGER)
    private List<User> users;

    @Override
    public String toString() {
        return "\nTask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", dedline=" + deadline +
                ", info='" + info + '\'' +
                '}';
    }

}
