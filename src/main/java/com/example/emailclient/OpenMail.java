package com.example.emailclient;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class OpenMail {

    @FXML
    private Label labelFrom;

    @FXML
    private Label labelSubject;

    @FXML
    private Label labelTo;

    @FXML
    private TextArea textAreaBody;



    public void loadStuff(String subject, String from, String to, String body)
    {
        labelSubject.setText(subject);
        labelFrom.setText(from);
        labelTo.setText(to);
        textAreaBody.setText(body);
        textAreaBody.setEditable(false);

    }

    public void initialize()
    {

    }

}