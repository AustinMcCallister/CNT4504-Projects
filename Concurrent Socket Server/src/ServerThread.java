import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class ServerThread extends Thread {
  private final Socket socket;
  public ServerThread(Socket socket) {
    this.socket = socket;
  }

  public void run() {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      String clientInstruction;
      while (!Objects.equals((clientInstruction = reader.readLine()), null)) {
        switch (clientInstruction) {
          case "1" -> runCommand(new String[]{"date"}, socket);
          case "2" -> runCommand(new String[]{"uptime"}, socket);
          case "3" -> runCommand(new String[]{"free"}, socket);
          case "4" -> runCommand(new String[]{"ss"}, socket);
          case "5" -> runCommand(new String[]{"w"}, socket);
          case "6" -> runCommand(new String[]{"ps", "aux"}, socket);
          default -> System.out.println("Something went wrong");
        }
      }
    }
    catch (IOException e) {
      System.out.println("Server exception: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void runCommand(String[] command, Socket socket) {
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
