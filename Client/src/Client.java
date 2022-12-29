import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Client {
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Usage: [Hostname] [Port]");
      return;
    }
    try (Socket socket = new Socket(args[0], Integer.parseInt(args[1]))) {
      socket.close();
      printMenu();
      runMenu(args[0], Integer.parseInt(args[1]));
    }
    catch (UnknownHostException e) {
      System.out.println("Server not found: " + e.getMessage());
    }
    catch (IOException e) {
      System.out.println("I/O error: " + e.getMessage());
    }
  }

  public static void printMenu() {
    System.out.println("1. Date and Time");
    System.out.println("2. Uptime");
    System.out.println("3. Memory Use");
    System.out.println("4. Netstat");
    System.out.println("5. Current Users");
    System.out.println("6. Running Processes");
    System.out.println("7. Quit");
    System.out.print("Enter your choice: ");
  }

  public static void runMenu(String hostname, int port) {
    Scanner scanner = new Scanner(System.in);

    byte choice = 0;
    do {
      try {
        choice = scanner.nextByte();
        switch (choice) {
          case 1, 2, 3, 4, 5, 6 -> sendInstruction(hostname, port, choice);
          case 7 -> {
            System.out.println("\nGood Bye");
            return;
          }
          default -> System.out.print("Invalid choice! Enter 1-7: ");
        }
      }
      catch (InputMismatchException e) {
        System.out.print("Invalid choice! Enter 1-7: ");
        scanner.next(); // Flush stdin
      }
    } while (choice != 7);
  }

  public static void sendInstruction(String hostname, int port, byte choice) {
    Scanner scanner = new Scanner(System.in);

    System.out.print("How many connections? (1 - 25): ");
    while (true) {
      try {
        byte connections = scanner.nextByte();
        if (connections >= 1 && connections <= 25) {
          ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);

          ArrayList<Long> responseTimes = new ArrayList<>();
          System.out.println();
          for (int i = 0; i < connections; i++) {
            try {
              pool.execute(new ClientThread(new Socket(hostname, port), choice, responseTimes));
            }
            catch (UnknownHostException e) {
              System.out.println("Server not found: " + e.getMessage());
            }
            catch (IOException e) {
              System.out.println("I/O error: " + e.getMessage());
            }
          }
          pool.shutdown();
          try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
              pool.shutdownNow();
              if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                System.err.println("Pool did not terminate");
              }
            }
          }
          catch (InterruptedException e) {
            pool.shutdownNow();
          }
          long totalTime = 0;
          for (Long i : responseTimes) {
            totalTime += i;
          }
          System.out.println("------Results------");
          System.out.println("Connections: " + connections);
          System.out.println("Total time: " + totalTime + "ms");
          System.out.println("Average time: " + (totalTime / connections) + "ms");
          System.out.println();
          printMenu();
          return;
        }
        else {
          System.out.print("Invalid choice! Enter 1-25: ");
        }
      }
      catch (InputMismatchException e) {
        System.out.print("Invalid choice! Enter 1-25: ");
        scanner.next(); // Flush stdin
      }
    }
  }
}
