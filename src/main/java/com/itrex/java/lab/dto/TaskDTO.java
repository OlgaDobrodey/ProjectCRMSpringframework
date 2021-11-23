package com.itrex.java.lab.dto;

import com.itrex.java.lab.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * The Task class is responsible for the task's for users.
 * task and user are linked in a cross table crm.user_task
 */

@AllArgsConstructor
@Data
@Builder
public class TaskDTO {

    private Integer id;
    private String title;
    private Status status;
    private LocalDate deadline;
    private String info;

    @Override
    public String toString() {
        return "\nTaskDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", deadline=" + deadline +
                ", info='" + info + '\'' +
                '}';
    }

}
