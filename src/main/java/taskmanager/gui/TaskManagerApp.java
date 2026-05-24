package taskmanager.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import taskmanager.model.Task;
import taskmanager.service.TaskManager;
import taskmanager.storage.TaskFileRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskManagerApp extends Application {

    private TaskManager manager;
    private TaskFileRepository repository;

    @Override
    public void start(Stage primaryStage) {
        repository = new TaskFileRepository("tasks.txt");

        List<Task> loaded;
        try {
            loaded = repository.loadAll();
        } catch (IOException e) {
            System.out.println("Could not load tasks: " + e.getMessage());
            loaded = new ArrayList<>();
        }

        manager = new TaskManager(loaded);

        MainWindow mainWindow = new MainWindow(manager);
        primaryStage.setScene(mainWindow.buildScene());
        primaryStage.setTitle("Task Manager");

        primaryStage.setOnCloseRequest(event -> {
            try {
                repository.saveAll(manager.getAllTasks());
            } catch (IOException e) {
                System.out.println("Could not save tasks: " + e.getMessage());
            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
