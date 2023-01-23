package com.example.emailclient;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class LoginScreen {

    @FXML
    private ChoiceBox<String> chooseServer;

    @FXML
    private Button confirmButton;

    @FXML
    private TextField email;

    @FXML
    private TextField inputPort;

    @FXML
    private TextField inputServer;

    @FXML
    private TextField outputPort;

    @FXML
    private TextField outputServer;

    @FXML
    private TextField password;

    @FXML
    private CheckBox passwordCheckBox;

    @FXML
    private TextField username;



    @FXML
    void confirmButtonClicked(ActionEvent event) throws IOException {
        if(username.getText().isEmpty() || password.getText().isEmpty() || inputServer.getText().isEmpty() || inputPort.getText().isEmpty() || outputPort.getText().isEmpty() || outputServer.getText().isEmpty() || email.getText().isEmpty())
        {
            showError("Fehler", "Füllen Sie alle Felder aus!");
        }
        else
        {
            if (passwordCheckBox.isSelected())
            {
                String data = username.getText() + "," + password.getText() + "," + inputServer.getText() + "," + inputPort.getText() + "," + outputServer.getText() + "," + outputPort.getText() + "," + chooseServer.getValue() + "," + email.getText();
                ReadWrite readWrite = new ReadWrite();
                readWrite.write(data);
            }


            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
            Parent root2 = fxmlLoader.load();
            MainScreen mainScreen = fxmlLoader.getController();
            mainScreen.setUserData(username.getText(), email.getText(), password.getText(), inputServer.getText(), Integer.valueOf(inputPort.getText()), outputServer.getText(), Integer.valueOf(outputPort.getText()));
            mainScreen.setDirectoryName(username.getText());
            mainScreen.initialize();
            Stage stage = new Stage();
            stage.setScene(new Scene(root2));
            stage.show();

            Stage window = (Stage) confirmButton.getScene().getWindow();
            window.close();
        }

    }

    @FXML
    private void initialize()
    {
        File filesDir = new File("C:\\files");
        if(!filesDir.exists())
        {
            filesDir.mkdirs();
        }
        File mailsDir = new File("C:\\mails");
        if(!mailsDir.exists())
        {
            mailsDir.mkdirs();
        }

        chooseServer.getItems().addAll("POP3", "SMTP");
        chooseServer.setValue("POP3");
        inputServer.setText("smtp.uni-jena.de");
        inputPort.setText("465");
        outputServer.setText("pop3.uni-jena.de");
        outputPort.setText("995");

        inputPort.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue)
            {
                if (!newValue.matches("\\d*"))
                {
                    inputPort.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        outputPort.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue)
            {
                if (!newValue.matches("\\d*"))
                {
                    outputPort.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    public void showUserData(String usernameData) throws IOException
    {
        ReadWrite write = new ReadWrite();
        username.setText(write.readUsername(usernameData));
        password.setText(write.readPassword(usernameData));
        inputServer.setText(write.readInputAdress(usernameData));
        inputPort.setText(write.readInputPort(usernameData));
        outputServer.setText(write.readOutputAdress(usernameData));
        outputPort.setText(write.readOutputPort(usernameData));
        chooseServer.setValue(write.readServer(usernameData));
        email.setText(write.readEmail(usernameData));
    }

    private void showError(String title, String message) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AlertBox.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        AlertBox alertBox = fxmlLoader.getController();
        alertBox.display(message);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root1));
        stage.show();
    }


}
