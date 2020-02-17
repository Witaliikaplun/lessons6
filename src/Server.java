import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        final int PORT = 8189;
        Socket soket = null;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущен ожидаем подключения...");
            soket = serverSocket.accept();
            System.out.println("Клиент подключился!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Socket finalSoket = soket;

        Thread stream1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //создаем потоки ввода-вывода------------------------------
                    DataInputStream in = new DataInputStream(finalSoket.getInputStream());
                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/end")) {
                            System.out.println("Клиент отключился!");
                            DataOutputStream out = new DataOutputStream(finalSoket.getOutputStream());
                            out.writeUTF(str);
                            break;
                        }
                        System.out.println("Клиент - " + str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        finalSoket.close();
                        } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread stream2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DataOutputStream out = new DataOutputStream(finalSoket.getOutputStream());
                    Scanner inTaste = new Scanner(System.in);
                    while (true) {
                        String str = inTaste.nextLine();
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }
                        out.writeUTF(str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        stream1.start();
        stream2.setDaemon(true);
        stream2.start();

        try {
            stream1.join();
            soket.close();
            serverSocket.close();
            finalSoket.close();
            serverSocket.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("END");


    }

}
