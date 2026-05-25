package taskmanager.service;

import taskmanager.model.Task;
import taskmanager.model.TaskPriority;
import taskmanager.model.TaskStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class UrgencyCalculator {

    public static int calculateScore(Task task) {
        if (task.getStatus() == TaskStatus.COMPLETED) {
            return 0;
        }
        return priorityScore(task.getPriority()) + dateScore(task.getDueDate());
    }

    private static int priorityScore(TaskPriority priority) {
        if (priority == TaskPriority.HIGH)   return 30;
        if (priority == TaskPriority.MEDIUM) return 20;
        return 10;
    }

    private static int dateScore(LocalDate dueDate) {
        if (dueDate == null) return 0;
        long days = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        if (days < 0)  return 40;
        if (days == 0) return 30;
        if (days == 1) return 20;
        if (days <= 7) return 10;
        return 0;
    }
}
