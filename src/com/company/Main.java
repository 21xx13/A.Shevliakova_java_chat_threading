package com.company;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Chat c = new Chat();
        c.startChat();
    }
}

class WriteThread extends Thread {
    String name;

    public WriteThread(String name) {
        this.name = name;
    }

    public void run() {
        while (true) {
            String message = new Scanner(System.in).nextLine();
            writeMessage(this.name + ": " + message);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Сообщение \"%s\" отправлено.%n", message);
        }
    }

    public void writeMessage(String message) {
        try {
            FileWriter fw = new FileWriter("src/message.txt", true);
            fw.write(message + "\n");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ReadThread extends Thread {
    int messageCount = 0;
    String friendName;

    public ReadThread(String name) {
        this.friendName = name;
    }

    public void run() {
        while (true) {
            readFileMessages();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void readFileMessages() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("src/message.txt"), Charset.forName("Windows-1251"));
            Object[] messagesObj = lines.stream().filter(x -> x.contains(this.friendName)).toArray();
            int newMes = messagesObj.length - this.messageCount;
            if (newMes != 0) {
                for (int i = messagesObj.length - newMes; i < messagesObj.length; i++)
                    System.out.println(messagesObj[i].toString());
                this.messageCount = messagesObj.length;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Chat {
    String name;
    String friendName;

    public Chat() {
        System.out.println("Введите свое имя");
        this.name = (new Scanner(System.in)).nextLine();
        System.out.println("Введите имя Вашего собеседника");
        this.friendName = (new Scanner(System.in)).nextLine();
    }

    public void startChat() throws IOException {
        File file = Paths.get("src/message.txt").toFile();
        if(file.delete())
            file.createNewFile();
        Thread t1 = new WriteThread(this.name);
        Thread t2 = new ReadThread(this.friendName);
        t1.start();
        t2.start();
    }
}
