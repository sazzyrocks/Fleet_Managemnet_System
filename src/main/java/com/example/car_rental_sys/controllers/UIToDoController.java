package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.dao.DAOFactory;
import com.example.car_rental_sys.dao.TodoDAO;
import com.example.car_rental_sys.model.Todo;
import com.example.car_rental_sys.model.TodoPriority;
import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.service.ServiceFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for Admin and Fleet Operations To-Do / Checklist Component.
 */
public class UIToDoController {

    @FXML
    private Label label1, label2, time1, time2;

    @FXML
    private Pane pane1, pane2, addTaskPane;

    @FXML
    private CheckBox checkBox1, checkBox2;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea taskContent;

    @FXML
    private ImageView addBtn;

    private Integer currentTodoId1;
    private Integer currentTodoId2;

    private final TodoDAO todoDAO = DAOFactory.getInstance().getTodoDAO();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        initEvents();
        hideAddTaskPane();
        loadTodoData();
    }

    private void initEvents() {
        if (pane1 != null) {
            pane1.setOnMouseClicked(event -> deleteToDo1());
        }
        if (pane2 != null) {
            pane2.setOnMouseClicked(event -> deleteToDo2());
        }
    }

    private void loadTodoData() {
        User user = ServiceFactory.getInstance().getAuthService().getCurrentUser();
        int userId = user != null ? user.getUserId() : 1;

        AsyncExecutor.getInstance().execute(
                () -> todoDAO.findPendingByUserId(userId),
                todos -> {
                    displayTodos(todos);
                },
                error -> {
                    System.err.println("[UIToDoController] Error loading todos: " + error.getMessage());
                }
        );
    }

    private void displayTodos(List<Todo> todos) {
        if (pane1 == null || pane2 == null) {
            return;
        }

        if (todos == null || todos.isEmpty()) {
            pane1.setVisible(false);
            pane2.setVisible(false);
        } else if (todos.size() == 1) {
            pane1.setVisible(true);
            pane2.setVisible(false);
            bindTodo(todos.get(0), label1, time1, checkBox1);
            currentTodoId1 = todos.get(0).getTodoId();
        } else {
            pane1.setVisible(true);
            pane2.setVisible(true);
            bindTodo(todos.get(0), label1, time1, checkBox1);
            bindTodo(todos.get(1), label2, time2, checkBox2);
            currentTodoId1 = todos.get(0).getTodoId();
            currentTodoId2 = todos.get(1).getTodoId();
        }
    }

    private void bindTodo(Todo todo, Label titleLbl, Label timeLbl, CheckBox cb) {
        if (titleLbl != null) {
            titleLbl.setText(todo.getTitle() != null ? todo.getTitle() : "Task");
            titleLbl.getStyleClass().remove("delete");
        }
        if (timeLbl != null) {
            String due = todo.getDueDate() != null ? todo.getDueDate().format(DATE_FMT) : "No Due Date";
            timeLbl.setText(due);
        }
        if (cb != null) {
            cb.setSelected(Boolean.TRUE.equals(todo.getIsCompleted()));
        }
    }

    private void deleteToDo1() {
        if (currentTodoId1 == null) {
            return;
        }
        if (checkBox1 != null) {
            checkBox1.setSelected(true);
        }
        if (label1 != null) {
            label1.getStyleClass().add("delete");
        }

        int id = currentTodoId1;
        AsyncExecutor.getInstance().execute(
                () -> todoDAO.markAsCompleted(id, true),
                success -> refreshAfterDelay(),
                error -> System.err.println("[UIToDoController] Failed to complete todo: " + error.getMessage())
        );
    }

    private void deleteToDo2() {
        if (currentTodoId2 == null) {
            return;
        }
        if (checkBox2 != null) {
            checkBox2.setSelected(true);
        }
        if (label2 != null) {
            label2.getStyleClass().add("delete");
        }

        int id = currentTodoId2;
        AsyncExecutor.getInstance().execute(
                () -> todoDAO.markAsCompleted(id, true),
                success -> refreshAfterDelay(),
                error -> System.err.println("[UIToDoController] Failed to complete todo: " + error.getMessage())
        );
    }

    private void refreshAfterDelay() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(800), event -> loadTodoData()));
        timeline.play();
    }

    @FXML
    private void showAddTaskPane() {
        if (addTaskPane == null) {
            return;
        }
        if (addTaskPane.isVisible()) {
            hideAddTaskPane();
            return;
        }
        addTaskPane.setVisible(true);
        if (taskContent != null) {
            Platform.runLater(taskContent::requestFocus);
        }
        if (datePicker != null) {
            datePicker.setValue(LocalDate.now());
        }
    }

    private void hideAddTaskPane() {
        if (addTaskPane != null) {
            addTaskPane.setVisible(false);
        }
    }

    @FXML
    private void addTaskBtnClick() {
        if (taskContent == null || taskContent.getText() == null || taskContent.getText().trim().isEmpty()) {
            hideAddTaskPane();
            return;
        }

        String content = taskContent.getText().trim();
        LocalDate due = (datePicker != null && datePicker.getValue() != null) ? datePicker.getValue() : LocalDate.now();

        User user = ServiceFactory.getInstance().getAuthService().getCurrentUser();
        int userId = user != null ? user.getUserId() : 1;

        Todo todo = new Todo(userId, content, content, TodoPriority.MEDIUM, due);

        AsyncExecutor.getInstance().execute(
                () -> todoDAO.save(todo),
                saved -> {
                    taskContent.clear();
                    hideAddTaskPane();
                    loadTodoData();
                },
                error -> {
                    System.err.println("[UIToDoController] Error creating todo: " + error.getMessage());
                    hideAddTaskPane();
                }
        );
    }
}

