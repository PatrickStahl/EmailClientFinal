package com.example.emailclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.io.IOException;

public class Client {

    @FXML
    private Button receiveButton;

    @FXML
    private Button sendButton;

    private String username, email, password, outPutServer, inputServer;
    private Integer inputPort, outputPort;

    @FXML
    void receiveButtonClicked(ActionEvent event)
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            MainScreen mainScreen = fxmlLoader.getController();
            mainScreen.setUserData(username, email, password, inputServer, inputPort, outPutServer, outputPort);

            Stage stage = new Stage();
            stage.setTitle("Irgendwas main-mäßiges");
            stage.setScene(new Scene(root1));
            stage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    void sendButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SendMail.fxml"));
        Parent root2 = fxmlLoader.load();
        SendMail sendMail = fxmlLoader.getController();
        sendMail.setFrom(email);
        sendMail.setUsername(username);
        Stage stage = new Stage();
        stage.setScene(new Scene(root2));
        stage.show();
    }

    public void setUserData(String username, String email, String password, String inputServer, Integer inputPort, String outPutServer, Integer outputPort)
    {
        this.username = username;
        this.email = email;
        this.password = password;
        this.inputPort = inputPort;
        this.inputServer = inputServer;
        this.outputPort = outputPort;
        this.outPutServer = outPutServer;
    }

}
