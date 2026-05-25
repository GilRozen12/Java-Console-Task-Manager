package taskmanager.service;

import taskmanager.model.Task;
import taskmanager.model.TaskPriority;
import taskmanager.model.TaskStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private final List<Task> tasks;
    private int nextId;

    public TaskManager() {
        this(new ArrayList<>());
    }

    public TaskManager(List<Task> loadedTasks) {
        this.tasks = new ArrayList<>(loadedTasks);
        int maxId = 0;
        for (Task task : this.tasks) {
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }
        this.nextId = maxId + 1;
    }

    public Task addTask(String title, String description, TaskPriority priority, LocalDate dueDate) {
        Task task = new Task(nextId++, title, description, TaskStatus.PENDING, priority, dueDate);
        tasks.add(task);
        return task;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public Task getTaskById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    public boolean deleteTask(int id) {
        return tasks.removeIf(task -> task.getId() == id);
    }

    public boolean updateTask(int id, String title, String description, TaskPriority priority, LocalDate dueDate) {
        Task task = getTaskById(id);
        if (task == null) {
            return false;
        }
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        return true;
    }

    public boolean updateStatus(int id, TaskStatus status) {
        Task task = getTaskById(id);
        if (task == null) {
            return false;
        }
        task.setStatus(status);
        return true;
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getStatus() == status) {
                result.add(task);
            }
        }
        return result;
    }

    public Task getMostUrgentTask() {
        Task mostUrgent = null;
        int highestScore = 0;
        for (Task task : tasks) {
            int score = UrgencyCalculator.calculateScore(task);
            if (score > highestScore) {
                highestScore = score;
                mostUrgent = task;
            }
        }
        return mostUrgent;
    }

    public List<Task> getTasksByPriority(TaskPriority priority) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getPriority() == priority) {
                result.add(task);
            }
        }
        return result;
    }
}
