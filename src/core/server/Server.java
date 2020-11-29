package core.server;

import core.highscore.HighScoreManager;
import core.question.QuestionManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Játék szerver osztálya.
 */
public class Server {
    private ExecutorService pool = Executors.newFixedThreadPool(8);
    private volatile boolean running = true;
    private ServerSocket listener;
    private Map<Socket, ClientHandle> clientHandleMap;

    /**
     * Szerver osztály inicializálója.
     * @param file Konfigurációs fájl útvonala
     * @return Igazzal tér vissza, ha sikerült inicializálni a szervert, különben hamis
     */
    public boolean init(String file) {
        try {
            ServerConfig config = ServerConfig.read(file);
            HighScoreManager.createInstance(config.getScoresData());
            QuestionManager.createInstance(config.getQuestionData());
            listener = new ServerSocket(config.getPort(), 0, InetAddress.getByName(config.getIp()));
            clientHandleMap = new HashMap<>();

            return true;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage() + "! Cannot start without question data!");
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Elindítja a szervert és fogadni kezdi a beérkező kapcsolatokat.
     */
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
                            System.out.println("New connection: " + s.toString());
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
                    if(clientHandleMap.entrySet().size() != 0) {
                        for(Map.Entry<Socket, ClientHandle> entry : clientHandleMap.entrySet()) {
                            System.out.println(entry.getKey().toString());
                        }
                    } else {
                        System.out.println("No information available!");
                    }
                }
            }
        }
        shutdown(60000);
    }

    /**
     * Szerver leállítója. Kiadás után több kapcsolatot nem fogad, de az éppen aktív kapcsolatokat nem szűnteti
     * meg azonnal, csak egy timeout érték után.
     * @param timeout timeout értéke
     */
    private void shutdown(long timeout) {
        pool.shutdown();
        if(!pool.isTerminated()) {
            try {
                pool.awaitTermination(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        closeConnections();
        HighScoreManager.getInstance().save();
        System.exit(0);
    }

    /**
     * Kapcsolat törlése az aktív kapcsolatok közül.
     * @param s Kliens socket
     */
    public synchronized void removeConnection(Socket s) {
        clientHandleMap.remove(s);
    }

    /**
     * Minden aktív kapcsolat lezárása.
     */
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
