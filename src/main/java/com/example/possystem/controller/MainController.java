package com.example.possystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Button posNavBtn;
    @FXML private Button inventoryNavBtn;

    @FXML
    public void initialize() {
        showPosView();
    }

    @FXML
    private void showPosView() {
        loadView("pos-view.fxml");
        setActiveButton(posNavBtn);
    }

    @FXML
    private void showInventoryView() {
        loadView("inventory-view.fxml");
        setActiveButton(inventoryNavBtn);
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: " + fxmlFile, e);
        }
    }

    private void setActiveButton(Button active) {
        posNavBtn.getStyleClass().remove("nav-active");
        inventoryNavBtn.getStyleClass().remove("nav-active");
        active.getStyleClass().add("nav-active");
    }
}