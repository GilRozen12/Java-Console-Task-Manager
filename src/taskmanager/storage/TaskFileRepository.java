package taskmanager.storage;

import taskmanager.model.Task;
import taskmanager.model.TaskPriority;
import taskmanager.model.TaskStatus;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskFileRepository {

    private final String filePath;

    public TaskFileRepository(String filePath) {
        this.filePath = filePath;
    }

    public void saveAll(List<Task> tasks) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Task task : tasks) {
                String dueDate = task.getDueDate() != null ? task.getDueDate().toString() : "none";
                writer.println(String.format("%d|%s|%s|%s|%s|%s",
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getPriority(),
                        dueDate));
            }
        }
    }

    public List<Task> loadAll() throws IOException {
        List<Task> tasks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = parseLine(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (FileNotFoundException e) {
            return tasks;
        }

        return tasks;
    }

    private Task parseLine(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length != 6) {
            System.err.println("Skipping malformed line: " + line);
            return null;
        }
        try {
            int id = Integer.parseInt(fields[0]);
            String title = fields[1];
            String description = fields[2];
            TaskStatus status = TaskStatus.valueOf(fields[3]);
            TaskPriority priority = TaskPriority.valueOf(fields[4]);
            LocalDate dueDate = fields[5].equals("none") ? null : LocalDate.parse(fields[5]);
            return new Task(id, title, description, status, priority, dueDate);
        } catch (Exception e) {
            System.err.println("Skipping malformed line: " + line);
            return null;
        }
    }
}
