package com.example.emailclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class HelloController {

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Label welcomeText;

    @FXML
    void loginButtonClick(ActionEvent event)
    {
        System.out.println("LoginButton pressed");
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root1));
            stage.setResizable(false);
            stage.show();
            Stage window = (Stage) loginButton.getScene().getWindow();
            window.close();
        }
        catch (Exception e)
        {
            System.out.println("Cant load new window");
            e.printStackTrace();
        }

    }

    @FXML
    void registerButtonClicked()
    {
        try
        {
            File folder = new File(Global.files);
            File[] listOfFiles = folder.listFiles();
            if(listOfFiles == null)
            {
                showError("Fehler", "Keine Daten vorhanden");
            }
            else
            {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChooseData.fxml"));
                Parent root1 = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Wähle Daten");
                stage.setScene(new Scene(root1));
                stage.setResizable(false);
                stage.show();
                Stage window = (Stage) registerButton.getScene().getWindow();
                window.close();
            }
        }
        catch (Exception e)
        {
            System.out.println("Cant load new window");
            e.printStackTrace();
        }
    }
    private void showError(String title, String message) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AlertBox.fxml"));
        Parent root1 = fxmlLoader.load();
        AlertBox alertBox = fxmlLoader.getController();
        alertBox.display(message);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root1));
        stage.setResizable(false);
        stage.show();
    }

}
