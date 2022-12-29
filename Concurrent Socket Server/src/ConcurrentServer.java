import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentServer {
  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Usage: [Port]");
      return;
    }
    int port = Integer.parseInt(args[0]);
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Server listening on port: " + port);

      ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
      while (true) {
        pool.execute(new ServerThread(serverSocket.accept()));
        System.out.println("Client connected");
      }
    }
    catch (IOException e) {
      System.out.println("Server exception: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
