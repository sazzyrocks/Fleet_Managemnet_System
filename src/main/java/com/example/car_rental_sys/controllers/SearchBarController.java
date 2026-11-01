package com.example.car_rental_sys.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.util.function.Consumer;

/**
 * Reusable Search and Filtering Controller component.
 */
public class SearchBarController extends Controller {

    @FXML
    private TextField searchBar;

    @FXML
    private Button filterBtn;

    private Consumer<String> searchAction;
    private Runnable filterAction;

    @FXML
    public void initialize() {
        if (searchBar != null) {
            searchBar.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER && searchAction != null) {
                    searchAction.accept(searchBar.getText().trim());
                }
            });
        }
    }

    public void setSearchAction(Consumer<String> action) {
        this.searchAction = action;
    }

    public void setFilterAction(Runnable action) {
        this.filterAction = action;
    }

    public String getSearchQuery() {
        return searchBar != null ? searchBar.getText().trim() : "";
    }

    @FXML
    private void filterBtnClicked(MouseEvent event) {
        if (filterAction != null) {
            filterAction.run();
        } else if (searchAction != null && searchBar != null) {
            searchAction.accept(searchBar.getText().trim());
        }
    }
}

