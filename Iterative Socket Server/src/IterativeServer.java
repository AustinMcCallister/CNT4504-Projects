import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;

public class IterativeServer {
  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Usage: [Port]");
      return;
    }
    int port = Integer.parseInt(args[0]);
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Server listening on port: " + port);
      while (true) {
        try (Socket socket = serverSocket.accept()) {
          System.out.println("Client connected");
          BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

          String clientInstruction;
          while (!Objects.equals((clientInstruction = reader.readLine()), null)) {
            switch (clientInstruction) {
              case "1" -> runCommand(new String[] {"date"}, socket);
              case "2" -> runCommand(new String[] {"uptime"}, socket);
              case "3" -> runCommand(new String[] {"free"}, socket);
              case "4" -> runCommand(new String[] {"ss"}, socket);
              case "5" -> runCommand(new String[] {"w"}, socket);
              case "6" -> runCommand(new String[] {"ps", "aux"}, socket);
              default -> System.out.println("Something went wrong");
            }
          }
        }
        catch (SocketException e) {
          System.out.println("Socket exception: " + e.getMessage());
        }
      }
    }
    catch (IOException e) {
      System.out.println("Server exception: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void runCommand(String[] command, Socket socket) {
    try {
      Process process = Runtime.getRuntime().exec(command);
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

      String output;
      while (!Objects.equals(output = reader.readLine(), null)) {
        writer.println(output);
      }
      writer.println("--end--");
    }
    catch (IOException e) {
      System.out.println("Server exception: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
