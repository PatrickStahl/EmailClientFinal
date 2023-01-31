package com.example.emailclient;
import java.io.*;

public class ReadWrite
{
    public void write(String data)
    {
        String[] dataParts = data.split("\\,");
        String username = dataParts[0];
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\files\\" +  username + ".txt"));
            bw.write(data);
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //filepath = username
    public String readUsername(String filePath) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader("C:\\files\\" + filePath + ".txt"));
        String data = br.readLine();
        br.close();
        String[] dataParts = data.split("\\,");

        return dataParts[0];
    }

    public String readPassword(String filePath) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader("C:\\files\\" + filePath + ".txt"));
        String data = br.readLine();
        br.close();
        String[] dataParts = data.split("\\,");

        return dataParts[1];
    }

    public String readInputAddress(String filePath) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader("C:\\files\\" + filePath + ".txt"));
        String data = br.readLine();
        br.close();
        String[] dataParts = data.split("\\,");

        return dataParts[2];
    }

    public String readInputPort(String filePath) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader("C:\\files\\" + filePath + ".txt"));
        String data = br.readLine();
        br.close();
        String[] dataParts = data.split("\\,");

        return dataParts[3];
    }

    public String readOutputAddress(String filePath) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader("C:\\files\\" + filePath + ".txt"));
        String data = br.readLine();
        br.close();
        String[] dataParts = data.split("\\,");

        return dataParts[4];
    }

    public String readOutputPort(String filePath) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader("C:\\files\\" + filePath + ".txt"));
        String data = br.readLine();
        br.close();
        String[] dataParts = data.split("\\,");

        return dataParts[5];
    }

    public String readServer(String filePath) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader("C:\\files\\" + filePath + ".txt"));
        String data = br.readLine();
        br.close();
        String[] dataParts = data.split("\\,");

        return dataParts[6];
    }

    public String readEmail(String filePath) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader("C:\\files\\" + filePath + ".txt"));
        String data = br.readLine();
        br.close();
        String[] dataParts = data.split("\\,");

        return dataParts[7];
    }

}
