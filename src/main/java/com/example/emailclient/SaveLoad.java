package com.example.emailclient;

import com.sun.mail.pop3.POP3SSLStore;
import com.sun.mail.pop3.POP3Store;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.*;


public class SaveLoad
{
    public void download(String host, String email, String password, Integer port) throws MessagingException {

        File folder = new File("C:\\mails");

        Boolean ssl = false;
        java.util.Properties properties = new java.util.Properties();

        properties.setProperty("mail.pop3.host", host);
        properties.setProperty("mail.pop3.port", port.toString());
        if(port.equals(995))
        {
            properties.setProperty("mail.pop3.ssl.enable", "true");
        }

        Session session = Session.getInstance(properties);
        POP3SSLStore sslStore = null;
        POP3Store store = null;

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
            store.connect(host, port, email, password);
            inbox = store.getFolder("INBOX");
        }

        inbox.open(Folder.READ_ONLY);
        Message[] messages = inbox.getMessages();
        System.out.println("Anzahl im Postfach: " + messages.length);
        System.out.println("Anzahl im Ordner: " + folder.listFiles().length);
        for (int i = folder.listFiles().length; i< messages.length; i++)
        {
            try
            {
                messages[i].writeTo(new FileOutputStream(new File("C:\\mails\\mail" + (i+1) + ".eml")));
                setFileCreationDate("C:\\mails\\mail" + (i+1) + ".eml", messages[i].getSentDate());
            }
            catch (IOException e)
            {
                System.out.print("could not save mail" + messages[i].getSubject());
                e.printStackTrace();
            }
        }
    }


    private void setFileCreationDate(String filePath, Date creationDate) throws IOException
    {

        BasicFileAttributeView attributes = Files.getFileAttributeView(Paths.get(filePath), BasicFileAttributeView.class);
        FileTime time = FileTime.fromMillis(creationDate.getTime());
        attributes.setTimes(time, time, time);
    }


    public void addHeader(String filePath, String headerValue, Date creationDate) throws IOException, MessagingException
    {
        File emlFile = new File(filePath);
        Properties props = System.getProperties();
        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = new FileInputStream(emlFile);
        MimeMessage message = new MimeMessage(mailSession, source);

        message.addHeader("X-Seen", headerValue);
        message.saveChanges();

        message.writeTo(new FileOutputStream(new File(filePath)));

        BasicFileAttributeView attributes = Files.getFileAttributeView(Paths.get(filePath), BasicFileAttributeView.class);
        FileTime time = FileTime.fromMillis(creationDate.getTime());
        attributes.setTimes(time, time, time);
    }

    public String loadBody(String subject) throws MessagingException, IOException
    {
        String path = "C:\\mails\\" + subject + ".eml";
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
                    // if it is MultiPart/Alternative, print the first bodypart
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
        // if the mail is not splitted into parts
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


    public String getSubject(String name) throws FileNotFoundException, MessagingException {
        String path = "C:\\mails\\" + name + ".eml";
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
                        catch (UnsupportedEncodingException e)
                        {
                            System.out.println("\u001B[31mUnsupported encoding: " + charset + "\u001B[0m");
                        }
                    }
                    else if (encoding.equals("b"))
                    {
                        byte[] bytes = Base64.getDecoder().decode(encodedText);
                        try
                        {
                            deciphered.append(new String(bytes, charset));
                        }
                        catch (UnsupportedEncodingException e)
                        {
                            System.out.println("\u001B[31mUnsupported encoding: " + charset + "\u001B[0m");
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

    public String getFrom(String name) throws MessagingException, FileNotFoundException {
        String path = "C:\\mails\\" + name + ".eml";
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

    public String getTo(String name) throws MessagingException, FileNotFoundException {
        String path = "C:\\mails\\" + name + ".eml";
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

    public String getDate(String name) throws MessagingException, FileNotFoundException {
        String path = "C:\\mails\\" + name + ".eml";
        File emlFile = new File(path);
        Properties props = System.getProperties();
        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = new FileInputStream(emlFile);
        MimeMessage message = new MimeMessage(mailSession, source);

        if(message.getSentDate() != null)
        {
            String date = message.getSentDate().toString();
            String[] dateParts = date.split(" ");


            dateParts[0] = dateParts[0].replace("Mon", "Montag, ");
            dateParts[0] = dateParts[0].replace("Thu", "Dienstag, ");
            dateParts[0] = dateParts[0].replace("Wed", "Mittwoch, ");
            dateParts[0] = dateParts[0].replace("Tue", "Donnerstag, ");
            dateParts[0] = dateParts[0].replace("Fri", "Freitag, ");
            dateParts[0] = dateParts[0].replace("Sat", "Samstag, ");
            dateParts[0] = dateParts[0].replace("Sun", "Sonntag, ");

            dateParts[1] = dateParts[1].replace("Jan", "01");
            dateParts[1] = dateParts[1].replace("Feb", "02");
            dateParts[1] = dateParts[1].replace("Mar", "03");
            dateParts[1] = dateParts[1].replace("Apr", "04");
            dateParts[1] = dateParts[1].replace("Mai", "05");
            dateParts[1] = dateParts[1].replace("Jun", "06");
            dateParts[1] = dateParts[1].replace("Jul", "07");
            dateParts[1] = dateParts[1].replace("Aug", "08");
            dateParts[1] = dateParts[1].replace("Sep", "09");
            dateParts[1] = dateParts[1].replace("Oct", "10");
            dateParts[1] = dateParts[1].replace("Nov", "11");
            dateParts[1] = dateParts[1].replace("Dec", "12");

            return dateParts[0] + dateParts[2] + "." + dateParts[1] + "." + dateParts[5] + " " + dateParts[3];
        }
        else
        {
            return "";
        }
    }

    public Date getSentDate(String name) throws MessagingException, FileNotFoundException {
        String path = "C:\\mails\\" + name + ".eml";
        File emlFile = new File(path);
        Properties props = System.getProperties();
        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = new FileInputStream(emlFile);
        MimeMessage message = new MimeMessage(mailSession, source);

        return message.getSentDate();
    }

    public String getHeaderValue(String name) throws FileNotFoundException, MessagingException {
        String path = "C:\\mails\\" + name + ".eml";
        File emlFile = new File(path);
        Properties props = System.getProperties();
        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = new FileInputStream(emlFile);
        MimeMessage message = new MimeMessage(mailSession, source);

        return message.getHeader("X-Seen", null );

    }

}
