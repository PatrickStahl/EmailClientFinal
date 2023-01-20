package com.example.emailclient;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import javax.mail.Session;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMail {

    @FXML
    private Label labelStatus;
    @FXML
    private Button addButton;

    @FXML
    private Button sendButton;

    @FXML
    private TextArea textFieldBody;

    @FXML
    private TextField textFieldCC;

    @FXML
    private TextField textFieldFrom;

    @FXML
    private TextField textFieldSubject;

    @FXML
    private TextField textFieldTo;

    String CC = "";
    String username;


    @FXML
    private void initialize()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                textFieldTo.requestFocus();
            }
        });

    }

    @FXML
    void addButtonClicked(ActionEvent event)
    {
        CC += textFieldCC.getText() + ",";
        labelStatus.setText("Receiver " + textFieldCC.getText() + " added");
        textFieldCC.setPromptText(CC.substring(0, CC.length()-1));
        textFieldCC.setText("");
    }

    @FXML
    void sendButtonClicked(ActionEvent event) throws IOException {
        if(textFieldFrom.getText().isEmpty() || textFieldTo.getText().isEmpty() || textFieldSubject.getText().isEmpty() || textFieldBody.getText().isEmpty())
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AlertBox.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            AlertBox alertBox = fxmlLoader.getController();
            alertBox.display("Füllen Sie alle Felder aus!");
            Stage stage = new Stage();
            stage.setTitle("Fehler");
            stage.setScene(new Scene(root1));
            stage.show();
        }
        else
        {
            try
            {
                send(textFieldFrom.getText(), textFieldTo.getText(), CC, textFieldSubject.getText(), textFieldBody.getText());
                Stage window = (Stage) sendButton.getScene().getWindow();
                window.close();
            }
            catch (MessagingException e)
            {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AlertBox.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                AlertBox alertBox = fxmlLoader.getController();
                alertBox.display("Email konnte mit diesen Daten nicht gesendet werden!");
                Stage stage = new Stage();
                stage.setTitle("Fehler");
                stage.setScene(new Scene(root1));
                stage.show();
                e.printStackTrace();
            }
        }
    }

    private void send(String from, String to, String CC, String subject, String body) throws IOException, MessagingException {
        ReadWrite readWrite = new ReadWrite();
        String host = readWrite.readInputAdress(username);
        String port = readWrite.readInputPort(username);
        String password = readWrite.readPassword(username);
        Boolean ssl = false;
        if(port.equals("465"))
        {
            ssl = true;
        }

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.host", host);

        if(ssl == true)
        {
            prop.put("mali.smtp.ssl.enable", "true");
            prop.put("mail.smtp.ssl.trust", host);
            prop.put("mail.smtp.starttls.enable", "true");
            prop.put("mail.smtp.ssl.socketFactory.port", port);
        }
        else
        {
            prop.put("mail.smtp.port", port);
        }

        Session session = Session.getInstance(prop, new Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(from, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        if(!(CC.equals("")))
        {
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC));
        }
        message.setSubject(subject);
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(body.toString(), "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    public void setFrom(String from)
    {
        textFieldFrom.setText(from);
        textFieldFrom.setEditable(false);
    }

    public void setUsername(String username)
    {
        this.username = username;
    }
}
