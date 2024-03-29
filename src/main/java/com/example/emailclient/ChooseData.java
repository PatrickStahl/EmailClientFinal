package com.example.emailclient;

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
    private void initialize() {
        String[] userAccounts = ChooseData.userAccounts();
        for (int i = 0; i < userAccounts.length; i++) {
            String account = userAccounts[i];
            account = account.substring(0, account.length() - 4);
            chooseData.getItems().addAll(account);
        }
        String firstAcc = userAccounts[0];
        chooseData.setValue(firstAcc.substring(0, firstAcc.length() - 4));
        //TODO das hier wegmachen
        passwordTextField.setText("UAY!nJQjNPT8Ur9xs6S7");
    }

    @FXML
    void cancelButtonClicked()
    {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void loginButtonClicked()
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
                    Parent root1 = fxmlLoader.load();

                    LoginScreen loginScreen = fxmlLoader.getController();
                    loginScreen.showUserData(username);

                    Stage stage = new Stage();
                    stage.setTitle("Login");
                    stage.setScene(new Scene(root1));
                    stage.setResizable(false);
                    stage.show();

                    Stage window = (Stage) loginButton.getScene().getWindow();
                    window.close();
                }
                catch (Exception ignored)
                {

                }
            }
            else
            {
                showError("Fehler", "Falsches Passwort");
            }
        }
        catch (IOException ignored)
        {

        }

    }

    private static String[] userAccounts()
    {
        File folder = new File(Global.files);
        File[] listOfFiles = folder.listFiles();

        String[] nameOfFiles = new String[listOfFiles.length];

        for (int i = 0; i < listOfFiles.length; i++)
        {
            if (listOfFiles[i].isFile()) {
                nameOfFiles[i] = listOfFiles[i].getName();
            }
        }
        return nameOfFiles;

    }

    private static void showError(String title, String message) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(ChooseData.class.getResource("AlertBox.fxml"));
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
