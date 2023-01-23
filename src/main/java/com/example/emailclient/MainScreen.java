package com.example.emailclient;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import static java.lang.Math.abs;

public class MainScreen {


    private String username;
    private String email;
    private String password;
    private String outPutServer;
    private Integer outputPort;
    ObservableList<Mail> list = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Mail, String> columnDate;

    @FXML
    private TableColumn<Mail, String> columnFrom;

    @FXML
    private TableColumn<Mail, String> columnSubject;

    @FXML
    private TableColumn<Mail, String> columnTo;

    @FXML
    private Label labelStatus;

    @FXML
    private Button refreshButton;

    @FXML
    private TableView<Mail> table;

    @FXML
    void refreshButtonClicked() throws MessagingException, IOException {
        labelStatus.setText("Aktualisiere...");
        SaveLoad saveLoad = new SaveLoad();
        Boolean checkIfContinue = saveLoad.download(username, outPutServer, email, password, outputPort);
        if(!checkIfContinue)
        {
            Stage stage = (Stage) refreshButton.getScene().getWindow();
            stage.close();
        }
        loadData();
    }


    @FXML
    void sendButtonClicked() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SendMail.fxml"));
        Parent root2 = fxmlLoader.load();
        SendMail sendMail = fxmlLoader.getController();
        sendMail.setFrom(email);
        sendMail.setUsername(username);
        Stage stage = new Stage();
        stage.setScene(new Scene(root2));
        stage.show();
    }

    public void initialize() {
        table.getColumns().clear();
        table.setItems(list);
        initiateCols();
        try
        {
            loadData();
        }
        catch (MessagingException | IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void initiateCols() {
        SaveLoad saveLoad = new SaveLoad();
        columnSubject.setCellValueFactory(new PropertyValueFactory<Mail, String>("subject"));
        columnFrom.setCellValueFactory(new PropertyValueFactory<Mail, String>("from"));
        columnTo.setCellValueFactory(new PropertyValueFactory<Mail, String>("to"));
        columnDate.setCellValueFactory(new PropertyValueFactory<Mail, String>("date"));

        columnSubject.setCellFactory(tc ->
        {
            TableCell<Mail, String> cell = new TableCell<Mail, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };


            cell.setOnMouseClicked(e ->
            {
                System.out.println(table.getSelectionModel().getSelectedIndex());
                Integer index = cell.getIndex();
                File folder = new File("C:\\mails\\" + username + "\\");
                index = abs((index - folder.listFiles().length));
                String name = "mail" + index;

                if (!cell.isEmpty())
                {
                    try
                    {
                        loadMail(saveLoad.getSubject(username, name), saveLoad.getFrom(username, name), saveLoad.getTo(username, name), saveLoad.loadBody(username, name));
                        saveLoad.addHeader("C:\\mails\\" + username + "\\" + name + ".eml", "1", saveLoad.getSentDate(username, name));
                    }
                    catch (IOException | MessagingException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                }
            });

            return cell;
        });

        table.getColumns().addAll(columnSubject, columnFrom, columnTo, columnDate);
    }

    //loads from PC
    private void loadData() throws MessagingException, IOException {
        list.clear();
        list.removeAll();
        File folder = new File("C:\\mails\\" + username + "\\");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            Arrays.sort(listOfFiles, Comparator.comparingLong(File::lastModified).reversed());
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    SaveLoad saveLoad = new SaveLoad();
                    String name = listOfFiles[i].getName();
                    name = name.substring(0, name.length() - 4);
                    Mail currentMail = new Mail(saveLoad.getSubject(username, name), saveLoad.getFrom(username, name), saveLoad.getTo(username, name), saveLoad.getDate(username, name));
                    list.add(currentMail);
                }
            }
            table.setItems(list);
            table.refresh();
            labelStatus.setText("");
        }

    }

    public void loadMail(String subject, String from, String to, String body) throws IOException {
        SaveLoad safeLoad = new SaveLoad();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("OpenMail.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        OpenMail openMail = fxmlLoader.getController();
        openMail.loadStuff(subject, from, to, body);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.show();
    }

    public void setUserData(String username, String email, String password, String inputServer, Integer inputPort, String outPutServer, Integer outputPort) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.outputPort = outputPort;
        this.outPutServer = outPutServer;
    }


    public static class Mail {
        private final SimpleStringProperty subject;
        private final SimpleStringProperty from;
        private final SimpleStringProperty to;
        private final SimpleStringProperty date;

        public Mail(String subject, String from, String to, String date) {
            this.subject = new SimpleStringProperty(subject);
            this.from = new SimpleStringProperty(from);
            this.to = new SimpleStringProperty(to);
            this.date = new SimpleStringProperty(date);
        }

        public String getSubject() {
            return subject.get();
        }

        public SimpleStringProperty subjectProperty() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject.set(subject);
        }

        public String getFrom() {
            return from.get();
        }

        public SimpleStringProperty fromProperty() {
            return from;
        }

        public void setFrom(String from) {
            this.from.set(from);
        }

        public String getTo() {
            return to.get();
        }

        public SimpleStringProperty toProperty() {
            return to;
        }

        public void setTo(String to) {
            this.to.set(to);
        }

        public String getDate() {
            return date.get();
        }

        public SimpleStringProperty dateProperty() {
            return date;
        }

        public void setDate(String date) {
            this.date.set(date);
        }
    }


    public void setDirectoryName(String name) {
        File directory = new File("C:\\mails\\" + name);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
}
