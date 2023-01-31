package com.example.emailclient;

import com.sun.mail.pop3.POP3SSLStore;
import com.sun.mail.pop3.POP3Store;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.util.*;


public class SaveLoad
{
    public boolean download(String username, String host, String email, String password, Integer port) throws MessagingException, IOException {

        File folder = new File("C:\\mails\\" + username);

        boolean ssl = false;
        java.util.Properties properties = new java.util.Properties();

        properties.setProperty("mail.pop3.host", host);
        properties.setProperty("mail.pop3.port", port.toString());
        if(port.equals(995))
        {
            ssl = true;
            properties.setProperty("mail.pop3.ssl.enable", "true");
        }

        Session session = Session.getInstance(properties);
        POP3SSLStore sslStore;
        POP3Store store;

        Folder inbox;
        if (ssl)
        {
            sslStore = new POP3SSLStore(session, null);
            sslStore.connect(host, port, email, password);
            inbox = sslStore.getFolder("INBOX");
        }
        else
        {
            store = new POP3Store(session, null);
            try
            {
                store.connect(host, port, email, password);
            }
            catch(MessagingException m)
            {
                showError("Fehler", "Falsche Logindaten\nPrüfen Sie Ihre Eingabe");
                return false;
            }
            inbox = store.getFolder("INBOX");
        }

        inbox.open(Folder.READ_ONLY);
        Message[] messages = inbox.getMessages();

        //Nullpointerexception never happens because directory is created beforehand every time
        for (int i = folder.listFiles().length; i< messages.length; i++)
        {
            try
            {
                messages[i].writeTo(new FileOutputStream("C:\\mails\\"+ username +"\\mail" + (i+1) + ".eml"));
                setFileCreationDate("C:\\mails\\" + username + "\\mail" + (i+1) + ".eml", messages[i].getSentDate());
            }
            catch (IOException ignored)
            {

            }
        }
        return true;
    }


    private void setFileCreationDate(String filePath, Date creationDate) throws IOException
    {
        BasicFileAttributeView attributes = Files.getFileAttributeView(Paths.get(filePath), BasicFileAttributeView.class);
        FileTime time = FileTime.fromMillis(creationDate.getTime());
        attributes.setTimes(time, time, time);
        File file = new File(filePath);
        file.setWritable(false);
    }


    public void addHeader(String filePath, String headerValue, Date creationDate) throws IOException, MessagingException
    {
        File emlFile = new File(filePath);
        emlFile.setWritable(true);
        Properties props = System.getProperties();
        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = new FileInputStream(emlFile);
        MimeMessage message = new MimeMessage(mailSession, source);

        message.addHeader("X-Seen", headerValue);
        message.saveChanges();

        message.writeTo(new FileOutputStream(filePath));

        BasicFileAttributeView attributes = Files.getFileAttributeView(Paths.get(filePath), BasicFileAttributeView.class);
        FileTime time = FileTime.fromMillis(creationDate.getTime());
        attributes.setTimes(time, time, time);
        emlFile.setWritable(false);
    }

    public String loadBody(String directory, String subject) throws MessagingException, IOException
    {
        String path = "C:\\mails\\" + directory + "\\" + subject + ".eml";
        File emlFile = new File(path);
        Properties props = System.getProperties();

        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = new FileInputStream(emlFile);
        MimeMessage message = new MimeMessage(mailSession, source);


        if (message.getContent() instanceof MimeMultipart)
        {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            for (int i = 0; i < mimeMultipart.getCount(); i++)
            {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                // If the mail is HTML, print it using JSoup
                String contentType = bodyPart.getContentType();
                String text;
                if (contentType.contains("text/html"))
                {
                    String html = (String) bodyPart.getContent();
                    Document doc = Jsoup.parse(html);
                    text = doc.text();
                }
                else if (contentType.contains("text/plain"))
                {
                    text = (String) bodyPart.getContent();
                }
                else
                {
                    // if it is MultiPart/Alternative, print the first body-part
                    if (contentType.contains("multipart/alternative"))
                    {
                        MimeMultipart mimeMultipartAlternative = (MimeMultipart) bodyPart.getContent();
                        BodyPart bodyPartAlternative = mimeMultipartAlternative.getBodyPart(0);
                        text = (String) bodyPartAlternative.getContent();
                    }
                    else
                    {
                        text = "\nFile: " + bodyPart.getFileName() + "\n";
                    }
                }
                return text;
            }
        }

        else
        {
            String contentType = message.getContentType();
            String text;
            if (contentType.contains("text/html"))
            {
                String html = (String) message.getContent();
                Document doc = Jsoup.parse(html);
                text = doc.text();
            }
            else if (contentType.contains("text/plain"))
            {
                text = (String) message.getContent();
            }
            else
            {
                text = "\nFile: " + message.getFileName() + "\n";
            }
            return text;
        }
        return "";
    }


