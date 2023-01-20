package com.example.emailclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ChooseData {

    @FXML
    private Button cancelButton;

    @FXML
    private Button loginButton;

    @FXML
    private TextField passwordTextField;

    @FXML
    private ChoiceBox<String> chooseData;

    @FXML
    private void initialize() throws IOException
    {
        String[] userAccounts = ChooseData.userAccounts();
        if(userAccounts.length == 0)
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AlertBox.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            AlertBox alertBox = fxmlLoader.getController();
            alertBox.display("Keine Daten vorhanden");
            Stage stage = new Stage();
            stage.setTitle("Fehler");
            stage.setScene(new Scene(root1));
            stage.show();
            cancelButtonClicked();
        }
        for(int i = 0; i < userAccounts.length; i++)
        {
            String account = userAccounts[i].toString();
            account = account.substring(0, account.length()-4);
            chooseData.getItems().addAll(account);
        }
        String firstAcc = userAccounts[0];
        chooseData.setValue(firstAcc.substring(0,firstAcc.length()-4));
    }

    @FXML
    void cancelButtonClicked()
    {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void loginButtonClicked(ActionEvent event)
    {
        ReadWrite readWrite = new ReadWrite();
        try
        {
            String username = chooseData.getValue();
            String password = readWrite.readPassword(username);
            if(passwordTextField.getText().equals(password))
            {
                try
                {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();

                    LoginScreen loginScreen = fxmlLoader.getController();
                    loginScreen.showUserData(username);

                    Stage stage = new Stage();
                    stage.setTitle("Login");
                    stage.setScene(new Scene(root1));
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
            else
            {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AlertBox.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                AlertBox alertBox = fxmlLoader.getController();
                alertBox.display("Falsches Passwort");
                Stage stage = new Stage();
                stage.setTitle("Fehler");
                stage.setScene(new Scene(root1));
                stage.show();
            }
        }
        catch (IOException e1)
        {

        }

    }

    private static String[] userAccounts()
    {
        File folder = new File("C:\\files");
        File[] listOfFiles = folder.listFiles();
        String[] nameOfFiles = new String[listOfFiles.length];

        for (int i = 0; i < listOfFiles.length; i++)
        {
            if (listOfFiles[i].isFile())
            {
                nameOfFiles[i] = listOfFiles[i].getName();
            }
        }
        return nameOfFiles;
    }

}
