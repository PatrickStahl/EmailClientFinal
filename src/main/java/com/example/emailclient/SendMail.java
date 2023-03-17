package com.example.emailclient;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Properties;

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

    @FXML
    private Button cancelButton;

    @FXML
    private Label ccLabel;


    String CC = "";
    String username;


    @FXML
    private void initialize()
    {
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                ccLabel.setVisible(false);
                textFieldTo.requestFocus();
            }
        });
    }

    @FXML
    void addButtonClicked()
    {
        CC += textFieldCC.getText() + ",";
//        labelStatus.setText("Receiver " + textFieldCC.getText() + " added");
//        textFieldCC.setPromptText(CC.substring(0, CC.length()-1));
        textFieldCC.setText("");
        ccLabel.setVisible(true);
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(3000));
        fadeTransition.setNode(ccLabel);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();
    }

    @FXML
    void cancelButtonClicked()
    {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void sendButtonClicked() throws IOException {
        if(textFieldFrom.getText().isEmpty() || textFieldTo.getText().isEmpty() || textFieldSubject.getText().isEmpty() || textFieldBody.getText().isEmpty())
        {
            showError("Fehler", "Füllen Sie alle Felder aus!");
        }
        else
        {
            try
            {
                if(textFieldCC.getText() != null)
                {
                    CC += textFieldCC.getText();
                }

                send(textFieldFrom.getText(), textFieldTo.getText(), CC, textFieldSubject.getText(), textFieldBody.getText());
                Stage window = (Stage) sendButton.getScene().getWindow();
                window.close();
            }
            catch (MessagingException e)
            {
                showError("Fehler", "Email konnte mit diesen Daten nicht gesendet werden\nPrüfen Sie Ihre Eingaben");
                Stage window = (Stage) sendButton.getScene().getWindow();
                window.close();
            }
        }
    }

    private void send(String from, String to, String CC, String subject, String body) throws IOException, MessagingException {
        ReadWrite readWrite = new ReadWrite();
        String host = readWrite.readInputAddress(username);
        String port = readWrite.readInputPort(username);
        String password = readWrite.readPassword(username);
        boolean ssl = false;
        if(port.equals("465"))
        {
            ssl = true;
        }

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.host", host);

        if(ssl)
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
        mimeBodyPart.setContent(body, "text/html; charset=utf-8");

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
