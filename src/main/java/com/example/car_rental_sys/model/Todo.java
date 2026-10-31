package com.example.car_rental_sys.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Task / Todo item entity for fleet operations.
 */
public class Todo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer todoId;
    private Integer userId;
    private String title;
    private String description;
    private TodoPriority priority;
    private Boolean isCompleted;
    private LocalDate dueDate;
    private LocalDateTime createdAt;

    public Todo() {
        this.priority = TodoPriority.MEDIUM;
        this.isCompleted = false;
        this.createdAt = LocalDateTime.now();
    }

    public Todo(Integer userId, String title, String description, TodoPriority priority, LocalDate dueDate) {
        this();
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.priority = priority != null ? priority : TodoPriority.MEDIUM;
        this.dueDate = dueDate;
    }

    public Integer getTodoId() {
        return todoId;
    }

    public void setTodoId(Integer todoId) {
        this.todoId = todoId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TodoPriority getPriority() {
        return priority;
    }

    public void setPriority(TodoPriority priority) {
        this.priority = priority;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Todo todo)) return false;
        return Objects.equals(todoId, todo.todoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId);
    }

    @Override
    public String toString() {
        return "Todo{" +
                "todoId=" + todoId +
                ", title='" + title + '\'' +
                ", priority=" + priority +
                ", isCompleted=" + isCompleted +
                '}';
    }
}
