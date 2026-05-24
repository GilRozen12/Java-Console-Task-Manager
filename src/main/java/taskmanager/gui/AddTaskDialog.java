package taskmanager.gui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import taskmanager.model.TaskPriority;

import java.time.LocalDate;
import java.util.Optional;

public class AddTaskDialog {

    public record Result(String title, String description, TaskPriority priority, LocalDate dueDate) {}

    public Optional<Result> show() {
        Dialog<Result> dialog = new Dialog<>();
        dialog.setTitle("Add Task");
        dialog.setHeaderText("Enter task details");

        ButtonType okButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        TextField titleField = new TextField();
        titleField.setPromptText("Title (required)");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        ComboBox<TaskPriority> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll(TaskPriority.values());
        priorityBox.setValue(TaskPriority.MEDIUM);

        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Optional");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Priority:"), 0, 2);
        grid.add(priorityBox, 1, 2);
        grid.add(new Label("Due Date:"), 0, 3);
        grid.add(dueDatePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);

        javafx.scene.Node okNode = dialog.getDialogPane().lookupButton(okButton);
        okNode.setDisable(true);

        Runnable validateInput = () -> {
            String title = titleField.getText().trim();
            String description = descriptionField.getText();
            boolean invalid = title.isEmpty() || title.contains("|") || description.contains("|");
            okNode.setDisable(invalid);
        };

        titleField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        descriptionField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());

        dialog.setResultConverter(button -> {
            if (button == okButton) {
                return new Result(
                        titleField.getText().trim(),
                        descriptionField.getText().trim(),
                        priorityBox.getValue(),
                        dueDatePicker.getValue()
                );
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
