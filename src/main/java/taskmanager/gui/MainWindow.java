package taskmanager.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import taskmanager.model.Task;
import taskmanager.model.TaskPriority;
import taskmanager.model.TaskStatus;
import taskmanager.service.TaskManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainWindow {

    private final TaskManager manager;
    private ObservableList<Task> taskList;
    private TableView<Task> table;
    private ComboBox<String> statusFilter;
    private ComboBox<String> priorityFilter;

    public MainWindow(TaskManager manager) {
        this.manager = manager;
    }

    public Scene buildScene() {
        table = buildTable();

        Button addButton = new Button("Add Task");
        addButton.setOnAction(e -> {
            new AddTaskDialog().show().ifPresent(result -> {
                manager.addTask(result.title(), result.description(), result.priority(), result.dueDate());
                applyFilters();
            });
        });

        Button deleteButton = new Button("Delete Task");
        deleteButton.setOnAction(e -> {
            Task selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("No Selection");
                alert.setHeaderText(null);
                alert.setContentText("Please select a task to delete.");
                alert.showAndWait();
                return;
            }
            manager.deleteTask(selected.getId());
            applyFilters();
        });

        Button updateStatusButton = new Button("Update Status");
        updateStatusButton.setOnAction(e -> {
            Task selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("No Selection");
                alert.setHeaderText(null);
                alert.setContentText("Please select a task to update.");
                alert.showAndWait();
                return;
            }
            ChoiceDialog<TaskStatus> dialog = new ChoiceDialog<>(
                    selected.getStatus(),
                    TaskStatus.values()
            );
            dialog.setTitle("Update Status");
            dialog.setHeaderText(null);
            dialog.setContentText("Choose new status:");
            dialog.showAndWait().ifPresent(chosen -> {
                manager.updateStatus(selected.getId(), chosen);
                applyFilters();
            });
        });

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> applyFilters());

        statusFilter = new ComboBox<>();
        statusFilter.getItems().add("All");
        for (TaskStatus s : TaskStatus.values()) statusFilter.getItems().add(s.name());
        statusFilter.setValue("All");
        statusFilter.setOnAction(e -> applyFilters());

        priorityFilter = new ComboBox<>();
        priorityFilter.getItems().add("All");
        for (TaskPriority p : TaskPriority.values()) priorityFilter.getItems().add(p.name());
        priorityFilter.setValue("All");
        priorityFilter.setOnAction(e -> applyFilters());

        HBox toolbar = new HBox(10, addButton, deleteButton, updateStatusButton, refreshButton);
        toolbar.setPadding(new Insets(10));

        HBox filterBar = new HBox(10,
                new Label("Status:"), statusFilter,
                new Label("Priority:"), priorityFilter);
        filterBar.setPadding(new Insets(0, 10, 10, 10));

        VBox topArea = new VBox(toolbar, filterBar);

        BorderPane root = new BorderPane();
        root.setTop(topArea);
        root.setCenter(table);

        return new Scene(root, 700, 450);
    }

    private void applyFilters() {
        String status = statusFilter.getValue();
        String priority = priorityFilter.getValue();
        List<Task> filtered = new ArrayList<>();
        for (Task task : manager.getAllTasks()) {
            if (!"All".equals(status) && !task.getStatus().name().equals(status)) continue;
            if (!"All".equals(priority) && !task.getPriority().name().equals(priority)) continue;
            filtered.add(task);
        }
        taskList.setAll(filtered);
    }

    private TableView<Task> buildTable() {
        TableColumn<Task, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Task, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);

        TableColumn<Task, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(120);

        TableColumn<Task, String> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityCol.setPrefWidth(100);

        TableColumn<Task, LocalDate> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        dueDateCol.setPrefWidth(120);

        taskList = FXCollections.observableArrayList(manager.getAllTasks());

        TableView<Task> table = new TableView<>(taskList);
        table.getColumns().add(idCol);
        table.getColumns().add(titleCol);
        table.getColumns().add(statusCol);
        table.getColumns().add(priorityCol);
        table.getColumns().add(dueDateCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }
}