    public String getSubject(String directory, String name) throws FileNotFoundException, MessagingException {
        String path = "C:\\mails\\" + directory + "\\" + name + ".eml";
        File emlFile = new File(path);
        Properties props = System.getProperties();
        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = new FileInputStream(emlFile);
        MimeMessage message = new MimeMessage(mailSession, source);

        return message.getSubject();
    }

    private String decode(String text)
    {
        if (text.startsWith("=?"))
        {
            String[] splits = text.split("=\\?");
            StringBuilder deciphered = new StringBuilder();

            for (int i = 0; i < splits.length; i++)
            {
                String split = splits[i];
                try
                {
                    if (split.isEmpty())
                    {
                        continue;
                    }
                    String[] parts = split.split("\\?");
                    String charset = parts[0];
                    String encoding = parts[1].toLowerCase();
                    String encodedText = parts[2];

                    if (encoding.equals("q"))
                    {
                        encodedText = encodedText.replaceAll("=([0-9A-Fa-f]{2})", "%$1"); // Replace all "=XX" with "%XX"
                        encodedText = encodedText.replaceAll("_", " ");

                        try
                        {
                            deciphered.append(URLDecoder.decode(encodedText, charset));
                        }
                        catch (UnsupportedEncodingException ignored)
                        {

                        }
                    }
                    else if (encoding.equals("b"))
                    {
                        byte[] bytes = Base64.getDecoder().decode(encodedText);
                        try
                        {
                            deciphered.append(new String(bytes, charset));
                        }
                        catch (UnsupportedEncodingException ignored)
                        {

                        }
                    }
                }
                catch (Exception ignored)
                {

                }
            }

            return deciphered.toString();
        }
        else
        {
            return text;
        }
    }

    public String getFrom(String directory, String name) throws MessagingException, FileNotFoundException {
        String path = "C:\\mails\\" + directory + "\\" + name + ".eml";
        File emlFile = new File(path);
        Properties props = System.getProperties();
        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = new FileInputStream(emlFile);
        MimeMessage message = new MimeMessage(mailSession, source);

        String sender = "";
        for(int i = 0; i< message.getFrom().length; i++)
        {
            String senderTemp = message.getFrom()[i].toString();
            if(senderTemp.contains("=?"))
            {
                sender += decode(senderTemp) + " ";
            }
            else if (senderTemp.contains("<"))
            {
                sender += senderTemp.substring(senderTemp.indexOf("<") + 1, senderTemp.indexOf(">")) + " ";
            }
            else
            {
                sender += senderTemp + " ";
            }
        }
        return sender;
    }

    public String getTo(String directory, String name) throws MessagingException, FileNotFoundException {
        String path = "C:\\mails\\" + directory + "\\" + name + ".eml";
        File emlFile = new File(path);
        Properties props = System.getProperties();
        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = new FileInputStream(emlFile);
        MimeMessage message = new MimeMessage(mailSession, source);

        String receiver = "";
        try
        {
            for (int i = 0; i < message.getAllRecipients().length; i++)
            {
                String receiverTemp = message.getAllRecipients()[i].toString();
                if (receiverTemp.contains("<"))
                {
                    receiverTemp = receiverTemp.substring(receiverTemp.indexOf("<") + 1, receiverTemp.indexOf(">"));
                }
                if (i == 0)
                {
                    receiver = receiverTemp;
                }
                else
                {
                    receiver = receiver + ", " + receiverTemp;
                }
            }
        }
        catch (NullPointerException ignored)
        {

        }
        return receiver;
    }

    public String getDate(String directory, String name) throws MessagingException, FileNotFoundException {
        String path = "C:\\mails\\" + directory + "\\" + name + ".eml";
        File emlFile = new File(path);
        Properties props = System.getProperties();
        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = new FileInputStream(emlFile);
        MimeMessage message = new MimeMessage(mailSession, source);

        if(message.getSentDate() != null)
        {
            String date = message.getSentDate().toString();

            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.GERMANY);
            return formatter.format(message.getSentDate());
        }
        else
        {
            return "";
        }
    }

    public Date getSentDate(String directory, String name) throws MessagingException, FileNotFoundException {
        String path = "C:\\mails\\" + directory + "\\" + name + ".eml";
        File emlFile = new File(path);
        Properties props = System.getProperties();
        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = new FileInputStream(emlFile);
        MimeMessage message = new MimeMessage(mailSession, source);

        return message.getSentDate();
    }

    public String getHeaderValue(String directory, String name) throws FileNotFoundException, MessagingException {
        String path = "C:\\mails\\" +directory + "\\" + name + ".eml";
        File emlFile = new File(path);
        Properties props = System.getProperties();
        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = new FileInputStream(emlFile);
        MimeMessage message = new MimeMessage(mailSession, source);

        return message.getHeader("X-Seen", null );
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
