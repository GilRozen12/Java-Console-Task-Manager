package taskmanager;

import taskmanager.model.Task;
import taskmanager.service.TaskManager;
import taskmanager.storage.TaskFileRepository;
import taskmanager.ui.ConsoleUI;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        TaskFileRepository repository = new TaskFileRepository("tasks.txt");
        List<Task> loaded = repository.loadAll();
        TaskManager manager = new TaskManager(loaded);
        ConsoleUI ui = new ConsoleUI(manager, repository);
        ui.start();
    }
}