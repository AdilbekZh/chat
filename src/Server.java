import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    ArrayList clientOutputStreams;

    public Server() {
        clientOutputStreams = new ArrayList();
        try {
            ServerSocket serverSocket = new ServerSocket(2345);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer =
                        new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("Got a connection");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class ClientHandler implements Runnable {

        BufferedReader reader;
        Socket socket;

        public ClientHandler(Socket clientSocket) {
            try {
                socket = clientSocket;
                InputStreamReader isr =
                        new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(isr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("Read: " + message);
                    tellEveryone(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void tellEveryone(String message) {
        for (int i = 0; i < clientOutputStreams.size(); i++) {
            PrintWriter writer = (PrintWriter) clientOutputStreams.get(i);
            writer.println(message);
            writer.flush();
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
