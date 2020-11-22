package core.server;
import core.highscore.HighScoreManager;
import core.question.QuestionManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private ExecutorService pool = Executors.newFixedThreadPool(10);
    private volatile boolean running = true;
    private ServerSocket listener;
    private List<Socket> connections = new ArrayList<>();

    String[] prizes = { "0", "5.000", "10.000","25.000", "50.000", "100.000", "200.000", "300.000", "500.000", "800.000", "1.500.000", "3.000.000", "5.000.000", "10.000.000", "20.000.000", "40.000.000" };

    private QuestionManager questionManager = QuestionManager.getInstance("questions.json");
    private HighScoreManager highScoreManager = HighScoreManager.getInstance();

    public String getPrize(int index) {
        return prizes[index];
    }

    public Server(String ip, int port) {
        try {
            //listener = new ServerSocket(port, 0, InetAddress.getByName("192.168.1.2")); // 58901
            listener = new ServerSocket(port, 0, InetAddress.getByName(ip)); // 58901
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Server starting...");
        Thread socketConnectionAsync = new Thread(() -> {
                    while(running) {
                        Socket s;
                        try {
                            s = listener.accept();
                            connections.add(s);
                            pool.execute(new ClientHandle(this, s,  questionManager, highScoreManager));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
        });
        socketConnectionAsync.start();
        run();
    }

    private void run() {
        Scanner scanner = new Scanner(System.in);
        while(running) {
            if(scanner.hasNext()) {
                String command = scanner.nextLine();
                if(command.equals("STOP")) {
                    System.out.println("Server stopping...");
                    running = false;
                } else if(command.equals("CONN NUM")) {
                    System.out.println("Number of users: " + connections.size());
                } else if(command.equals("CONN INFO")) {
                    for(Socket s : connections) {
                        System.out.println(s.toString());
                    }
                }
            }
        }

        shutdown();
    }

    private void shutdown() {
        pool.shutdown();
        if(!pool.isTerminated()) {
            try {
                pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        closeConnections();
        highScoreManager.close();
        System.exit(0);
    }

    public synchronized void removeConnection(Socket s) {
        connections.remove(s);
    }

    private void closeConnections() {
        for(Socket s : connections) {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
