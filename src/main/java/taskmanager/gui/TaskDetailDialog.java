package taskmanager.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import taskmanager.model.Task;

public class TaskDetailDialog {

    public static void show(Task task) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Task Details");
        alert.setHeaderText(task.getTitle());

        String[][] rows = {
            {"Description:", task.getDescription() != null ? task.getDescription() : ""},
            {"Status:",      task.getStatus().toString()},
            {"Priority:",    task.getPriority().toString()},
            {"Due Date:",    task.getDueDate() != null ? task.getDueDate().toString() : "None"}
        };

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10, 20, 10, 10));

        for (int i = 0; i < rows.length; i++) {
            Label key = new Label(rows[i][0]);
            key.setStyle("-fx-font-weight: bold;");
            grid.add(key, 0, i);
            grid.add(new Label(rows[i][1]), 1, i);
        }

        alert.getDialogPane().setContent(grid);
        alert.showAndWait();
    }
}
