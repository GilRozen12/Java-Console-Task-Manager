package taskmanager.ui;

import taskmanager.model.Task;
import taskmanager.model.TaskPriority;
import taskmanager.model.TaskStatus;
import taskmanager.service.TaskManager;
import taskmanager.storage.TaskFileRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {

    private final TaskManager manager;
    private final TaskFileRepository repository;
    private final Scanner scanner;

    public ConsoleUI(TaskManager manager, TaskFileRepository repository) {
        this.manager = manager;
        this.repository = repository;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to Task Manager!");

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleCreateTask();
                    break;
                case "2":
                    handleShowAllTasks();
                    break;
                case "3":
                    handleDeleteTask();
                    break;
                case "4":
                    handleUpdateStatus();
                    break;
                case "5":
                    handleUpdateTask();
                    break;
                case "6":
                    handleFilterTasks();
                    break;
                case "7":
                    try {
                        repository.saveAll(manager.getAllTasks());
                        System.out.println("Tasks saved. Goodbye!");
                    } catch (IOException e) {
                        System.out.println("Could not save tasks: " + e.getMessage());
                    }
                    return;
                default:
                    System.out.println("Invalid option. Please enter 1 through 7.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Create task");
        System.out.println("2. Show all tasks");
        System.out.println("3. Delete task");
        System.out.println("4. Update task status");
        System.out.println("5. Update task");
        System.out.println("6. Filter tasks");
        System.out.println("7. Exit");
        System.out.print("Choose an option: ");
    }

    private void handleCreateTask() {
        String title = promptNoPipe("Title: ", true);
        String description = promptNoPipe("Description: ", false);
        TaskPriority priority = promptPriority();
        LocalDate dueDate = promptDueDate();

        Task task = manager.addTask(title, description, priority, dueDate);
        System.out.println("Task created: " + task);
    }

    private void handleShowAllTasks() {
        List<Task> tasks = manager.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        System.out.println("\n--- All Tasks ---");
        for (Task task : tasks) {
            System.out.println(task);
        }
    }

    private void handleDeleteTask() {
        Integer id = promptTaskId("Task ID to delete: ");
        if (id == null) return;
        if (manager.deleteTask(id)) {
            System.out.println("Task deleted.");
        } else {
            System.out.println("No task found with ID " + id + ".");
        }
    }

    private void handleUpdateStatus() {
        Integer id = promptTaskId("Task ID: ");
        if (id == null) return;
        TaskStatus status = promptStatus();
        if (manager.updateStatus(id, status)) {
            System.out.println("Status updated.");
        } else {
            System.out.println("No task found with ID " + id + ".");
        }
    }

    private void handleUpdateTask() {
        Integer id = promptTaskId("Task ID to update: ");
        if (id == null) return;
        if (manager.getTaskById(id) == null) {
            System.out.println("No task found with ID " + id + ".");
            return;
        }
        String title = promptNoPipe("New title: ", true);
        String description = promptNoPipe("New description: ", false);
        TaskPriority priority = promptPriority();
        LocalDate dueDate = promptDueDate();
        manager.updateTask(id, title, description, priority, dueDate);
        System.out.println("Task updated.");
    }

    private void handleFilterTasks() {
        System.out.println("Filter by: (1) Status  (2) Priority");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();

        List<Task> results;
        if (choice.equals("1")) {
            TaskStatus status = promptStatus();
            results = manager.getTasksByStatus(status);
        } else if (choice.equals("2")) {
            TaskPriority priority = promptPriority();
            results = manager.getTasksByPriority(priority);
        } else {
            System.out.println("Invalid option.");
            return;
        }

        if (results.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        System.out.println("\n--- Filtered Tasks ---");
        for (Task task : results) {
            System.out.println(task);
        }
    }

    private Integer promptTaskId(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
            return null;
        }
    }

    private String promptNoPipe(String prompt, boolean required) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.contains("|")) {
                System.out.println("Input cannot contain '|'. Please try again.");
            } else if (required && input.isEmpty()) {
                System.out.println("This field is required. Please try again.");
            } else {
                return input;
            }
        }
    }

    private TaskPriority promptPriority() {
        while (true) {
            System.out.print("Priority (LOW, MEDIUM, HIGH): ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return TaskPriority.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid priority. Please enter LOW, MEDIUM, or HIGH.");
            }
        }
    }

    private TaskStatus promptStatus() {
        while (true) {
            System.out.print("Status (PENDING, IN_PROGRESS, COMPLETED): ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return TaskStatus.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status. Please enter PENDING, IN_PROGRESS, or COMPLETED.");
            }
        }
    }

    private LocalDate promptDueDate() {
        System.out.print("Due date (yyyy-MM-dd) or press Enter to skip: ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Due date skipped.");
            return null;
        }
    }
}
