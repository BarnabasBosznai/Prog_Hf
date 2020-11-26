package core.server;
import core.highscore.HighScoreManager;
import core.message.Message;
import core.message.MessageType;
import core.question.QuestionManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private ExecutorService pool = Executors.newFixedThreadPool(8);
    private volatile boolean running = true;
    private ServerSocket listener;
    private Map<Socket, ClientHandle> clientHandleMap;

    public Server(String ip, int port) {
        try {
            HighScoreManager.createInstance("highscores.txt");
            QuestionManager.createInstance("questions.json");
            listener = new ServerSocket(port, 0, InetAddress.getByName(ip));
            clientHandleMap = new HashMap<>();
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
                            ClientHandle ch = new ClientHandle(this, s);
                            clientHandleMap.put(s, ch);
                            pool.execute(ch);
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
                } else if(command.equals("CON NUM")) {
                    System.out.println("Number of users: " + clientHandleMap.size());
                } else if(command.equals("CON INFO")) {
                    for(Map.Entry<Socket, ClientHandle> entry : clientHandleMap.entrySet()) {
                        System.out.println(entry.getKey().toString());
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
                //pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                pool.awaitTermination(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        closeConnections();
        HighScoreManager.getInstance().close();
        System.exit(0);
    }

    public synchronized void removeConnection(Socket s) {
        clientHandleMap.remove(s);
    }

    private void closeConnections() {
        for(Map.Entry<Socket, ClientHandle> entry : clientHandleMap.entrySet()) {
            try {
                entry.getKey().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
