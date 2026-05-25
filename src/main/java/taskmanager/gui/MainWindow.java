package taskmanager.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import taskmanager.model.Task;
import taskmanager.model.TaskPriority;
import taskmanager.model.TaskStatus;
import taskmanager.service.TaskManager;
import taskmanager.service.UrgencyCalculator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainWindow {

    private final TaskManager manager;
    private ObservableList<Task> taskList;
    private TableView<Task> table;
    private ComboBox<String> statusFilter;
    private ComboBox<String> priorityFilter;
    private Label urgentLabel;
    private Map<Integer, String> rowStyles = new HashMap<>();
    private CheckBox sortByUrgency;

    public MainWindow(TaskManager manager) {
        this.manager = manager;
    }

    public Scene buildScene() {
        table = buildTable();
        refreshRowStyles();

        Button addButton = new Button("Add Task");
        addButton.setOnAction(e -> {
            new AddTaskDialog().show().ifPresent(result -> {
                manager.addTask(result.title(), result.description(), result.priority(), result.dueDate());
                applyFilters();
            });
        });

        Button editButton = new Button("Edit Task");
        editButton.setOnAction(e -> {
            Task selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("No Selection");
                alert.setHeaderText(null);
                alert.setContentText("Please select a task to edit.");
                alert.showAndWait();
                return;
            }
            new EditTaskDialog(selected).show().ifPresent(result -> {
                manager.updateTask(selected.getId(), result.title(), result.description(), result.priority(), result.dueDate());
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
            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.setTitle("Delete Task");
            confirm.setHeaderText(null);
            confirm.setContentText("Delete task: \"" + selected.getTitle() + "\"?");
            confirm.showAndWait().ifPresent(button -> {
                if (button == ButtonType.OK) {
                    manager.deleteTask(selected.getId());
                    applyFilters();
                }
            });
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

        sortByUrgency = new CheckBox("Sort by urgency");
        sortByUrgency.setOnAction(e -> applyFilters());

        HBox toolbar = new HBox(10, addButton, editButton, deleteButton, updateStatusButton, refreshButton);
        toolbar.setPadding(new Insets(10));

        urgentLabel = new Label();
        urgentLabel.setPadding(new Insets(0, 10, 6, 10));
        urgentLabel.setStyle("-fx-font-weight: bold;");
        refreshUrgentLabel();

        HBox filterBar = new HBox(10,
                new Label("Status:"), statusFilter,
                new Label("Priority:"), priorityFilter,
                sortByUrgency);
        filterBar.setPadding(new Insets(0, 10, 10, 10));

        VBox topArea = new VBox(toolbar, urgentLabel, filterBar);

        BorderPane root = new BorderPane();
        root.setTop(topArea);
        root.setCenter(table);

        return new Scene(root, 700, 450);
    }

    private void applyFilters() {
        refreshRowStyles();
        String status = statusFilter.getValue();
        String priority = priorityFilter.getValue();
        List<Task> filtered = new ArrayList<>();
        for (Task task : manager.getAllTasks()) {
            if (!"All".equals(status) && !task.getStatus().name().equals(status)) continue;
            if (!"All".equals(priority) && !task.getPriority().name().equals(priority)) continue;
            filtered.add(task);
        }
        if (sortByUrgency.isSelected()) {
            filtered.sort(Comparator.comparingInt(UrgencyCalculator::calculateScore).reversed());
        }
        taskList.setAll(filtered);
        refreshUrgentLabel();
    }

    private void refreshUrgentLabel() {
        Task urgent = manager.getMostUrgentTask();
        if (urgent == null) {
            urgentLabel.setText("No urgent tasks.");
        } else {
            String due = urgent.getDueDate() != null ? urgent.getDueDate().toString() : "no due date";
            urgentLabel.setText("Most urgent: " + urgent.getTitle()
                    + " — " + urgent.getPriority() + ", due " + due);
        }
    }

    private void refreshRowStyles() {
        Map<Integer, String> styles = new HashMap<>();
        LocalDate today = LocalDate.now();
        List<Task> active = new ArrayList<>();

        for (Task task : manager.getAllTasks()) {
            if (task.getStatus() == TaskStatus.COMPLETED) {
                styles.put(task.getId(), "-fx-background-color: #e0e0e0;");
            } else if (task.getDueDate() != null && task.getDueDate().isBefore(today)) {
                styles.put(task.getId(), "-fx-background-color: #ffcccc;");
            } else {
                active.add(task);
            }
        }

        active.sort(Comparator.comparingInt(UrgencyCalculator::calculateScore).reversed());

        String[] amberShades = {
            "-fx-background-color: #f0af51;",
            "-fx-background-color: #f7c073;",
            "-fx-background-color: #fbd197;"
        };

        for (int i = 0; i < Math.min(active.size(), 3); i++) {
            styles.put(active.get(i).getId(), amberShades[i]);
        }

        rowStyles = styles;
    }

    private static String toDisplayName(String rawName) {
        String[] words = rawName.split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (result.length() > 0) result.append(" ");
            result.append(Character.toUpperCase(word.charAt(0)));
            result.append(word.substring(1).toLowerCase());
        }
        return result.toString();
    }

    private TableView<Task> buildTable() {
        TableColumn<Task, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Task, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);

        TableColumn<Task, TaskStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(120);
        statusCol.setCellFactory(col -> new TableCell<Task, TaskStatus>() {
            @Override
            protected void updateItem(TaskStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : toDisplayName(item.name()));
            }
        });

        TableColumn<Task, TaskPriority> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityCol.setPrefWidth(100);
        priorityCol.setCellFactory(col -> new TableCell<Task, TaskPriority>() {
            @Override
            protected void updateItem(TaskPriority item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : toDisplayName(item.name()));
            }
        });

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

        table.setRowFactory(tv -> new TableRow<Task>() {
            {
                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && !isEmpty()) {
                        TaskDetailDialog.show(getItem());
                    }
                });
            }

            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                setStyle(empty || task == null ? "" : rowStyles.getOrDefault(task.getId(), ""));
            }
        });

        return table;
    }
}
