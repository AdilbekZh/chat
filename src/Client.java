import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    JTextArea textArea;
    JTextField textField;
    BufferedReader reader;
    PrintWriter writer;
    Socket socket;
	
	private final static String HOST_NAME = "localhost";

    Client() {
        JFrame frame = new JFrame("Chat");
        JPanel panel = new JPanel();
        textArea = new JTextArea(15, 50);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        JScrollPane qScroller = new JScrollPane(textArea);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        textField = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());
        panel.add(qScroller);
        panel.add(textField);
        panel.add(sendButton);
        setUpNetworking();

        Thread thread = new Thread(new MessageHandler());
        thread.start();

        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    void setUpNetworking() {
        try {
            socket = new Socket(HOST_NAME, 2345);
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(isr);
            writer = new PrintWriter(socket.getOutputStream());
            System.out.println("Networking estabilished");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            writer.println(textField.getText());
            writer.flush();

            textField.setText("");
            textField.requestFocus();
        }
    }

    class MessageHandler implements Runnable{
        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("Read: " + message);
                    textArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
