package com.example.emailclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AlertBox {

    @FXML
    private Label LabelText;

    @FXML
    private Button okButton;

    @FXML
    void okButtonClicked()
    {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }


    public void display(String label)
    {
        LabelText.setText(label);
    }
}
