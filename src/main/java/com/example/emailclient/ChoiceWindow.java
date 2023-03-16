package com.example.emailclient;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ChoiceWindow {

    @FXML
    protected Button noButton;

    @FXML
    protected Button yesButton;

    @FXML
    void noButtonClicked()
    {

    }

    @FXML
    void yesButtonClicked()
    {

    }

    public void close()
    {
        Stage stage = (Stage) yesButton.getScene().getWindow();
        stage.close();
    }


}
