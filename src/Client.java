import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Socket socket = null;
        Scanner sc = new Scanner(System.in);
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataInputStream finalIn = in;
        DataOutputStream finalOut = out;
        Thread stream1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String str = finalIn.readUTF();
                        System.out.println("Сервер - " + str);
                        if (str.equals("/end")) {
                            System.out.println("Сервер отключился");
                            finalOut.writeUTF(str);
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread stream2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String str = sc.nextLine();
                    if(Thread.currentThread().isInterrupted()) break;
                    try {
                        finalOut.writeUTF(str);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        stream1.start();
        stream2.start();

        try {
            stream1.join();
            stream2.interrupt();
            stream2.join();
            socket.close();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("END");
    }
}
